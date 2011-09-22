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
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.AbstractAction;

import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.LogDataTableModel;
import pl.otros.logview.gui.StatusObserver;

public class ClearMarkingsAction extends AbstractAction {

  private static final Logger LOGGER = Logger.getLogger(ClearMarkingsAction.class.getName());
  private LogDataTableModel dataTableModel;
  private StatusObserver statusObserver;

  public ClearMarkingsAction(LogDataTableModel dataTableModel) {
    this(dataTableModel, null);
  }

  public ClearMarkingsAction(LogDataTableModel dataTableModel, StatusObserver statusObserver) {
    super("Clear markings", Icons.MARKINGS_CLEAR);
    this.dataTableModel = dataTableModel;
    this.statusObserver = statusObserver;
  }

  @Override
  public void actionPerformed(ActionEvent arg0) {
    long start = System.currentTimeMillis();
    int rowCount = dataTableModel.getRowCount();
    LOGGER.info(String.format("Started action of clearing marks, have %d rows to check.", rowCount));
    ArrayList<Integer> markedRows = new ArrayList<Integer>();
    for (int i = 0; i < rowCount; i++) {
      if (dataTableModel.isMarked(i)) {
        markedRows.add(i);
      }
    }
    LOGGER.info(String.format("%d rows to unmark.", markedRows.size()));
    int[] toUnmark = new int[markedRows.size()];
    for (int i = 0; i < toUnmark.length; i++) {
      toUnmark[i] = markedRows.get(i);

    }
    dataTableModel.unmarkRows(toUnmark);
    LOGGER.info(String.format("Row markings [%d] cleared in %d ms", toUnmark.length, (System.currentTimeMillis() - start)));
    if (statusObserver != null) {
      statusObserver.updateStatus("Markings have been cleard");
    }
  }

}
