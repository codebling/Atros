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
package pl.otros.logview.reader;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import pl.otros.logview.gui.LogDataTableModel;
import pl.otros.logview.gui.LogViewPanelWrapper;
import pl.otros.logview.gui.StatusObserver;
import pl.otros.logview.gui.TabHeader;
import pl.otros.logview.importer.InitializationException;
import pl.otros.logview.importer.UtilLoggingXmlLogImporter;
import pl.otros.logview.parser.ParsingContext;

public class SocketLogReader {

  private ServerSocket serverSocket;
  private JTabbedPane tabbedPane;
  private StatusObserver observer;

  public SocketLogReader(JTabbedPane tabbedPane, StatusObserver observer) {
    super();
    this.tabbedPane = tabbedPane;
    this.observer = observer;
  }

  public void close() throws IOException {
    if (serverSocket != null) {
      serverSocket.close();
      serverSocket = null;
    }
  }

  public void start(int port) throws Exception {
    serverSocket = new ServerSocket(port);
    Runnable r = new Runnable() {

      @Override
      public void run() {
        try {
          while (true) {
            Socket s = serverSocket.accept();
            SocketHandler handler = new SocketHandler(s);
            Thread t = new Thread(handler, "Socket handler: " + s.getInetAddress() + ":" + s.getPort());
            t.setDaemon(true);
            t.start();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    Thread t = new Thread(r, "Socket listener");
    t.setDaemon(true);
    t.start();

  }

  public boolean isClosed() {
    return serverSocket == null || serverSocket.isClosed();
  }

  private class SocketHandler implements Runnable {

    public Socket socket;

    public SocketHandler(Socket socket) {
      super();
      this.socket = socket;
    }

    @Override
    public void run() {
      String adress = socket.getInetAddress().toString() + ":" + socket.getPort();
      // final LogViewInternalFrame frame = FrameManger.getInstance().createFrame(adress);
      final LogViewPanelWrapper panel = new LogViewPanelWrapper(adress, observer);
      SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
          String tabName = panel.getName();
          int tabNumber = tabbedPane.getTabCount();
          tabbedPane.addTab(null, panel);
          tabbedPane.setTabComponentAt(tabNumber, new TabHeader(tabbedPane, tabName, "Socket listener: " + tabName));

          panel.switchToContentView();
        }
      });

      try {
        InputStream in = socket.getInputStream();
        ParsingContext parsingContext = new ParsingContext();
        LogDataTableModel dataTableModel = panel.getDataTableModel();
        UtilLoggingXmlLogImporter logImporter = new UtilLoggingXmlLogImporter();
        logImporter.init(new Properties());
        logImporter.importLogs(in, dataTableModel, parsingContext);
        observer.updateStatus(adress + " - connection finished ");
      } catch (IOException e) {
        e.printStackTrace();
        observer.updateStatus(adress + " - connection broken: " + e.getMessage(), StatusObserver.LEVEL_ERROR);
      }  catch (Exception e) {
        e.printStackTrace();
        observer.updateStatus("Can't initialize log parser", StatusObserver.LEVEL_ERROR);
      }
    }

  }
}
