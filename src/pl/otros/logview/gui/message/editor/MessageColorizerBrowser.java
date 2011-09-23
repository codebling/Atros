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
package pl.otros.logview.gui.message.editor;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.StatusObserver;
import pl.otros.logview.gui.actions.AbstractActionWithConfirmation;
import pl.otros.logview.gui.message.MessageColorizer;
import pl.otros.logview.gui.message.pattern.PropertyPatternMessageColorizer;
import pl.otros.logview.gui.util.DirectoryRestrictedFileSystemView;
import pl.otros.logview.importer.InitializationException;
import pl.otros.logview.loader.LvDynamicLoader;
import pl.otros.logview.pluginable.AllPluginables;
import pl.otros.logview.pluginable.PluginableElementListModel;
import pl.otros.logview.pluginable.PluginableElementNameListRenderer;
import pl.otros.logview.pluginable.PluginableElementsContainer;

public class MessageColorizerBrowser extends JPanel {

  private static final String MESSAGE_COLORIZER_EDITOR_DEFAULT_CONTENT_TXT = "MessageColorizerEditorDefaultContent.txt";
  private static final Logger LOGGER = Logger.getLogger(MessageColorizerBrowser.class.getName());
  private PluginableElementsContainer<MessageColorizer> container;
  private JList jList;
  private StatusObserver statusObserver;
  private PluginableElementListModel<MessageColorizer> listModel;
  private JPanel contentPanel;
  private CardLayout cardLayout;
  private static final String CARD_LAYOUT_EDITOR = "editor";
  private static final String CARD_LAYOUT_NOT_EDITABLE = "notEditable";
  private static final String CARD_LAYOUT_NO_SELECTED = "noSelected";
  private MessageColorizerEditor editor;
  private String defualtContent;
  private JToolBar toolBar;
  private JButton useButton;
  private JButton saveButton;
  private JButton saveAsButton;
  private JButton deleteButton;

  private JFileChooser chooser;
  private DeleteSelected deleteAction;

