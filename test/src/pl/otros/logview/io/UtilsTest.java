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
package pl.otros.logview.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UtilsTest {

  public static FileSystemManager fsManager;

  @BeforeClass
  public static void setUp() throws FileSystemException {
    fsManager = VFS.getManager();
  }

  @Before
  public void aa() {

  }

  @Test
  public void testEmptyFile() throws IOException {
    FileObject resolveFile = fsManager.resolveFile(new File("./test/resources/"), "empty");
    assertEquals(0, resolveFile.getContent().getSize());
    boolean checkIfIsGzipped = Utils.checkIfIsGzipped(resolveFile);
    assertFalse(checkIfIsGzipped);
  }

  @Test
  public void testEmptyGzipedFile() throws IOException {
    FileObject resolveFile = fsManager.resolveFile(new File("./test/resources/"), "empty.gz");
    assertEquals(26, resolveFile.getContent().getSize());
    boolean checkIfIsGzipped = Utils.checkIfIsGzipped(resolveFile);
    assertTrue(checkIfIsGzipped);
  }

  @Test
  public void testGzipedFile() throws IOException {
    FileObject resolveFile = fsManager.resolveFile(new File("./test/resources/"), "jul_log.txt.gz");
    boolean checkIfIsGzipped = Utils.checkIfIsGzipped(resolveFile);
    assertTrue(resolveFile.getName() + " should be compressed", checkIfIsGzipped);
  }

  @Test
  public void testNotGzipedFile() throws IOException {
    FileObject resolveFile = fsManager.resolveFile(new File("./test/resources/"), "jul_log.txt");
    boolean checkIfIsGzipped = Utils.checkIfIsGzipped(resolveFile);
    assertFalse(resolveFile.getName() + " should be not compressed", checkIfIsGzipped);
  }

  @Test
  public void testSmallGzipedFile() throws IOException {
    FileObject resolveFile = fsManager.resolveFile(new File("./test/resources/"), "smallFile.txt.gz");
    boolean checkIfIsGzipped = Utils.checkIfIsGzipped(resolveFile);
    assertTrue(resolveFile.getName() + " should be compressed", checkIfIsGzipped);
  }
}
