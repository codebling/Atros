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

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTabbedPane;

import pl.otros.logview.gui.StatusObserver;
import pl.otros.logview.reader.SocketLogReader;

public class StartSocketListener extends AbstractAction {

  private int port = 50505;
  private SocketLogReader logReader = null;
  private StatusObserver observer;
  private JTabbedPane jTabbedPane;

  public StartSocketListener(JTabbedPane jTabbedPane, StatusObserver observer) {
    this.observer = observer;
    this.jTabbedPane = jTabbedPane;
    putValue(Action.NAME, "Start socket listener on port " + port);
    putValue(Action.SHORT_DESCRIPTION, "Start socket listener on port " + port + ". Log have to  be formated by java.util.logging.XMLFormatter.");
    putValue(Action.LONG_DESCRIPTION, "Start socket listener on port " + port + ". Log have to  be formated by java.util.logging.XMLFormatter.");
  }

  @Override
  public void actionPerformed(ActionEvent arg0) {
    if (logReader == null || logReader.isClosed()) {
      logReader = new SocketLogReader(jTabbedPane, observer);

      try {
        logReader.start(port);
        putValue(Action.NAME, "Close socket listener");
        observer.updateStatus("Socket opened on port " + port + " with java.util.logging.XMLFormatter.");
      } catch (Exception e) {
        e.printStackTrace();
        observer.updateStatus("Failed to open listener " + e.getMessage(), StatusObserver.LEVEL_ERROR);
      }
    } else {
      try {
        logReader.close();
        putValue(Action.NAME, "Start socket listener on port " + port);
        observer.updateStatus("Socket closed, log listening finished.");
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        observer.updateStatus("Failed to close listener " + e.getMessage(), StatusObserver.LEVEL_ERROR);
      }
    }
  }

}
