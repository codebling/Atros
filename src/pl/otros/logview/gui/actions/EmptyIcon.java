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
import java.awt.Graphics;

import javax.swing.Icon;

public final class EmptyIcon implements Icon {

  public EmptyIcon() {
  }

  public int getIconHeight() {
    return 0;
  }

  public int getIconWidth() {
    return 0;
  }

  public void paintIcon(Component c, Graphics g, int x, int y) {

  }

}
