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
package pl.otros.logview.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.RandomAccessContent;
import org.apache.commons.vfs.util.RandomAccessMode;

import pl.otros.logview.importer.LogImporter;
import pl.otros.logview.parser.ParsingContext;
import pl.otros.logview.reader.ProxyLogDataCollector;

public class Utils {

  private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());
  private static final int GZIP_MIN_SIZE = 26;
  private static final int GZIP_CHECK_BUFFER_SIZE = 1024;
  private static final int DETECT_LOAD_SIZE = 3 * 1024;

  public static boolean checkIfIsGzipped(FileObject fileObject) throws IOException {
    boolean gziped = false;
    if (fileObject.getContent().getSize() == 0) {
      LOGGER.fine("File object " + fileObject.getName() + " is empty, can't detect gzip compression");
      return false;
    }
    InputStream inputStream = fileObject.getContent().getInputStream();
    byte[] loadProbe = loadProbe(inputStream, GZIP_CHECK_BUFFER_SIZE);
    IOUtils.closeQuietly(inputStream);
    if (loadProbe.length < GZIP_MIN_SIZE) {
      LOGGER.info("Loaded probe is too small to check if it is gziped");
      return false;
    }
    try {
      ByteArrayInputStream bin = new ByteArrayInputStream(loadProbe);
      GZIPInputStream gzipInputStream = new GZIPInputStream(bin);
      int available = bin.available();
      byte[] b = new byte[available < GZIP_CHECK_BUFFER_SIZE ? available : GZIP_CHECK_BUFFER_SIZE];
      gzipInputStream.read(b, 0, bin.available());
      gziped = true;
    } catch (IOException e) {
      // Not gziped
      LOGGER.fine(fileObject.getName() + " is not gzip");
    }

    return gziped;
  }

  private static byte[] loadProbe(InputStream in, int buffSize) throws IOException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    byte[] buff = new byte[buffSize];
    int read = in.read(buff);
    bout.write(buff, 0, read);
    return bout.toByteArray();
  }

  public static LoadingInfo openFileObject(FileObject fileObject) throws Exception {
    return openFileObject(fileObject, false);
  }

  public static LoadingInfo openFileObject(FileObject fileObject, boolean tailing) throws Exception {
    LoadingInfo loadingInfo = new LoadingInfo();
    loadingInfo.setFileObject(fileObject);
    loadingInfo.setFriendlyUrl(fileObject.getName().getFriendlyURI());
    boolean isGzipped = checkIfIsGzipped(fileObject);
    loadingInfo.setGziped(isGzipped);
    loadingInfo.setLastFileSize(fileObject.getContent().getSize());
    InputStream inputStream = fileObject.getContent().getInputStream();
    ObservableInputStreamImpl observableStream = new ObservableInputStreamImpl(inputStream);
    loadingInfo.setObserableInputStreamImpl(observableStream);
    if (isGzipped) {
      loadingInfo.setContentInputStream(new GZIPInputStream(observableStream));
    } else {
      loadingInfo.setContentInputStream(observableStream);
    }
    loadingInfo.setTailing(tailing);

    return loadingInfo;
  }

  public static void reloadFileObject(LoadingInfo loadingInfo) throws IOException {
    loadingInfo.getFileObject().refresh();
    long lastFileSize = loadingInfo.getLastFileSize();
    long currentSize = loadingInfo.getFileObject().getContent().getSize();
    if (currentSize > lastFileSize) {
      IOUtils.closeQuietly(loadingInfo.getObserableInputStreamImpl());

      RandomAccessContent randomAccessContent = loadingInfo.getFileObject().getContent().getRandomAccessContent(RandomAccessMode.READ);
      randomAccessContent.seek(lastFileSize);
      loadingInfo.setLastFileSize(currentSize);
      ObservableInputStreamImpl observableStream = new ObservableInputStreamImpl(randomAccessContent.getInputStream());
      loadingInfo.setObserableInputStreamImpl(observableStream);
      if (loadingInfo.isGziped()) {
        loadingInfo.setContentInputStream(new GZIPInputStream(observableStream));
      } else {
        loadingInfo.setContentInputStream(observableStream);
      }
    } else if (currentSize < lastFileSize) {
      IOUtils.closeQuietly(loadingInfo.getObserableInputStreamImpl());
      InputStream inputStream = loadingInfo.getFileObject().getContent().getInputStream();
      ObservableInputStreamImpl observableStream = new ObservableInputStreamImpl(inputStream);
      loadingInfo.setObserableInputStreamImpl(observableStream);
      if (loadingInfo.isGziped()) {
        loadingInfo.setContentInputStream(new GZIPInputStream(observableStream));
      } else {
        loadingInfo.setContentInputStream(observableStream);
      }
      loadingInfo.setLastFileSize(loadingInfo.getFileObject().getContent().getSize());
    }

  }

  public static LogImporter detectLogImporter(Collection<LogImporter> importers, byte[] buff) {
    LogImporter highestImporter = null;
    int messageImported = 0;
    for (LogImporter logImporter : importers) {
      ByteArrayInputStream bin = new ByteArrayInputStream(buff);
      ProxyLogDataCollector logCollector = new ProxyLogDataCollector();
      try {
        logImporter.importLogs(bin, logCollector, new ParsingContext());
      } catch (Exception e1) {
        // Some log parser can throw exception, due to incomplete line
      }
      int currentLogMessageImported = logCollector.getLogData().length;
      if (messageImported < currentLogMessageImported) {
        messageImported = currentLogMessageImported;
        highestImporter = logImporter;
      }
    }
    return highestImporter;

  }

  public static LogImporter detectLogImporter(Collection<LogImporter> importers, FileObject fileObject) throws IOException {
    boolean checkIfIsGzipped = checkIfIsGzipped(fileObject);
    InputStream in = checkIfIsGzipped ? new GZIPInputStream(fileObject.getContent().getInputStream()) : fileObject.getContent().getInputStream();
    byte[] loadProbe = loadProbe(in, DETECT_LOAD_SIZE);
    in.close();
    return detectLogImporter(importers, loadProbe);
  }

  public static byte[] loadProbe(FileObject fileObject, int buffSize) throws IOException {
    InputStream in = null;
    try {
      if (Utils.checkIfIsGzipped(fileObject)) {
        in = new GZIPInputStream(fileObject.getContent().getInputStream());
      } else {
        in = fileObject.getContent().getInputStream();
      }
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      byte[] buff = new byte[buffSize];
      int read = in.read(buff);
      bout.write(buff, 0, read);
      return bout.toByteArray();
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  public static void closeQuietly(FileObject fileObject) {

    if (fileObject != null) {
      String friendlyURI = fileObject.getName().getFriendlyURI();
      try {
        LOGGER.info(String.format("Closing file %s", friendlyURI));
        fileObject.close();
        LOGGER.info(String.format("File %s closed", friendlyURI));
      } catch (FileSystemException ignore) {
        LOGGER.info(String.format("File %s is not closed: %s", friendlyURI, ignore.getMessage()));
      }
    }
  }

  public static String getFileObjectShortName(FileObject fileObject) {
    StringBuilder sb = new StringBuilder();
    try {
      URI uri = new URI(fileObject.getName().getURI());
      String scheme = fileObject.getName().getScheme();
      sb.append(scheme);
      sb.append("://");
      if (!"file".equals(scheme)) {
        String host = uri.getHost().split("\\.")[0];
        sb.append(host).append('/');
      }
      sb.append(fileObject.getName().getBaseName());
    } catch (URISyntaxException e) {
      LOGGER.warning("Problem with preparing short name of fileobject: " + e.getMessage());
      sb.setLength(0);
      sb.append(fileObject.getName().getScheme()).append("://").append(fileObject.getName().getBaseName());
    }
    return sb.toString();
  }
}
