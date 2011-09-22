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
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import pl.otros.logview.filter.LogFilter;
import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.TabHeader;
import pl.otros.logview.gui.markers.AutomaticMarker;
import pl.otros.logview.gui.message.MessageColorizer;
import pl.otros.logview.gui.message.MessageFormatter;
import pl.otros.logview.pluginable.AllPluginables;
import pl.otros.logview.pluginable.PluginableElement;
import pl.otros.logview.pluginable.PluginableElementsContainer;

public class ShowLoadedComponets extends AbstractAction {

  private JTabbedPane tabbedPane;
  private JComponent loadedComponents;
  private JTextArea textArea;

  public ShowLoadedComponets(JTabbedPane jTabbedPane) {
    super();
    this.tabbedPane = jTabbedPane;
    putValue(NAME, "Show loaded plugins");
    putValue(SHORT_DESCRIPTION, "Show loaded plugihns.");
    putValue(SMALL_ICON, Icons.PLUGIN);

    textArea = new JTextArea();
    textArea.setEditable(false);
    JScrollPane jScrollPane = new JScrollPane(textArea);
    loadedComponents = jScrollPane;
  }

  @Override
  public void actionPerformed(ActionEvent arg0) {
    refreshData();
    if (tabbedPane.indexOfComponent(loadedComponents) == -1) {
      int tabCount = tabbedPane.getTabCount();
      tabbedPane.addTab(null, Icons.MARKER, loadedComponents);
      tabbedPane.setTabComponentAt(tabCount, new TabHeader(tabbedPane, "Loaded plugins", Icons.PLUGIN, "Loaded plugins"));
      tabbedPane.setSelectedIndex(tabCount);
    } else {
      tabbedPane.setSelectedComponent(loadedComponents);
    }

  }

  protected void refreshData() {
    textArea.setText("");
    StringBuilder sb = new StringBuilder();
    AllPluginables pluginables = AllPluginables.getInstance();
    PluginableElementsContainer<LogFilter> logFiltersContainer = pluginables.getLogFiltersContainer();
    PluginableElementsContainer<AutomaticMarker> markersContainser = pluginables.getMarkersContainser();
    PluginableElementsContainer<MessageColorizer> messageColorizers = pluginables.getMessageColorizers();
    PluginableElementsContainer<MessageFormatter> messageFormatters = pluginables.getMessageFormatters();

    sb.append("Log filters:\n");
    for (PluginableElement element : logFiltersContainer.getElements()) {
      sb.append("\t").append(element.getName()).append(" [").append(element.getPluginableId()).append("]\n");
    }

    sb.append("\nMarkers:\n");
    for (PluginableElement element : markersContainser.getElements()) {
      sb.append("\t").append(element.getName()).append(" [").append(element.getPluginableId()).append("]\n");
    }

    sb.append("\nMessage colorizers:\n");
    for (PluginableElement element : messageColorizers.getElements()) {
      sb.append("\t").append(element.getName()).append(" [").append(element.getPluginableId()).append("]\n");
    }

    sb.append("\nMessage formatters:\n");
    for (PluginableElement element : messageFormatters.getElements()) {
      sb.append("\t").append(element.getName()).append(" [").append(element.getPluginableId()).append("]\n");
    }
    textArea.setText(sb.toString());

  }
}
