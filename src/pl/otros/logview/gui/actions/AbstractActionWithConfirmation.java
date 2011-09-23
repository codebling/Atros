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

import pl.otros.logview.gui.util.PersistentConfirmationDialog;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public abstract class AbstractActionWithConfirmation extends AbstractAction {

  private final String configKey;
  
  public AbstractActionWithConfirmation() {
    super();
    configKey = new Throwable().getStackTrace()[1].getClassName();
  }

  public AbstractActionWithConfirmation(String configKey) {
    super();
    this.configKey = configKey;
  }

  @Override
  public final void actionPerformed(ActionEvent e) {
    if (PersistentConfirmationDialog.showConfirmDialog((Component) e.getSource(), getWarnningMessage(), configKey)) {
      actionPerformedHook(e);
    }
  }

  public abstract void actionPerformedHook(ActionEvent e);

  public abstract String getWarnningMessage();
}
