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
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;

import pl.otros.logview.MarkerColors;
import pl.otros.logview.VersionUtil;
import pl.otros.logview.batch.BatchProcessor;
import pl.otros.logview.gui.actions.AboutAction;
import pl.otros.logview.gui.actions.CheckForNewVersionAction;
import pl.otros.logview.gui.actions.CloseAllTabsAction;
import pl.otros.logview.gui.actions.ExitAction;
import pl.otros.logview.gui.actions.GettingStartedAction;
import pl.otros.logview.gui.actions.JumpToMarkedAction;
import pl.otros.logview.gui.actions.JumpToMarkedAction.Direction;
import pl.otros.logview.gui.actions.MarkAllFoundAction;
import pl.otros.logview.gui.actions.OpenLogInvestigationAction;
import pl.otros.logview.gui.actions.SaveLogInvestigationAction;
import pl.otros.logview.gui.actions.SearchByLevel;
import pl.otros.logview.gui.actions.ShowLoadedComponets;
import pl.otros.logview.gui.actions.ShowLog4jPatternParserEditor;
import pl.otros.logview.gui.actions.ShowMarkersEditor;
import pl.otros.logview.gui.actions.ShowMessageColorizerEditor;
import pl.otros.logview.gui.actions.StartSocketListener;
import pl.otros.logview.gui.actions.TailLogActionListener;
import pl.otros.logview.gui.actions.globalhotkeys.FocusComponentOnHotKey;
import pl.otros.logview.gui.actions.globalhotkeys.KeyboardTabSwitcher;
import pl.otros.logview.gui.actions.read.DragAndDropFilesHandler;
import pl.otros.logview.gui.actions.read.ImportLogWithAutoDetectedImporterActionListener;
import pl.otros.logview.gui.actions.read.ImportLogWithGivenImporterActionListener;
import pl.otros.logview.gui.actions.search.RegexValidatorDocumentListener;
import pl.otros.logview.gui.actions.search.SearchAction;
import pl.otros.logview.gui.actions.search.SearchAction.SearchMode;
import pl.otros.logview.gui.actions.search.SearchDirection;
import pl.otros.logview.gui.actions.search.SearchFieldKeyListener;
import pl.otros.logview.gui.message.MessageColorizer;
import pl.otros.logview.gui.message.SearchResultColorizer;
import pl.otros.logview.gui.renderers.MarkerColorsComboBoxRenderer;
import pl.otros.logview.gui.tip.TipOfTheDay;
import pl.otros.logview.gui.util.DocumentInsertUpdateHandler;
import pl.otros.logview.importer.InitializationException;
import pl.otros.logview.importer.LogImporter;
import pl.otros.logview.loader.IconsLoader;
import pl.otros.logview.loader.LvDynamicLoader;
import pl.otros.logview.persistance.PersistentConfiguration;
import pl.otros.logview.pluginable.AllPluginables;
import pl.otros.logview.pluginable.PluginableElementsContainer;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;

public class LogViewMainFrame extends JFrame {

  private static final Logger LOGGER = Logger.getLogger(LogViewMainFrame.class.getName());

  private static final String CARD_LAYOUT_LOGS_TABLE = "cardLayoutLogsTable";

  private static final String CARD_LAYOUT_EMPTY = "cardLayoutEmpty";

  private JToolBar toolBar;

  private LogImporter[] importers = new LogImporter[0];
  private JButton buttonSearch;
  private JLabelStatusObserver observer;
  private JTabbedPane logsTabbedPane;
  private EnableDisableComponetsForTabs enableDisableComponetsForTabs;
  private CardLayout cardLayout;
  private JPanel cardLayoutPanel;

  private JTextField searchField;

  private AllPluginables allPluginables;

  private PluginableElementsContainer<LogImporter> logImportersContainer;
  private PluginableElementsContainer<MessageColorizer> messageColorizercontainer;
  private SearchResultColorizer searchResultColorizer;

