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
package pl.otros.logview.gui.actions.read;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;

import net.sf.vfsjfilechooser.VFSJFileChooser;
import net.sf.vfsjfilechooser.VFSJFileChooser.RETURN_TYPE;
import net.sf.vfsjfilechooser.VFSJFileChooser.SELECTION_MODE;
import net.sf.vfsjfilechooser.accessories.DefaultAccessoriesPanel;

import org.apache.commons.vfs2.FileObject;

import pl.otros.logview.gui.StatusObserver;

public abstract class ImportLogActionListener implements ActionListener {

  static final Logger LOGGER = Logger.getLogger(ImportLogActionListener.class.getName());
  private JTabbedPane tabbedPane;
  private static VFSJFileChooser chooser;
  private StatusObserver observer;
  String title;

  public ImportLogActionListener(String title, JTabbedPane tabbedPane, StatusObserver observer) {
    super();
    this.title = title;
    this.tabbedPane = tabbedPane;
    this.observer = observer;
  }

  public synchronized void actionPerformed(ActionEvent e) {
    prepareFileChooser();
    RETURN_TYPE result = chooser.showOpenDialog((Component) e.getSource());
    if (result != RETURN_TYPE.APPROVE) {
      return;
    }
    openSelectedFiles();
  }

  private void prepareFileChooser() {
    if (chooser == null) {
      createFileChooser();
    }
    chooser.setDialogTitle(title);
  }

  private void openSelectedFiles() {
    final FileObject[] files = chooser.getSelectedFiles();
    for (int i = 0; i < files.length; i++) {
      final FileObject file = files[i];
      new LogFileInNewTabOpener(getLogImporterProvider(), tabbedPane, observer).open(file);
    }
  }

  private void createFileChooser() {
    chooser = new VFSJFileChooser();
    chooser.setAccessory(new DefaultAccessoriesPanel(chooser));
    chooser.setFileHidingEnabled(false);
    chooser.setFileSelectionMode(SELECTION_MODE.FILES_ONLY);
    chooser.setMultiSelectionEnabled(true);
  }

  protected abstract LogImporterProvider getLogImporterProvider();

}
