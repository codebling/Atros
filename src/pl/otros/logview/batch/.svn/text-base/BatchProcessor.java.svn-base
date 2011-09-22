/*******************************************************************************
 * Copyright 2011 krzyh
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
package pl.otros.logview.batch;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;

import pl.otros.logview.gui.actions.read.AutoDetectingImporterProvider;
import pl.otros.logview.importer.InitializationException;
import pl.otros.logview.importer.LogImporter;
import pl.otros.logview.io.LoadingInfo;
import pl.otros.logview.io.Utils;
import pl.otros.logview.loader.LvDynamicLoader;
import pl.otros.logview.parser.ParsingContext;
import pl.otros.logview.pluginable.AllPluginables;

public class BatchProcessor {

  private List<String> files;
  private BatchProcessingContext batchProcessingContext;
  private boolean verbose = false;
  private LogDataParsedListener logDataParsedListener;

  public BatchProcessor() throws IOException, InitializationException {
    files = new ArrayList<String>();
    batchProcessingContext = new BatchProcessingContext();
    LvDynamicLoader.getInstance().loadAll();
  }

  public void process() {
    StreamProcessingLogDataCollector logDataCollector = new StreamProcessingLogDataCollector(logDataParsedListener, batchProcessingContext);
    FileSystemManager manager = null;
    try {
      manager = VFS.getManager();
    } catch (FileSystemException e1) {
      return;
    }
    int i = 0;
    ArrayList<FileObject> fileObjects = new ArrayList<FileObject>();
    for (String file : files) {
      i++;
      FileObject resolveFile = null;
      try {
        print("Resolving file %s [%d of %d]", file, i, files.size());
        try {
          resolveFile = manager.resolveFile(file);
        } catch (Exception e) {
          file = new File(file).getAbsolutePath();
          resolveFile = manager.resolveFile(file);
        }
        if (resolveFile != null) {
          fileObjects.add(resolveFile);
        }
      } catch (Exception e) {
        System.err.printf("Error resolving %s: %s", file, e.getMessage());
      }
    }
    batchProcessingContext.setAllFiles(fileObjects);

    if (logDataParsedListener instanceof BatchProcessingListener) {
      ((BatchProcessingListener) logDataParsedListener).processingStarted(batchProcessingContext);
    }

    AutoDetectingImporterProvider importerProvider = new AutoDetectingImporterProvider(AllPluginables.getInstance().getLogImportersContainer().getElements());
    i = 0;
    for (FileObject resolveFile : fileObjects) {
      i++;

      String fileName = resolveFile.getName().getBaseName();
      try {
        print("Opening file %s [%d of %d]", fileName, i, fileObjects.size());
        batchProcessingContext.setCurrentFile(resolveFile);
        if (logDataParsedListener instanceof SingleFileBatchProcessingListener) {
          ((SingleFileBatchProcessingListener) logDataParsedListener).processingFileStarted(batchProcessingContext);
        }
        LoadingInfo openFileObject = Utils.openFileObject(resolveFile);
        LogImporter logImporter = importerProvider.getLogImporter(openFileObject);
        if (logImporter == null) {
          System.err.println("Can't find suitable log importer for " + fileName);
          continue;
        }
        // TODO for HTTP, Attempted read on closed stream. issue related to checking if file is gziped
        Utils.closeQuietly(resolveFile);
        String fileUrl = resolveFile.getURL().toString();
        resolveFile = manager.resolveFile(fileUrl);
        openFileObject = Utils.openFileObject(resolveFile);

        batchProcessingContext.setCurrentFile(resolveFile);
        ParsingContext context = new ParsingContext();
        logImporter.importLogs(openFileObject.getContentInputStream(), logDataCollector, context);

        if (logDataParsedListener instanceof SingleFileBatchProcessingListener) {
          ((SingleFileBatchProcessingListener) logDataParsedListener).processingFileFinished(batchProcessingContext);
        }
        print("File %s processed  [%d of %d]", fileName, i, files.size());
      } catch (Exception e) {
        print("Error processing file %s: %s", fileName, e.getMessage());
        System.err.println("Can't resolve file " + fileName + ": " + e.getMessage());
        continue;
      }

    }
    if (logDataParsedListener instanceof BatchProcessingListener) {
      ((BatchProcessingListener) logDataParsedListener).processingFinished(batchProcessingContext);
    }
  }

  public void print(String message) {
    if (verbose) {
      System.out.println(message);
    }
  }

  public void print(String message, Object... args) {
    if (verbose) {
      System.out.println(String.format(message, args));
    }
  }

  public List<String> getFiles() {
    return files;
  }

  public void setFiles(List<String> files) {
    this.files = files;
  }

  public boolean isVerbose() {
    return verbose;
  }

  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  public static void main(String[] args) throws IOException, InitializationException {

    CmdLineConfig parserCmdLine = new CmdLineConfig();
    try {
      parserCmdLine.parserCmdLine(args);
      if (parserCmdLine.printHelp) {
        parserCmdLine.printUsage();
        System.exit(0);
      }
    } catch (Exception e) {
      parserCmdLine.printUsage();
      System.err.println(e.getMessage());
      return;
    }
    BatchProcessor batchProcessor = new BatchProcessor();
    batchProcessor.setFiles(parserCmdLine.files);
    if (parserCmdLine.dirWithJars != null) {
      File f = new File(parserCmdLine.dirWithJars);
      URL[] urls = null;
      if (f.isDirectory()) {
        File[] listFiles = f.listFiles(new FileFilter() {

          @Override
          public boolean accept(File f) {
            return f.isFile() && f.getName().endsWith(".jar");
          }
        });
        urls = new URL[listFiles.length];
        for (int i = 0; i < urls.length; i++) {
          urls[i] = listFiles[i].toURI().toURL();
        }
      } else if (f.getName().endsWith("jar")) {
        urls = new URL[] { f.toURI().toURL() };
      }
      if (urls == null) {
        System.err.println("Dir with additional jars or single jars do not point to dir with jars or single jar");
        System.exit(1);
      }
      URLClassLoader classLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
      Thread.currentThread().setContextClassLoader(classLoader);
    }
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    try {
      batchProcessor.setLogDataParsedListener((LogDataParsedListener) cl.loadClass(parserCmdLine.logDataParsedListenerClass).newInstance());
      // batchProcessor.setLogDataCollector((LogDataParsedListener) cl.loadClass(parserCmdLine.logDataParsedListenerClass).newInstance());
    } catch (Exception e2) {
      System.err.println("Can't load log data collector: " + e2.getMessage());
    }

    batchProcessor.setVerbose(parserCmdLine.verbose);

    batchProcessor.print("Processing started");
    batchProcessor.process();
    batchProcessor.print("Finished");
  }

  public LogDataParsedListener getLogDataParsedListener() {
    return logDataParsedListener;
  }

  public void setLogDataParsedListener(LogDataParsedListener logDataParsedListener) {
    this.logDataParsedListener = logDataParsedListener;
  }

}
