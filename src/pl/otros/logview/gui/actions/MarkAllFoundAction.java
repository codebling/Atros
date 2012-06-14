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
package pl.otros.logview.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;

import pl.otros.logview.MarkerColors;
import pl.otros.logview.gui.*;

public class MarkAllFoundAction extends AbstractAction implements KeyListener {

  private StatusObserver statusObserver;
  private JTabbedPane jTabbedPane;
  private JTextField searchField;

  public MarkAllFoundAction(StatusObserver statusObserver, JTabbedPane jTabbedPane, JTextField searchField) {
    super();
    this.putValue(Action.NAME, "Mark all found");
    this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
    this.putValue(SMALL_ICON, Icons.AUTOMATIC_MARKERS);
    this.statusObserver = statusObserver;
    this.jTabbedPane = jTabbedPane;
    this.searchField = searchField;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    LogViewPanelWrapper lvFrame = (LogViewPanelWrapper) jTabbedPane.getSelectedComponent();
    if (lvFrame == null) {
      return;
    }
    JTable table = lvFrame.getLogViewPanel().getTable();
    LogDataTableModel model = lvFrame.getDataTableModel();
    int marked = markAllFound(table, model, searchField.getText(), LogViewMainFrame.getInstance().getMarkerColors());
    statusObserver.updateStatus(marked + " messages marked for string \"" + searchField.getText() + "\"");
  }

  public int markAllFound(JTable table, LogDataTableModel dataTableModel, String string, MarkerColors markerColors) {
    string = string.trim().toLowerCase();
    if (string.length() == 0) {
      return 0;
    }
    ArrayList<Integer> toMark = new ArrayList<Integer>();
    for (int i = 0; i < table.getRowCount(); i++) {
      String message = dataTableModel.getLogData(table.convertRowIndexToModel(i)).getMessage().toLowerCase();
      if (message.contains(string)) {
        toMark.add(table.convertRowIndexToModel(i));
      }
    }
    if (toMark.size() > 0) {

      int[] rows = new int[toMark.size()];
      for (int i = 0; i < rows.length; i++) {
        rows[i] = toMark.get(i);

      }
      dataTableModel.markRows(markerColors, rows);
    }
    return toMark.size();
  }

  @Override
  public void keyTyped(KeyEvent e) {

  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == 10 && KeyEvent.CTRL_MASK == e.getModifiers()) {
      actionPerformed(new ActionEvent(e.getSource(), e.getID(), ""));
    }

  }

  @Override
  public void keyReleased(KeyEvent e) {

  }
}
