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
package pl.otros.logview;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;

public class VersionUtil {

  private static final String CURRENT_VERSION_PAGE_URL = "http://otroslogviewer.appspot.com/services/currentVersion?runningVersion=";

  public static String getCurrentVersion(String running) throws MalformedURLException, IOException {
    String page = IOUtils.toString(new URL(CURRENT_VERSION_PAGE_URL + running).openStream());
    ByteArrayInputStream bin = new ByteArrayInputStream(page.getBytes());
    Properties p = new Properties();
    p.load(bin);
    String result = p.getProperty("currentVersion", "?");
    return result;

  }

  public static String getRunningVersion() throws IOException {
    String result = "";
    JarFile jarFile = new JarFile("logview.jar");
    Attributes map = jarFile.getManifest().getMainAttributes();
    if (map.getValue("Implementation-Version") != null) {
      result = map.getValue("Implementation-Version");
    }
    return result;
  }

}
