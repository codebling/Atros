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
package pl.otros.logview.gui.table;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class ColumnHideTableModelDecorator implements TableModel {

  private boolean[] columnsShow;
  private int[] columntMapping;
  private TableModel delegate;
  private DefaultTableModel dummy;
  private int visibleColumntCount = 0;

  public ColumnHideTableModelDecorator(TableModel delegate) {
    this.delegate = delegate;
    columnsShow = new boolean[delegate.getColumnCount()];
    columntMapping = new int[delegate.getColumnCount()];
    dummy = new DefaultTableModel();
    for (int i = 0; i < delegate.getColumnCount(); i++) {
      columnsShow[i] = true;
      columntMapping[i] = i;
    }
    remap();
  }

  public void setColumnVisible(int column, boolean visible) {
    columnsShow[column] = visible;
    remap();
  }

  private void remap() {

    visibleColumntCount = 0;
    for (boolean visible : columnsShow) {
      if (visible) {
        visibleColumntCount++;
      }
    }

    int visibleColumn = 0;
    for (int i = 0; i < columnsShow.length; i++) {
      boolean visible = columnsShow[i];
      if (visible) {
        columntMapping[visibleColumn++] = i;
      }
    }
    dummy.fireTableStructureChanged();
  }

  public int convertToDelegateColumnt(int hideColumnModelIndex) {
    return columntMapping[hideColumnModelIndex];
  }

  public int convertToHideModel(int delegateColumntIndex) {
    int result = -1;
    for (int i = 0; i < columntMapping.length; i++) {
      if (columntMapping[i] == delegateColumntIndex) {
        result = i;
        break;
      }
    }
    return result;
  }

  public int getRowCount() {
    return delegate.getRowCount();
  }

  public int getColumnCount() {
    return visibleColumntCount;
  }

  public String getColumnName(int columnIndex) {
    return delegate.getColumnName(columntMapping[columnIndex]);
  }

  public Class<?> getColumnClass(int columnIndex) {
    return delegate.getColumnClass(columntMapping[columnIndex]);
  }

  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return delegate.isCellEditable(rowIndex, columntMapping[columnIndex]);
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    return delegate.getValueAt(rowIndex, columntMapping[columnIndex]);
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    delegate.setValueAt(aValue, rowIndex, columntMapping[columnIndex]);
  }

  public void addTableModelListener(TableModelListener l) {
    delegate.addTableModelListener(l);
    dummy.addTableModelListener(l);
  }

  public void removeTableModelListener(TableModelListener l) {
    delegate.removeTableModelListener(l);
    dummy.removeTableModelListener(l);
  }

  public TableModel getDelegate() {
    return delegate;
  }

  public boolean isVisible(int i) {
    return columnsShow[i];
  }

}
