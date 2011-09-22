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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import pl.otros.logview.LogData;

public class LogDataListPersistanceVer2 implements LogDataListPersistance {

  public static final String HEADER_ID = "ID";
  public static final String HEADER_TIMESTAMP = "TIMESTAMP";
  public static final String HEADER_MESSAGE = "MESSAGE";
  public static final String HEADER_CLASS = "CLASS";
  public static final String HEADER_METHOD = "METHOD";
  public static final String HEADER_LEVEL = "LEVEL";
  public static final String HEADER_LOGGER = "LOGGER";
  public static final String HEADER_THREAD = "THREAD";
  public static final String FIELD_SEPERATOR = "|";
  public static final String FIELD_SEPERATOR_TO_SPLIT = "\\|";

  /*
   * (non-Javadoc)
   * 
   * @see pl.otros.logview.persistance.LogDataListPersistance#saveLogsList(java.io.OutputStream, java.util.List)
   */
  public void saveLogsList(OutputStream out, List<LogData> list) throws IOException {
    BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

    ArrayList<String> saveMapOrder = getSaveMapOrder();
    for (String string : saveMapOrder) {
      bout.write(string);
      bout.write(FIELD_SEPERATOR);
    }
    bout.write("\n");
    for (LogData logData : list) {
      Map<String, String> map = toMap(logData);
      for (String key : saveMapOrder) {
        String s = map.get(key);
        if (s == null) {
          s = "";
        }
        bout.write(escpageString(s));
        bout.write(FIELD_SEPERATOR);
      }
      bout.write("\n");
    }
    bout.flush();

  }

  private Map<String, String> toMap(LogData logData) {
    Map<String, String> m = new HashMap<String, String>();
    m.put(HEADER_ID, Integer.toString(logData.getId()));
    m.put(HEADER_CLASS, logData.getClazz());
    m.put(HEADER_LEVEL, logData.getLevel().toString());
    m.put(HEADER_LOGGER, logData.getLoggerName());
    m.put(HEADER_MESSAGE, logData.getMessage());
    m.put(HEADER_METHOD, logData.getMethod());
    m.put(HEADER_THREAD, logData.getThread());
    m.put(HEADER_TIMESTAMP, Long.toString(logData.getDate().getTime()));
    return m;
  }

  private ArrayList<String> getSaveMapOrder() {
    ArrayList<String> m = new ArrayList<String>();
    m.add(HEADER_ID);
    m.add(HEADER_TIMESTAMP);
    m.add(HEADER_MESSAGE);
    m.add(HEADER_CLASS);
    m.add(HEADER_METHOD);
    m.add(HEADER_LEVEL);
    m.add(HEADER_LOGGER);
    m.add(HEADER_THREAD);
    return m;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.otros.logview.persistance.LogDataListPersistance#loadLogsList(java.io.InputStream)
   */
  public List<LogData> loadLogsList(InputStream in) throws IOException {
    BufferedReader bin = new BufferedReader(new InputStreamReader(in, "UTF-8"));
    String line = bin.readLine();
    String[] split = line.split(FIELD_SEPERATOR_TO_SPLIT);
    HashMap<String, Integer> fieldMapping = new HashMap<String, Integer>();
    for (int i = 0; i < split.length; i++) {
      String string = split[i];
      fieldMapping.put(string, Integer.valueOf(i));
    }

    ArrayList<LogData> list = new ArrayList<LogData>();
    while ((line = bin.readLine()) != null) {
      String[] params = line.split(FIELD_SEPERATOR_TO_SPLIT);
      LogData parseLogData = parseLogData(params, fieldMapping);
      list.add(parseLogData);
    }

    return list;
  }

  protected LogData parseLogData(String[] line, Map<String, Integer> fieldMapping) {
    for (int i = 0; i < line.length; i++) {
      line[i] = unescapgeString(line[i]);
    }
    LogData ld = new LogData();
    ld.setId(Integer.parseInt(line[fieldMapping.get(HEADER_ID)]));
    ld.setClazz(line[fieldMapping.get(HEADER_CLASS)]);
    ld.setDate(new Date(Long.parseLong(line[fieldMapping.get(HEADER_TIMESTAMP)])));
    ld.setLevel(Level.parse(line[fieldMapping.get(HEADER_LEVEL)]));
    ld.setLoggerName(line[fieldMapping.get(HEADER_LOGGER)]);
    ld.setMessage(line[fieldMapping.get(HEADER_MESSAGE)]);
    ld.setMethod(line[fieldMapping.get(HEADER_METHOD)]);
    ld.setThread(line[fieldMapping.get(HEADER_THREAD)]);
    return ld;
  }

  protected String escpageString(String s) {
    s = s.replace("\\", "\\S"); // "\" -> "\S"
    s = s.replace("|", "\\P"); // "|" -> "\P"
    s = s.replace("\n", "\\n");
    return s;
  }

  protected String unescapgeString(String s) {
    s = s.replace("\\n", "\n");
    s = s.replace("\\P", "|");
    s = s.replace("\\S", "\\");
    return s;
  }

}
