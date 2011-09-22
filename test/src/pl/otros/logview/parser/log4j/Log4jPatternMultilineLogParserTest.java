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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import pl.otros.logview.LogData;
import pl.otros.logview.importer.InitializationException;
import pl.otros.logview.importer.LogImporterUsingParser;
import pl.otros.logview.parser.ParsingContext;
import pl.otros.logview.reader.ProxyLogDataCollector;

public class Log4jPatternMultilineLogParserTest {

  @Test
  public void testCustomLevel() throws IOException, InitializationException {
    Properties p = new Properties();
    p.put("type", "log4j");
    p.put("pattern", "TIMESTAMP LEVEL [THREAD]  MESSAGE");
    p.put("dateFormat", "yyyy-MM-dd HH:mm:ss,SSS");
    p.put("customLevels", "L1=TRACE,L2=DEBUG,L3=INFO,L4=WARN,L5=ERROR");
    Log4jPatternMultilineLogParser logParser = new Log4jPatternMultilineLogParser();
    InputStream in = loadLog("log4j/log4j_cusom_level.txt");
    LogImporterUsingParser importerUsingParser = new LogImporterUsingParser(logParser);
    importerUsingParser.init(p);

    // when
    ParsingContext context = new ParsingContext();
    ProxyLogDataCollector dataCollector = new ProxyLogDataCollector();
    importerUsingParser.importLogs(in, dataCollector, context);

    // then
    LogData[] logDatas = dataCollector.getLogData();
    assertEquals(10, logDatas.length);
    assertEquals(Level.FINEST, logDatas[0].getLevel());
    assertEquals(Level.FINE, logDatas[1].getLevel());
    assertEquals(Level.INFO, logDatas[2].getLevel());
    assertEquals(Level.WARNING, logDatas[3].getLevel());
    assertEquals(Level.SEVERE, logDatas[4].getLevel());
    assertEquals(Level.FINEST, logDatas[5].getLevel());
    assertEquals(Level.FINE, logDatas[6].getLevel());
    assertEquals(Level.INFO, logDatas[7].getLevel());
    assertEquals(Level.WARNING, logDatas[8].getLevel());
    assertEquals(Level.SEVERE, logDatas[9].getLevel());

  }

  @Test
  public void testDefaultCharset() throws InitializationException {
    Properties p = new Properties();
    p.put("type", "log4j");
    p.put("pattern", "TIMESTAMP LEVEL [THREAD]  MESSAGE");
    p.put("dateFormat", "yyyy-MM-dd HH:mm:ss,SSS");
    // p.put("name", "windows-1250");
    Log4jPatternMultilineLogParser logParser = new Log4jPatternMultilineLogParser();
    logParser.init(p);

    assertEquals("UTF-8", logParser.getParserDescription().getCharset());
  }

  @Test
  public void testCustomCharset() throws Throwable {
    // given
    Properties p = new Properties();
    p.put("type", "log4j");
    p.put("pattern", "TIMESTAMP LEVEL [THREAD]  MESSAGE");
    p.put("dateFormat", "yyyy-MM-dd HH:mm:ss,SSS");
    p.put("name", "windows-1250");
    p.put("charset", "windows-1250");
    InputStream in = loadLog("log4j/log4j_pl.txt");
    Log4jPatternMultilineLogParser logParser = new Log4jPatternMultilineLogParser();
    LogImporterUsingParser importerUsingParser = new LogImporterUsingParser(logParser);
    importerUsingParser.init(p);

    // when
    ParsingContext context = new ParsingContext();
    ProxyLogDataCollector dataCollector = new ProxyLogDataCollector();
    importerUsingParser.importLogs(in, dataCollector, context);

    // then

    assertEquals("windows-1250", logParser.getParserDescription().getCharset());
    LogData[] logDatas = dataCollector.getLogData();
    assertEquals(6, logDatas.length);
    assertEquals('a', logDatas[2].getMessage().toCharArray()[0]);
    assertEquals(261, logDatas[2].getMessage().toCharArray()[1]);

    assertEquals('e', logDatas[3].getMessage().toCharArray()[0]);
    assertEquals(281, logDatas[3].getMessage().toCharArray()[1]);

    assertEquals('z', logDatas[4].getMessage().toCharArray()[0]);
    assertEquals(380, logDatas[4].getMessage().toCharArray()[1]);

    assertEquals('l', logDatas[5].getMessage().toCharArray()[0]);
    assertEquals(322, logDatas[5].getMessage().toCharArray()[1]);
  }

  private InputStream loadLog(String resource) throws IOException {
    return new ByteArrayInputStream(IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream(resource)));
  }

}
