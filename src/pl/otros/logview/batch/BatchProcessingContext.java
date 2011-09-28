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

import java.util.HashMap;
import java.util.List;

import org.apache.commons.vfs2.FileObject;

public class BatchProcessingContext {

  private FileObject currentFile;
  private List<FileObject> allFiles;
  private HashMap<String, Object> attributes = new HashMap<String, Object>();

  public <T> T getAttribute(String key, Class<T> clazz) {
    return (T) attributes.get(key);
  }

  public <T> T getAttribute(String key, Class<T> clazz, T defaultValue) {
    T result = null;
    if (attributes.containsKey(key)) {
      result = getAttribute(key, clazz);
    } else {
      result = defaultValue;
    }
    return result;
  }

  public void setAttribute(String key, Object value) {
    attributes.put(key, value);
  }

  public boolean containsAttribute(String key) {
    return attributes.containsKey(key);
  }

  public List<FileObject> getAllFiles() {
    return allFiles;
  }

  public void setAllFiles(List<FileObject> allFiles) {
    this.allFiles = allFiles;
  }

  public FileObject getCurrentFile() {
    return currentFile;
  }

  public void setCurrentFile(FileObject currentFile) {
    this.currentFile = currentFile;
  }

}
