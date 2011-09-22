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
package pl.otros.logview.pluginable;

import pl.otros.logview.filter.LogFilter;
import pl.otros.logview.gui.markers.AutomaticMarker;
import pl.otros.logview.gui.message.MessageColorizer;
import pl.otros.logview.gui.message.MessageFormatter;
import pl.otros.logview.importer.LogImporter;

public class AllPluginables {

  private PluginableElementsContainer<LogImporter> logImportersContainer;
  private PluginableElementsContainer<AutomaticMarker> markersContainer;
  private PluginableElementsContainer<MessageColorizer> messageColorizers;
  private PluginableElementsContainer<MessageFormatter> messageFormatters;
  private PluginableElementsContainer<LogFilter> logFiltersContainer;
  private PluginableElementsContainer<PluginableElement> allPluginables;

  private static AllPluginables instance = new AllPluginables();

  public static AllPluginables getInstance() {
    return instance;
  }

  public AllPluginables() {
    markersContainer = new PluginableElementsContainer<AutomaticMarker>();
    messageColorizers = new PluginableElementsContainer<MessageColorizer>();
    messageFormatters = new PluginableElementsContainer<MessageFormatter>();
    logFiltersContainer = new PluginableElementsContainer<LogFilter>();
    logFiltersContainer = new PluginableElementsContainer<LogFilter>();
    logImportersContainer = new PluginableElementsContainer<LogImporter>();
    allPluginables = new PluginableElementsContainer<PluginableElement>();
  }

  public PluginableElementsContainer<LogImporter> getLogImportersContainer() {
    return logImportersContainer;
  }

  public PluginableElementsContainer<LogFilter> getLogFiltersContainer() {
    return logFiltersContainer;
  }

  public PluginableElementsContainer<AutomaticMarker> getMarkersContainser() {
    return markersContainer;
  }

  public PluginableElementsContainer<PluginableElement> getAllPluginables() {
    return allPluginables;
  }

  public PluginableElementsContainer<MessageColorizer> getMessageColorizers() {
    return messageColorizers;
  }

  public PluginableElementsContainer<MessageFormatter> getMessageFormatters() {
    return messageFormatters;
  }

}
