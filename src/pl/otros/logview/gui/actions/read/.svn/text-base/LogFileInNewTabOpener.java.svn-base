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

import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import org.apache.commons.vfs.FileObject;

import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.LogViewPanelWrapper;
import pl.otros.logview.gui.StatusObserver;
import pl.otros.logview.gui.TabHeader;
import pl.otros.logview.importer.LogImporter;
import pl.otros.logview.io.LoadingInfo;
import pl.otros.logview.io.Utils;
import pl.otros.logview.parser.TableColumnNameSelfDescribable;

public class LogFileInNewTabOpener {

  static final Logger LOGGER = Logger.getLogger(LogFileInNewTabOpener.class.getName());
  private JTabbedPane tabbedPane;
  private StatusObserver observer;
  private LogImporterProvider importerProvider;

  public LogFileInNewTabOpener(LogImporterProvider importerProvider, JTabbedPane tabbedPane, StatusObserver observer) {
    this.tabbedPane = tabbedPane;
    this.observer = observer;
    this.importerProvider = importerProvider;
  }

  public void open(FileObject file) {
    try {
      final LoadingInfo openFileObject = Utils.openFileObject(file);

      LogImporter importer = chooseImporter(openFileObject);
      if (isInvalid(importer)) {
        handleInvalidImporter(file);
        return;
      }

      final LogViewPanelWrapper panel = createPanelForLog(file, openFileObject, importer);
      addPanelAsTab(file, panel);
      startThreadToImportLogDataFromFile(file, openFileObject, importer, panel);
    } catch (Exception e1) {
      LOGGER.severe("Error loading log (" + file.getName().getFriendlyURI() + "): " + e1.getMessage());
      JOptionPane.showMessageDialog(null, "Error loading log: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void startThreadToImportLogDataFromFile(FileObject file, final LoadingInfo openFileObject, LogImporter importer, final LogViewPanelWrapper panel) {
    new Thread(new ImportLogRunnable(openFileObject, panel, file, importer)).start();
  }

  private void addPanelAsTab(FileObject file, final LogViewPanelWrapper panel) {
    tabbedPane.addTab(null, panel);
    tabbedPane.setTabComponentAt(getIndexOfNewTab(), new TabHeader(tabbedPane, getTabName(file), Icons.FOLDER_OPEN, file.getName().getFriendlyURI()));
  }

  private LogViewPanelWrapper createPanelForLog(FileObject file, final LoadingInfo openFileObject, LogImporter importer) {
    final LogViewPanelWrapper panel = new LogViewPanelWrapper(file.getName().getBaseName(), observer, openFileObject.getObserableInputStreamImpl());
    if (importer instanceof TableColumnNameSelfDescribable) {
      TableColumnNameSelfDescribable describable = (TableColumnNameSelfDescribable) importer;
      panel.getLogViewPanel().showOnlyThisColumns(describable.getTableColumnsToUse());
    }
    return panel;
  }

  private int getIndexOfNewTab() {
    return tabbedPane.getTabCount() - 1;
  }

  private String getTabName(FileObject file) {
    return Utils.getFileObjectShortName(file);
  }

  private LogImporter chooseImporter(LoadingInfo openFileObject) {
    return importerProvider.getLogImporter(openFileObject);
  }

  private void handleInvalidImporter(final FileObject file) {
    LOGGER.severe("Error loading log (" + file.getName().getFriendlyURI() + "): no suitable log parser found");

    String errorMessage = "Error loading log file: no suitable log parser found for " + file.getName().getFriendlyURI() + "\n"
        + "Go http://code.google.com/p/otroslogviewer/wiki/Log4jPatternLayout to check how to parse log4j custom pattern.";
    JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private boolean isInvalid(LogImporter importer) {
    return importer == null;
  }

}
