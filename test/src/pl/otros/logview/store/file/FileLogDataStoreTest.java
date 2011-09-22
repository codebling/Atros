package pl.otros.logview.store.file;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import pl.otros.logview.LogData;

public class FileLogDataStoreTest {

  private LogData[] logDatas;
  private FileLogDataStore dataStore;

  @Before
  public void prepare() throws FileNotFoundException, IOException {
    dataStore = new FileLogDataStore();
    logDatas = new LogData[10];
    for (int i = 0; i < logDatas.length; i++) {
      logDatas[i] = new LogData();
      logDatas[i].setId(i);
      logDatas[i].setMessage("A: " + i);
      logDatas[i].setDate(new Date());
    }
  }

  @Test
  public void testIterator() throws IOException {
    // given
    dataStore.add(logDatas);
    Set<LogData> expected = new HashSet<LogData>();
    for (LogData ld : logDatas) {
      expected.add(ld);
    }

    // when
    ArrayList<LogData> acutal = new ArrayList<LogData>();
    for (LogData ld : dataStore) {
      acutal.add(ld);
    }

    // then
    assertArrayEquals(logDatas, acutal.toArray(new LogData[0]));

  }

  @Test
  public void testGetCount() throws IOException {
    // given
    dataStore.add(logDatas);

    // when
    int count = dataStore.getCount();

    // then
    assertEquals(logDatas.length, count);

  }

  @Test
  public void testAdd() throws IOException {
    dataStore.add(logDatas);

    // when
    int count = dataStore.getCount();

    // then
    assertEquals(logDatas.length, count);
  }

  @Test
  public void testRemove() throws IOException {
    dataStore.add(logDatas);

    // when
    dataStore.remove(1, 4, 6);

    // then
    assertEquals(logDatas.length - 3, dataStore.getCount());
    assertEquals(0, dataStore.getLogData(0).getId());
    assertEquals(2, dataStore.getLogData(1).getId());
    assertEquals(3, dataStore.getLogData(2).getId());
    assertEquals(5, dataStore.getLogData(3).getId());
    assertEquals(7, dataStore.getLogData(4).getId());
    assertEquals(8, dataStore.getLogData(5).getId());
    assertEquals(9, dataStore.getLogData(6).getId());
  }

  @Test
  public void testGetLogDataInt() throws IOException {
    dataStore.add(logDatas);

    // when

    // then
    assertEquals(logDatas.length, dataStore.getCount());
    assertEquals(0, dataStore.getLogData(0).getId());
    assertEquals(1, dataStore.getLogData(1).getId());
    assertEquals(2, dataStore.getLogData(2).getId());
    assertEquals(3, dataStore.getLogData(3).getId());
    assertEquals(4, dataStore.getLogData(4).getId());
    assertEquals(5, dataStore.getLogData(5).getId());
    assertEquals(6, dataStore.getLogData(6).getId());
  }

  @Test
  public void testGetLogData() throws IOException {

    dataStore.add(logDatas);

    // when
    LogData[] logData = dataStore.getLogData();
    // then
    assertEquals(logDatas.length, logData.length);
    for (int i = 0; i < logData.length; i++) {
      assertEquals(logDatas[i], logData[i]);

    }
  }

  @Test
  public void testSetLimit() {
    dataStore.setLimit(100);
    assertEquals(100, dataStore.getLimit());
    assertEquals(0, dataStore.getCount());

    dataStore.add(logDatas);

    assertEquals(logDatas.length, dataStore.getCount());
    dataStore.setLimit(5);
    assertEquals(5, dataStore.getCount());

    LogData ld = new LogData();
    ld.setId(100);
    ld.setMessage("");
    ld.setDate(new Date());

    dataStore.add(ld);
    assertEquals(5, dataStore.getCount());

  }

  @Test
  public void testClear() {
    // given
    dataStore.add(logDatas);

    assertEquals(logDatas.length, dataStore.getCount());

    // when
    dataStore.clear();

    // then
    assertEquals(0, dataStore.getCount());
    assertEquals(0, dataStore.getLogData().length);
  }

  
  

}
