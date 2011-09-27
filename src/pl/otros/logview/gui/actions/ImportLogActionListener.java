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
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import net.sf.vfsjfilechooser.VFSJFileChooser;
import net.sf.vfsjfilechooser.VFSJFileChooser.RETURN_TYPE;
import net.sf.vfsjfilechooser.VFSJFileChooser.SELECTION_MODE;
import net.sf.vfsjfilechooser.accessories.DefaultAccessoriesPanel;

import org.apache.commons.vfs2.FileObject;

import pl.otros.logview.Stoppable;
import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.LogDataTableModel;
import pl.otros.logview.gui.LogImportStats;
import pl.otros.logview.gui.LogViewPanelWrapper;
import pl.otros.logview.gui.StatusObserver;
import pl.otros.logview.gui.TabHeader;
import pl.otros.logview.importer.LogImporter;
import pl.otros.logview.io.LoadingInfo;
import pl.otros.logview.io.ObservableInputStreamImpl;
import pl.otros.logview.io.Utils;
import pl.otros.logview.parser.ParsingContext;
import pl.otros.logview.parser.TableColumnNameSelfDescribable;
import pl.otros.logview.reader.ProxyLogDataCollector;

public class ImportLogActionListener implements ActionListener {

  private static final Logger LOGGER = Logger.getLogger(ImportLogActionListener.class.getName());
  private JTabbedPane tabbedPane;
  private static VFSJFileChooser chooser;
  private LogImporter importer;
  private StatusObserver observer;
  private LogImportStats importStats;

  public ImportLogActionListener(JTabbedPane tabbedPane, LogImporter importer, StatusObserver observer) {
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
      try {

        final LoadingInfo openFileObject = Utils.openFileObject(file);
        final LogViewPanelWrapper panel = new LogViewPanelWrapper(files[i].getName().getBaseName(), observer, openFileObject.getObserableInputStreamImpl());
        String tabName = Utils.getFileObjectShortName(file);
        int tabNumber = tabbedPane.getTabCount();
        tabbedPane.addTab(null, panel);
        tabbedPane.setTabComponentAt(tabNumber, new TabHeader(tabbedPane, tabName, Icons.FOLDER_OPEN, file.getName().getFriendlyURI()));
        if (importer instanceof TableColumnNameSelfDescribable) {
          TableColumnNameSelfDescribable describable = (TableColumnNameSelfDescribable) importer;
          panel.getLogViewPanel().showOnlyThisColumns(describable.getTableColumnsToUse());
        }
        Runnable r = new Runnable() {

          @Override
          public void run() {
            ProgressWatcher watcher = null;
            final ProxyLogDataCollector collector = new ProxyLogDataCollector();
            ParsingContext parsingContext = new ParsingContext(file.getName().getFriendlyURI());
            importStats = new LogImportStats(file.getName().getFriendlyURI());
            panel.getStatsTable().setModel(importStats);
            watcher = new ProgressWatcher(openFileObject.getObserableInputStreamImpl(), panel, file);
            Thread t = new Thread(watcher, "Log loader: " + file.getName().toString());
            t.setDaemon(true);
            t.start();
            panel.addHierarchyListener(new ReadingStopperForRemove(openFileObject.getObserableInputStreamImpl()));
            importer.importLogs(openFileObject.getContentInputStream(), collector, parsingContext);
            final LogDataTableModel dataTableModel = panel.getDataTableModel();
            LOGGER.info("File " + file.getName().getFriendlyURI() + " loaded");
            dataTableModel.add(collector.getLogData());
            SwingUtilities.invokeLater(new Runnable() {

              @Override
              public void run() {
                panel.switchToContentView();
              }
            });
            watcher.updateFinish("Loaded");
            Utils.closeQuietly(openFileObject.getFileObject());
          }
        };
        Thread t = new Thread(r);
        t.start();
      } catch (Exception e1) {
        LOGGER.severe("Error loading log (" + file.getName().getFriendlyURI() + "): " + e1.getMessage());
        JOptionPane.showMessageDialog(null, "Error loading log: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        continue;
      }

    }
  }

