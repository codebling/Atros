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

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.Log4jPatternParserEditor;
import pl.otros.logview.gui.StatusObserver;
import pl.otros.logview.gui.TabHeader;

public class ShowLog4jPatternParserEditor extends AbstractAction {

  private JTabbedPane tabbedPane;
  private Log4jPatternParserEditor log4jEditor;
  private StatusObserver observer;

  public ShowLog4jPatternParserEditor(JTabbedPane tabbedPane, StatusObserver observer) {
    super();
    this.tabbedPane = tabbedPane;
    this.observer = observer;
    putValue(NAME, "Show Log4j pattern parser editor");
    putValue(SHORT_DESCRIPTION, "Show Log4j pattern parser editor.");
    putValue(SMALL_ICON, Icons.WRENCH);

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (log4jEditor == null) {
      log4jEditor = new Log4jPatternParserEditor(observer);
    }

    if (tabbedPane.indexOfComponent(log4jEditor) == -1) {
      int tabCount = tabbedPane.getTabCount();
      tabbedPane.addTab(null, Icons.WRENCH, log4jEditor);
      tabbedPane.setTabComponentAt(tabCount, new TabHeader(tabbedPane, "Lo4j pattern parser editor", Icons.WRENCH, "Log4j pattern parser editor"));
      tabbedPane.setSelectedIndex(tabCount);
    } else {
      tabbedPane.setSelectedComponent(log4jEditor);
    }
  }

}
