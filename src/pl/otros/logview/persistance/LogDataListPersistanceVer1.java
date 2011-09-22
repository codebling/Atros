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
package pl.otros.logview.persistance;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import pl.otros.logview.LogData;

public class LogDataListPersistanceVer1 implements LogDataListPersistance {

  @Override
  public List<LogData> loadLogsList(InputStream in) throws Exception {
    ObjectInputStream oin = new ObjectInputStream(in);
    TreeMap<Integer, LogData> list = (TreeMap<Integer, LogData>) oin.readObject();
    ArrayList<LogData> r = new ArrayList<LogData>(list.size());
    r.addAll(list.values());
    return r;
  }

  @Override
  public void saveLogsList(OutputStream out, List<LogData> list) throws IOException {
    TreeMap<Integer, LogData> m = new TreeMap<Integer, LogData>();
    for (int i = 0; i < list.size(); i++) {
      m.put(Integer.valueOf(i), list.get(i));
    }
    ObjectOutputStream oout = new ObjectOutputStream(out);
    oout.writeObject(m);
  }

}
