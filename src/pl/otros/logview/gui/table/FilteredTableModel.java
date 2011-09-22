package pl.otros.logview.gui.table;

import java.util.HashSet;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import pl.otros.logview.LogData;
import pl.otros.logview.filter.LogFilter;
import pl.otros.logview.gui.LogDataTableModel;

public class FilteredTableModel extends AbstractTableModel {

  private LogDataTableModel logDataTableModel;
  private Set<LogFilter> appliedFilters;
  private int[] modelToView;
  private int[] viewToModel;

  public FilteredTableModel(LogDataTableModel logDataTableModel) {
    super();
    this.logDataTableModel = logDataTableModel;
    appliedFilters = new HashSet<LogFilter>();
    logDataTableModel.addTableModelListener(new TableModelListener() {

      @Override
      public void tableChanged(TableModelEvent e) {
        doFiltering();
      }
    });
    doFiltering();
  }

  protected void doFiltering() {
    int filterRowIndex = 0;
    int rowCount = logDataTableModel.getRowCount();
    modelToView = new int[rowCount];
    viewToModel = new int[rowCount];
    for (int row = 0; row < rowCount; row++) {
      boolean accept = true;
      LogData logData = logDataTableModel.getLogData(row);
      for (LogFilter logFilter : appliedFilters) {
        accept = accept && logFilter.accept(logData, row);
      }
      System.out.printf("FilteredTableModel.doFiltering() acceppt row %d: %s\n", row, accept);
      if (accept) {
        modelToView[row] = filterRowIndex;
        viewToModel[filterRowIndex] = row;
        filterRowIndex++;
      } else {
        modelToView[row] = -1;
      }
    }
    int[] newViewToModel = new int[filterRowIndex];
    System.arraycopy(viewToModel, 0, newViewToModel, 0, filterRowIndex);
    viewToModel = newViewToModel;
  }

  @Override
  public int getRowCount() {
    return viewToModel.length;
  }

  @Override
  public int getColumnCount() {
    return logDataTableModel.getColumnCount();
  }

  public int convertModelToView(int row) {
    return modelToView[row];
  }

  public int convertViewToModel(int row) {
    return viewToModel[row];
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    return logDataTableModel.getValueAt(convertViewToModel(rowIndex), columnIndex);
  }

  public void addFilter(LogFilter filter) {
    appliedFilters.add(filter);
    filterChanged();
  }

  public void removeFilter(LogFilter filter) {
    appliedFilters.remove(filter);
    filterChanged();
  }

  public void filterChanged() {
    doFiltering();
    fireTableDataChanged();
  }

  public String getColumnName(int columnIndex) {
    return logDataTableModel.getColumnName(columnIndex);
  }

  public Class<?> getColumnClass(int columnIndex) {
    return logDataTableModel.getColumnClass(columnIndex);
  }

  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return logDataTableModel.isCellEditable(rowIndex, columnIndex);
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    logDataTableModel.setValueAt(aValue, rowIndex, columnIndex);
  }

  public void addTableModelListener(TableModelListener l) {
    logDataTableModel.addTableModelListener(l);
  }

  public void removeTableModelListener(TableModelListener l) {
    logDataTableModel.removeTableModelListener(l);
  }

}
