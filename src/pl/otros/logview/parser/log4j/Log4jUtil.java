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
package pl.otros.logview.parser.log4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.spi.LoggingEvent;

import pl.otros.logview.LogData;
import pl.otros.logview.gui.table.TableColumns;

import com.google.common.collect.ImmutableMap;

public class Log4jUtil {

  private static final Logger LOGGER = Logger.getLogger(Log4jUtil.class.getName());
  private static final Map<String, String> IMMUTABLE_EMPTY_MAP = new ImmutableMap.Builder<String, String>().build();

  public static LogData translateLog4j(LoggingEvent event) {
    LogData ld = new LogData();
    ld.setDate(new Date(event.getTimeStamp()));
    StringBuilder sb = new StringBuilder();
    sb.append(event.getMessage());
    String[] throwableStrRep = event.getThrowableInformation().getThrowableStrRep();
    for (String string : throwableStrRep) {
      sb.append('\n');
      sb.append(string);
    }
    ld.setMessage(sb.toString().trim());

    ld.setLevel(parseLevel(event.getLevel().toString()));
    ld.setClazz(event.getLocationInformation().getClassName());
    ld.setMethod(event.getLocationInformation().getMethodName());
    ld.setFile(event.getLocationInformation().getFileName());
    ld.setLine(event.getLocationInformation().getLineNumber());
    ld.setNDC(event.getNDC());
    ld.setThread(event.getThreadName());
    ld.setLoggerName(event.getLoggerName());

    ld.setProperties(IMMUTABLE_EMPTY_MAP);
    Map properties = event.getProperties();
    if (properties != null) {
      Map<String, String> props = new HashMap<String, String>(properties.size());
      for (Object key : properties.keySet()) {
        String value = (String) properties.get(key);
        if (StringUtils.isNotBlank(value)) {
          props.put(key.toString(), value);
        }
      }
      if (props.size() > 0) {
        ld.setProperties(props);
      }
    }

    return ld;
  }

  public static Level parseLevel(String s) {
    if (s.equalsIgnoreCase("INFO")) {
      return Level.INFO;
    } else if (s.equalsIgnoreCase("ERROR") || s.equalsIgnoreCase("FATAL")) {
      return Level.SEVERE;
    } else if (s.equalsIgnoreCase("WARN")) {
      return Level.WARNING;
    } else if (s.equalsIgnoreCase("DEBUG")) {
      return Level.FINE;
    } else if (s.equalsIgnoreCase("TRACE")) {
      return Level.FINEST;
    }
    LOGGER.severe("Level \"" + s + "\" not parsed!");
    return null;

  }

  public static TableColumns[] getUsedColumns(String pattern) {
    ArrayList<TableColumns> list = new ArrayList<TableColumns>();
    if (pattern.contains(Log4jPatternMultilineLogParser.CLASS)) {
      list.add(TableColumns.CLASS);
    }
    if (pattern.contains(Log4jPatternMultilineLogParser.TIMESTAMP)) {
      list.add(TableColumns.TIME);
    }
    if (pattern.contains(Log4jPatternMultilineLogParser.LEVEL)) {
      list.add(TableColumns.LEVEL);
    }
    if (pattern.contains(Log4jPatternMultilineLogParser.THREAD)) {
      list.add(TableColumns.THREAD);
    }
    if (pattern.contains(Log4jPatternMultilineLogParser.MESSAGE)) {
      list.add(TableColumns.MESSAGE);
    }
    if (pattern.contains(Log4jPatternMultilineLogParser.METHOD)) {
      list.add(TableColumns.METHOD);
    }
    list.add(TableColumns.ID);
    list.add(TableColumns.MARK);
    list.add(TableColumns.NOTE);

    if (pattern.contains(Log4jPatternMultilineLogParser.FILE)) {
      list.add(TableColumns.FILE);
    }

    if (pattern.contains(Log4jPatternMultilineLogParser.LINE)) {
      list.add(TableColumns.LINE);
    }

    if (pattern.contains(Log4jPatternMultilineLogParser.NDC)) {
      list.add(TableColumns.NDC);
    }

    if (pattern.matches(".*PROP\\(.*\\).*")) {
      list.add(TableColumns.PROPERTIES);
    }
    return list.toArray(new TableColumns[0]);

  }
}
