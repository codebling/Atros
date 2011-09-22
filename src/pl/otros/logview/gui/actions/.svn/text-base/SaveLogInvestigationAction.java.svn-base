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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import pl.otros.logview.LogInvestiagionPersitanceUtil;
import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.LogDataTableModel.Memento;
import pl.otros.logview.gui.LogViewPanelWrapper;
import pl.otros.logview.gui.StatusObserver;

public class SaveLogInvestigationAction extends AbstractAction {

  private StatusObserver observer;
  private JTabbedPane jTabbedPane;

  public SaveLogInvestigationAction(JTabbedPane jTabbedPane, StatusObserver observer) {
    putValue(Action.NAME, "Save log investigation");
    putValue(Action.SMALL_ICON, Icons.EXPORT);
    this.jTabbedPane = jTabbedPane;
    this.observer = observer;
  }

  @Override
  public void actionPerformed(ActionEvent arg0) {
    // JInternalFrame frame = FrameManger.getInstance().getDesktopPane().getSelectedFrame();
    JFileChooser chooser = LogInvestiagionPersitanceUtil.getFileChooser();
    int result = chooser.showSaveDialog((Component) arg0.getSource());
    if (result != JFileChooser.APPROVE_OPTION) {
      return;
    }
    LogViewPanelWrapper lvFrame = (LogViewPanelWrapper) jTabbedPane.getSelectedComponent();
    Memento m = lvFrame.getDataTableModel().saveToMemento();
    m.setName(lvFrame.getName());
    File f = chooser.getSelectedFile();
    if (!f.getName().endsWith("zip.olv")) {
      f = new File(f.getAbsolutePath() + ".zip.olv");
    }
    try {
      LogInvestiagionPersitanceUtil.saveMemento(m, new FileOutputStream(f));
      observer.updateStatus("Log \"" + lvFrame.getName() + "\" saved.");
    } catch (IOException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog((Component) arg0.getSource(), "Problem with saving: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      observer.updateStatus("Log \"" + lvFrame.getName() + "\" not saved.", StatusObserver.LEVEL_ERROR);
    }

  }

}
