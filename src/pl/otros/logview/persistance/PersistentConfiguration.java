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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.File;
import java.util.logging.Logger;

public class PersistentConfiguration extends DataConfiguration {
  private static final Logger LOGGER = Logger.getLogger(PersistentConfiguration.class.getName());
  private static final PersistentConfiguration instance = new PersistentConfiguration("config.xml");

  private PersistentConfiguration(String file) {
    super(getXMLConfiguration(file));
  }

  private static XMLConfiguration getXMLConfiguration(String file) {
    XMLConfiguration xmlConfiguration = new XMLConfiguration();
    try {
      xmlConfiguration = new XMLConfiguration();
      xmlConfiguration.setFile(new File(file));
      xmlConfiguration.load();
      xmlConfiguration.setAutoSave(true);
    } catch (ConfigurationException e) {
      LOGGER.severe("Can't load configuration, creating new " + e.getMessage());

      try {
        xmlConfiguration.save();
        xmlConfiguration.setAutoSave(true);
      } catch (ConfigurationException e1) {
        LOGGER.severe("Can't create persistent configuration: " + e1.getMessage());
      }
    }
    return xmlConfiguration;
  }
  
  public static PersistentConfiguration getInstance() {
    return instance;
  }
  
}
