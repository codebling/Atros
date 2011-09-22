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
import java.util.HashSet;

import javax.swing.JCheckBox;
import javax.swing.JTable;

import pl.otros.logview.LogData;
import pl.otros.logview.filter.ClassFilter;
import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.LogDataTableModel;

public class FocusOnSelectedClassesAction extends FocusOnThisAbstractAction<ClassFilter> {

  public FocusOnSelectedClassesAction(JTable jTable, ClassFilter filter, LogDataTableModel dataTableModel, JCheckBox filterEnableCheckBox) {
    super(jTable, filter, dataTableModel, filterEnableCheckBox);
    this.putValue(NAME, "Focus on selected classes");
    this.putValue(SMALL_ICON, Icons.CLASS);
  }

  @Override
  public void action(ActionEvent e, ClassFilter filter, LogData... selectedLogData) {
    HashSet<String> classes = new HashSet<String>();
    for (LogData logData : selectedLogData) {
      classes.add(logData.getClazz());
    }
    filter.focusOn(classes.toArray(new String[0]));
  }

}
