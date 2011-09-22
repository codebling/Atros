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

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import pl.otros.logview.LogData;
import pl.otros.logview.Note;
import pl.otros.logview.gui.message.MessageColorizer;
import pl.otros.logview.gui.message.MessageFormatter;
import pl.otros.logview.gui.message.SearchResultColorizer;
import pl.otros.logview.gui.note.NoteEvent;
import pl.otros.logview.gui.note.NoteObserver;
import pl.otros.logview.gui.renderers.LevelRenderer;
import pl.otros.logview.pluginable.PluginableElement;
import pl.otros.logview.pluginable.PluginableElementEventListener;
import pl.otros.logview.pluginable.PluginableElementsContainer;

import com.google.common.base.Joiner;

public class MessageDetailListener implements ListSelectionListener, NoteObserver {

  private static final Logger LOGGER = Logger.getLogger(MessageDetailListener.class.getName());

  private JTable table;
  private JTextPane logDetailTextArea;
  private LogDataTableModel dataTableModel;
  private SimpleDateFormat dateFormat;

  private Style defaultStyle = null;
  private Style mainStyle = null;
  private Style classMethodStyle = null;
  private Style noteStyle = null;
  private StyleContext sc;

  private final PluginableElementsContainer<MessageColorizer> colorizersContainer;
  private final PluginableElementsContainer<MessageFormatter> formattersContainer;
  private MessageColorizer searchResultMessageColorizer = null;

  public MessageDetailListener(JTable table, JTextPane logDetailTextArea, LogDataTableModel dataTableModel, SimpleDateFormat dateFormat,
      PluginableElementsContainer<MessageFormatter> formattersContainer, PluginableElementsContainer<MessageColorizer> colorizersContainer) {
    super();
    this.table = table;
    this.logDetailTextArea = logDetailTextArea;
    this.dataTableModel = dataTableModel;
    this.dateFormat = dateFormat;
    this.formattersContainer = formattersContainer;
    this.colorizersContainer = colorizersContainer;

    sc = new StyleContext();
    defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
    mainStyle = sc.addStyle("MainStyle", defaultStyle);
    StyleConstants.setFontSize(mainStyle, 12);
    StyleConstants.setForeground(mainStyle, Color.BLACK);

    classMethodStyle = sc.addStyle("classMethod", null);
    StyleConstants.setFontFamily(classMethodStyle, "monospaced");
    StyleConstants.setForeground(classMethodStyle, Color.BLUE);
    noteStyle = sc.addStyle("note", mainStyle);
    StyleConstants.setFontFamily(noteStyle, "arial");
    StyleConstants.setBold(noteStyle, true);

    formattersContainer.addListener(new PluginableElementEventListenerImplementation<MessageFormatter>());
    colorizersContainer.addListener(new PluginableElementEventListenerImplementation<MessageColorizer>());
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    updateInfo();

  }

  @Override
  public void update(NoteEvent noteEvent) {
    updateInfo();
  }

