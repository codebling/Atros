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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import pl.otros.logview.gui.Icons;

public class GettingStartedAction extends AbstractAction {

  private JFrame parent;
  private JTextArea textArea;

  public GettingStartedAction(JFrame parent) {
    super();
    this.parent = parent;
    this.putValue(SMALL_ICON, Icons.HELP);
    this.putValue(NAME, "Getting started");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    textArea = new JTextArea();
    textArea.setBackground(new JLabel().getBackground());
    textArea.setEditable(false);
    textArea.setText("For getting started please go to page: http://code.google.com/p/otroslogviewer/wiki/GettingStarted?tm=6");
    JOptionPane.showMessageDialog(parent, textArea, "About", JOptionPane.INFORMATION_MESSAGE, new EmptyIcon());
  }

}
