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
package pl.otros.logview.accept;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JTable;

import pl.otros.logview.LogData;
import pl.otros.logview.gui.LogDataTableModel;

public class SelectedThreadAcceptCondition extends SelectionAwareAcceptCondition {

  protected Set<String> threads = new HashSet<String>();

  public SelectedThreadAcceptCondition(JTable jTable, LogDataTableModel dataTableModel) {
    super(jTable, dataTableModel);
  }

  @Override
  public boolean accept(LogData data) {
    return threads.contains(data.getThread());
  }

  @Override
  public String getName() {
    return "Selected threads";
  }

  @Override
  public String getDescription() {
    return "Selected threads";
  }

  @Override
  protected void updateAfterSelection() {
    threads.clear();
    int[] selectedRows = jTable.getSelectedRows();
    for (int i : selectedRows) {
      LogData logData = dataTableModel.getLogData(jTable.convertRowIndexToModel(i));
      threads.add(logData.getThread());
    }
  }
}
