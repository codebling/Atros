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
package pl.otros.logview.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;
import pl.otros.logview.LogData;
import pl.otros.logview.LogDataCollector;
import pl.otros.logview.Note;
import pl.otros.logview.accept.HigherIdAcceptCondition;
import pl.otros.logview.accept.LevelLowerAcceptContition;
import pl.otros.logview.accept.LowerIdAcceptCondition;
import pl.otros.logview.accept.SelectedClassAcceptCondition;
import pl.otros.logview.accept.SelectedEventsAcceptCondition;
import pl.otros.logview.accept.SelectedThreadAcceptCondition;
import pl.otros.logview.filter.ClassFilter;
import pl.otros.logview.filter.FilterPanel;
import pl.otros.logview.filter.LogFilter;
import pl.otros.logview.filter.LogFilterValueChangeListener;
import pl.otros.logview.filter.ThreadFilter;
import pl.otros.logview.filter.TimeFilter;
import pl.otros.logview.gui.actions.AutomaticMarkUnamrkActionListener;
import pl.otros.logview.gui.actions.ClearMarkingsAction;
import pl.otros.logview.gui.actions.FocusOnEventsAfter;
import pl.otros.logview.gui.actions.FocusOnEventsBefore;
import pl.otros.logview.gui.actions.FocusOnSelectedClassesAction;
import pl.otros.logview.gui.actions.FocusOnThisTrheadAction;
import pl.otros.logview.gui.actions.IgnoreSelectedEventsClasses;
import pl.otros.logview.gui.actions.MarkRowAction;
import pl.otros.logview.gui.actions.RemoveByAcceptanceCriteria;
import pl.otros.logview.gui.actions.ShowCallHierarhyAction;
import pl.otros.logview.gui.actions.TableResizeActionListener;
import pl.otros.logview.gui.actions.UnMarkRowAction;
import pl.otros.logview.gui.actions.table.MarkRowBySpaceKeyListener;
import pl.otros.logview.gui.markers.AutomaticMarker;
import pl.otros.logview.gui.message.MessageColorizer;
import pl.otros.logview.gui.message.MessageFormatter;
import pl.otros.logview.gui.note.NoteEvent;
import pl.otros.logview.gui.note.NoteEvent.EventType;
import pl.otros.logview.gui.note.NoteObserver;
import pl.otros.logview.gui.renderers.DateRenderer;
import pl.otros.logview.gui.renderers.LevelRenderer;
import pl.otros.logview.gui.renderers.LevelRenderer.Mode;
import pl.otros.logview.gui.renderers.NoteRenderer;
import pl.otros.logview.gui.renderers.NoteTableEditor;
import pl.otros.logview.gui.renderers.TableMarkDecoratorRenderer;
import pl.otros.logview.gui.table.ColumnHideMenu;
import pl.otros.logview.gui.table.ColumnHideTableModelDecorator;
import pl.otros.logview.gui.table.JTableWith2RowHighliting;
import pl.otros.logview.gui.table.TableColumns;
import pl.otros.logview.pluginable.AllPluginables;
import pl.otros.logview.pluginable.PluginableElement;
import pl.otros.logview.pluginable.PluginableElementEventListener;
import pl.otros.logview.pluginable.PluginableElementsContainer;
import pl.otros.logview.pluginable.SynchronizePluginableContainerListener;

public class LogViewPanel extends JPanel implements LogDataCollector {

  private Font menuLabelFont;
  private JPanel filtersPanel;
  private JPanel logsTablePanel;
  private JPanel logsMarkersPanel;
  private JPanel leftPanel;

  private JMenu automaticMarkersMenu;
  private JMenu automaticUnmarkersMenu;
  private LogDataTableModel dataTableModel;
  private JTextPane logDetailTextArea;
  private JTable table;
  private TableRowSorter<LogDataTableModel> sorter;
  private StatusObserver statusObserver;
  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
  private JTabbedPane jTabbedPane;
  private JTextArea notes;
  private JScrollPane scrollPane;
  private FocusOnThisTrheadAction focusOnThisTrheadAction;
  private FocusOnEventsAfter focusOnEventsAfter;
  private FocusOnEventsBefore focusOnEventsBefore;
  private FocusOnSelectedClassesAction focusOnSelectedClassesAction;
  private IgnoreSelectedEventsClasses ignoreSelectedEventsClasses;
  private ColumnHideTableModelDecorator columnHideTableModelDecorator;
  private PluginableElementsContainer<AutomaticMarker> markersContainser;
  private PluginableElementsContainer<LogFilter> logFiltersContainer;
  private PluginableElementsContainer<MessageColorizer> messageColorizersContainer;
  private PluginableElementsContainer<MessageFormatter> messageFormattersContainer;
  private PluginableElementsContainer<MessageColorizer> selectedMessageColorizersContainer;
  private PluginableElementsContainer<MessageFormatter> selectedMessageFormattersContainer;
  private JToolBar messageDetailTooblar;

