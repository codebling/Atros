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

import pl.otros.logview.gui.util.PersistentConfirmationDialog;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

public class TabHeader extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static final Icon NORMAL_ICON = Icons.TAB_HEADER_NORMAL;
  private static final Icon HOVER_ICON = Icons.TAB_HEADER_HOVER;
  private JTabbedPane pane;

  private JButton iconButton;
  private JLabel label;

  public TabHeader(JTabbedPane pane, String name, String tooltip) {
    this(pane, name, null, tooltip);
  }

  public TabHeader(JTabbedPane pane, String name, Icon icon, String tooltip) {
    super(new FlowLayout(FlowLayout.LEFT, 3, 0));

    this.pane = pane;
    iconButton = new JButton(NORMAL_ICON);
    iconButton.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseEntered(MouseEvent e) {
        iconButton.setIcon(HOVER_ICON);
      }

      @Override
      public void mouseExited(MouseEvent e) {
        iconButton.setIcon(NORMAL_ICON);
      }

    });
    iconButton.setToolTipText("Close tab");
    iconButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        closeTab();
      }
    });
    label = new JLabel(name, icon, SwingConstants.LEFT);
    label.setToolTipText(tooltip);
    label.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        TabHeader.this.pane.setSelectedIndex(TabHeader.this.pane.indexOfTabComponent(TabHeader.this));
      }

    });

    Dimension d = new Dimension(16, 16);
    iconButton.setPreferredSize(d);
    iconButton.setMinimumSize(d);
    iconButton.setMaximumSize(d);
    iconButton.setBorderPainted(false);
    add(label);
    add(iconButton);
    setOpaque(false);
    label.setOpaque(false);
    iconButton.setOpaque(false);
  }

  protected void closeTab() {
    int tabNumber = pane.indexOfTabComponent(TabHeader.this);
    if (tabNumber != -1) {
      if (PersistentConfirmationDialog.showConfirmDialog(pane, "Do you really want to close \"" + label.getText() + "\"?", "closetab")) {
        pane.remove(tabNumber);
      }
    }

  }

}
