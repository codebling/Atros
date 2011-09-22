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
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import pl.otros.logview.VersionUtil;

public class CheckForNewVersionAction extends AbstractAction {

  public CheckForNewVersionAction() {
    putValue(Action.NAME, "Check for new version");
  }

  @Override
  public void actionPerformed(ActionEvent arg0) {

    try {
      String running = VersionUtil.getRunningVersion();
      String current = VersionUtil.getCurrentVersion(running);
      String message = null;
      if (current.compareTo(running) > 0) {
        message = "Your version is " + running + ", current version is " + current + ". Go to http://code.google.com/p/otroslogviewer/ for donwloading.";
      } else {
        message = "Your version is up to date!";
      }
      JOptionPane.showMessageDialog((Component) arg0.getSource(), message);

    } catch (Exception e) {
      String message = "Problem with checking new version: " + e.getLocalizedMessage();
      JOptionPane.showMessageDialog((Component) arg0.getSource(), message, "Error!", JOptionPane.ERROR_MESSAGE);
    }

  }

  public static void main(String[] args) throws MalformedURLException, IOException {
    String running = "2010-08-24";
    String current = VersionUtil.getCurrentVersion(running);
    System.out.println(current);
    System.out.println(current.compareTo(running));
  }

}
