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
import pl.otros.logview.gui.StatusObserver;
import pl.otros.logview.gui.TabHeader;
import pl.otros.logview.gui.message.MessageColorizer;
import pl.otros.logview.gui.message.editor.MessageColorizerBrowser;
import pl.otros.logview.pluginable.PluginableElementsContainer;

public class ShowMessageColorizerEditor extends AbstractAction {

  private JTabbedPane tabbedPane;
  private MessageColorizerBrowser mcEditor;
  private PluginableElementsContainer<MessageColorizer> container;
  private StatusObserver statusObserver;
  private String messageText = null;

  public ShowMessageColorizerEditor(JTabbedPane tabbedPane, PluginableElementsContainer<MessageColorizer> container, StatusObserver statusObserver) {
    super();
    this.tabbedPane = tabbedPane;
    this.container = container;
    this.statusObserver = statusObserver;
    putValue(NAME, "Show message colorizer editor");
    putValue(SHORT_DESCRIPTION, "Show message colorizer editor. You can edit or create message colorizer.");
    putValue(SMALL_ICON, Icons.MESSAGE_COLORIZER);

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (mcEditor == null) {
      mcEditor = new MessageColorizerBrowser(container, statusObserver);
    }
    if (tabbedPane.indexOfComponent(mcEditor) == -1) {
      int tabCount = tabbedPane.getTabCount();
      tabbedPane.addTab(null, Icons.MESSAGE_COLORIZER, mcEditor);
      tabbedPane.setTabComponentAt(tabCount, new TabHeader(tabbedPane, "MessageColorizer editor", Icons.MESSAGE_COLORIZER, "MessageColorizer editor"));
      tabbedPane.setSelectedIndex(tabCount);
    } else {
      tabbedPane.setSelectedComponent(mcEditor);
    }
  }

  public String getMessageText() {
    return messageText;
  }

  public void setMessageText(String messageText) {
    this.messageText = messageText;
  }

}
