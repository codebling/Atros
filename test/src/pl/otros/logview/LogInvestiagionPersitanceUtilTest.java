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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

import pl.otros.logview.gui.LogDataTableModel.Memento;

public class LogInvestiagionPersitanceUtilTest {

  public Memento getMemento() {
    Memento m = new Memento();
    m.setAddIndex(0);
    m.setShift(0);
    m.setName("superlog.txt");
    for (int i = 0; i < 30; i++) {
      m.getList().add(TestUtils.generateLogData());
      m.getNotes().put(i, new Note("Note " + i));
      m.getMarks().put(i, i % 2 == 0 ? true : false);
    }
    return m;
  }

  @Test
  public void testLoadMemento() throws Exception {
    Memento source = getMemento();
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    LogInvestiagionPersitanceUtil.saveMemento(source, bout);
    ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
    Memento restored = LogInvestiagionPersitanceUtil.loadMemento(bin);

    Assert.assertEquals(source.getName(), restored.getName());
    Assert.assertEquals(source.getAddIndex(), restored.getAddIndex());

    Assert.assertEquals(source.getShift(), restored.getShift());
    TreeMap<Integer, Boolean> sourceMarks = source.getMarks();
    for (Integer indx : sourceMarks.keySet()) {
      Assert.assertEquals(sourceMarks.get(indx), restored.getMarks().get(indx));
    }
    TreeMap<Integer, Note> sourceNotes = source.getNotes();
    for (Integer indx : sourceNotes.keySet()) {
      Assert.assertEquals(sourceNotes.get(indx).getNote(), restored.getNotes().get(indx).getNote());
    }
  }

}