  public MessageColorizerBrowser(PluginableElementsContainer<MessageColorizer> container, StatusObserver statusObserver) {
    super(new BorderLayout());
    this.container = container;
    this.statusObserver = statusObserver;
    toolBar = new JToolBar();
    editor = new MessageColorizerEditor(container, statusObserver);
    JLabel noEditable = new JLabel("Selected MessageColorizer is not editable.", SwingConstants.CENTER);
    JLabel nothingSelected = new JLabel("Nothing selected", SwingConstants.CENTER);

    listModel = new PluginableElementListModel<MessageColorizer>(container);
    jList = new JList(listModel);
    jList.setCellRenderer(new PluginableElementNameListRenderer());
    cardLayout = new CardLayout();
    contentPanel = new JPanel(cardLayout);
    contentPanel.add(editor, CARD_LAYOUT_EDITOR);
    contentPanel.add(noEditable, CARD_LAYOUT_NOT_EDITABLE);
    contentPanel.add(nothingSelected, CARD_LAYOUT_NO_SELECTED);
    cardLayout.show(contentPanel, CARD_LAYOUT_NOT_EDITABLE);
    JSplitPane mainSplitPane = new JSplitPane(SwingConstants.VERTICAL, new JScrollPane(jList), contentPanel);
    mainSplitPane.setDividerLocation(220);

    jList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent e) {
        showSelected();
        enableDisableButtonsForSelectedColorizer();
      }

    });

    jList.addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_DELETE) {
          ActionEvent actionEvent = new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, "");
          deleteAction.actionPerformed(actionEvent);
        }
      }
    });

    JButton createNew = new JButton("Create new", Icons.ADD);
    createNew.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        saveAsButton.setEnabled(false);
        createNew();
      }
    });

    saveButton = new JButton("Save and use", Icons.DISK);
    saveButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {

        try {
          PropertyPatternMessageColorizer mc = editor.createMessageColorizer();
          File selectedFile = null;
          String f = mc.getFile();
          if (StringUtils.isNotBlank(f)) {
            selectedFile = new File(mc.getFile());
          } else {
            int response = chooser.showSaveDialog(MessageColorizerBrowser.this);
            if (response != JFileChooser.APPROVE_OPTION) {
              return;
            }
            selectedFile = chooser.getSelectedFile();
            if (!selectedFile.getName().endsWith(".pattern")) {
              selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".pattern");
            }
          }
          removeMessageColorizerWithNullFile();
          applyMessageColorizer(selectedFile);
          saveMessageColorizer(selectedFile);
          jList.setSelectedValue(mc, true);
        } catch (ConfigurationException e1) {
          String errorMessage = String.format("Can't save message colorizer: %s", e1.getMessage());
          LOGGER.severe(errorMessage);
          MessageColorizerBrowser.this.statusObserver.updateStatus(errorMessage, StatusObserver.LEVEL_ERROR);
        }
      }
    });

    saveAsButton = new JButton("Save as", Icons.DISK_PLUS);
    saveAsButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          int response = chooser.showSaveDialog(MessageColorizerBrowser.this);
          if (response != JFileChooser.APPROVE_OPTION) {
            return;
          }
          File selectedFile = chooser.getSelectedFile();
          selectedFile = chooser.getSelectedFile();
          if (!selectedFile.getName().endsWith(".pattern")) {
            selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".pattern");
          }
          removeMessageColorizerWithNullFile();
          applyMessageColorizer(selectedFile);
          saveMessageColorizer(selectedFile);
          jList.setSelectedValue(editor.createMessageColorizer(), true);
        } catch (ConfigurationException e1) {
          String errorMessage = String.format("Can't save message colorizer: %s", e1.getMessage());
          LOGGER.severe(errorMessage);
          MessageColorizerBrowser.this.statusObserver.updateStatus(errorMessage, StatusObserver.LEVEL_ERROR);
        }
      }
    });

    useButton = new JButton("Use without saving");
    useButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        try {
          removeMessageColorizerWithNullFile();
          applyMessageColorizer(File.createTempFile("messageColorizer", "pattern"));
        } catch (Exception e) {
          LOGGER.severe("Cannot create message colorizer: " + e.getMessage());
        }

      }
    });

    deleteAction = new DeleteSelected();
    deleteButton = new JButton(deleteAction);

    toolBar.setFloatable(false);
    toolBar.add(createNew);
    toolBar.add(saveButton);
    toolBar.add(saveAsButton);
    toolBar.add(useButton);
    toolBar.add(deleteButton);
    enableDisableButtonsForSelectedColorizer();
    initFileChooser();
    this.add(mainSplitPane);
    this.add(toolBar, BorderLayout.SOUTH);
  }

  protected void deleteSelected() {
    Object selectedValue = jList.getSelectedValue();
    if (selectedValue instanceof PropertyPatternMessageColorizer) {
      PropertyPatternMessageColorizer mc = (PropertyPatternMessageColorizer) selectedValue;
      container.removeElement(mc);
      File f = new File(mc.getFile());
      boolean deleted = f.delete();
      if (deleted) {
        statusObserver.updateStatus(String.format("Message colorizer \"%s\" have been deleted [file %s]", mc.getName(), mc.getFile()));
      } else {
        statusObserver.updateStatus(String.format("Message colorizer \"%s\" have been not deleted [file %s]", mc.getName(), mc.getFile()),
            StatusObserver.LEVEL_ERROR);
      }

    }
  }

  protected void removeMessageColorizerWithNullFile() {
    PropertyPatternMessageColorizer mc = new PropertyPatternMessageColorizer();
    mc.setFile("");
    container.removeElement(mc);
  }

  private void initFileChooser() {
    File rootDirectory = new File("./plugins/message");
    DirectoryRestrictedFileSystemView view = new DirectoryRestrictedFileSystemView(rootDirectory);
    chooser = new JFileChooser(rootDirectory, view);
    chooser.setFileFilter(new FileFilter() {

      @Override
      public String getDescription() {
        return "*.pattern";
      }

      @Override
      public boolean accept(File f) {
        return f.isFile() && f.getName().endsWith("pattern");
      }
    });

  }

  protected void saveMessageColorizer(File selectedFile) {
    FileOutputStream fout = null;
    try {
      PropertyPatternMessageColorizer mc = editor.createMessageColorizer();
      mc.setTestMessage(editor.getTextToColorize());
      fout = new FileOutputStream(selectedFile);
      mc.store(fout);
    } catch (ConfigurationException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(fout);
    }
  }

  protected void applyMessageColorizer(File f) throws ConfigurationException {
    PropertyPatternMessageColorizer mc = editor.createMessageColorizer();
    String name2 = mc.getName();
    while (StringUtils.isBlank(name2)) {
      name2 = JOptionPane.showInputDialog("Enter the message colorizer name.");
      mc.setName(name2);
    }
    mc.setFile(f.getAbsolutePath());
    mc.setTestMessage(editor.getTextToColorize());
    container.addElement(mc);
    jList.setSelectedValue(mc, true);
  }

  protected void createNew() {
    PropertyPatternMessageColorizer colorizer = new PropertyPatternMessageColorizer();
    try {
      colorizer.init(new ByteArrayInputStream(getDefaultContent().getBytes()));
    } catch (ConfigurationException e) {
      statusObserver.updateStatus("Can't load message colorizer template: " + e.getMessage(), StatusObserver.LEVEL_WARNING);
    }
    colorizer.setFile("");
    container.addElement(colorizer);
    jList.setSelectedValue(colorizer, true);
    saveAsButton.setEnabled(false);
  }

  protected void showSelected() {
    MessageColorizer selectedValue = (MessageColorizer) jList.getSelectedValue();
    boolean actionEnabled = false;
    if (selectedValue == null) {
      cardLayout.show(contentPanel, CARD_LAYOUT_NO_SELECTED);
    } else if (selectedValue instanceof PropertyPatternMessageColorizer) {
      PropertyPatternMessageColorizer mc = (PropertyPatternMessageColorizer) selectedValue;
      try {
        editor.setMessageColorizer(mc);
        actionEnabled = true;
      } catch (ConfigurationException e) {
        statusObserver.updateStatus("Can't edit message colorizer: " + e.getMessage(), StatusObserver.LEVEL_ERROR);
      }

      cardLayout.show(contentPanel, CARD_LAYOUT_EDITOR);
    } else {
      cardLayout.show(contentPanel, CARD_LAYOUT_NOT_EDITABLE);
    }
    useButton.setEnabled(actionEnabled);
    saveButton.setEnabled(actionEnabled);
    saveAsButton.setEnabled(actionEnabled);

  }

  protected String getDefaultContent() {
    if (defualtContent == null) {
      try {
        defualtContent = IOUtils.toString(this.getClass().getResourceAsStream(MESSAGE_COLORIZER_EDITOR_DEFAULT_CONTENT_TXT));
      } catch (IOException e) {
        LOGGER.severe(String.format("Can't load content of %s: %s", MESSAGE_COLORIZER_EDITOR_DEFAULT_CONTENT_TXT, e.getMessage()));
      }
    }
    return defualtContent;
  }

  class DeleteSelected extends AbstractActionWithConfirmation {

    public DeleteSelected() {
      super("deleteselectedmessagecolorizer");
      putValue(NAME, "Delete");
      putValue(SHORT_DESCRIPTION, "Wiil delete selected message colorizer");
      putValue(SMALL_ICON, Icons.DELETE);
    }

    @Override
    public void actionPerformedHook(ActionEvent e) {
      deleteSelected();
    }

    @Override
    public String getWarnningMessage() {
      return String.format("Do you want to delete message colorizer \"%s\"?", ((MessageColorizer) jList.getSelectedValue()).getName());
    }

  }

  private void enableDisableButtonsForSelectedColorizer() {
    Object selectedValue = jList.getSelectedValue();
    if (selectedValue != null && selectedValue instanceof PropertyPatternMessageColorizer) {
      saveButton.setEnabled(true);
      saveAsButton.setEnabled(true);
      useButton.setEnabled(true);
      deleteButton.setEnabled(true);
    } else {
      saveButton.setEnabled(false);
      saveAsButton.setEnabled(false);
      useButton.setEnabled(false);
      deleteButton.setEnabled(false);
    }
  }

  public static void main(String[] args) throws Throwable {
    SwingUtilities.invokeAndWait(new Runnable() {

      @Override
      public void run() {
        try {
          LvDynamicLoader.getInstance().loadAll();
        } catch (IOException e) {
          e.printStackTrace();
        } catch (InitializationException e) {
          e.printStackTrace();
        }
        PluginableElementsContainer<MessageColorizer> container = AllPluginables.getInstance().getMessageColorizers();

        JFrame f = new JFrame("MessageColorizerBrowser");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final Container c = f.getContentPane();
        c.setLayout(new BorderLayout());
        c.add(new MessageColorizerBrowser(container, new StatusObserver() {

          @Override
          public void updateStatus(String text, int level) {
            System.out.println(level + ": " + text);
          }

          @Override
          public void updateStatus(String text) {
            updateStatus(text, StatusObserver.LEVEL_NORMAL);
          }
        }));
        f.setSize(500, 500);
        f.setVisible(true);
      }
    });
  }
}
