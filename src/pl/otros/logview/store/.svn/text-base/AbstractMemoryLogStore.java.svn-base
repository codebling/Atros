package pl.otros.logview.store;

import java.util.TreeMap;

import pl.otros.logview.LogData;
import pl.otros.logview.MarkerColors;
import pl.otros.logview.Note;
import pl.otros.logview.gui.note.NotableTableModel;
import pl.otros.logview.gui.note.NotableTableModelImpl;

public abstract class AbstractMemoryLogStore implements LogDataStore {

  protected NotableTableModel notable;
  protected int limit = 2000000;

  public AbstractMemoryLogStore() {
    notable = new NotableTableModelImpl();
  }

  @Override
  public boolean isMarked(int row) {
    return getLogData(row).isMarked();
  }

  @Override
  public MarkerColors getMarkerColors(int row) {
    return getLogData(row).getMarkerColors();
  }

  @Override
  public void markRows(MarkerColors markerColor, int... rows) {
    for (int i = 0; i < rows.length; i++) {
      getLogData(rows[i]).setMarked(true);
      getLogData(rows[i]).setMarkerColors(markerColor);
    }
  }

  @Override
  public void unmarkRows(int... rows) {
    for (int i = 0; i < rows.length; i++) {
      getLogData(rows[i]).setMarked(false);
      getLogData(rows[i]).setMarkerColors(null);
    }
  }

  @Override
  public int getLimit() {
    return limit;
  }

  @Override
  public void setLimit(int limit) {
    this.limit = limit;
    ensureLimit();
  }

  protected void ensureLimit() {
    if (limit <= getCount()) {
      int[] toDelete = new int[getCount() - limit];
      for (int i = 0; i < toDelete.length; i++) {
        toDelete[i] = i;
      }
      // TODO notify when some rows have to be removed!
      remove(toDelete);
    }
  }

  public void addNoteToRow(int row, Note note) {
    LogData logData = getLogData(row);
    logData.setNote(note);
    notable.addNoteToRow(logData.getId(), note);
  }

  public Note getNote(int row) {
    return getLogData(row).getNote();
  }

  public Note removeNote(int row) {
    getLogData(row).setNote(null);
    return notable.removeNote(row);
  }

  public void removeNote(int row, boolean notify) {
    getLogData(row).setNote(null);
    notable.removeNote(row, notify);
  }

  public void clearNotes() {
    int count = getCount();
    for (int i = 0; i < count; i++) {
      getLogData(i).setNote(null);
    }
    notable.clearNotes();
  }

  public TreeMap<Integer, Note> getAllNotes() {
    TreeMap<Integer, Note> result = new TreeMap<Integer, Note>();
    int count = getCount();
    for (int i = 0; i < count; i++) {
      Note note = getNote(i);
      if (note != null && note.getNote().length() > 0) {
        result.put(Integer.valueOf(i), note);
      }
    }
    return result;
  }

}