  public LogViewPanel(final LogDataTableModel dataTableModel, StatusObserver statusObserver) {
    super();
    this.dataTableModel = dataTableModel;
    this.statusObserver = statusObserver;

    AllPluginables allPluginable = AllPluginables.getInstance();
    markersContainser = allPluginable.getMarkersContainser();
    markersContainser.addListener(new MarkersMenuReloader());
    logFiltersContainer = allPluginable.getLogFiltersContainer();
    messageColorizersContainer = allPluginable.getMessageColorizers();
    messageFormattersContainer = allPluginable.getMessageFormatters();
    selectedMessageColorizersContainer = new PluginableElementsContainer<MessageColorizer>();
    selectedMessageFormattersContainer = new PluginableElementsContainer<MessageFormatter>();
    for (MessageColorizer messageColorizer : messageColorizersContainer.getElements()) {
      selectedMessageColorizersContainer.addElement(messageColorizer);
    }
    for (MessageFormatter messageFormatter : messageFormattersContainer.getElements()) {
      selectedMessageFormattersContainer.addElement(messageFormatter);
    }
    messageColorizersContainer.addListener(new SynchronizePluginableContainerListener<MessageColorizer>(selectedMessageColorizersContainer));
    messageFormattersContainer.addListener(new SynchronizePluginableContainerListener<MessageFormatter>(selectedMessageFormattersContainer));

    columnHideTableModelDecorator = new ColumnHideTableModelDecorator(dataTableModel);

    menuLabelFont = new JLabel().getFont().deriveFont(Font.BOLD);
    filtersPanel = new JPanel();
    logsTablePanel = new JPanel();
    logsMarkersPanel = new JPanel();
    leftPanel = new JPanel(new MigLayout());
    // pagingTableModel = new PagingTableDataModel(dataTableModel);
    logDetailTextArea = new JTextPane();
    // logDetailTextArea.setLineWrap(true);
    // logDetailTextArea.getEditorKit()
    logDetailTextArea.setEditable(false);
    logDetailTextArea.setBorder(BorderFactory.createTitledBorder("Details"));
    // table = new JTable(pagingTableModel);
    table = new JTableWith2RowHighliting(columnHideTableModelDecorator);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    updateColumnsSize();
    table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
    table.setDefaultRenderer(Object.class, new TableMarkDecoratorRenderer(table.getDefaultRenderer(Object.class)));
    table.setDefaultRenderer(Integer.class, new TableMarkDecoratorRenderer(table.getDefaultRenderer(Object.class)));
    table.setDefaultRenderer(Level.class, new TableMarkDecoratorRenderer(new LevelRenderer(Mode.IconsOnly)));
    table.setDefaultRenderer(Date.class, new TableMarkDecoratorRenderer(new DateRenderer()));
    table.setDefaultRenderer(Boolean.class, new TableMarkDecoratorRenderer(table.getDefaultRenderer(Boolean.class)));
    table.setDefaultRenderer(Note.class, new TableMarkDecoratorRenderer(new NoteRenderer()));
    table.setDefaultEditor(Note.class, new NoteTableEditor());
    sorter = new TableRowSorter<LogDataTableModel>(dataTableModel);
    for (int i = 0; i < dataTableModel.getColumnCount(); i++) {
      sorter.setSortable(i, false);
    }
    sorter.setSortable(TableColumns.ID.getColumn(), true);
    sorter.setSortable(TableColumns.TIME.getColumn(), true);
    table.setRowSorter(sorter);

    MessageDetailListener messageDetailListener = new MessageDetailListener(table, logDetailTextArea, dataTableModel, dateFormat,
        selectedMessageFormattersContainer, selectedMessageColorizersContainer);
    table.getSelectionModel().addListSelectionListener(messageDetailListener);
    dataTableModel.addNoteObserver(messageDetailListener);

    notes = new JTextArea();
    notes.setEditable(false);
    NoteObserver allNotesObserver = new AllNotesTextAreaObserver(notes);
    dataTableModel.addNoteObserver(allNotesObserver);

    addFiltersGUIsToPanel(filtersPanel);
    logsTablePanel.setLayout(new BorderLayout());
    logsTablePanel.add(new JScrollPane(table));
    ScrollableHeighOnlyPanel scrollableHeighOnlyPanel = new ScrollableHeighOnlyPanel(new BorderLayout());
    scrollableHeighOnlyPanel.add(logDetailTextArea);
    scrollPane = new JScrollPane(scrollableHeighOnlyPanel);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    JPanel messageDetailsPanel = new JPanel(new BorderLayout());
    messageDetailTooblar = new JToolBar("MessageDetail");
    messageDetailsPanel.add(messageDetailTooblar, BorderLayout.NORTH);
    messageDetailsPanel.add(scrollPane);
    initMessageDetailsTooblar();

    jTabbedPane = new JTabbedPane();
    jTabbedPane.add("Message detail", messageDetailsPanel);
    jTabbedPane.add("All notes", new JScrollPane(notes));

    leftPanel.add(filtersPanel, "wrap, growx");
    leftPanel.add(new JSeparator(SwingConstants.HORIZONTAL), "wrap,growx");
    leftPanel.add(logsMarkersPanel, "wrap,growx");

    JSplitPane splitPaneLogsTableAndDetails = new JSplitPane(JSplitPane.VERTICAL_SPLIT, logsTablePanel, jTabbedPane);
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(leftPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), splitPaneLogsTableAndDetails);
    splitPane.setOneTouchExpandable(true);
    this.setLayout(new BorderLayout());
    this.add(splitPane);

