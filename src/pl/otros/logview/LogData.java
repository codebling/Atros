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

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;


public class LogData implements Serializable {

  private Date date = new Date();
  private Level level = Level.INFO;
  private String messageId = "";
  private String clazz = "";
  private String method = "";
  private String file = "";
  private String line = "";
  private String ndc = "";
  private String thread = "";
  private String loggerName = "";
  private String message = "";
  private int id;
  private Map<String, String> properties;
  private Note note;
  private boolean marked;
  private MarkerColors markerColors;

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Level getLevel() {
    return level;
  }

  public void setLevel(Level level) {
    this.level = level;
  }

  public String getMessageId() {
    return messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  public String getClazz() {
    return clazz;
  }

  public void setClazz(String clazz) {
    this.clazz = clazz;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public String getLine() {
    return line;
  }

  public void setLine(String line) {
    this.line = line;
  }

  public String getNDC() {
    return ndc;
  }

  public void setNDC(String ndc) {
    this.ndc = ndc;
  }

  public String getThread() {
    return thread;
  }

  public void setThread(String thread) {
    this.thread = thread;
  }

  public String getLoggerName() {
    return loggerName;
  }

  public void setLoggerName(String loggerName) {
    this.loggerName = loggerName;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass());
    sb.append(", Id: ");
    sb.append(getId());
    sb.append(", Date: ");
    sb.append(getDate());
    sb.append(", Level: ");
    sb.append(level);
    sb.append(", Message: ");
    sb.append(message);
    sb.append(", class: ");
    sb.append(clazz);
    sb.append(", method: ");
    sb.append(method);
    sb.append(", file: ");
    sb.append(file);
    sb.append(", line: ");
    sb.append(line);
    sb.append(", ndc: ");
    sb.append(ndc);

    return sb.toString();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    LogData logData = (LogData) o;

    if (id != logData.id) return false;
    if (marked != logData.marked) return false;
    if (clazz != null ? !clazz.equals(logData.clazz) : logData.clazz != null) return false;
    if (date != null ? !date.equals(logData.date) : logData.date != null) return false;
    if (file != null ? !file.equals(logData.file) : logData.file != null) return false;
    if (level != null ? !level.equals(logData.level) : logData.level != null) return false;
    if (line != null ? !line.equals(logData.line) : logData.line != null) return false;
    if (loggerName != null ? !loggerName.equals(logData.loggerName) : logData.loggerName != null) return false;
    if (markerColors != logData.markerColors) return false;
    if (message != null ? !message.equals(logData.message) : logData.message != null) return false;
    if (messageId != null ? !messageId.equals(logData.messageId) : logData.messageId != null) return false;
    if (method != null ? !method.equals(logData.method) : logData.method != null) return false;
    if (ndc != null ? !ndc.equals(logData.ndc) : logData.ndc != null) return false;
    if (note != null ? !note.equals(logData.note) : logData.note != null) return false;
    if (properties != null ? !properties.equals(logData.properties) : logData.properties != null) return false;
    if (thread != null ? !thread.equals(logData.thread) : logData.thread != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = date != null ? date.hashCode() : 0;
    result = 31 * result + (level != null ? level.hashCode() : 0);
    result = 31 * result + (messageId != null ? messageId.hashCode() : 0);
    result = 31 * result + (clazz != null ? clazz.hashCode() : 0);
    result = 31 * result + (method != null ? method.hashCode() : 0);
    result = 31 * result + (file != null ? file.hashCode() : 0);
    result = 31 * result + (line != null ? line.hashCode() : 0);
    result = 31 * result + (ndc != null ? ndc.hashCode() : 0);
    result = 31 * result + (thread != null ? thread.hashCode() : 0);
    result = 31 * result + (loggerName != null ? loggerName.hashCode() : 0);
    result = 31 * result + (message != null ? message.hashCode() : 0);
    result = 31 * result + id;
    result = 31 * result + (properties != null ? properties.hashCode() : 0);
    result = 31 * result + (note != null ? note.hashCode() : 0);
    result = 31 * result + (marked ? 1 : 0);
    result = 31 * result + (markerColors != null ? markerColors.hashCode() : 0);
    return result;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }

  public Note getNote() {
    return note;
  }

  public void setNote(Note note) {
    this.note = note;
  }

  public boolean isMarked() {
    return marked;
  }

  public void setMarked(boolean marked) {
    this.marked = marked;
  }

  public MarkerColors getMarkerColors() {
    return markerColors;
  }

  public void setMarkerColors(MarkerColors markerColors) {
    this.markerColors = markerColors;
  }

}
