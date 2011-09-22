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
import java.awt.event.ActionListener;

import javax.swing.JTable;

import pl.otros.logview.MarkerColors;
import pl.otros.logview.gui.MarkableTableModel;
import pl.otros.logview.gui.StatusObserver;

public class MarkRowAction implements ActionListener {

  private JTable table;
  private MarkableTableModel model;
  private StatusObserver observer;

  public MarkRowAction(JTable table, MarkableTableModel model, StatusObserver statusObserver) {
    this.table = table;
    this.model = model;
    this.observer = statusObserver;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    int[] selected = table.getSelectedRows();
    for (int i = 0; i < selected.length; i++) {
      selected[i] = table.convertRowIndexToModel(selected[i]);
    }
    model.markRows(MarkerColors.Lime, selected);
    if (observer != null) {
      observer.updateStatus(selected.length + " rows marked");
    }

  }

}
