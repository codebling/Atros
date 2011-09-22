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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import pl.otros.logview.gui.markers.AutomaticMarker;
import pl.otros.logview.gui.markers.PropertyFileAbstractMarker;
import pl.otros.logview.gui.markers.RegexMarker;
import pl.otros.logview.gui.markers.StringMarker;

public class AutomaticMarkerLoader {

  private static BaseLoader baseLoader = new BaseLoader();

  public static ArrayList<AutomaticMarker> loadInternalMarkers() throws IOException {

    ArrayList<AutomaticMarker> markers = new ArrayList<AutomaticMarker>();
    BufferedReader in = new BufferedReader(new InputStreamReader(AutomaticMarkerLoader.class.getClassLoader().getResourceAsStream("markers.txt")));
    String line = null;
    while ((line = in.readLine()) != null) {
      if (line.startsWith("#")) {
        continue;
      }
      try {
        Class<?> c = AutomaticMarkerLoader.class.getClassLoader().loadClass(line);
        AutomaticMarker am = (AutomaticMarker) c.newInstance();
        markers.add(am);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }

    }

    return markers;

  }

  public static ArrayList<AutomaticMarker> load(File dir) {
    ArrayList<AutomaticMarker> markers = new ArrayList<AutomaticMarker>();
    baseLoader.load(dir, AutomaticMarker.class);
    markers.addAll(markers);
    return markers;
  }

  public static ArrayList<AutomaticMarker> loadRegexMarkers(File dir) {
    ArrayList<AutomaticMarker> markers = new ArrayList<AutomaticMarker>();
    File[] files = dir.listFiles(new FileFilter() {

      @Override
      public boolean accept(File pathname) {
        return pathname.isFile() && pathname.getName().endsWith(".regexMarker");
      }
    });
    if (files != null) {
      for (File file : files) {
        try {
          markers.add(loadRegexMarkerFromProperties(file));
        } catch (Exception e) {
          System.err.println("Cannot initalize RegexMarker from file " + file.getName());
        }
      }
    }
    return markers;
  }

  public static ArrayList<AutomaticMarker> loadStringMarkers(File dir) {
    ArrayList<AutomaticMarker> markers = new ArrayList<AutomaticMarker>();
    File[] files = dir.listFiles(new FileFilter() {

      @Override
      public boolean accept(File pathname) {
        return pathname.isFile() && pathname.getName().endsWith(".stringMarker");
      }
    });
    if (files != null) {
      for (File file : files) {
        try {
          markers.add(loadStringMarkerFromProperties(file));
        } catch (Exception e) {
          System.err.println("Cannot initalize StringMarker from file " + file.getName());
        }
      }
    }
    return markers;
  }

  private static AutomaticMarker loadRegexMarkerFromProperties(File file) throws Exception {
    Properties p = new Properties();
    p.load(new FileInputStream(file));
    RegexMarker marker = new RegexMarker(p);
    marker.setFileName(file.getName());
    return marker;
  }

  private static AutomaticMarker loadStringMarkerFromProperties(File file) throws Exception {
    Properties p = new Properties();
    p.load(new FileInputStream(file));
    StringMarker marker = new StringMarker(p);
    marker.setFileName(file.getName());
    return marker;
  }

  public static AutomaticMarker loadPropertyBasedMarker(Properties p) throws Exception {
    String type = p.getProperty(PropertyFileAbstractMarker.TYPE, "");
    AutomaticMarker marker = null;
    if (type.equalsIgnoreCase(PropertyFileAbstractMarker.TYPE_STRING)) {
      marker = new StringMarker(p);
    } else if (type.equalsIgnoreCase(PropertyFileAbstractMarker.TYPE_REGEX)) {
      marker = new RegexMarker(p);
    }
    if (marker == null) {
      throw new Exception("Unknown type of marker: " + type);
    }
    return marker;
  }

  public static Collection<? extends AutomaticMarker> loadPatternMarker(File dir) {
    ArrayList<AutomaticMarker> markers = new ArrayList<AutomaticMarker>();
    File[] files = dir.listFiles(new FileFilter() {

      @Override
      public boolean accept(File pathname) {
        return pathname.isFile() && pathname.getName().endsWith(".marker");
      }
    });
    if (files != null) {
      for (File file : files) {
        try {
          Properties p = new Properties();
          FileInputStream fin = new FileInputStream(file);
          p.load(fin);
          markers.add(loadPropertyBasedMarker(p));
          fin.close();
        } catch (Exception e) {
          System.err.println("Cannot initalize RegexMarker from file " + file.getName());
        }
      }
    }
    return markers;
  }

}
