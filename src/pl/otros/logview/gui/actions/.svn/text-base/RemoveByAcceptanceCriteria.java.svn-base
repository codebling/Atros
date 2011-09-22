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
import javax.swing.Icon;

import pl.otros.logview.accept.AcceptCondition;
import pl.otros.logview.gui.HasIcon;
import pl.otros.logview.gui.LogDataTableModel;
import pl.otros.logview.gui.StatusObserver;

public class RemoveByAcceptanceCriteria extends AbstractAction {

  private AcceptCondition acceptCondition;
  private StatusObserver observer;
  private LogDataTableModel dataTableModel;

  public RemoveByAcceptanceCriteria(AcceptCondition acceptCondition, LogDataTableModel dataTableModel, StatusObserver observer) {
    this(acceptCondition, dataTableModel, (Icon) null, observer);
  }

  public RemoveByAcceptanceCriteria(AcceptCondition acceptCondition, LogDataTableModel dataTableModel, Icon icon, StatusObserver observer) {
    this.acceptCondition = acceptCondition;
    this.dataTableModel = dataTableModel;
    this.observer = observer;
    putValue(NAME, acceptCondition.getName());
    putValue(SHORT_DESCRIPTION, acceptCondition.getDescription());
    if (icon == null && acceptCondition instanceof HasIcon) {
      icon = ((HasIcon) acceptCondition).getIcon();
    }
    putValue(SMALL_ICON, icon);
  }

  public RemoveByAcceptanceCriteria(AcceptCondition acceptCondition, LogDataTableModel dataTableModel, String name, Icon icon, StatusObserver observer) {
    this(acceptCondition, dataTableModel, icon, observer);
    putValue(NAME, name);
  }

  public RemoveByAcceptanceCriteria(AcceptCondition acceptCondition, LogDataTableModel dataTableModel, String name, StatusObserver observer) {
    this(acceptCondition, dataTableModel, name, null, observer);
  }

  @Override
  public void actionPerformed(ActionEvent arg0) {
    int removeRows = dataTableModel.removeRows(acceptCondition);
    if (observer != null) {
      observer.updateStatus(String.format("Removed %d rows using \"%s\"", removeRows, acceptCondition.getName()));
    }

  }

}
