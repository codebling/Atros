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
package pl.otros.logview.batch;

import pl.otros.logview.LogData;
import pl.otros.logview.LogDataCollector;

public class StreamProcessingLogDataCollector implements LogDataCollector {

  private LogDataParsedListener logDataParsedListener;
  private BatchProcessingContext batchProcessingContext;

  public StreamProcessingLogDataCollector(LogDataParsedListener logDataParsedListener, BatchProcessingContext batchProcessingContext) {
    super();
    this.logDataParsedListener = logDataParsedListener;
    this.batchProcessingContext = batchProcessingContext;
  }

  @Override
  public void add(LogData... logDatas) {
    for (int i = 0; i < logDatas.length; i++) {
      logDataParsedListener.logDataParsed(logDatas[i], batchProcessingContext);
    }
  }

  @Override
  public LogData[] getLogData() {
    return new LogData[0];
  }

  @Override
  public int clear() {
    return 0;
  }

}
