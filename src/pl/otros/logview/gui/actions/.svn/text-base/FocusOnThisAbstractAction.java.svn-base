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

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JTable;

import pl.otros.logview.LogData;
import pl.otros.logview.filter.LogFilter;
import pl.otros.logview.gui.LogDataTableModel;

public abstract class FocusOnThisAbstractAction<T extends LogFilter> extends AbstractAction {

  protected JTable jTable;
  protected T filter;
  protected LogDataTableModel dataTableModel;
  protected JCheckBox filterEnableCheckBox;

  public FocusOnThisAbstractAction(JTable jTable, T filter, LogDataTableModel dataTableModel, JCheckBox filterEnableCheckBox) {
    super();
    this.jTable = jTable;
    this.filter = filter;
    this.dataTableModel = dataTableModel;
    this.filterEnableCheckBox = filterEnableCheckBox;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    int[] selectedRows = jTable.getSelectedRows();
    if (selectedRows.length <= 0) {
      return;
    }

    LogData[] selectedLogData = new LogData[selectedRows.length];
    for (int i = 0; i < selectedRows.length; i++) {
      selectedLogData[i] = dataTableModel.getLogData(jTable.convertRowIndexToModel(selectedRows[i]));

    }
    action(e, filter, selectedLogData);
    filterEnableCheckBox.setSelected(true);
    filter.setEnable(true);
  }

  public abstract void action(ActionEvent e, T filter, LogData... selectedLogData);
}
