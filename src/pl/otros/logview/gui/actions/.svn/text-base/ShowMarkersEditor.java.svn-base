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
import pl.otros.logview.gui.TabHeader;
import pl.otros.logview.gui.markers.editor.MarkersEditor;

public class ShowMarkersEditor extends AbstractAction {

  private JTabbedPane tabbedPane;
  private MarkersEditor markersEditor;

  public ShowMarkersEditor(JTabbedPane tabbedPane) {
    super();
    this.tabbedPane = tabbedPane;
    putValue(NAME, "Show markers editor");
    putValue(SHORT_DESCRIPTION, "Show markers editor. You can edit or create new marker.");
    putValue(SMALL_ICON, Icons.MARKER);

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (markersEditor == null) {
      markersEditor = new MarkersEditor();
    }

    if (tabbedPane.indexOfComponent(markersEditor) == -1) {
      int tabCount = tabbedPane.getTabCount();
      tabbedPane.addTab(null, Icons.MARKER, markersEditor);
      tabbedPane.setTabComponentAt(tabCount, new TabHeader(tabbedPane, "Markers editor", Icons.MARKER, "Markers editor"));
      tabbedPane.setSelectedIndex(tabCount);
    } else {
      tabbedPane.setSelectedComponent(markersEditor);
    }
  }

}
