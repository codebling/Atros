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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.table.TableModel;

public class ColumnHideMenu {

  private ColumnHideTableModelDecorator columnHideTableModel;
  private TableModel hidedTableModel;
  private JMenu menu;

  public static void generateMenu(JMenu menu, ColumnHideTableModelDecorator columnHideTableModel) {
    new ColumnHideMenu(menu, columnHideTableModel);
  }

  private ColumnHideMenu(JMenu menu, ColumnHideTableModelDecorator columnHideTableModel) {
    super();
    this.menu = menu;
    this.columnHideTableModel = columnHideTableModel;
    hidedTableModel = columnHideTableModel.getDelegate();
    generateMenu();
  }

  private void generateMenu() {
    int columntCount = hidedTableModel.getColumnCount();
    for (int i = 0; i < columntCount; i++) {
      JCheckBoxMenuItem component = null;
      component = new JCheckBoxMenuItem();
      component.addActionListener(new ShowHideAction(i, component));
      menu.add(component);
      String columnName = hidedTableModel.getColumnName(i);
      component.setText(columnName);
      component.setSelected(columnHideTableModel.isVisible(i));
    }
  }

  private class ShowHideAction implements ActionListener {

    private int colIndex;
    private AbstractButton checkBox;

    public ShowHideAction(int colIndex, AbstractButton checkBox) {
      super();
      this.colIndex = colIndex;
      this.checkBox = checkBox;
    }

    public void actionPerformed(ActionEvent e) {
      columnHideTableModel.setColumnVisible(colIndex, checkBox.isSelected());
    }
  }
}
