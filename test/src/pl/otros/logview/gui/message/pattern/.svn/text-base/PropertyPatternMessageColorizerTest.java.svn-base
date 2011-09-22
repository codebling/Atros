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
package pl.otros.logview.gui.message.pattern;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Before;
import org.junit.Test;

public class PropertyPatternMessageColorizerTest {

  Properties p = new Properties();
  PropertyPatternMessageColorizer colorizer;

  @Before
  public void setUp() throws ConfigurationException, IOException {
    p.put(PropertyPatternMessageColorizer.PROP_NAME, "Test");
    p.put(PropertyPatternMessageColorizer.PROP_DESCRIPTION, "D");
    p.put(PropertyPatternMessageColorizer.PROP_PATTERN, "a(\\d+\\(a\\))a");
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    p.store(bout, "");
    ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());

    colorizer = new PropertyPatternMessageColorizer();
    colorizer.init(bin);
  }

  @Test
  public void testColorizingNeeded() {
    assertTrue(colorizer.colorizingNeeded("a3(a)a"));
  }

  @Test
  public void testGetName() {
    assertEquals("Test", colorizer.getName());
  }

  @Test
  public void testGetDescription() {
    assertEquals("D", colorizer.getDescription());
  }

  @Test
  public void testCountGroups() {
    assertEquals(0, colorizer.countGroups(Pattern.compile("")));
    assertEquals(1, colorizer.countGroups(Pattern.compile("a(a)")));
    assertEquals(3, colorizer.countGroups(Pattern.compile("(a(a)(b))")));
    assertEquals(0, colorizer.countGroups(Pattern.compile("\\(asdf")));
    assertEquals(1, colorizer.countGroups(Pattern.compile("aaa\\(ffds(a)fsfd\\)\\(\\)")));
  }

}
