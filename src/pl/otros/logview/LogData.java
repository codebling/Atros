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

    return sb.toString();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    result = prime * result + id;
    result = prime * result + ((level == null) ? 0 : level.hashCode());
    result = prime * result + ((loggerName == null) ? 0 : loggerName.hashCode());
    result = prime * result + ((message == null) ? 0 : message.hashCode());
    result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
    result = prime * result + ((method == null) ? 0 : method.hashCode());
    result = prime * result + ((thread == null) ? 0 : thread.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof LogData))
      return false;
    LogData other = (LogData) obj;
    if (clazz == null) {
      if (other.clazz != null)
        return false;
    } else if (!clazz.equals(other.clazz))
      return false;
    if (date == null) {
      if (other.date != null)
        return false;
    } else if (!date.equals(other.date))
      return false;
    if (id != other.id)
      return false;
    if (level == null) {
      if (other.level != null)
        return false;
    } else if (!level.equals(other.level))
      return false;
    if (loggerName == null) {
      if (other.loggerName != null)
        return false;
    } else if (!loggerName.equals(other.loggerName))
      return false;
    if (message == null) {
      if (other.message != null)
        return false;
    } else if (!message.equals(other.message))
      return false;
    if (messageId == null) {
      if (other.messageId != null)
        return false;
    } else if (!messageId.equals(other.messageId))
      return false;
    if (method == null) {
      if (other.method != null)
        return false;
    } else if (!method.equals(other.method))
      return false;
    if (thread == null) {
      if (other.thread != null)
        return false;
    } else if (!thread.equals(other.thread))
      return false;
    return true;
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
