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

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class LogViewInternalFrame {

  private String name;
  private LogDataTableModel dataTableModel = new LogDataTableModel();
  private LogViewPanel logViewPanel;
  private JInternalFrame iframe;
  private JDesktopPane desktopPane;
  private StatusObserver statusObserver;
  private CardLayout cardLayout;
  private static final String CARD_LAYOUT_LOADING = "card layou loading";
  private static final String CARD_LAYOUT_CONTENT = "card layout content";
  private JProgressBar loadingProgressBar;

  public LogViewInternalFrame(String name, JDesktopPane desktopPane, StatusObserver statusObserver) {
    this.name = name;
    this.desktopPane = desktopPane;
    this.statusObserver = statusObserver;
    dataTableModel = new LogDataTableModel();
    logViewPanel = new LogViewPanel(dataTableModel, statusObserver);

    cardLayout = new CardLayout();
    JPanel panelLoading = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(10, 10, 10, 10);
    c.anchor = GridBagConstraints.PAGE_START;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridx = 0;
    c.gridy = 0;
    c.ipadx = 12;
    c.ipady = 12;
    JLabel label = new JLabel("Loading file " + name);
    panelLoading.add(label, c);

    c.gridy = 1;
    c.weighty = 5;
    c.weightx = 3;

    loadingProgressBar = new JProgressBar();
    loadingProgressBar.setIndeterminate(false);
    loadingProgressBar.setStringPainted(true);
    loadingProgressBar.setString("Connecting...");
    panelLoading.add(loadingProgressBar, c);

    iframe = new JInternalFrame(name, true, true, true, true);
    iframe.getContentPane().setLayout(cardLayout);
    iframe.getContentPane().add(panelLoading, CARD_LAYOUT_LOADING);
    iframe.getContentPane().add(logViewPanel, CARD_LAYOUT_CONTENT);
    cardLayout.show(iframe.getContentPane(), CARD_LAYOUT_LOADING);
    iframe.setBounds(50, 50, 200, 100);
    desktopPane.add(iframe);

    iframe.setVisible(true);
    iframe.pack();
    try {
      iframe.setMaximum(true);
    } catch (PropertyVetoException ignore) {
    }

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LogDataTableModel getDataTableModel() {
    return dataTableModel;
  }

  public void setDataTableModel(LogDataTableModel dataTableModel) {
    this.dataTableModel = dataTableModel;
  }

  public LogViewPanel getLogViewPanel() {
    return logViewPanel;
  }

  public void setLogViewPanel(LogViewPanel logViewPanel) {
    this.logViewPanel = logViewPanel;
  }

  public JInternalFrame getIframe() {
    return iframe;
  }

  public void setIframe(JInternalFrame iframe) {
    this.iframe = iframe;
  }

  @Override
  public boolean equals(Object obj) {
    return iframe.equals(obj);
  }

  @Override
  public int hashCode() {
    return iframe.hashCode();
  }

  @Override
  public String toString() {
    return name;
  }

  public JProgressBar getLoadingProgressBar() {
    return loadingProgressBar;
  }

  public void switchToContentView() {
    cardLayout.show(iframe.getContentPane(), CARD_LAYOUT_CONTENT);

  }
}
