package pl.otros.logview.store.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import pl.otros.logview.LogData;
import pl.otros.logview.MarkerColors;
import pl.otros.logview.Note;
import pl.otros.logview.gui.note.NotableTableModel;
import pl.otros.logview.gui.note.NotableTableModelImpl;
import pl.otros.logview.store.AbstractMemoryLogStore;
import pl.otros.logview.store.LogDataStore;

public class FileLogDataStore extends AbstractMemoryLogStore implements LogDataStore {

  private static final Logger LOGGER = Logger.getLogger(FileLogDataStore.class.getName());
  private static final int INITIAL_MAPPING_SIZE = 10000;
  private HashMap<Integer, Long> storeIdFilePositionMapping;
  private RandomAccessFile randomAccessFile;
  private ArrayList<Integer> logDatasId;
  protected TreeMap<Integer, Boolean> marks;
  protected TreeMap<Integer, MarkerColors> marksColor;
  protected NotableTableModel notable;

  public FileLogDataStore() throws FileNotFoundException, IOException {
    init();
    marks = new TreeMap<Integer, Boolean>();
    marksColor = new TreeMap<Integer, MarkerColors>();
    notable = new NotableTableModelImpl();
  }

  protected void init() throws IOException {
    File createTempFile = File.createTempFile("OLV_", "_");
    createTempFile.deleteOnExit();
    randomAccessFile = new RandomAccessFile(createTempFile, "rw");
    storeIdFilePositionMapping = new HashMap<Integer, Long>(INITIAL_MAPPING_SIZE);
    logDatasId = new ArrayList<Integer>(INITIAL_MAPPING_SIZE);
  }

  @Override
  public Iterator<LogData> iterator() {
    return new LogDataIterator(logDatasId.iterator());
  }

  @Override
  public int getCount() {
    return logDatasId.size();
  }

  @Override
  public void add(LogData... logDatas) {
    ObjectOutputStream oout = null;
    ByteArrayOutputStream byteArrayOutputStream = null;
    try {
      HashMap<Integer, Long> newLogDataPosition = new HashMap<Integer, Long>(logDatas.length);
      long length = randomAccessFile.length();
      LOGGER.finest(String.format("Setting position in file %s to %d", randomAccessFile.getFD().toString(), length));
      randomAccessFile.seek(length);
      for (int i = 0; i < logDatas.length; i++) {
        byteArrayOutputStream = new ByteArrayOutputStream();
        oout = new ObjectOutputStream(byteArrayOutputStream);
        int logDataId = logDatas[i].getId();
        long positionInFile = randomAccessFile.length();

        oout.writeObject(logDatas[i]);
        oout.flush();

        randomAccessFile.writeInt(byteArrayOutputStream.size());
        randomAccessFile.write(byteArrayOutputStream.toByteArray());
        newLogDataPosition.put(Integer.valueOf(logDataId), Long.valueOf(positionInFile));
      }
      storeIdFilePositionMapping.putAll(newLogDataPosition);

      for (int i = 0; i < logDatas.length; i++) {
        logDatasId.add(Integer.valueOf(logDatas[i].getId()));
      }

      ensureLimit();
    } catch (IOException e) {
      LOGGER.severe(String.format("Error adding %d events: %s", logDatas.length, e.getMessage()));
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(oout);
    }

  }

  @Override
  public void remove(int... rows) {
    LOGGER.fine(String.format("Removing %d rows, first sorting by id", rows.length));
    Arrays.sort(rows);
    LOGGER.finest("Rows sorted, removing from end");
    for (int i = rows.length - 1; i >= 0; i--) {
      Integer removeId = logDatasId.remove(rows[i]);
      notable.removeNote(removeId, false);
      marks.remove(removeId);
      storeIdFilePositionMapping.remove(removeId);
    }
    LOGGER.finest(String.format("%d rows where removed ", rows.length));

  }

  @Override
  public LogData getLogData(int row) {
    Integer logDataId = logDatasId.get(row);
    try {
      return getLogDataById(logDataId);
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.severe(String.format("Can't load data for row %d: %s", row, e.getMessage()));
    }
    return null;
  }

  @Override
  public Integer getLogDataIdInRow(int row) {
    return logDatasId.get(row);
  }

  private LogData getLogDataById(Integer logDataId) throws IOException, ClassNotFoundException {
    Long eventPositionInStream = storeIdFilePositionMapping.get(logDataId);
    long position = eventPositionInStream.longValue();
    randomAccessFile.seek(position);
    int size = randomAccessFile.readInt();
    byte[] buff = new byte[size];
    randomAccessFile.readFully(buff);
    ByteArrayInputStream bin = new ByteArrayInputStream(buff);
    ObjectInputStream objectInputStreams = new ObjectInputStream(bin);
    LogData readObject = (LogData) objectInputStreams.readObject();
    return readObject;
  }

  @Override
  public LogData[] getLogData() {
    ArrayList<LogData> list = new ArrayList<LogData>(getCount());
    for (LogData ld : this) {
      list.add(ld);
    }
    return list.toArray(new LogData[0]);
  }

  @Override
  public int clear() {
    int size = logDatasId.size();
    RandomAccessFile old = randomAccessFile;
    storeIdFilePositionMapping.clear();
    logDatasId.clear();
    try {
      init();
    } catch (IOException e) {
      LOGGER.info("Can't initialize new log file after clear: " + e.getMessage());
      return 0;
    }
    try {
      old.close();
    } catch (IOException e) {
      LOGGER.warning("Can't close temporary file: " + e.getMessage());
      e.printStackTrace();
    }
    return size;
  }

  private class LogDataIterator implements Iterator<LogData> {

    private Iterator<Integer> idsIterator;

    public LogDataIterator(Iterator<Integer> idsIterator) {
      this.idsIterator = idsIterator;
    }

    @Override
    public boolean hasNext() {
      return idsIterator.hasNext();
    }

    @Override
    public LogData next() {
      Integer logId = idsIterator.next();
      try {
        return getLogDataById(logId);
      } catch (Exception e) {
        // TODO
        e.printStackTrace();
        throw new RuntimeException("Can't get next data: " + e.getMessage(), e);
      }
    }

    @Override
    public void remove() {
      idsIterator.remove();
    }

  }

  public void addNoteToRow(int row, Note note) {
    notable.addNoteToRow(row, note);
  }

  public Note getNote(int row) {
    return notable.getNote(row);
  }

  public Note removeNote(int row) {
    return notable.removeNote(row);
  }

  public void removeNote(int row, boolean notify) {
    notable.removeNote(row, notify);
  }

  public void clearNotes() {
    notable.clearNotes();
  }

  public TreeMap<Integer, Note> getAllNotes() {
    return notable.getAllNotes();
  }

}
