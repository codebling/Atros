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

import pl.otros.logview.gui.util.PersistentConfirmationDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class ExitAction extends WindowAdapter implements ActionListener {

  private JFrame frame;

  public ExitAction(JFrame frame) {
    this.frame = frame;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    askAndExit();
  }

  @Override
  public void windowClosing(WindowEvent e) {
    askAndExit();
  }

  protected void askAndExit() {
    if (PersistentConfirmationDialog.showConfirmDialog(
        frame, "Exit OtrosLogViewer and go back to parsing logs with 'grep'?", "exit")) {
      frame.setVisible(false);
      frame.dispose();
      System.exit(0);
    }
  }

}
