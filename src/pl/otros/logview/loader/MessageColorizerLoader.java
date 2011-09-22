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
package pl.otros.logview.loader;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import pl.otros.logview.gui.message.MessageColorizer;
import pl.otros.logview.gui.message.SearchResultColorizer;
import pl.otros.logview.gui.message.SoapMessageColorizer;
import pl.otros.logview.gui.message.StackTraceColorizer;
import pl.otros.logview.gui.message.pattern.PropertyPatternMessageColorizer;

public class MessageColorizerLoader {

  private static final Logger LOGGER = Logger.getLogger(MessageColorizerLoader.class.getName());
  private BaseLoader baseLoader = new BaseLoader();

  public ArrayList<MessageColorizer> loadInternal() {
    ArrayList<MessageColorizer> list = new ArrayList<MessageColorizer>();
    list.add(new SearchResultColorizer());
    list.add(new StackTraceColorizer());
    list.add(new SoapMessageColorizer());
    return list;

  }

  public Collection<MessageColorizer> loadFromJars(File dir) {
    return baseLoader.load(dir, MessageColorizer.class);

  }

  public ArrayList<MessageColorizer> loadFromProperies(File dir) {
    ArrayList<MessageColorizer> list = new ArrayList<MessageColorizer>();
    File[] listFiles = dir.listFiles(new FileFilter() {

      @Override
      public boolean accept(File pathname) {
        return (pathname.isFile() && pathname.getName().endsWith("pattern"));
      }

    });
    if (listFiles != null) {
      for (File file : listFiles) {
        FileInputStream in = null;
        try {
          in = new FileInputStream(file);
          PropertyPatternMessageColorizer colorizer = new PropertyPatternMessageColorizer();
          colorizer.init(in);
          colorizer.setFile(file.getAbsolutePath());
          list.add(colorizer);
        } catch (Exception e) {
          LOGGER.severe(String.format("Can't load property file based message colorizer from file %s : %s", file.getName(), e.getMessage()));
          e.printStackTrace();
        } finally {
          IOUtils.closeQuietly(in);
        }
      }
    }
    return list;

  }
}