  /**
   * @param args
   * @throws InitializationException
   * @throws InvocationTargetException
   * @throws InterruptedException
   */
  public static void main(String[] args) throws InitializationException, InterruptedException, InvocationTargetException {
    if (args.length > 0 && "-batch".equals(args[0])) {
      try {
        String[] batchArgs = new String[args.length - 1];
        System.arraycopy(args, 1, batchArgs, 0, batchArgs.length);
        BatchProcessor.main(batchArgs);
      } catch (IOException e) {
        System.err.println("Error during batch processing");
      }
      return;
    }
    LOGGER.info("Starting application");
    OtrosSplash.setMessage("Starting application");
    try {
      System.setOut(new PrintStream(new File("olv.out.txt")));
    } catch (FileNotFoundException e2) {
      LOGGER.warning("Error during setting sysout redirection");
    }
    try {
      System.setErr(new PrintStream(new File("olv.err.txt")));
    } catch (FileNotFoundException e2) {
      LOGGER.warning("Error during setting syserr redirection");
    }
    OtrosSplash.setMessage("Loading configuration");

    final PersistentConfiguration c = PersistentConfiguration.getInstance(); //for convenience
    IconsLoader.loadIcons();
    OtrosSplash.setMessage("Loading icons");
    SwingUtilities.invokeAndWait(new Runnable() {

      @Override
      public void run() {
        try {
          OtrosSplash.setMessage("Loading L&F");
          String lookAndFeel = c.getString("lookAndFeel", "com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
          LOGGER.config("Initializing look and feelL: " + lookAndFeel);
          PlasticLookAndFeel.setTabStyle(Plastic3DLookAndFeel.TAB_STYLE_METAL_VALUE);
          UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e1) {
          LOGGER.warning("Cannot initialize LookAndFeel: " + e1.getMessage());
        }
        try {

          final LogViewMainFrame mf = new LogViewMainFrame();
          mf.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
              c.setProperty("gui.state", mf.getExtendedState());
              if (mf.getExtendedState() == Frame.NORMAL) {
                c.setProperty("gui.width", mf.getWidth());
                c.setProperty("gui.height", mf.getHeight());
              }
            }

            @Override
            public void componentMoved(ComponentEvent e) {
              c.setProperty("gui.location.x", mf.getLocation().x);
              c.setProperty("gui.location.y", mf.getLocation().y);
            }

          });
          mf.addWindowListener(new ExitAction(mf));
        } catch (InitializationException e) {
          LOGGER.severe("Cannot initialize main frame");
        }

      }
    });

  }

  public LogViewMainFrame() throws InitializationException {
    super();
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    String title = "OtrosLogViewer ";
    try {
      String version = VersionUtil.getRunningVersion();
      title = title + version;
    } catch (Exception e) {
      LOGGER.warning("Can't load version of running OLV");
    }
    this.setTitle(title);

    try {
      String iconPath = "img/otros/logo16.png";
      if (System.getProperty("os.name").indexOf("Linux") > -1) {
        iconPath = "img/otros/logo64.png";
      }
      BufferedImage icon = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(iconPath));
      this.setIconImage(icon);
    } catch (Exception e1) {
      LOGGER.warning("Can't load icon: " + e1.getMessage());
    }

    try {
      OtrosSplash.setMessage("Loading plugins");
      LvDynamicLoader.getInstance().setStatusObserver(OtrosSplash.getSplashStatusObserver());
      LvDynamicLoader.getInstance().loadAll();
      OtrosSplash.setMessage("Loading plugins loadeds");
    } catch (IOException e) {
      LOGGER.severe("Problem with loading automatic markers, filter or log imprters: " + e.getMessage());
      JOptionPane.showMessageDialog(null, "Problem with loading automatic markers, filter or log imprters: " + e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
    }
    allPluginables = AllPluginables.getInstance();
    logImportersContainer = allPluginables.getLogImportersContainer();
    messageColorizercontainer = allPluginables.getMessageColorizers();
    searchResultColorizer = (SearchResultColorizer) messageColorizercontainer.getElement(SearchResultColorizer.class.getName());
    importers = logImportersContainer.getElements().toArray(importers);

    cardLayout = new CardLayout();
    cardLayoutPanel = new JPanel(cardLayout);

    JLabel statusLabel = new JLabel(" ");
    observer = new JLabelStatusObserver(statusLabel);
    logsTabbedPane = new JTabbedPane();
    enableDisableComponetsForTabs = new EnableDisableComponetsForTabs(logsTabbedPane);
    logsTabbedPane.addChangeListener(enableDisableComponetsForTabs);
    JProgressBar heapBar = new JProgressBar();
    heapBar.setPreferredSize(new Dimension(190, 15));
    new Thread(new MemoryUsedStatsUpdater(heapBar, 1500), "MemoryUsedUpdater").start();
    JPanel statusPanel = new JPanel(new BorderLayout());
    statusPanel.add(statusLabel);
    statusPanel.add(heapBar, BorderLayout.EAST);

    cardLayoutPanel.add(logsTabbedPane, CARD_LAYOUT_LOGS_TABLE);
    EmptyViewPanel emptyViewPanel = new EmptyViewPanel(logsTabbedPane, observer, logImportersContainer.getElements());
    cardLayoutPanel.add(emptyViewPanel, CARD_LAYOUT_EMPTY);
    cardLayout.show(cardLayoutPanel, CARD_LAYOUT_EMPTY);

    logsTabbedPane.addContainerListener(new ContainerListener() {

      @Override
      public void componentRemoved(ContainerEvent e) {
        setCard();
      }

      @Override
      public void componentAdded(ContainerEvent e) {
        setCard();
      }

      public void setCard() {
        int tabCount = logsTabbedPane.getTabCount();
        if (tabCount > 0) {
          cardLayout.show(cardLayoutPanel, CARD_LAYOUT_LOGS_TABLE);
        } else {
          cardLayout.show(cardLayoutPanel, CARD_LAYOUT_EMPTY);
        }
      }
    });

    setTransferHandler(new DragAndDropFilesHandler(logImportersContainer.getElements(), logsTabbedPane, observer));

    // initLiseners();
    initMenu();
    initToolbar();
    enableDisableComponetsForTabs.stateChanged(null);
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(toolBar, BorderLayout.NORTH);
    cp.add(cardLayoutPanel, BorderLayout.CENTER);
    cp.add(statusPanel, BorderLayout.SOUTH);

    initGlobalHotKeys();
    initPosition();
    OtrosSplash.hide();
    setVisible(true);
    new TipOfTheDay().showTipOfTheDayIfNotDisabled(this);
  }

  private void initToolbar() {
    toolBar = new JToolBar();

    final JComboBox searchMode = new JComboBox(new String[] { "String contains search: ", "Regex search: " });
    final SearchAction searchActionForward = new SearchAction(observer, logsTabbedPane, SearchDirection.FORWARD);
    final SearchAction searchActionBackward = new SearchAction(observer, logsTabbedPane, SearchDirection.REVERSE);
    searchField = new JTextField(10);
    searchField.setMinimumSize(new Dimension(100, 10));
    searchField.setToolTipText("This is\na multiline\nTooltip!");
    searchField.addKeyListener(new SearchFieldKeyListener(searchActionForward, searchField));
    searchField.getDocument().addDocumentListener(new DocumentInsertUpdateHandler() {

      @Override
      protected void documentChanged(DocumentEvent e) {
        searchResultColorizer.setSearchString(searchField.getText());
      }
    });
    searchField.setToolTipText("<HTML>Enter text to search.<BR/>" + "Enter - search next,<BR/>Alt+Enter search previous,<BR/>"
        + "Ctrl+Enter - mark all found</HTML>");

    final RegexValidatorDocumentListener regexValidatorDocumentListener = new RegexValidatorDocumentListener(searchField, observer);
    searchField.getDocument().addDocumentListener(regexValidatorDocumentListener);
    searchMode.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        SearchMode mode = null;
        if (searchMode.getSelectedIndex() == 0) {
          mode = SearchMode.STRING_CONTAINS;
          regexValidatorDocumentListener.setEnable(false);
        } else {
          mode = SearchMode.REGEX;
          regexValidatorDocumentListener.setEnable(true);
        }
        searchActionForward.setSearchMode(mode);
        searchActionBackward.setSearchMode(mode);
        PersistentConfiguration.getInstance().setProperty("gui.searchMode", mode);
        searchResultColorizer.setSearchMode(mode);
      }
    });
    final PersistentConfiguration c = PersistentConfiguration.getInstance();
    searchMode.setSelectedIndex(c.get(SearchMode.class, "gui.searchMode", SearchMode.STRING_CONTAINS).equals(SearchMode.STRING_CONTAINS) ? 0 : 1);

    final JCheckBox markFound = new JCheckBox("Mark search result");
    markFound.setMnemonic(KeyEvent.VK_M);
    final MarkAllFoundAction markAllFoundAction = new MarkAllFoundAction(observer, logsTabbedPane, searchField);
    searchField.addKeyListener(markAllFoundAction);
    c.addConfigurationListener(markAllFoundAction);
    JButton markAllFoundButton = new JButton(markAllFoundAction);

    final JComboBox markColor = new JComboBox(MarkerColors.values());
    markFound.setSelected(c.getBoolean("gui.markFound", true));
    markFound.addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {
        boolean selected = markFound.isSelected();
        searchActionForward.setMarkFound(selected);
        searchActionBackward.setMarkFound(selected);
        c.setProperty("gui.markFound", markFound.isSelected());
      }
    });

    markColor.setRenderer(new MarkerColorsComboBoxRenderer());
    markColor.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        MarkerColors markerColors = (MarkerColors) markColor.getSelectedItem();
        searchActionForward.setMarkerColors(markerColors);
        searchActionBackward.setMarkerColors(markerColors);
        markAllFoundAction.setMarkerColors(markerColors);
        c.setProperty("gui.markColor", markColor.getSelectedItem());
      }
    });
    markColor.getModel().setSelectedItem(c.get(MarkerColors.class, "gui.markColor", MarkerColors.Aqua));

    buttonSearch = new JButton(searchActionForward);
    searchActionForward.setSearchStringField(searchField);
    buttonSearch.setMnemonic(KeyEvent.VK_N);
    JButton buttonSearchPrev = new JButton(searchActionBackward);
    searchActionBackward.setSearchStringField(searchField);
    buttonSearchPrev.setMnemonic(KeyEvent.VK_P);

    enableDisableComponetsForTabs.addComponet(buttonSearch);
    enableDisableComponetsForTabs.addComponet(buttonSearchPrev);
    enableDisableComponetsForTabs.addComponet(searchField);
    enableDisableComponetsForTabs.addComponet(markFound);
    enableDisableComponetsForTabs.addComponet(markAllFoundButton);
    enableDisableComponetsForTabs.addComponet(searchMode);
    enableDisableComponetsForTabs.addComponet(markColor);

    toolBar.add(searchMode);
    toolBar.add(searchField);
    toolBar.add(buttonSearch);
    toolBar.add(buttonSearchPrev);
    toolBar.add(markFound);
    toolBar.add(markAllFoundButton);
    toolBar.add(markColor);

    JButton nextMarked = new JButton(new JumpToMarkedAction(Direction.FORWARD, observer, logsTabbedPane));
    nextMarked.setMnemonic(KeyEvent.VK_E);
    enableDisableComponetsForTabs.addComponet(nextMarked);
    toolBar.add(nextMarked);
    JButton prevMarked = new JButton(new JumpToMarkedAction(Direction.BACKWARD, observer, logsTabbedPane));
    prevMarked.setMnemonic(KeyEvent.VK_R);
    enableDisableComponetsForTabs.addComponet(prevMarked);
    toolBar.add(prevMarked);
    toolBar.add(new JSeparator(JSeparator.VERTICAL));

    enableDisableComponetsForTabs.addComponet(toolBar.add(new SearchByLevel(observer, logsTabbedPane, 1, Level.INFO)));
    enableDisableComponetsForTabs.addComponet(toolBar.add(new SearchByLevel(observer, logsTabbedPane, 1, Level.WARNING)));
    enableDisableComponetsForTabs.addComponet(toolBar.add(new SearchByLevel(observer, logsTabbedPane, 1, Level.SEVERE)));
    enableDisableComponetsForTabs.addComponet(toolBar.add(new SearchByLevel(observer, logsTabbedPane, -1, Level.INFO)));
    enableDisableComponetsForTabs.addComponet(toolBar.add(new SearchByLevel(observer, logsTabbedPane, -1, Level.WARNING)));
    enableDisableComponetsForTabs.addComponet(toolBar.add(new SearchByLevel(observer, logsTabbedPane, -1, Level.SEVERE)));

  }

  private void initMenu() {
    JMenuBar menuBar = new JMenuBar();

    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);

    Font menuGroupFont = null;
    JLabel labelOpenLog = new JLabel("Open log", Icons.FOLDER_OPEN, SwingConstants.LEFT);
    menuGroupFont = labelOpenLog.getFont().deriveFont(13f).deriveFont(Font.BOLD);
    labelOpenLog.setFont(menuGroupFont);
    fileMenu.add(labelOpenLog);

    JMenuItem openAutoDetectLog = new JMenuItem("Open log with autodetect type");
    openAutoDetectLog.addActionListener(new ImportLogWithAutoDetectedImporterActionListener(logsTabbedPane, logImportersContainer.getElements(), observer));
    openAutoDetectLog.setMnemonic(KeyEvent.VK_O);
    openAutoDetectLog.setIcon(Icons.WIZARD);
    fileMenu.add(openAutoDetectLog);

    for (int i = 0; i < importers.length; i++) {
      JMenuItem openLog = new JMenuItem("Open " + importers[i].getName() + " log");
      openLog.addActionListener(new ImportLogWithGivenImporterActionListener(logsTabbedPane, importers[i], observer));
      if (importers[i].getKeyStrokeAccelelator() != null) {
        openLog.setAccelerator(KeyStroke.getKeyStroke(importers[i].getKeyStrokeAccelelator()));
      }
      if (importers[i].getMnemonic() > 0) {
        openLog.setMnemonic(importers[i].getMnemonic());
      }
      Icon icon = importers[i].getIcon();
      if (icon != null) {
        openLog.setIcon(icon);
      }
      fileMenu.add(openLog);
    }

    fileMenu.add(new JSeparator());
    JLabel labelTailLog = new JLabel("Tail log", Icons.ARROW_REPEAT, SwingConstants.LEFT);
    labelTailLog.setFont(menuGroupFont);
    fileMenu.add(labelTailLog);
    for (int i = 0; i < importers.length; i++) {
      JMenuItem openLog = new JMenuItem("Tail " + importers[i].getName() + " log");
      openLog.addActionListener(new TailLogActionListener(logsTabbedPane, importers[i], observer));
      if (importers[i].getKeyStrokeAccelelator() != null) {
        openLog.setAccelerator(KeyStroke.getKeyStroke(importers[i].getKeyStrokeAccelelator()));
      }
      if (importers[i].getMnemonic() > 0) {
        openLog.setMnemonic(importers[i].getMnemonic());
      }
      Icon icon = importers[i].getIcon();
      if (icon != null) {
        openLog.setIcon(icon);
      }
      fileMenu.add(openLog);
    }
    fileMenu.add(new JSeparator());

    JMenuItem saveLogsInvest = new JMenuItem(new SaveLogInvestigationAction(logsTabbedPane, observer));
    enableDisableComponetsForTabs.addComponet(saveLogsInvest);
    fileMenu.add(saveLogsInvest);
    fileMenu.add(new OpenLogInvestigationAction(logsTabbedPane, observer));

    JMenuItem exitMenuItem = new JMenuItem("Exit", 'e');
    exitMenuItem.setIcon(Icons.TURN_OFF);
    exitMenuItem.setAccelerator(KeyStroke.getKeyStroke("control F4"));
    exitMenuItem.addActionListener(new ExitAction(this));

    fileMenu.add(new JSeparator());
    fileMenu.add(exitMenuItem);

    JMenu toolsMenu = new JMenu("Tools");
    toolsMenu.setMnemonic(KeyEvent.VK_T);
    JMenuItem closeAll = new JMenuItem(new CloseAllTabsAction(logsTabbedPane));
    enableDisableComponetsForTabs.addComponet(closeAll);

    // JMenuItem umlMenuItem = new JMenuItem("Create UML", 'U');
    // umlMenuItem.addActionListener(new CreateUMLActionListener());

    // toolsMenu.add(umlMenuItem);
    toolsMenu.add(new JMenuItem(new StartSocketListener(logsTabbedPane, observer)));
    toolsMenu.add(new ShowMarkersEditor(logsTabbedPane));
    toolsMenu.add(new ShowLog4jPatternParserEditor(logsTabbedPane, observer));
    toolsMenu.add(new ShowMessageColorizerEditor(logsTabbedPane, messageColorizercontainer, observer));
    toolsMenu.add(new ShowLoadedComponets(logsTabbedPane));
    toolsMenu.add(closeAll);

    JMenu helpMenu = new JMenu("Help");
    JMenuItem about = new JMenuItem("About");
    AboutAction action = new AboutAction(this);
    action.putValue(Action.NAME, "About");
    about.setAction(action);
    helpMenu.add(about);

    JMenuItem checkForNewVersion = new JMenuItem(new CheckForNewVersionAction());
    helpMenu.add(checkForNewVersion);
    helpMenu.add(new GettingStartedAction(this));

    menuBar.add(fileMenu);
    menuBar.add(toolsMenu);
    menuBar.add(helpMenu);
    setJMenuBar(menuBar);

  }

  private void initGlobalHotKeys() {
    KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    manager.addKeyEventPostProcessor(new KeyboardTabSwitcher(logsTabbedPane));
    manager.addKeyEventPostProcessor(new FocusComponentOnHotKey(searchField, KeyEvent.VK_F, KeyEvent.CTRL_MASK));
  }

  private void initPosition() {
    final PersistentConfiguration c = PersistentConfiguration.getInstance(); //for convenience
    Dimension size = new Dimension(c.getInt("gui.width", 1280), c.getInt("gui.height", 780));
    Point location = new Point(c.getInt("gui.location.x", 100), c.getInt("gui.location.y", 100));
    int state = c.getInt("gui.state", Frame.NORMAL);
    Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    if (location.x > screensize.width) {
      location.x = 0;
    }
    if (location.y > screensize.height) {
      location.y = 0;
    }
    size.width = Math.min(screensize.width, size.width);
    size.height = Math.min(screensize.height, size.height);
    this.setSize(size);
    this.setLocation(location);
    this.setExtendedState(state);
  }

}
