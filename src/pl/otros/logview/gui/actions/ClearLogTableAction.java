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

import javax.swing.Action;

import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.LogDataTableModel;

public class ClearLogTableAction extends AbstractActionWithConfirmation {

  private LogDataTableModel dataTableModel;
  private static final String ACTION_NAME = "Clear table";

  public ClearLogTableAction(LogDataTableModel dataTableModel) {
    super("clear");
    putValue(NAME, ACTION_NAME);
    putValue(SMALL_ICON, Icons.CLEAR);
    this.dataTableModel = dataTableModel;
    putValue(Action.SHORT_DESCRIPTION, "Clear all loaded events");

  }

  @Override
  public void actionPerformedHook(ActionEvent e) {
    dataTableModel.clear();
  }

  @Override
  public String getWarnningMessage() {
    return "Are you sure, that you want to clear?";
  }

}
