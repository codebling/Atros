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
package pl.otros.logview.importer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Vector;

import javax.swing.Icon;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.XMLDecoder;

import pl.otros.logview.LogData;
import pl.otros.logview.LogDataCollector;
import pl.otros.logview.parser.ParsingContext;
import pl.otros.logview.parser.log4j.Log4jUtil;

public class Log4jXmlLogImporter extends AbstractPluginableElement implements LogImporter {

  public Log4jXmlLogImporter() {
    super("Log4j xml", "Parser log4j xml");

  }

  @Override
  public void init(Properties properties) throws InitializationException {

  }

  @Override
  public void importLogs(InputStream in, LogDataCollector dataCollector, ParsingContext parsingContext) {
    try {
      XMLDecoder decoder = XMLDecoder.class.newInstance();
      BufferedReader bin = new BufferedReader(new InputStreamReader(in));
      String line = null;
      // IOUtils.toString(in);
      // decoder.decodeEvents(arg0)
      while ((line = bin.readLine()) != null) {
        Vector decodeEvents = decoder.decodeEvents(line + System.getProperty("line.separator"));
        if (decodeEvents != null) {
          for (Object object : decodeEvents) {
            LoggingEvent event = (LoggingEvent) object;
            LogData logdata = Log4jUtil.translateLog4j(event);
            logdata.setId(parsingContext.getGeneratedIdAndIncrease());
            dataCollector.add(logdata);
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getKeyStrokeAccelelator() {
    return null;
  }

  @Override
  public int getMnemonic() {
    return 0;
  }

  @Override
  public Icon getIcon() {
    return null;
  }

}
