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

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import pl.otros.logview.LogData;
import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.LogDataTableModel;
import pl.otros.logview.gui.LogViewPanelWrapper;
import pl.otros.logview.gui.StatusObserver;

public class SearchByLevel extends AbstractAction {

  private LogDataTableModel model;
  private JTable table;
  private int lastRow = 0;
  private StatusObserver statusObserver;
  private JTabbedPane jTabbedPane;
  // -1 for backward search
  private int direction = 1;
  private int minLevel;

  public SearchByLevel(StatusObserver statusObserver, JTabbedPane jTabbedPane, int direction, Level minLevel) {
    super();
    putValue(SHORT_DESCRIPTION, "Search for " + (direction > 0 ? "next" : "previous") + " event with level " + minLevel.getName() + " or higher.");

    Icon icon = null;
    if (minLevel.intValue() == Level.INFO.intValue()) {
      icon = direction > 0 ? Icons.NEXT_LEVEL_INFO : Icons.PREV_LEVEL_INFO;
    } else if (minLevel.intValue() == Level.WARNING.intValue()) {
      icon = direction > 0 ? Icons.NEXT_LEVEL_WARNING : Icons.PREV_LEVEL_WARNING;
    } else if (minLevel.intValue() == Level.SEVERE.intValue()) {
      icon = direction > 0 ? Icons.NEXT_LEVEL_ERROR : Icons.PREV_LEVEL_ERROR;
    }
    putValue(SMALL_ICON, icon);
    this.direction = direction;
    this.statusObserver = statusObserver;
    this.jTabbedPane = jTabbedPane;
    this.minLevel = minLevel.intValue();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    LogViewPanelWrapper lvFrame = (LogViewPanelWrapper) jTabbedPane.getSelectedComponent();
    if (lvFrame == null) {
      return;
    }
    table = lvFrame.getLogViewPanel().getTable();
    model = lvFrame.getDataTableModel();

    if (table.getRowCount() == 0) {
      statusObserver.updateStatus("Empty log", StatusObserver.LEVEL_WARNING);
      return;
    }

    int rowSearchStart = lastRow;

    if (table.getSelectedRow() >= 0) {
      lastRow = table.getSelectedRow() + direction;
      rowSearchStart = lastRow;
    } else {
      rowSearchStart = 0;
      lastRow = 0;
    }

    if (direction > 0 && lastRow > table.getRowCount() - 1) {
      lastRow = 0;
      rowSearchStart = 0;
    }

    if (direction < 0 && lastRow < 0) {
      lastRow = table.getRowCount() - 1;
      rowSearchStart = lastRow;
    }

    while (true) {
      LogData ld = model.getLogData(table.convertRowIndexToModel(lastRow));
      if (ld.getLevel().intValue() >= minLevel) {

        Rectangle rect = table.getCellRect(lastRow, 0, true);
        table.scrollRectToVisible(rect);
        table.clearSelection();
        table.setRowSelectionInterval(lastRow, lastRow);
        lastRow += direction;
        statusObserver.updateStatus("Founded at row " + (lastRow - direction), StatusObserver.LEVEL_NORMAL);
        break;
      }
      lastRow += direction;
      if (direction > 0 && lastRow >= table.getRowCount()) {
        lastRow = 0;
      }
      if (direction < 0 && lastRow < 0) {
        lastRow = table.getRowCount() - 1;
      }

      if (lastRow == rowSearchStart) {
        lastRow = 0;
        statusObserver.updateStatus("Not found", StatusObserver.LEVEL_ERROR);
        break;
      }
    }
  }

  public StatusObserver getStatusObserver() {
    return statusObserver;
  }

  public void setStatusObserver(StatusObserver statusObserver) {
    this.statusObserver = statusObserver;
  }

}
