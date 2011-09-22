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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import pl.otros.logview.Note;
import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.LogDataTableModel;
import pl.otros.logview.gui.LogViewPanelWrapper;
import pl.otros.logview.gui.StatusObserver;

public class JumpToMarkedAction extends AbstractAction {

  private StatusObserver observer;
  private Direction direction;
  private JTabbedPane tabbedPane;

  public enum Direction {
    FORWARD, BACKWARD;
  }

  public JumpToMarkedAction(Direction direction, StatusObserver observer, JTabbedPane tabbedPane) {
    this.observer = observer;
    this.direction = direction;
    this.tabbedPane = tabbedPane;
    if (direction == Direction.FORWARD) {
      putValue(Action.NAME, "Next marked/noted");
      putValue(Action.SMALL_ICON, Icons.ARROW_DOWN_IN_BOX);
    } else {
      putValue(Action.NAME, "Previous marked/noted");
      putValue(Action.SMALL_ICON, Icons.ARROW_UP_IN_BOX);
    }

  }

  @Override
  public void actionPerformed(ActionEvent arg0) {
    // LogViewInternalFrame lvFrame = FrameManger.getInstance().getActiveLogViewInternalFrame();
    LogViewPanelWrapper lvPanel = (LogViewPanelWrapper) tabbedPane.getSelectedComponent();
    if (lvPanel == null) {
      observer.updateStatus("Log file not open", StatusObserver.LEVEL_ERROR);
      return;

    }
    JTable table = lvPanel.getLogViewPanel().getTable();
    LogDataTableModel model = lvPanel.getDataTableModel();
    if (table.getRowCount() == 0) {
      observer.updateStatus("Empty log", StatusObserver.LEVEL_ERROR);
      return;
    }
    int startRow = table.getSelectedRow();
    startRow = startRow < 0 ? startRow = 0 : startRow;
    int nextRow = startRow;
    while (true) {
      if (direction == Direction.FORWARD) {
        nextRow++;
      } else {
        nextRow--;
      }

      if (nextRow == table.getRowCount()) {
        nextRow = 0;
      } else if (nextRow < 0) {
        nextRow = table.getRowCount() - 1;
      }

      if (nextRow == startRow) {
        // not found
        observer.updateStatus("Next marked not found", StatusObserver.LEVEL_ERROR);
        return;
      }
      observer.updateStatus("Checking row: " + nextRow);
      int rowInModel = table.convertRowIndexToModel(nextRow);
      boolean marked = model.isMarked(rowInModel);
      Note n = model.getNote(rowInModel);
      boolean haveNote = n != null && n.getNote() != null && n.getNote().length() > 0;
      if (marked || haveNote) {
        Rectangle rect = table.getCellRect(nextRow, 0, true);
        table.scrollRectToVisible(rect);
        table.clearSelection();
        table.setRowSelectionInterval(nextRow, nextRow);
        observer.updateStatus("Next marked/noted found at " + table.convertRowIndexToView(nextRow), StatusObserver.LEVEL_NORMAL);
        break;
      }

    }

  }

}
