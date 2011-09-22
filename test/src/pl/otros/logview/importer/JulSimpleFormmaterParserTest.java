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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.otros.logview.LogDataCollector;
import pl.otros.logview.parser.JulSimpleFormmaterParser;
import pl.otros.logview.parser.ParsingContext;
import pl.otros.logview.reader.ProxyLogDataCollector;

public class JulSimpleFormmaterParserTest {

  LogImporterUsingParser importerUsingParser;
  JulSimpleFormmaterParser parser;

  @Before
  public void init() {
    parser = new JulSimpleFormmaterParser();
    importerUsingParser = new LogImporterUsingParser(parser);
  }

  @Test
  public void testLocalesEnUs() throws IOException {
    importLogs(new Locale("en", "US"));
  }

  @Test
  public void testLocalesPlPL() throws IOException {
    importLogs(new Locale("pl", "PL"));
  }

  @Test
  public void testLocalesDeDe() throws IOException {
    importLogs(new Locale("de", "DE"));
  }

  @Test
  public void testLocalesEsEs() throws IOException {
    importLogs(new Locale("es", "ES"));
  }

  @Test
  public void testLocalesEnGb() throws IOException {
    importLogs(new Locale("en", "GB"));
  }

  private LogDataCollector importLogs(Locale locale) throws IOException {
    String s = locale.toString();
    String resName = "log_" + s + ".txt";
    InputStream in = new FileInputStream("./test/resources/" + resName);
    // this.getClass().getClassLoader().getResourceAsStream(resName);
    Assert.assertNotNull("Log input strem from " + resName, in);
    ProxyLogDataCollector collector = new ProxyLogDataCollector();
    importerUsingParser.importLogs(in, collector, new ParsingContext());
    Assert.assertEquals("Logs loaded", 10, collector.getLogData().length);
    return collector;
  }
}
