package pl.otros.logview.store;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import pl.otros.logview.LogData;
import pl.otros.logview.MarkerColors;
import pl.otros.logview.Note;
import pl.otros.logview.store.file.FileLogDataStore;

import com.google.common.collect.MapMaker;

public class CachedLogStore implements LogDataStore {

  protected FileLogDataStore fileLogDataStore;
  protected ConcurrentMap<Integer, LogData> cache;

  public CachedLogStore(FileLogDataStore fileLogDataStore) {
    super();
    this.fileLogDataStore = fileLogDataStore;
    int initialCapicity = 100;

    cache = new MapMaker().softValues().softKeys().initialCapacity(initialCapicity).expireAfterAccess(30, TimeUnit.MINUTES).makeMap();
  }

  @Override
  public int getCount() {
    return fileLogDataStore.getCount();
  }

  @Override
  public void add(LogData... logDatas) {
    for (LogData logData : logDatas) {
      cache.put(Integer.valueOf(logData.getId()), logData);
    }

    fileLogDataStore.add(logDatas);
  }

  @Override
  public void remove(int... rows) {
    for (int row : rows) {
      cache.remove(fileLogDataStore.getLogDataIdInRow(row));
    }
    fileLogDataStore.remove(rows);

  }

  @Override
  public LogData getLogData(int row) {
    Integer logDataIdInRow = fileLogDataStore.getLogDataIdInRow(row);
    LogData logData = cache.get(logDataIdInRow);
    if (logData != null) {
      return logData;
    }
    return fileLogDataStore.getLogData(row);
  }

  @Override
  public LogData[] getLogData() {
    return fileLogDataStore.getLogData();
  }

  @Override
  public Integer getLogDataIdInRow(int row) {
    return fileLogDataStore.getLogDataIdInRow(row);
  }

  @Override
  public int clear() {
    cache.clear();
    return fileLogDataStore.clear();
  }

  @Override
  public Iterator<LogData> iterator() {
    return fileLogDataStore.iterator();
  }

  public boolean isMarked(int row) {
    return fileLogDataStore.isMarked(row);
  }

  public MarkerColors getMarkerColors(int row) {
    return fileLogDataStore.getMarkerColors(row);
  }

  public void markRows(MarkerColors markerColor, int... rows) {
    fileLogDataStore.markRows(markerColor, rows);
  }

  public void unmarkRows(int... rows) {
    fileLogDataStore.unmarkRows(rows);
  }

  public int getLimit() {
    return fileLogDataStore.getLimit();
  }

  public void setLimit(int limit) {
    fileLogDataStore.setLimit(limit);
  }

  public void addNoteToRow(int row, Note note) {
    fileLogDataStore.addNoteToRow(row, note);
  }

  public Note getNote(int row) {
    return fileLogDataStore.getNote(row);
  }

  public Note removeNote(int row) {
    return fileLogDataStore.removeNote(row);
  }

  public void clearNotes() {
    fileLogDataStore.clearNotes();
  }

  public TreeMap<Integer, Note> getAllNotes() {
    return fileLogDataStore.getAllNotes();
  }

}
