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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import pl.otros.logview.gui.actions.OpenLogInvestigationAction;
import pl.otros.logview.gui.actions.TailLogActionListener;
import pl.otros.logview.gui.actions.read.ImportLogWithAutoDetectedImporterActionListener;
import pl.otros.logview.gui.actions.read.ImportLogWithGivenImporterActionListener;
import pl.otros.logview.importer.LogImporter;

public class EmptyViewPanel extends JPanel {

  private JTabbedPane tabbedPane;
  private StatusObserver statusObserver;
  private TreeSet<LogImporter> logImportersList;

  public EmptyViewPanel(JTabbedPane tabbedPane, StatusObserver statusObserver, Collection<LogImporter> logImporters) {
    super();
    this.tabbedPane = tabbedPane;
    this.statusObserver = statusObserver;
    TreeSet<LogImporter> logImportersList = new TreeSet<LogImporter>(new Comparator<LogImporter>() {

      @Override
      public int compare(LogImporter o1, LogImporter o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });

    GridBagLayout bagLayout = new GridBagLayout();
    GridBagConstraints bagConstraints = new GridBagConstraints();
    bagConstraints.anchor = GridBagConstraints.CENTER;
    bagConstraints.gridwidth = 3;
    bagConstraints.ipadx = 10;
    bagConstraints.ipady = 10;
    bagConstraints.gridy = 0;
    bagConstraints.insets = new Insets(5, 5, 5, 5);
    bagConstraints.fill = GridBagConstraints.HORIZONTAL;
    this.setLayout(bagLayout);

    JLabel jLabel = new JLabel("Welcome to OtrosLogViewer", Icons.LOGO_OTROS_64, SwingConstants.CENTER);
    jLabel.setFont(jLabel.getFont().deriveFont(20f).deriveFont(Font.BOLD));
    this.add(jLabel, bagConstraints);
    bagConstraints.gridy++;
    ImportLogWithAutoDetectedImporterActionListener autoDetectActionListener = new ImportLogWithAutoDetectedImporterActionListener(tabbedPane, logImporters,
        statusObserver);
    JButton jb = new JButton("Open log with type autodetection", Icons.WIZARD);
    jb.addActionListener(autoDetectActionListener);
    this.add(jb, bagConstraints);
    bagConstraints.gridy++;

    OpenLogInvestigationAction openLogInvestigationAction = new OpenLogInvestigationAction(tabbedPane, statusObserver);
    JButton jb2 = new JButton("Open log investigation", Icons.IMPORT_24);
    jb2.addActionListener(openLogInvestigationAction);
    this.add(jb2, bagConstraints);
    bagConstraints.gridy++;

    this.add(new JSeparator(SwingConstants.HORIZONTAL), bagConstraints);
    bagConstraints.gridy++;

    int startY = bagConstraints.gridy;
    bagConstraints.gridwidth = 1;

    JLabel openLabel = new JLabel("Open log", SwingConstants.CENTER);
    this.add(openLabel, bagConstraints);
    bagConstraints.gridy++;
    for (LogImporter logImporter : logImporters) {
      ImportLogWithGivenImporterActionListener importLogActionListener = new ImportLogWithGivenImporterActionListener(tabbedPane, logImporter, statusObserver);
      JButton b = new JButton("Open " + logImporter.getName(), logImporter.getIcon());
      b.setHorizontalAlignment(SwingConstants.LEFT);
      b.addActionListener(importLogActionListener);
      this.add(b, bagConstraints);
      bagConstraints.gridy++;
    }

    bagConstraints.gridy = startY;
    JLabel tailLabel = new JLabel("Tail log", SwingConstants.CENTER);
    this.add(tailLabel, bagConstraints);
    bagConstraints.gridy++;
    for (LogImporter logImporter : logImporters) {
      TailLogActionListener importLogActionListener = new TailLogActionListener(tabbedPane, logImporter, statusObserver);
      JButton b = new JButton("Tail " + logImporter.getName(), logImporter.getIcon());
      b.addActionListener(importLogActionListener);
      b.setHorizontalAlignment(SwingConstants.LEFT);
      this.add(b, bagConstraints);
      bagConstraints.gridy++;
    }

    JTextArea visitTf = new JTextArea(
        "Have a different log format? Go to http://code.google.com/p/otroslogviewer/wiki/Log4jPatternLayout\nto check how to create a log parser based on the log4j PatternLayout.");
    visitTf.setEditable(false);
    visitTf.setBackground(tailLabel.getBackground());
    visitTf.setBorder(null);
    bagConstraints.gridwidth = 2;
    bagConstraints.gridx = 0;
    this.add(visitTf, bagConstraints);

  }
}
