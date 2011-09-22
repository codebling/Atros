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
package pl.otros.logview.gui.renderers;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class DateRenderer implements TableCellRenderer {

  private JLabel label = new JLabel();
  private DateFormat dateFormatter = null;

  public DateRenderer() {
    this("HH:mm:ss .SSS");
  }

  public DateRenderer(String dateFormat) {
    label.setOpaque(true);
    dateFormatter = new SimpleDateFormat(dateFormat);
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    if (value instanceof Date) {
      Date date = (Date) value;
      label.setText(dateFormatter.format(date));
    } else {
      label.setText("");
    }

    return label;
  }

  public DateFormat getDateFormatter() {
    return dateFormatter;
  }

  public void setDateFormatter(DateFormat dateFormatter) {
    this.dateFormatter = dateFormatter;
  }

}
