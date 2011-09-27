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
package pl.otros.logview.gui.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import net.miginfocom.swing.MigLayout;
import pl.otros.logview.VersionUtil;
import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.JAnimatedLogo;

public class AboutAction extends AbstractAction {

  private JFrame frame;
  private String build = "?";
  private static final Logger LOGGER = Logger.getLogger(AboutAction.class.getName());
  private JTextPane textArea;

  private Style defaultStyle = null;
  private Style mainStyle = null;
  private Style licenceStyle = null;
  private Style poweredByStyle = null;
  private Style titleStyle = null;
  private StyleContext sc;
  private JAnimatedLogo animatedLogo;
  private JPanel panel;

  public AboutAction(JFrame frame) {
    this.frame = frame;
    this.putValue(SMALL_ICON, Icons.LOGO_OTROS_16);
    try {
      build = VersionUtil.getRunningVersion();
    } catch (IOException e) {
      LOGGER.severe("Problem with checking running version: " + e.getMessage());
    }

    animatedLogo = new JAnimatedLogo();
    textArea = new JTextPane();
    textArea.setText("");
    textArea.setEditable(false);
    textArea.setBackground(new JLabel().getBackground());

    sc = new StyleContext();
    defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
    mainStyle = sc.addStyle("MainStyle", defaultStyle);
    StyleConstants.setFontSize(mainStyle, 12);
    StyleConstants.setForeground(mainStyle, Color.BLACK);

    licenceStyle = sc.addStyle("classMethod", null);
    StyleConstants.setFontFamily(licenceStyle, "monospaced");
    StyleConstants.setForeground(licenceStyle, Color.BLUE);
    titleStyle = sc.addStyle("note", mainStyle);
    StyleConstants.setFontFamily(titleStyle, "arial");
    StyleConstants.setBold(titleStyle, true);
    StyleConstants.setFontSize(titleStyle, 20);

    poweredByStyle = sc.addStyle("poweredBy", defaultStyle);

    try {
      fillText();
    } catch (BadLocationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    panel = new JPanel(new MigLayout());
    panel.add(animatedLogo, "top");
    panel.add(textArea, "grow");
  }

  private void fillText() throws BadLocationException {
    textArea.setText("");
    StyledDocument sd = textArea.getStyledDocument();
    sd.insertString(0, "OtrosLogViewer\n", titleStyle);
    sd.insertString(sd.getLength(), "Build: " + build + "\n", mainStyle);
    sd.insertString(sd.getLength(), "Project web page: http://code.google.com/p/otroslogviewer/\n", mainStyle);
    sd.insertString(sd.getLength(), "Program documentation: http://code.google.com/p/otroslogviewer/w/list  \n", mainStyle);
    sd.insertString(sd.getLength(), "\nPowered by: \n", poweredByStyle);

    sd.insertString(sd.getLength(), "  * commons-beanutils-1.8.3.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * commons-beanutils-bean-collections-1.8.3.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * commons-beanutils-core-1.8.3.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * commons-codec-1.5.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * commons-collections-3.2.1.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * commons-compress-1.0.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * commons-configuration-1.6.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * commons-digester-2.1.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * commons-httpclient-3.0.1.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * commons-io-1.4.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * commons-lang-2.4.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * commons-logging-1.1.1.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * commons-logging-adapters-1.1.1.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * commons-net-2.0.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * commons-net-ftp-2.0.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * commons-vfs2-2.1-SNAPSHOT.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * jakarta-oro-2.0.8.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * jcifs-1.3.14.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * jcommander-1.13.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * jsch-0.1.44m.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * jsyntaxpane-0.9.5-b29.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * log4j-1.3alpha-7.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * log4j-xml-1.3alpha-7.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * looks-2.3.1.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * miglayout-3.7.3.1-swing.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * trident.jar \n", poweredByStyle);
    sd.insertString(sd.getLength(), "  * VFSJFileChooser-0.0.5-olv.jar \n", poweredByStyle);

    sd.insertString(sd.getLength(), "License: Apache Commons 2.0", licenceStyle);

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    animatedLogo.start();
    JOptionPane.showMessageDialog(frame, panel, "About", JOptionPane.INFORMATION_MESSAGE, new EmptyIcon());
    animatedLogo.stop();

  }

}