  public void updateInfo() {
    Collection<MessageFormatter> formatters = formattersContainer.getElements();
    Collection<MessageColorizer> colorizers = colorizersContainer.getElements();
    int row = table.getSelectedRow();
    if (row >= 0 && row < table.getRowCount()) {
      logDetailTextArea.setText("");
      int rowConverted = table.convertRowIndexToModel(row);
      LogData ld = dataTableModel.getLogData(rowConverted);
      StyledDocument document = logDetailTextArea.getStyledDocument();
      synchronized (document) {
        try {
          document.remove(0, document.getLength());
          String s1 = "Date:    " + dateFormat.format(ld.getDate()) + "\n";
          document.insertString(0, s1, mainStyle);
          s1 = "Class:   " + ld.getClazz() + "\n";
          document.insertString(document.getLength(), s1, classMethodStyle);
          s1 = "Method:  " + ld.getMethod() + "\n";
          document.insertString(document.getLength(), s1, classMethodStyle);
          s1 = "Level:   ";
          document.insertString(document.getLength(), s1, classMethodStyle);
          Icon levelIcon = LevelRenderer.getIconByLevel(ld.getLevel());
          if (levelIcon != null) {
            logDetailTextArea.insertIcon(levelIcon);
          }
          s1 = " " + ld.getLevel().getName() + "\n";
          document.insertString(document.getLength(), s1, classMethodStyle);
          s1 = "Message: ";
          document.insertString(document.getLength(), s1, mainStyle);
          int beforeMessage = document.getLength();
          s1 = ld.getMessage();
          for (MessageFormatter messageFormatter : formatters) {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            try {
              Thread.currentThread().setContextClassLoader(messageFormatter.getClass().getClassLoader());
              if (messageFormatter.formattingNeeded(s1)) {
                s1 = messageFormatter.format(s1);
              }
            } catch (Throwable e) {
              LOGGER.severe(String.format("Error occured when using message formatter %s: %s", messageFormatter.getName(), e.getMessage()));
              LOGGER.fine(String.format("Error occured when using message formatter %s with message\"%s\"", messageFormatter.getName(), s1));
            } finally {
              Thread.currentThread().setContextClassLoader(contextClassLoader);
            }

          }
          document.insertString(document.getLength(), s1, mainStyle);
          searchResultMessageColorizer = null;
          for (MessageColorizer messageColorizer : colorizers) {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            try {
              Thread.currentThread().setContextClassLoader(messageColorizer.getClass().getClassLoader());
              if (messageColorizer.colorizingNeeded(s1)) {
                messageColorizer.colorize(document, beforeMessage, document.getLength() - beforeMessage);
              }
            } catch (Throwable e) {
              LOGGER.severe(String.format("Error occured when using message colorizer %s: %s", messageColorizer.getName(), e.getMessage()));
              LOGGER.fine(String.format("Error occured when using message colorizer %s with message\"%s\"", messageColorizer.getName(), s1));
            } finally {
              Thread.currentThread().setContextClassLoader(contextClassLoader);
            }

            if (messageColorizer.getPluginableId().equals(SearchResultColorizer.class.getName())) {
              searchResultMessageColorizer = messageColorizer;
            }

          }
          if (searchResultMessageColorizer != null && searchResultMessageColorizer.colorizingNeeded(s1)) {
            searchResultMessageColorizer.colorize(document, beforeMessage, document.getLength() - beforeMessage);
          }
          document.insertString(document.getLength(), "\n", mainStyle);
          if (ld.getProperties() != null && ld.getProperties().size() > 0) {
            document.insertString(document.getLength(), "\nProperties:\n", noteStyle);
            String prop = Joiner.on("\n").withKeyValueSeparator("=").join(ld.getProperties());
            document.insertString(document.getLength(), prop, noteStyle);
            document.insertString(document.getLength(), "\n", noteStyle);
          }
          Note note = dataTableModel.getNote(rowConverted);
          if (note != null && note.getNote() != null && note.getNote().length() > 0) {
            s1 = "\nNote: " + note.getNote();
            document.insertString(document.getLength(), s1, noteStyle);
          }
        } catch (BadLocationException e) {
          LOGGER.warning("Cant set message details: " + e.getMessage());
        }
      }
    } else {
      StyledDocument document = logDetailTextArea.getStyledDocument();
      synchronized (document) {
        try {
          document.remove(0, document.getLength());
          document.insertString(0, "No event selected", mainStyle);
        } catch (BadLocationException e) {
          LOGGER.warning("Cant set message details: " + e.getMessage());
        }
      }
    }
    logDetailTextArea.setCaretPosition(0);
  }

  public LogDataTableModel getDataTableModel() {
    return dataTableModel;
  }

  public void setDataTableModel(LogDataTableModel dataTableModel) {
    this.dataTableModel = dataTableModel;
  }

  private final class PluginableElementEventListenerImplementation<T extends PluginableElement> implements PluginableElementEventListener<T> {

    @Override
    public void elementRemoved(T element) {
      updateInfo();
    }

    @Override
    public void elementChanged(T element) {
      updateInfo();
    }

    @Override
    public void elementAdded(T element) {
      updateInfo();
    }
  }
}
