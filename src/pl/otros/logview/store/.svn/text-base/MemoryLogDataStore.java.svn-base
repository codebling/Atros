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

package pl.otros.logview.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import pl.otros.logview.LogData;

public class MemoryLogDataStore extends AbstractMemoryLogStore implements LogDataStore {

  private static final Logger LOGGER = Logger.getLogger(MemoryLogDataStore.class.getName());

  ArrayList<LogData> list;

  public MemoryLogDataStore() {
    list = new ArrayList<LogData>();

  }

  @Override
  public int getCount() {
    return list.size();
  }

  @Override
  public void add(LogData... logDatas) {
    for (int i = 0; i < logDatas.length; i++) {
      LogData logData = logDatas[i];
      list.add(logData);
      if (list.size() >= limit) {
        remove(0);
      }
    }
  }

  @Override
  public void remove(int... ids) {
    LOGGER.fine(String.format("Removing %d rows, first sorting by id", ids.length));
    Arrays.sort(ids);
    LOGGER.finest("Rows sorted, removing from end");
    for (int i = ids.length - 1; i >= 0; i--) {
      LogData removed = list.remove(ids[i]);
      notable.removeNote(removed.getId(), false);
    }
    LOGGER.finest(String.format("%d rows where removed ", ids.length));

  }

  @Override
  public LogData getLogData(int id) {
    LogData logData = list.get(id);
    return logData;
  }

  @Override
  public LogData[] getLogData() {
    LogData[] datas = new LogData[list.size()];
    datas = list.toArray(datas);
    return datas;
  }

  @Override
  public int clear() {
    int size = list.size();
    if (size > 0) {
      list.clear();
      clearNotes();
    }
    return size;
  }

  @Override
  public Iterator<LogData> iterator() {
    return list.iterator();
  }

  @Override
  public Integer getLogDataIdInRow(int row) {
    return list.get(row).getId();
  }

}
