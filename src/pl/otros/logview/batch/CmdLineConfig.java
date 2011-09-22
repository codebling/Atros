/*******************************************************************************
 * Copyright 2011 krzyh
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
package pl.otros.logview.batch;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class CmdLineConfig {

  @Parameter(names = { "-c" }, description = "Implementation class of LogDataParsedListener", required = true)
  public String logDataParsedListenerClass = null;

  @Parameter(names = { "-verbose" }, description = "Print debug information")
  public boolean verbose = false;

  @Parameter(names = { "-d", "-dirWithJars" }, description = "Dir with additional jars or single jar")
  public String dirWithJars = null;

  @Parameter(description = "Files or URLs")
  public List<String> files = new ArrayList<String>();

  @Parameter(names = { "-h", "-help" }, description = "Display help")
  public boolean printHelp = false;

  private JCommander jCommander;

  public CmdLineConfig() {
    jCommander = new JCommander(this);
    jCommander.setProgramName("olv-bath.[bat|sh]");

  }

  public void parserCmdLine(String[] args) throws Exception {
    jCommander.parse(args);
    if (files.size() == 0) {
      throw new Exception("Enter at least one file or url");
    }
  }

  public void printUsage() {
    jCommander.usage();
    System.out.println("Check wiki for more information: http://code.google.com/p/otroslogviewer/wiki/BatchProcessing");
  }

}
