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
package pl.otros.logview.gui.actions.table;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTable;

import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;

import pl.otros.logview.MarkerColors;
import pl.otros.logview.gui.LogDataTableModel;

public class MarkRowBySpaceKeyListener extends KeyAdapter implements ConfigurationListener {

  private JTable table;
  private LogDataTableModel dataTableModel;
  private MarkerColors markerColors = MarkerColors.Aqua;

  public MarkRowBySpaceKeyListener(JTable table, LogDataTableModel dataTableModel) {
    super();
    this.table = table;
    this.dataTableModel = dataTableModel;
  }

  @Override
  public void keyReleased(KeyEvent e) {
    char keyChar = e.getKeyChar();
    if (keyChar == ' ') {
      markUnmarkRow();
    }
  }

  private void markUnmarkRow() {
    int[] selected = table.getSelectedRows();
    if (selected.length == 0) {
      return;
    }
    boolean modeMark = !dataTableModel.isMarked(selected[0]);

    if (modeMark) {
      dataTableModel.markRows(markerColors, selected);
    } else {
      dataTableModel.unmarkRows(selected);
    }

  }

  @Override
  public void configurationChanged(ConfigurationEvent e) {
    if (e.getPropertyName().equals("gui.markColor")) {
      markerColors = (MarkerColors) e.getPropertyValue();
    }

  }

}
