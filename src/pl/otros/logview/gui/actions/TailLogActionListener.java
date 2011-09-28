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
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import net.sf.vfsjfilechooser.VFSJFileChooser;
import net.sf.vfsjfilechooser.VFSJFileChooser.RETURN_TYPE;
import net.sf.vfsjfilechooser.VFSJFileChooser.SELECTION_MODE;
import net.sf.vfsjfilechooser.accessories.DefaultAccessoriesPanel;

import org.apache.commons.vfs2.FileObject;

import pl.otros.logview.BufferingLogDataCollectorProxy;
import pl.otros.logview.Stoppable;
import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.LogDataTableModel;
import pl.otros.logview.gui.LogImportStats;
import pl.otros.logview.gui.LogViewPanelWrapper;
import pl.otros.logview.gui.StatusObserver;
import pl.otros.logview.gui.TabHeader;
import pl.otros.logview.importer.LogImporter;
import pl.otros.logview.io.LoadingInfo;
import pl.otros.logview.io.Utils;
import pl.otros.logview.parser.ParsingContext;
import pl.otros.logview.parser.TableColumnNameSelfDescribable;

public class TailLogActionListener implements ActionListener {

  private static final Logger LOGGER = Logger.getLogger(TailLogActionListener.class.getName());
  private JTabbedPane tabbedPane;
  private static VFSJFileChooser chooser;
  private LogImporter importer;
  private StatusObserver observer;
  private LogImportStats importStats;

  public TailLogActionListener(JTabbedPane tabbedPane, LogImporter importer, StatusObserver observer) {
    super();
    this.tabbedPane = tabbedPane;
    this.importer = importer;
    this.observer = observer;
  }

  public void actionPerformed(ActionEvent e) {
    if (chooser == null) {
      initFileChooser();
    }
    RETURN_TYPE result = chooser.showOpenDialog((Component) e.getSource());
    if (result != RETURN_TYPE.APPROVE) {
      return;
    }
    final FileObject[] files = chooser.getSelectedFiles();
    for (int i = 0; i < files.length; i++) {
      final FileObject file = files[i];

      final LoadingInfo loadingInfo;
      try {
        loadingInfo = Utils.openFileObject(file, false);
      } catch (Exception e2) {
        LOGGER.severe("Cannot open tailing input stream for " + file.getName().getFriendlyURI() + ", " + e2.getMessage());
        JOptionPane.showMessageDialog(null, "Cannot open tailing input stream for: " + file.getName().getFriendlyURI() + ", " + e2.getMessage(), "Error",
            JOptionPane.ERROR_MESSAGE);
        continue;
      }

      // final TailInputStreamDecorator in = new TailInputStreamDecorator(vfsIn);
      final LogViewPanelWrapper panel = new LogViewPanelWrapper(files[i].getName().getBaseName(), observer, loadingInfo.getObserableInputStreamImpl());
      if (importer instanceof TableColumnNameSelfDescribable) {
        TableColumnNameSelfDescribable describable = (TableColumnNameSelfDescribable) importer;
        panel.getLogViewPanel().showOnlyThisColumns(describable.getTableColumnsToUse());
      }
      panel.goToLiveMode();

      String tabName = Utils.getFileObjectShortName(file);
      int tabNumber = tabbedPane.getTabCount();
      tabbedPane.addTab(null, panel);
      tabbedPane.setTabComponentAt(tabNumber, new TabHeader(tabbedPane, tabName, Icons.ARROW_REPEAT, loadingInfo.getFriendlyUrl()));

      Runnable r = new Runnable() {

        @Override
        public void run() {
          ParsingContext parsingContext = new ParsingContext(file.getName().getFriendlyURI());
          LogDataTableModel dataTableModel = panel.getDataTableModel();
          importStats = new LogImportStats(file.getName().getFriendlyURI());
          panel.getStatsTable().setModel(importStats);
          BufferingLogDataCollectorProxy bufferingLogDataCollectorProxy = new BufferingLogDataCollectorProxy(dataTableModel, 2000, panel.getConfiguration());
          panel.addHierarchyListener(new ReadingStopperForRemove(loadingInfo.getObserableInputStreamImpl(), bufferingLogDataCollectorProxy,
              new ParsingContextStopperForClosingTab(parsingContext)));
          while (parsingContext.isParsingInProgress()) {
            try {
              importer.importLogs(loadingInfo.getContentInputStream(), bufferingLogDataCollectorProxy, parsingContext);
              Thread.sleep(1000);
              // TODO check how behave JUL XML log importer!
              Utils.reloadFileObject(loadingInfo);
            } catch (Exception e) {
              LOGGER.warning("Exceoption in tailing loop: " + e.getMessage());
            }
          }
          LOGGER.info(String.format("Loading of files %s is finished", loadingInfo.getFriendlyUrl()));
          parsingContext.setParsingInProgress(false);
          LOGGER.info("File " + loadingInfo.getFriendlyUrl() + " loaded");
          observer.updateStatus("File " + loadingInfo.getFriendlyUrl() + " stop tailing");
          Utils.closeQuietly(file);
        }

      };
      Thread t = new Thread(r, "Log reader-" + file.getName().getFriendlyURI());
      t.setDaemon(true);
      t.start();
      SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
          panel.switchToContentView();
        }
      });

    }
  }

  private void initFileChooser() {
    chooser = new VFSJFileChooser();
    chooser.setDialogTitle("Tail " + importer.getName() + " log");
    chooser.setAccessory(new DefaultAccessoriesPanel(chooser));
    chooser.setFileHidingEnabled(false);
    chooser.setFileSelectionMode(SELECTION_MODE.FILES_ONLY);
    chooser.setMultiSelectionEnabled(true);
  }

  public LogImporter getImporter() {
    return importer;
  }

  public void setImporter(LogImporter importer) {
    this.importer = importer;
  }

  private static class ReadingStopperForRemove implements HierarchyListener {

    private static final Logger LOGGER = Logger.getLogger(ReadingStopperForRemove.class.getName());

    private List<SoftReference<Stoppable>> referencesList;

    public ReadingStopperForRemove(Stoppable... stoppables) {
      super();
      referencesList = new ArrayList<SoftReference<Stoppable>>();
      for (Stoppable stoppable : stoppables) {
        referencesList.add(new SoftReference<Stoppable>(stoppable));
      }
    }

    @Override
    public void hierarchyChanged(HierarchyEvent e) {
      if (e.getChangeFlags() == 1 && e.getChanged().getParent() == null) {
        // if (e.getChangeFlags() == 6) {
        for (SoftReference<Stoppable> ref : referencesList) {
          Stoppable stoppable = ref.get();
          LOGGER.fine("Tab removed, stopping thread if reference is != null (actual: " + stoppable + ")");
          if (stoppable != null) {
            stoppable.stop();
          }
        }
      }

    }
  }

  private static class ParsingContextStopperForClosingTab implements Stoppable {

    private static final Logger LOGGER = Logger.getLogger(ParsingContextStopperForClosingTab.class.getName());
    private ParsingContext context;

    public ParsingContextStopperForClosingTab(ParsingContext context) {
      super();
      this.context = context;
    }

    @Override
    public void stop() {
      LOGGER.info("Closing tab, setting parsingInProgress to false.");
      context.setParsingInProgress(false);
    }

  }

}
