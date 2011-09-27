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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.TransferHandler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

import pl.otros.logview.gui.StatusObserver;
import pl.otros.logview.importer.LogImporter;

/**
 * Responsible for global drag-and-drop operations.
 * 
 * Currently supports dropping possibly multiple log files that are then opened by autodetection.
 * 
 * @author murat
 */
public class DragAndDropFilesHandler extends TransferHandler {

  private static final long serialVersionUID = 3830464109280595888L;

  static final Logger LOGGER = Logger.getLogger(DragAndDropFilesHandler.class.getName());

  private JTabbedPane tabbedPane;
  private StatusObserver observer;
  private Collection<LogImporter> importers;

  public DragAndDropFilesHandler(Collection<LogImporter> importers, JTabbedPane tabbedPane, StatusObserver observer) {
    this.tabbedPane = tabbedPane;
    this.observer = observer;
    this.importers = importers;
  }

  @Override
  public boolean canImport(TransferSupport support) {
    if (isText(support) || isListOfFiles(support)) {
      return true;
    }
    return super.canImport(support);
  }

  private boolean isListOfFiles(TransferSupport support) {
    return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
  }

  private boolean isText(TransferSupport support) {
    return DataFlavor.selectBestTextFlavor(support.getDataFlavors()) != null;
  }

  @Override
  public boolean importData(TransferSupport support) {
    if (isText(support)) {
      return importString(support);
    }

    if (isListOfFiles(support)) {
      return importListOfFiles(support);
    }

    return super.importData(support);
  }

  private boolean importListOfFiles(TransferSupport support) {
    try {
      return tryToImportListOfFiles(support);
    } catch (RuntimeException e) {
      LOGGER.log(Level.SEVERE, "Problem dropping files on the GUI: " + e.getMessage(), e);
      JOptionPane.showMessageDialog(null, "Problem during drag-and-drop of files: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }

  private boolean tryToImportListOfFiles(TransferSupport support) {
    for (File file : getListOfFiles(support)) {
      openLogFile(getFileObjectForLocalFile(file));
    }
    return true;
  }

  @SuppressWarnings("unchecked")
  private List<File> getListOfFiles(TransferSupport support) {
    try {
      return (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
    } catch (UnsupportedFlavorException e) {
      throw new RuntimeException("The dropped data is not supported. The unsupported DataFlavors are " + StringUtils.join(support.getDataFlavors(), ","), e);
    } catch (IOException e) {
      throw new RuntimeException("Cannot read the dropped data.", e);
    }
  }

  private boolean importString(TransferSupport support) {
    try {
      tryToImportString(support);
      return true;
    } catch (RuntimeException e) {
      LOGGER.log(Level.SEVERE, "Problem dropping something on the GUI: " + e.getMessage(), e);
      JOptionPane.showMessageDialog(null, "Problem during drag-and-drop of strings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }

  private void tryToImportString(TransferSupport support) {
    for (FileObject file : getFileObjects(support)) {
      openLogFile(file);
    }
  }

  private List<FileObject> getFileObjects(TransferSupport support) {
    List<FileObject> files = new ArrayList<FileObject>();
    for (String uriString : getFileUris(support)) {
      files.add(getFileObjectForLocalFile(getFileForUriString(uriString)));
    }
    return files;
  }

  public List<String> getFileUris(TransferSupport support) {
    BufferedReader reader = null;
    List<String> files = new ArrayList<String>();
    try {
      reader = new BufferedReader(DataFlavor.selectBestTextFlavor(support.getDataFlavors()).getReaderForText(support.getTransferable()));
      String uri = null;
      while ((uri = reader.readLine()) != null) {
        files.add(uri);
      }
    } catch (UnsupportedFlavorException e) {
      throw new RuntimeException("The dropped data is not supported. The unsupported DataFlavors are " + StringUtils.join(support.getDataFlavors(), ","), e);
    } catch (IOException e) {
      throw new RuntimeException("Cannot read the dropped data.", e);
    }
    return files;
  }

  private FileObject getFileObjectForLocalFile(File file) {
    try {
      return VFS.getManager().toFileObject(file);
    } catch (FileSystemException e) {
      throw new RuntimeException("Cannot open file: " + file, e);
    }
  }

  private File getFileForUriString(String uriString) {
    return new File(URI.create(uriString));
  }

  private void openLogFile(FileObject file) {
    new LogFileInNewTabOpener(new AutoDetectingImporterProvider(importers), tabbedPane, observer).open(file);
  }

}