  private void initFileChooser() {
    chooser = new VFSJFileChooser();
    chooser.setDialogTitle("Open " + importer.getName() + " log");
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

  private class ProgressWatcher implements Runnable {

    private ObservableInputStreamImpl in;
    private JProgressBar progressBar;
    private boolean refreshProgress = true;
    private LogViewPanelWrapper frame;
    private FileObject fileName;
    private NumberFormat npf = NumberFormat.getPercentInstance();
    private NumberFormat nbf = NumberFormat.getIntegerInstance();

    public ProgressWatcher(ObservableInputStreamImpl in, LogViewPanelWrapper frame, FileObject fileName) {
      super();
      this.in = in;
      this.frame = frame;
      this.fileName = fileName;
      this.progressBar = frame.getLoadingProgressBar();
      npf.setMaximumFractionDigits(0);
      nbf.setGroupingUsed(true);
    }

    @Override
    public void run() {
      while (refreshProgress) {
        try {
          long max = fileName.getContent().getSize();
          if (max <= 0) {
            updateNotDetermined("Loading");
          } else {
            long current = in.getCurrentRead();
            float percent = (float) current / max;
            long currentInKb = current / 1024;
            long maxInKb = max / 1024;
            importStats.updateStats(System.currentTimeMillis(), currentInKb, maxInKb);
            String message = "Loading " + fileName.getName().getBaseName() + " ... " + npf.format(percent) + "[" + nbf.format(currentInKb) + "kb of "
                + nbf.format(maxInKb) + "kb]";
            updateProgress(message, (int) current, 0, (int) max);

          }
          Thread.sleep(500);
        } catch (InterruptedException e) {
          // waiting interrupted, nothing serious.

        } catch (IOException e) {
          // e.printStackTrace();
          // stream closed - all loaded
          break;
        }
      }
      updateFinish("Loaded");

    }

    public void updateNotDetermined(final String message) {
      Runnable r = new Runnable() {

        @Override
        public void run() {
          progressBar.setIndeterminate(true);
          progressBar.setString(message);
        }

      };

      SwingUtilities.invokeLater(r);
    }

    public void updateProgress(final String message, final int current, final int min, final int max) {
      Runnable r = new Runnable() {

        @Override
        public void run() {
          progressBar.setIndeterminate(false);
          progressBar.setMaximum(max);
          progressBar.setMinimum(min);
          progressBar.setValue(current);
          progressBar.setString(message);
          // refreshProgress = false;

        }

      };
      try {
        SwingUtilities.invokeAndWait(r);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }

    }

    public void updateFinish(final String message) {
      Runnable r = new Runnable() {

        @Override
        public void run() {
          progressBar.setIndeterminate(false);

          progressBar.setMaximum(1);
          progressBar.setMinimum(0);
          progressBar.setValue(1);
          progressBar.setString(message);
          frame.switchToContentView();
          refreshProgress = false;
        }

      };
      SwingUtilities.invokeLater(r);

    }

  }

  private static class ReadingStopperForRemove implements HierarchyListener {

    private static final Logger LOGGER = Logger.getLogger(ReadingStopperForRemove.class.getName());

    private SoftReference<Stoppable> reference;

    public ReadingStopperForRemove(Stoppable stoppable) {
      super();
      reference = new SoftReference<Stoppable>(stoppable);
    }

    @Override
    public void hierarchyChanged(HierarchyEvent e) {
      if (e.getChangeFlags() == 1 && e.getChanged().getParent() == null) {
        Stoppable stoppable = reference.get();
        LOGGER.fine("Tab removed, stopping thread if reference is != null (actual: " + stoppable + ")");
        if (stoppable != null) {
          stoppable.stop();
        }
      }

    }

  }
}
