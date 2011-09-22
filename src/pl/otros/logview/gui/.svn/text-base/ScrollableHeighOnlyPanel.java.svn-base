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
package pl.otros.logview.gui;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.Scrollable;

public class ScrollableHeighOnlyPanel extends JPanel implements Scrollable {

  public ScrollableHeighOnlyPanel() {
    super();
  }

  public ScrollableHeighOnlyPanel(boolean arg0) {
    super(arg0);
  }

  public ScrollableHeighOnlyPanel(LayoutManager arg0, boolean arg1) {
    super(arg0, arg1);
  }

  public ScrollableHeighOnlyPanel(LayoutManager arg0) {
    super(arg0);
  }

  @Override
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  @Override
  public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
    return 60;
  }

  @Override
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }

  @Override
  public boolean getScrollableTracksViewportWidth() {
    return true;
  }

  @Override
  public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
    return 20;
  }

}
