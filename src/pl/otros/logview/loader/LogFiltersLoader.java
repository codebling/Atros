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

import java.util.ArrayList;
import java.util.Collection;

import pl.otros.logview.filter.ClassFilter;
import pl.otros.logview.filter.LevelFilter;
import pl.otros.logview.filter.LogFilter;
import pl.otros.logview.filter.MarkNoteFilter;
import pl.otros.logview.filter.PropertyFilter;
import pl.otros.logview.filter.RegexFilter;
import pl.otros.logview.filter.StringContainsFilter;
import pl.otros.logview.filter.ThreadFilter;
import pl.otros.logview.filter.TimeFilter;

public class LogFiltersLoader {

  public Collection<LogFilter> loadInternalFilters() {
    ArrayList<LogFilter> list = new ArrayList<LogFilter>();
    list.add(new StringContainsFilter());
    list.add(new RegexFilter());
    list.add(new LevelFilter());
    list.add(new TimeFilter());
    list.add(new MarkNoteFilter());
    list.add(new ThreadFilter());
    list.add(new ClassFilter());
    list.add(new PropertyFilter());
    return list;

  }

}
