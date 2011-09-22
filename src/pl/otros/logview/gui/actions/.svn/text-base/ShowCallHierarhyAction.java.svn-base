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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JTable;

import pl.otros.logview.LogData;
import pl.otros.logview.gui.LogDataTableModel;
import pl.otros.logview.gui.LogViewPanel;
import pl.otros.logview.gui.StatusObserver;
import pl.otros.logview.uml.Message;
import pl.otros.logview.uml.Message.MessageType;

public class ShowCallHierarhyAction implements ActionListener {

  private JTable table;
  private LogDataTableModel model;
  private StatusObserver observer;

  public ShowCallHierarhyAction(JTable table, LogDataTableModel model, StatusObserver observer) {
    super();
    this.table = table;
    this.model = model;
    this.observer = observer;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    int selected = table.getSelectedRow();
    if (selected < 0) {
      observer.updateStatus("Row not selected");
    }
    selected = table.convertRowIndexToModel(selected);
    LogData ld = model.getLogData(selected);
    String thread = ld.getThread();
    LogData[] lds = model.getLogData();
    LinkedList<LogData> stack = new LinkedList<LogData>();
    HashSet<String> entrys = new HashSet<String>();
    HashSet<String> exits = new HashSet<String>();
    for (int i = 0; i < lds.length; i++) {
      LogData logData = lds[i];
      if (!logData.getThread().equals(thread)) {
        continue;
      }
      Message m = new Message(logData.getMessage());
      String s = "" + entrys.size();
      for (int j = 0; j < stack.size(); j++) {
        s += " ";
      }

      if (m.getType().equals(MessageType.TYPE_ENTRY)) {
        stack.addLast(logData);
        entrys.add(logData.getClazz() + "." + logData.getMethod());
      } else if (m.getType().equals(MessageType.TYPE_EXIT) /* && theSameLogMethod(stack.getLast(), logData) */) {
        stack.removeLast();
        exits.add(logData.getClazz() + "." + logData.getMethod());
      }
      if (logData.getId() == ld.getId()) {
        break;
      }
    }

    LogDataTableModel collector = new LogDataTableModel();
    for (int i = 0; i < stack.size(); i++) {
      LogData logData = stack.get(i);
      collector.add(logData);
    }

    LogViewPanel logViewPanel = new LogViewPanel(collector, null);
    JFrame frame = new JFrame("Call hierarchy");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(logViewPanel);
    frame.pack();
    frame.setVisible(true);

  }

}
