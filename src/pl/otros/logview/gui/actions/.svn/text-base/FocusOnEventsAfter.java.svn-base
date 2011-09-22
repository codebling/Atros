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

import javax.swing.JCheckBox;
import javax.swing.JTable;

import pl.otros.logview.LogData;
import pl.otros.logview.filter.TimeFilter;
import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.LogDataTableModel;

public class FocusOnEventsAfter extends FocusOnThisAbstractAction<TimeFilter> {

  public FocusOnEventsAfter(JTable jTable, TimeFilter filter, LogDataTableModel dataTableModel, JCheckBox filterEnableCheckBox) {
    super(jTable, filter, dataTableModel, filterEnableCheckBox);
    this.putValue(NAME, "Focus on subsequent events");
    this.putValue(SMALL_ICON, Icons.ARROW_TURN_270);
  }

  @Override
  public void action(ActionEvent e, TimeFilter filter, LogData... selectedLogData) {
    filter.setStart(selectedLogData[0].getDate());
    filter.setStartFilteringEnabled(true);
  }
}
