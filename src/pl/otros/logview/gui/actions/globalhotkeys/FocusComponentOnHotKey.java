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
package pl.otros.logview.gui.actions.globalhotkeys;

import java.awt.KeyEventPostProcessor;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;

public class FocusComponentOnHotKey implements KeyEventPostProcessor {

  private JComponent component;
  private int keyCode;
  private int modifiers;

  public FocusComponentOnHotKey(JComponent component, int keyCode, int modifiers) {
    super();
    this.component = component;
    this.keyCode = keyCode;
    this.modifiers = modifiers;
  }

  @Override
  public boolean postProcessKeyEvent(KeyEvent e) {
    if (e.getID() == KeyEvent.KEY_PRESSED) {
      if (e.getKeyCode() == keyCode && modifiers == e.getModifiers()) {
        component.requestFocus();
        return true;
      }
    }

    return false;
  }
}