    splitPaneLogsTableAndDetails.setDividerLocation(0.5d);
    splitPaneLogsTableAndDetails.setOneTouchExpandable(true);
    splitPane.setDividerLocation(leftPanel.getPreferredSize().width + 10);

    initTableContextMenu(columnHideTableModelDecorator);

    dataTableModel.notifyAllNoteObservers(new NoteEvent(EventType.CLEAR, dataTableModel, null, 0));

    table.addKeyListener(new MarkRowBySpaceKeyListener(table, dataTableModel));
  }

  private void updateColumnsSize() {
    updateColumnSizeIfVisible(TableColumns.ID, 40, 100);
    updateColumnSizeIfVisible(TableColumns.TIME, 140, 240);
    updateColumnSizeIfVisible(TableColumns.LEVEL, 16, 16);
    updateColumnSizeIfVisible(TableColumns.THREAD, 100, 500);
    updateColumnSizeIfVisible(TableColumns.MARK, 30, 30);
    updateColumnSizeIfVisible(TableColumns.NOTE, 100, 1500);
  }

  private void updateColumnSizeIfVisible(TableColumns column, int width, int maxWidth) {
    if (columnHideTableModelDecorator.isVisible(column.getColumn())) {
      int converted = columnHideTableModelDecorator.convertToHideModel(column.getColumn());
      table.getColumnModel().getColumn(converted).setMaxWidth(maxWidth);
      table.getColumnModel().getColumn(converted).setWidth(width);
    }
  }

  public JTextPane getLogDetailTextArea() {
    return logDetailTextArea;
  }

  public JScrollPane getScrollPane() {
    return scrollPane;
  }

  public void add(LogData[] logDatas) {
    dataTableModel.add(logDatas);

  }

  public void add(LogData logData) {
    dataTableModel.add(logData);
  }

  public LogData[] getLogData() {
    return dataTableModel.getLogData();
  }

  private void addFiltersGUIsToPanel(JPanel filtersPanel) {
    filtersPanel.setLayout(new MigLayout("", "[grow]", ""));

    Collection<LogFilter> loadeFilters = logFiltersContainer.getElements();

    // Reload filters, every instance of filter is connected to listeners, data table etc.
    Collection<LogFilter> filtersList = new ArrayList<LogFilter>();
    for (LogFilter logFilter : loadeFilters) {
      try {
        LogFilter filter = logFilter.getClass().newInstance();
        filtersList.add(filter);
      } catch (Exception e) {
        System.err.println("can't initialize filter: " + logFilter.getClass());
        continue;
      }

    }
    JLabel filtersLabel = new JLabel("Filters:");
    filtersLabel.setMinimumSize(new Dimension(200, 16));
    filtersLabel.setPreferredSize(new Dimension(200, 16));
    filtersLabel.setIcon(Icons.FILTER);
    Font f = filtersLabel.getFont().deriveFont(Font.BOLD);
    filtersLabel.setFont(f);
    filtersPanel.add(filtersLabel, "wrap, growx, span");
    LogFilterValueChangeListener listener = new LogFilterValueChangeListener(sorter, filtersList, statusObserver);
    for (LogFilter filter : filtersList) {
      filter.init(new Properties(), dataTableModel);
      FilterPanel filterPanel = new FilterPanel(filter, listener);
      filtersPanel.add(filterPanel, "wrap, growx");
      if (filter instanceof ThreadFilter) {
        ThreadFilter threadFilter = (ThreadFilter) filter;
        focusOnThisTrheadAction = new FocusOnThisTrheadAction(table, threadFilter, dataTableModel, filterPanel.getEnableCheckBox());
      } else if (filter instanceof TimeFilter) {
        focusOnEventsAfter = new FocusOnEventsAfter(table, (TimeFilter) filter, dataTableModel, filterPanel.getEnableCheckBox());
        focusOnEventsBefore = new FocusOnEventsBefore(table, (TimeFilter) filter, dataTableModel, filterPanel.getEnableCheckBox());
      } else if (filter instanceof ClassFilter) {
        focusOnSelectedClassesAction = new FocusOnSelectedClassesAction(table, (ClassFilter) filter, dataTableModel, filterPanel.getEnableCheckBox());
        ignoreSelectedEventsClasses = new IgnoreSelectedEventsClasses(table, (ClassFilter) filter, dataTableModel, filterPanel.getEnableCheckBox());
      }
    }
    filtersLabel.add(logsMarkersPanel, "span, grow");
  }

  private void initTableContextMenu(ColumnHideTableModelDecorator columnHideTableModelDecorator) {
    JPopupMenu menu = new JPopupMenu("Menu");
    JMenuItem mark = new JMenuItem("Mark selected rows");
    mark.addActionListener(new MarkRowAction(table, dataTableModel, statusObserver));
    JMenuItem unmark = new JMenuItem("Unmark selected rows");
    unmark.addActionListener(new UnMarkRowAction(table, dataTableModel, statusObserver));

    JMenuItem callHierarchy = new JMenuItem("Call hierarchy");
    callHierarchy.addActionListener(new ShowCallHierarhyAction(table, dataTableModel, statusObserver));

    JMenuItem autoResizeMenu = new JMenu("Table auto resize mode");
    autoResizeMenu.setIcon(Icons.TABLE_RESIZE);
    JMenuItem autoResizeSubseqent = new JMenuItem("Subsequent columns");
    autoResizeSubseqent.addActionListener(new TableResizeActionListener(table, JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS));
    JMenuItem autoResizeLast = new JMenuItem("Last column");
    autoResizeLast.addActionListener(new TableResizeActionListener(table, JTable.AUTO_RESIZE_LAST_COLUMN));
    JMenuItem autoResizeNext = new JMenuItem("Next column");
    autoResizeNext.addActionListener(new TableResizeActionListener(table, JTable.AUTO_RESIZE_NEXT_COLUMN));
    JMenuItem autoResizeAll = new JMenuItem("All columns");
    autoResizeAll.addActionListener(new TableResizeActionListener(table, JTable.AUTO_RESIZE_ALL_COLUMNS));
    JMenuItem autoResizeOff = new JMenuItem("Auto resize off");
    autoResizeOff.addActionListener(new TableResizeActionListener(table, JTable.AUTO_RESIZE_OFF));
    autoResizeMenu.add(autoResizeSubseqent);
    autoResizeMenu.add(autoResizeOff);
    autoResizeMenu.add(autoResizeNext);
    autoResizeMenu.add(autoResizeLast);
    autoResizeMenu.add(autoResizeAll);
    JMenu removeMenu = new JMenu("Remove");
    removeMenu.add(new JMenuItem(new RemoveByAcceptanceCriteria(new SelectedEventsAcceptCondition(table, dataTableModel), dataTableModel, statusObserver)));
    removeMenu.add(new JMenuItem(new RemoveByAcceptanceCriteria(new LowerIdAcceptCondition(table, dataTableModel), dataTableModel, statusObserver)));
    removeMenu.add(new JMenuItem(new RemoveByAcceptanceCriteria(new HigherIdAcceptCondition(table, dataTableModel), dataTableModel, statusObserver)));
    removeMenu.add(new JMenuItem(new RemoveByAcceptanceCriteria(new SelectedClassAcceptCondition(table, dataTableModel), dataTableModel, statusObserver)));
    removeMenu.add(new JMenuItem(new RemoveByAcceptanceCriteria(new SelectedThreadAcceptCondition(table, dataTableModel), dataTableModel, statusObserver)));
    removeMenu.add(new JMenuItem(new RemoveByAcceptanceCriteria(new LevelLowerAcceptContition(Level.INFO), dataTableModel, statusObserver)));
    removeMenu.add(new JMenuItem(new RemoveByAcceptanceCriteria(new LevelLowerAcceptContition(Level.WARNING), dataTableModel, statusObserver)));
    removeMenu.add(new JMenuItem(new RemoveByAcceptanceCriteria(new LevelLowerAcceptContition(Level.SEVERE), dataTableModel, statusObserver)));

    menu.add(new JSeparator());
    JLabel labelMarkingRows = new JLabel("Marking/unmarking rows");
    labelMarkingRows.setFont(menuLabelFont);
    menu.add(labelMarkingRows);
    menu.add(new JSeparator());
    menu.add(mark);
    menu.add(unmark);
    JMenu[] markersMenu = getAutomaticMarkersMenu();
    menu.add(markersMenu[0]);
    menu.add(markersMenu[1]);
    menu.add(new ClearMarkingsAction(dataTableModel, statusObserver));
    menu.add(new JSeparator());
    JLabel labelQuickFilters = new JLabel("Quick filters");
    labelQuickFilters.setFont(menuLabelFont);
    menu.add(labelQuickFilters);
    menu.add(new JSeparator());
    menu.add(focusOnThisTrheadAction);
    menu.add(focusOnEventsAfter);
    menu.add(focusOnEventsBefore);
    menu.add(focusOnSelectedClassesAction);
    menu.add(ignoreSelectedEventsClasses);
    menu.add(removeMenu);
    menu.add(callHierarchy);
    menu.add(new JSeparator());
    JLabel labelTableOptions = new JLabel("Table options");
    labelTableOptions.setFont(menuLabelFont);
    menu.add(labelTableOptions);
    menu.add(new JSeparator());
    menu.add(autoResizeMenu);
    JMenu columntHideMenu = new JMenu("Column show/hide");
    columntHideMenu.setIcon(Icons.TABLE_COLUMN);
    ColumnHideMenu.generateMenu(columntHideMenu, columnHideTableModelDecorator);
    menu.add(columntHideMenu);
    PopupListener popupListener = new PopupListener(menu);
    table.addMouseListener(popupListener);
    table.addKeyListener(popupListener);
  }

  private JMenu[] getAutomaticMarkersMenu() {
    // AutomaticMarker[] markers = MarkersContainer.getInstance().getMarkers().toArray(new AutomaticMarker[0]);
    automaticMarkersMenu = new JMenu("Mark rows automatically ->");
    automaticMarkersMenu.setIcon(Icons.AUTOMATIC_MARKERS);
    automaticUnmarkersMenu = new JMenu("Unmark rows automatically ->");
    automaticUnmarkersMenu.setIcon(Icons.AUTOMATIC_UNMARKERS);
    updateMarkerMenu(markersContainser.getElements());
    return new JMenu[] { automaticMarkersMenu, automaticUnmarkersMenu };
  }

  private void addMarkerToMenu(JMenu menu, AutomaticMarker automaticMarker, HashMap<String, JMenu> marksGroups, boolean mode) {
    String[] groups = automaticMarker.getMarkerGroups();
    if (groups == null || groups.length == 0) {
      groups = new String[] { "" };
    }
    for (String g : groups) {
      JMenuItem markerMenuItem = new JMenuItem(automaticMarker.getName());

      Icon icon = new ColorIcon(automaticMarker.getColors().getBackground(), automaticMarker.getColors().getForeground(), 16, 16);
      markerMenuItem.setIcon(icon);
      markerMenuItem.setToolTipText(automaticMarker.getDescription());
      markerMenuItem.addActionListener(new AutomaticMarkUnamrkActionListener(dataTableModel, automaticMarker, mode, statusObserver));
      if (g.length() > 0) {
        JMenu m = marksGroups.get(g);
        if (m == null) {
          m = new JMenu(g);
          marksGroups.put(g, m);
          menu.add(m);
        }
        m.add(markerMenuItem);
      } else {
        menu.add(markerMenuItem);
      }
    }

  }

  public void updateMarkerMenu(Collection<AutomaticMarker> markers) {
    HashMap<String, JMenu> marksGroups = new HashMap<String, JMenu>();
    HashMap<String, JMenu> unmarksGroups = new HashMap<String, JMenu>();

    automaticMarkersMenu.removeAll();
    automaticUnmarkersMenu.removeAll();
    for (AutomaticMarker automaticMarker : markers) {
      addMarkerToMenu(automaticMarkersMenu, automaticMarker, marksGroups, AutomaticMarkUnamrkActionListener.MODE_MARK);
      addMarkerToMenu(automaticUnmarkersMenu, automaticMarker, unmarksGroups, AutomaticMarkUnamrkActionListener.MODE_UNMARK);
    }
    GuiUtils.sortDirAndAlfabetic(automaticMarkersMenu);
    GuiUtils.sortDirAndAlfabetic(automaticUnmarkersMenu);

  }

  public void showOnlyThisColumns(TableColumns[] columns) {
    Set<TableColumns> columnsSet = new HashSet<TableColumns>();
    for (TableColumns tableColumns : columns) {
      columnsSet.add(tableColumns);
    }
    for (int j = columnHideTableModelDecorator.getColumnCount() - 1; j >= 0; j--) {
      columnHideTableModelDecorator.setColumnVisible(j, columnsSet.contains(TableColumns.getColumnById(j)));
    }
    updateColumnsSize();
  }

  protected void initMessageDetailsTooblar() {
    final JButton buttonFormatters = new JButton("Message formatters", Icons.MESSAGE_FORMATTER);
    buttonFormatters.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        showMessageFormatterOrColorizerPopupMenu(e, "Mesasge formatters", selectedMessageFormattersContainer, messageFormattersContainer);
      }
    });

    messageDetailTooblar.add(buttonFormatters);

    final JButton buttonColorizers = new JButton("Message colorizers", Icons.MESSAGE_COLORIZER);
    buttonColorizers.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {
        showMessageFormatterOrColorizerPopupMenu(e, "Message colorizers", selectedMessageColorizersContainer, messageColorizersContainer);
      }
    });
    messageDetailTooblar.add(buttonColorizers);

  }

  private void showMessageFormatterOrColorizerPopupMenu(MouseEvent e, String menuTitle, PluginableElementsContainer selectedPluginableElementsContainer,
      PluginableElementsContainer pluginableElementsContainer) {
    final JPopupMenu popupMenu = new JPopupMenu(menuTitle);
    popupMenu.add(new JLabel(menuTitle));
    for (final Object pluginableElement : pluginableElementsContainer.getElements()) {
      addMessageFormatterOrColorizerToMenu(popupMenu, (PluginableElement) pluginableElement, selectedPluginableElementsContainer);
    }
    popupMenu.show(e.getComponent(), e.getX(), e.getY());
  }

  private void addMessageFormatterOrColorizerToMenu(final JPopupMenu menu, final PluginableElement pluginable,
      final PluginableElementsContainer selectedPluginableContainer) {
    {
      final JCheckBoxMenuItem boxMenuItem = new JCheckBoxMenuItem(pluginable.getName(), selectedPluginableContainer.contains(pluginable));
      boxMenuItem.setToolTipText(pluginable.getDescription());
      menu.add(boxMenuItem);
      boxMenuItem.addChangeListener(new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
          if (boxMenuItem.isSelected() && !selectedPluginableContainer.contains(pluginable)) {
            selectedPluginableContainer.addElement(pluginable);
          } else if (!boxMenuItem.isSelected() && selectedPluginableContainer.contains(pluginable)) {
            selectedPluginableContainer.removeElement(pluginable);
          }
        }
      });
    }
  }

  public JTable getTable() {
    return table;
  }

  public LogDataTableModel getDataTableModel() {
    return dataTableModel;
  }

  private class MarkersMenuReloader implements PluginableElementEventListener<AutomaticMarker> {

    PluginableElementsContainer<AutomaticMarker> markersContainser = AllPluginables.getInstance().getMarkersContainser();

    @Override
    public void elementAdded(AutomaticMarker element) {
      updateMarkerMenu(markersContainser.getElements());

    }

    @Override
    public void elementRemoved(AutomaticMarker element) {
      updateMarkerMenu(markersContainser.getElements());
    }

    @Override
    public void elementChanged(AutomaticMarker element) {
      updateMarkerMenu(markersContainser.getElements());
    }

  }

  public JPanel getLogsMarkersPanel() {
    return logsMarkersPanel;
  }

  @Override
  public int clear() {
    return dataTableModel.clear();
  }

}
