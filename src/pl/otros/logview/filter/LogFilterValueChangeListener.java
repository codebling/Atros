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

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.table.TableRowSorter;

import pl.otros.logview.gui.LogDataTableModel;
import pl.otros.logview.gui.StatusObserver;

public class LogFilterValueChangeListener {

  private TableRowSorter<LogDataTableModel> rowSorter;
  private Collection<LogFilter> logFilters;
  private StatusObserver observer;

  public LogFilterValueChangeListener(TableRowSorter<LogDataTableModel> rowSorter, Collection<LogFilter> logFilters, StatusObserver statusObserver) {
    super();
    this.rowSorter = rowSorter;
    this.logFilters = logFilters;
    this.observer = statusObserver;
  }

  public void valueChanged() {

    ArrayList<LogFilter> enabledFiltersList = new ArrayList<LogFilter>();
    for (LogFilter logFilter : logFilters) {
      if (logFilter.isEnable()) {
        enabledFiltersList.add(logFilter);
      }
    }

    LogFilter[] enabledFilters = new LogFilter[enabledFiltersList.size()];
    enabledFilters = enabledFiltersList.toArray(enabledFilters);

    LogDataRowFilter dataRowFilter = new LogDataRowFilter(enabledFilters);
    rowSorter.setRowFilter(dataRowFilter);
    int filtered = rowSorter.getViewRowCount();
    if (observer != null) {
      if (filtered > 0) {
        observer.updateStatus(filtered + " messages passed filters");
      } else {
        observer.updateStatus("No messages passed filters", StatusObserver.LEVEL_ERROR);
      }
    }
  }
}
