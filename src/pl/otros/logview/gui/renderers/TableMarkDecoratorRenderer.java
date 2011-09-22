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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import pl.otros.logview.MarkerColors;
import pl.otros.logview.gui.MarkableTableModel;
import pl.otros.logview.gui.table.ColumnHideTableModelDecorator;

public class TableMarkDecoratorRenderer implements TableCellRenderer {

  private TableCellRenderer subjectRenderer;

  public TableMarkDecoratorRenderer(TableCellRenderer subjectRenderer) {
    super();
    this.subjectRenderer = subjectRenderer;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component c = subjectRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    boolean marked = false;
    TableModel model = table.getModel();
    if (model instanceof ColumnHideTableModelDecorator) {
      ColumnHideTableModelDecorator chm = (ColumnHideTableModelDecorator) table.getModel();
      model = chm.getDelegate();
    }

    MarkerColors markerColors = MarkerColors.Aqua;
    if (model instanceof MarkableTableModel) {
      MarkableTableModel markedModel = (MarkableTableModel) model;
      try {
        marked = markedModel.isMarked(table.convertRowIndexToModel(row));
        markerColors = markedModel.getMarkerColors(table.convertRowIndexToModel(row));
      } catch (NullPointerException e) {
        System.err.println("TableMarkDecoratorRenderer.getTableCellRendererComponent() null pointer catched at index " + row);
        e.printStackTrace();
      }
    }

    Color bg = null;
    Color fg = DefaultColors.FOREGROUND;
    if (!isSelected && !marked) {
      bg = DefaultColors.BACKGROUND;
    } else if (isSelected && !marked) {
      bg = DefaultColors.SELECTED;
    } else if (marked && !isSelected) {
      // bg = DefaultColors.MARKED;
      bg = markerColors.getBackground();
      fg = markerColors.getForeground();
    } else {
      bg = markerColors.getBackground().darker();
      fg = markerColors.getForeground();
      float[] bgHSB = Color.RGBtoHSB(bg.getRed(), bg.getGreen(), bg.getBlue(), null);
      if (bgHSB[2] > 0.4f) {
        bgHSB[2] -= 0.08;
      } else {
        bgHSB[2] += 0.04;
      }
      bgHSB[1] = 0.3f;
      bg = new Color(Color.HSBtoRGB(bgHSB[0], bgHSB[1], bgHSB[2]));
    }
    c.setBackground(bg);
    c.setForeground(fg);

    return c;
  }

}
