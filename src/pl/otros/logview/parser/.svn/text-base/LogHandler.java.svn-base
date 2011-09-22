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
package pl.otros.logview.parser;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pl.otros.logview.LogData;
import pl.otros.logview.LogDataCollector;

public class LogHandler extends DefaultHandler {

  protected int id = 0;
  protected LogData logData;
  protected LogDataCollector dataTableModel;
  protected Phase phase;
  protected StringBuffer buffer;

  protected enum Phase {
    DATE, MILIS, SEQUENCE, LOGGER, LEVEL, CLASS, METHOD, THREAD, MESSAGE, RECORD
  };

  public LogHandler(LogDataCollector dataTableModel) {
    this.dataTableModel = dataTableModel;
    buffer = new StringBuffer();
  }

  @Override
  public void endElement(String uri, String localName, String rawName) throws SAXException {
    // super.endElement(uri, localName, rawName);
    if (rawName.equals("record")) {
      dataTableModel.add(logData);
      // phase = Phase.RECORD;
    } else if (rawName.equalsIgnoreCase("date")) {
      // phase = Phase.DATE;
    } else if (rawName.equalsIgnoreCase("millis")) {
      logData.setDate(new Date(Long.parseLong(buffer.toString())));
    } else if (rawName.equalsIgnoreCase("sequence")) {
      // phase = Phase.SEQUENCE;
    } else if (rawName.equalsIgnoreCase("logger")) {
      logData.setLoggerName(buffer.toString());
    } else if (rawName.equalsIgnoreCase("level")) {
      logData.setLevel(Level.parse(buffer.toString()));
    } else if (rawName.equalsIgnoreCase("class")) {
      logData.setClazz(buffer.toString());
    } else if (rawName.equalsIgnoreCase("method")) {
      logData.setMethod(buffer.toString());
    } else if (rawName.equalsIgnoreCase("thread")) {
      logData.setThread(buffer.toString());
    } else if (rawName.equalsIgnoreCase("message")) {
      logData.setMessage(buffer.toString());
    }
    buffer.setLength(0);
  }

  public void startElement(String nsURI, String localName, String rawName, Attributes attributes) throws SAXException {
    if (rawName.equalsIgnoreCase("record")) {
      logData = new LogData();
      logData.setId(id++);
    } else if (rawName.equalsIgnoreCase("date")) {
      phase = Phase.DATE;
    } else if (rawName.equalsIgnoreCase("millis")) {
      phase = Phase.MILIS;
    } else if (rawName.equalsIgnoreCase("sequence")) {
      phase = Phase.SEQUENCE;
    } else if (rawName.equalsIgnoreCase("logger")) {
      phase = Phase.LOGGER;
    } else if (rawName.equalsIgnoreCase("level")) {
      phase = Phase.LEVEL;
    } else if (rawName.equalsIgnoreCase("class")) {
      phase = Phase.CLASS;
    } else if (rawName.equalsIgnoreCase("method")) {
      phase = Phase.METHOD;
    } else if (rawName.equalsIgnoreCase("thread")) {
      phase = Phase.THREAD;
    } else if (rawName.equalsIgnoreCase("message")) {
      phase = Phase.MESSAGE;
    }
  }

  public void characters(char[] ch, int start, int length) {
    buffer.append(ch, start, length);
    // String s = new String(ch, start, length);
    // switch (phase) {
    // case RECORD:
    // break;
    // case DATE:
    // break;
    // case MILIS:
    // logData.setDate(new Date(Long.parseLong(s)));
    // break;
    // case SEQUENCE:
    // System.out.println("Sequence: " + s);
    // break;
    // case LOGGER:
    // logData.setLoggerName(s);
    // break;
    // case LEVEL:
    // System.out.println("Paring :" + s);
    // if (s.equalsIgnoreCase("INF")){
    // System.out.println("BLAD!");
    // }try {
    // logData.setLevel(Level.parse(s));
    // } catch (IllegalArgumentException e) {
    //
    // }
    // break;
    // case CLASS:
    // logData.setClazz(s);
    // break;
    // case METHOD:
    // //When parsing <init> method, it can be called 3 times [<, init, >]
    // if (logData.getMethod() != null){
    // logData.setMethod(logData.getMethod() + s);
    // } else {
    // logData.setMethod(s);
    // }
    // break;
    // case THREAD:
    // logData.setThread(s);
    // break;
    // case MESSAGE:
    // if (logData.getMessage() != null) {
    // logData.setMessage(logData.getMessage() + s);
    // } else {
    // logData.setMessage(s);
    // }
    // break;
    //
    // default:
    // break;
    // }

  }

  @Override
  public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
    if (systemId.indexOf("logger.dtd") >= 0) {
      InputSource in = new InputSource(this.getClass().getClassLoader().getResourceAsStream("logger.dtd"));
      return in;
    }
    return super.resolveEntity(publicId, systemId);
  }
}
