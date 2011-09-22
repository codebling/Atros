/*******************************************************************************
 * Copyright 2011 Krzysztof Otrebski
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package pl.otros.logview.gui.actions.search;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import pl.otros.logview.MarkerColors;
import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.LogViewPanelWrapper;
import pl.otros.logview.gui.StatusObserver;

public class SearchAction extends AbstractAction {

  public enum SearchMode {
    STRING_CONTAINS("String conatins search"), REGEX("Regex search");

    private final String name;

    private SearchMode(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

  }

  private SearchMode searchMode = SearchMode.STRING_CONTAINS;
  private StatusObserver statusObserver;
  private boolean markFound = false;
  private JTabbedPane jTabbedPane;
  private JTextField searchStringField;
  private SearchEngine searchEngine;
  private MarkerColors markerColors = MarkerColors.Aqua;
  private final SearchDirection searchDirection;

  public SearchAction(StatusObserver statusObserver, JTabbedPane jTabbedPane, SearchDirection searchDirection) {
    this.statusObserver = statusObserver;
    this.jTabbedPane = jTabbedPane;
    this.searchDirection = searchDirection;
    if (searchDirection.equals(SearchDirection.FORWARD)) {
      this.putValue(Action.NAME, "Next");
      this.putValue(Action.SMALL_ICON, Icons.ARROW_DOWN);
    } else {
      this.putValue(Action.NAME, "Previous");
      this.putValue(Action.SMALL_ICON, Icons.ARROW_UP);
    }
    searchEngine = new SearchEngine();
  }

  @Override
  public void actionPerformed(ActionEvent arg0) {
    String text = searchStringField.getText().trim();
    if (text.trim().length() == 0) {
      statusObserver.updateStatus("No search criteria", StatusObserver.LEVEL_WARNING);
      return;
    }
    performSearch(text, searchDirection);
  }

  public void performSearch(String text, SearchDirection direction) {

    LogViewPanelWrapper lvPanel = (LogViewPanelWrapper) jTabbedPane.getSelectedComponent();
    if (lvPanel == null) {
      return;
    }
    JTable table = lvPanel.getLogViewPanel().getTable();

    NextRowProvider nextRowProvider = NextRowProviderFactory.getFilteredTableRow(table, direction);
    SearchContext context = new SearchContext();
    context.setDataTableModel(lvPanel.getDataTableModel());
    SearchMatcher searchMatcher = null;
    if (SearchMode.STRING_CONTAINS.equals(searchMode)) {
      searchMatcher = new StringContainsSearchMatcher(text);
    } else if (SearchMode.REGEX.equals(searchMode)) {
      try {
        searchMatcher = new RegexMatcher(text);
      } catch (Exception e) {
        statusObserver.updateStatus("Error in regular expression: " + e.getMessage(), StatusObserver.LEVEL_ERROR);
        return;
      }
    }
    context.setSearchMatcher(searchMatcher);
    SearchResult searchNext = searchEngine.searchNext(context, nextRowProvider);
    if (searchNext.isFound()) {
      int row = table.convertRowIndexToView(searchNext.getRow());
      Rectangle rect = table.getCellRect(row, 0, true);
      table.scrollRectToVisible(rect);
      table.clearSelection();
      table.setRowSelectionInterval(row, row);
      statusObserver.updateStatus(String.format("Found at row %d", row), StatusObserver.LEVEL_NORMAL);
      if (markFound) {
        lvPanel.getDataTableModel().markRows(markerColors, table.convertRowIndexToModel(row));
      }

      scrollToSearchResult(searchMatcher.getFoundTextFragments(lvPanel.getDataTableModel().getLogData(table.convertRowIndexToModel(row))), lvPanel
          .getLogViewPanel().getLogDetailTextArea());
    } else {
      statusObserver.updateStatus(String.format("\"%s\" not found", text), StatusObserver.LEVEL_WARNING);
    }
  }

  private void scrollToSearchResult(ArrayList<String> toHighlight, JTextPane textPane) {
    if (toHighlight.size() == 0) {
      return;
    }
    try {
      StyledDocument logDetailsDocument = textPane.getStyledDocument();
      String text = logDetailsDocument.getText(0, logDetailsDocument.getLength());
      String string = toHighlight.get(0);
      textPane.setCaretPosition(Math.max(text.indexOf(string), 0));
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
  }

  public JTextField getSearchStringField() {
    return searchStringField;
  }

  public void setSearchStringField(JTextField searchStringField) {
    this.searchStringField = searchStringField;
  }

  public boolean isMarkFound() {
    return markFound;
  }

  public void setMarkFound(boolean markFound) {
    this.markFound = markFound;
  }

  public MarkerColors getMarkerColors() {
    return markerColors;
  }

  public void setMarkerColors(MarkerColors markerColors) {
    this.markerColors = markerColors;
  }

  public void setSearchMode(SearchMode searchMode) {
    this.searchMode = searchMode;
  }

  public SearchMode getSearchMode() {
    return searchMode;
  }

}
