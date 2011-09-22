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
package pl.otros.logview.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

public class PopupListener implements MouseListener, KeyListener {

  private JPopupMenu menu;

  public PopupListener(JPopupMenu menu) {
    this.menu = menu;
  }

  public void mousePressed(MouseEvent e) {
    showPopup(e);
  }

  public void mouseReleased(MouseEvent e) {
    showPopup(e);
  }

  private void showPopup(MouseEvent e) {
    if (e.isPopupTrigger()) {
      if (e.getComponent() instanceof JTable) {
        JTable table = (JTable) e.getComponent();
        if (table.getSelectedRowCount() <= 0) {
          int row = table.rowAtPoint(e.getPoint());
          table.getSelectionModel().setSelectionInterval(row, row);
        }
      }
      menu.show(e.getComponent(), e.getX(), e.getY());
    }
  }

  public void mouseClicked(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void keyPressed(KeyEvent e) {
    Point p = new Point(e.getComponent().getLocation());

    if (e.getKeyCode() == 525) {
      if (e.getComponent() instanceof JTable) {
        JTable table = (JTable) e.getComponent();
        int selectedRow = table.getSelectedRow();
        Rectangle cellRect = table.getCellRect(selectedRow, 0, true);
        p.setLocation(cellRect.getCenterX(), cellRect.getCenterY());
      }
      menu.show(e.getComponent(), (int) p.getX(), (int) p.getY());
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {

  }

  @Override
  public void keyTyped(KeyEvent e) {

  }
}
