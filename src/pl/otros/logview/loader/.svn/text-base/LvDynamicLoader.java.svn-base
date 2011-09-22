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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import pl.otros.logview.filter.LogFilter;
import pl.otros.logview.gui.StatusObserver;
import pl.otros.logview.gui.markers.AutomaticMarker;
import pl.otros.logview.gui.message.MessageColorizer;
import pl.otros.logview.gui.message.MessageFormatter;
import pl.otros.logview.gui.message.SoapMessageFormatter;
import pl.otros.logview.importer.InitializationException;
import pl.otros.logview.importer.LogImporter;
import pl.otros.logview.pluginable.AllPluginables;
import pl.otros.logview.pluginable.PluginableElementsContainer;

public class LvDynamicLoader {

  private static final Logger LOGGER = Logger.getLogger(LvDynamicLoader.class.getName());
  private static LvDynamicLoader instance = null;
  private LogImportersLoader logImportersLoader;
  private LogFiltersLoader logFiltersLoader;
  private MessageColorizerLoader messageColorizerLoader;
  private StatusObserver statusObserver;

  public static LvDynamicLoader getInstance() {
    if (instance == null) {
      synchronized (LvDynamicLoader.class) {
        if (instance == null) {
          instance = new LvDynamicLoader();

        }
      }
    }
    return instance;
  }

  private Collection<AutomaticMarker> automaticMarkers;
  private Collection<LogImporter> logImporters;
  private Collection<LogFilter> logFilters;
  private Collection<MessageColorizer> messageColorizers;
  private Collection<MessageFormatter> messageFormatters;
  private BaseLoader baseLoader;

  private LvDynamicLoader() {
    super();
    baseLoader = new BaseLoader();
    logImportersLoader = new LogImportersLoader();
    logFiltersLoader = new LogFiltersLoader();
    messageColorizerLoader = new MessageColorizerLoader();

    automaticMarkers = new ArrayList<AutomaticMarker>();
    logImporters = new ArrayList<LogImporter>();
    logFilters = new ArrayList<LogFilter>();
    messageColorizers = new ArrayList<MessageColorizer>();
    messageFormatters = new ArrayList<MessageFormatter>();
  }

  public void loadAll() throws IOException, InitializationException {
    updateStatus("Loading automatic markers");
    loadAutomaticMarkers();

    updateStatus("Loading log filters");
    loadLogFilters();

    updateStatus("Loading log importers");
    loadLogImporters();

    updateStatus("Loading message colorizers");
    loadMessageColorized();

    updateStatus("Loading message formatters");
    loadMessageFormatter();
  }

  private void loadMessageFormatter() {
    messageFormatters.add(new SoapMessageFormatter());
    messageFormatters.addAll(baseLoader.load(new File("./plugins/message"), MessageFormatter.class));
    PluginableElementsContainer<MessageFormatter> pluginableElementsContainer = AllPluginables.getInstance().getMessageFormatters();
    for (MessageFormatter messageFormatter : messageFormatters) {
      LOGGER.info(String.format("Loaded message formatter: %s", messageFormatter));
      pluginableElementsContainer.addElement(messageFormatter);
    }
  }

  private void loadMessageColorized() {
    File file = new File("./plugins/message/");
    messageColorizers.addAll(messageColorizerLoader.loadInternal());
    messageColorizers.addAll(messageColorizerLoader.loadFromJars(file));
    messageColorizers.addAll(messageColorizerLoader.loadFromProperies(file));
    PluginableElementsContainer<MessageColorizer> pluginableElementsContainer = AllPluginables.getInstance().getMessageColorizers();
    for (MessageColorizer messageColorizer : messageColorizers) {
      LOGGER.info(String.format("Loaded message colorizer: %s", messageColorizer));
      pluginableElementsContainer.addElement(messageColorizer);
    }

  }

  private void loadAutomaticMarkers() throws IOException {

    automaticMarkers.addAll(AutomaticMarkerLoader.loadInternalMarkers());
    File f = new File("plugins/markers");
    automaticMarkers.addAll(AutomaticMarkerLoader.load(f));
    automaticMarkers.addAll(AutomaticMarkerLoader.loadRegexMarkers(f));
    automaticMarkers.addAll(AutomaticMarkerLoader.loadStringMarkers(f));
    automaticMarkers.addAll(AutomaticMarkerLoader.loadPatternMarker(f));
    String userHome = System.getProperty("user.home");
    File f2 = new File(userHome + "/.lv/plugins/markers");
    automaticMarkers.addAll(AutomaticMarkerLoader.load(f2));
    automaticMarkers.addAll(AutomaticMarkerLoader.loadRegexMarkers(f2));
    automaticMarkers.addAll(AutomaticMarkerLoader.loadStringMarkers(f2));

    PluginableElementsContainer<AutomaticMarker> markersContainser = AllPluginables.getInstance().getMarkersContainser();
    for (AutomaticMarker marker : automaticMarkers) {
      markersContainser.addElement(marker);
    }

  }

  private void loadLogImporters() throws InitializationException {
    loadLogImportersFromUserHome();
    loadLogImportersFromPluginDirectory();
    loadLogImportsThatAreProvidedInternally();
    PluginableElementsContainer<LogImporter> logImportersContainer = AllPluginables.getInstance().getLogImportersContainer();
    for (LogImporter logImporter : logImporters) {
      logImportersContainer.addElement(logImporter);
    }
  }

  private void loadLogImportsThatAreProvidedInternally() throws InitializationException {
    logImporters.addAll(logImportersLoader.loadInternalLogImporters());
  }

  private void loadLogImportersFromPluginDirectory() {
    File f = new File("plugins/logimporters");
    logImporters.addAll(logImportersLoader.load(f));
    logImporters.addAll(logImportersLoader.loadPropertyPatternFileFromDir(f));
  }

  private void loadLogImportersFromUserHome() {
    String userHome = System.getProperty("user.home");
    File f2 = new File(userHome + "/.lv/plugins/logimporters");
    logImporters.addAll(logImportersLoader.load(f2));
    logImporters.addAll(logImportersLoader.loadPropertyPatternFileFromDir(f2));
  }

  private void loadLogFilters() {
    logFilters.addAll(logFiltersLoader.loadInternalFilters());
    logFilters.addAll(baseLoader.load(new File("plugins/filters"), LogFilter.class));
    PluginableElementsContainer<LogFilter> logFiltersContainer = AllPluginables.getInstance().getLogFiltersContainer();
    for (LogFilter logFilter : logFilters) {
      logFiltersContainer.addElement(logFilter);
    }
  }

  private void updateStatus(String status) {
    if (statusObserver != null) {
      statusObserver.updateStatus(status);
    }
  }

  public StatusObserver getStatusObserver() {
    return statusObserver;
  }

  public void setStatusObserver(StatusObserver statusObserver) {
    this.statusObserver = statusObserver;
  }

}
