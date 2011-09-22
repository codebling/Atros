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
import java.io.FileInputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import pl.otros.logview.LogInvestiagionPersitanceUtil;
import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.LogViewPanelWrapper;
import pl.otros.logview.gui.StatusObserver;
import pl.otros.logview.gui.TabHeader;
import pl.otros.logview.gui.LogDataTableModel.Memento;

public class OpenLogInvestigationAction extends AbstractAction {

  private StatusObserver observer;
  private JTabbedPane jTabbedPane;

  public OpenLogInvestigationAction(JTabbedPane jTabbedPane, StatusObserver observer) {
    putValue(Action.NAME, "Open log investigation");
    putValue(Action.SMALL_ICON, Icons.IMPORT);
    this.observer = observer;
    this.jTabbedPane = jTabbedPane;
  }

  @Override
  public void actionPerformed(ActionEvent arg0) {
    JFileChooser chooser = LogInvestiagionPersitanceUtil.getFileChooser();
    int result = chooser.showOpenDialog((Component) arg0.getSource());
    if (result != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File f = chooser.getSelectedFile();
    try {
      Memento memento = LogInvestiagionPersitanceUtil.loadMemento(new FileInputStream(f));
      LogViewPanelWrapper panelWrapper = new LogViewPanelWrapper(memento.getName(), observer);
      String tabName = panelWrapper.getName();
      int tabNumber = jTabbedPane.getTabCount();
      jTabbedPane.addTab(null, panelWrapper);
      jTabbedPane.setTabComponentAt(tabNumber, new TabHeader(jTabbedPane, tabName,Icons.FOLDER_OPEN, tabName));
      panelWrapper.getDataTableModel().restoreFromMemento(memento);
      panelWrapper.switchToContentView();
      observer.updateStatus("Log \"" + panelWrapper.getName() + "\" loaded.");
    } catch (Exception e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog((Component) arg0.getSource(), "Problem with loading: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      observer.updateStatus("Log not loaded.", StatusObserver.LEVEL_ERROR);
    }
  }

}
