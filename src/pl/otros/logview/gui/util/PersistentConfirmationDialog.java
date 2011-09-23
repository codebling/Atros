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
package pl.otros.logview.gui.util;

import pl.otros.logview.persistance.PersistentConfiguration;

import javax.swing.*;
import java.awt.*;

public final class PersistentConfirmationDialog {
  /**
   * Shows a confirmation dialog with a 'do not ask again' checkbox or, if the checkbox was previously checked,
   * does not display the dialog and just returns true. To avoid headaches, we don't 'remember' anything if YES
   * was not chosen.
   *
   * @param parent the parent component for the modal dialog which will appear
   * @param warningMessage the message to show in the dialog
   * @param confirmationName the name under which the persistence data will be stored.(must be unique per call location)
   * @return true if "Yes" or if 'do not ask again' was previously selected; false otherwise
   */
  public static boolean showConfirmDialog(Component parent, String warningMessage, String confirmationName) {
    String configKey = "gui.noaskagain." + confirmationName;
    boolean doNotAskAgain = PersistentConfiguration.getInstance().getBoolean(configKey, false);
    boolean confirmed = true;
    if (!doNotAskAgain) {
      JPanel component = new JPanel(new BorderLayout());
      component.add(new JLabel(warningMessage));
      JCheckBox jCheckBox = new JCheckBox("Do not ask again!");
      component.add(jCheckBox, BorderLayout.SOUTH);
      int showConfirmDialog = JOptionPane.showConfirmDialog(parent, component, "Confirm", JOptionPane.YES_NO_OPTION);
      confirmed = showConfirmDialog == JOptionPane.YES_OPTION;
      if (jCheckBox.isSelected() && confirmed) {
        PersistentConfiguration.getInstance().setProperty(configKey, true);
      }
    }
    return confirmed;
  }
}
