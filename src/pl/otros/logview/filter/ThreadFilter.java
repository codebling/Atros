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
package pl.otros.logview.filter;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import pl.otros.logview.LogData;
import pl.otros.logview.gui.LogDataTableModel;

public class ThreadFilter extends AbstractLogFilter {

  private static final String NAME = "Thread Filter";
  private static final String DESCRIPTION = "Filtering events based on a thread.";
  private JComboBox gui;
  private HashSet<String> threads;
  private DefaultComboBoxModel boxModel;
  private static final String ALL_THREADS = "--ALL--";

  public ThreadFilter() {
    super(NAME, DESCRIPTION);
    boxModel = new DefaultComboBoxModel();
    boxModel.addElement(ALL_THREADS);
    gui = new JComboBox(boxModel);
    threads = new HashSet<String>();

    gui.setOpaque(true);
    gui.setEditable(false);
    gui.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        listener.valueChanged();
      }
    });

  }

  @Override
  public boolean accept(LogData logData, int row) {
    if (gui.getSelectedIndex() == 0 || logData.getThread().equals(boxModel.getSelectedItem())) {
      return true;
    }
    return false;
  }

  @Override
  public Component getGUI() {
    return gui;
  }

  @Override
  public void init(Properties properties, LogDataTableModel collector) {
    this.collector = collector;

  }

  @Override
  public void setEnable(boolean enable) {
    super.setEnable(enable);
    if (enable) {
      relaodThreads();
    }
  }

  private void relaodThreads() {
    LogData[] ld = collector.getLogData();
    threads.clear();
    for (LogData logData : ld) {
      threads.add(logData.getThread());
    }

    ArrayList<String> toRemoveList = new ArrayList<String>();
    for (int index = 1; index < boxModel.getSize(); index++) {
      String thread = (String) boxModel.getElementAt(index);
      if (!threads.contains(thread)) {
        toRemoveList.add(thread);
      }
    }
    for (String toRemove : toRemoveList) {
      boxModel.removeElement(toRemove);
    }

    for (String thread : threads) {
      if (boxModel.getIndexOf(thread) == -1) {
        boxModel.addElement(thread);
      }
    }
  }

  public void setThreadToFilter(String thread) {
    boxModel.setSelectedItem(thread);
  }

}
