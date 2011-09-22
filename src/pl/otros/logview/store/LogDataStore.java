package pl.otros.logview.store;

import java.util.TreeMap;

import pl.otros.logview.LogData;
import pl.otros.logview.LogDataCollector;
import pl.otros.logview.Note;
import pl.otros.logview.gui.MarkableTableModel;

public interface LogDataStore extends MarkableTableModel, Iterable<LogData>, LogDataCollector {

  public int getCount();

  public void remove(int... ids);

  public LogData getLogData(int row);

  public LogData[] getLogData();

  public Integer getLogDataIdInRow(int row);

  public int getLimit();

  public void setLimit(int limit);

  public int clear();

  public void addNoteToRow(int row, Note note);

  public Note getNote(int row);

  public Note removeNote(int row);

  public void clearNotes();

  public TreeMap<Integer, Note> getAllNotes();

}
