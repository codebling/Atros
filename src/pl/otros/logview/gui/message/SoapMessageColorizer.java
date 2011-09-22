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
package pl.otros.logview.gui.message;

import static pl.otros.logview.gui.message.MessageColorizerUtils.colorizeRegex;

import java.awt.Color;
import java.util.SortedSet;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class SoapMessageColorizer implements MessageColorizer {

  private static final String NAME = "Soap message";

  private SoapFinder soapFinder = new SoapFinder();

  private Style styleElementName;
  private Style styleAttribtuteName;
  private Style styleAttribtuteValue;
  private Style styleContent;
  private Style styleOperator;
  private Style styleComments;
  private Style styleCData;
  private Style styleProcessingInstructions;
  private Style styleDOCTYPE;
  private StyleContext sc;

  private static final Pattern pComment = Pattern.compile("(<!--.*?-->)");
  private static final Pattern pContent = Pattern.compile(">(.*?)<");
  private static final Pattern pOperator = Pattern.compile("(<|>|<!--|-->)");
  private static final Pattern pElementName = Pattern.compile("</?([:a-zA-z0-9_-]*)\\s?.*?(>|/>)");
  private static final Pattern pAttributeName = Pattern.compile("\\s+([-\\w\\d_:]*?)=\".*?\"");
  private static final Pattern pAttributeValue = Pattern.compile("\\s+[-\\w\\d_:]*?=(\".*?\")");
  private static final Pattern pCdata = Pattern.compile("(<!\\[CDATA\\[.*?\\]\\]>)");
  private static final Pattern pDoctype = Pattern.compile("(<!doctype.*?>)", Pattern.CASE_INSENSITIVE);
  private static final Pattern pXmlHeader = Pattern.compile("(<\\?xml .*?\\?>)", Pattern.CASE_INSENSITIVE);

  private static final String DESCRIPTION = "Colorize soap message.";

  public SoapMessageColorizer() {
    initStyles();
  }

  private void initStyles() {
    sc = new StyleContext();
    Style parent = sc.getStyle(StyleContext.DEFAULT_STYLE);

    StyleConstants.setFontFamily(parent, "courier");
    StyleConstants.setFontSize(parent, 13);

    styleElementName = sc.addStyle("elementName", parent);
    StyleConstants.setForeground(styleElementName, new Color(128, 0, 0));

    styleAttribtuteName = sc.addStyle("attributeName", parent);
    StyleConstants.setForeground(styleAttribtuteName, Color.RED);

    styleAttribtuteValue = sc.addStyle("attributeValue", parent);

    styleContent = sc.addStyle("content", parent);
    StyleConstants.setBackground(styleContent, new Color(200, 255, 100));

    styleOperator = sc.addStyle("operator", parent);
    StyleConstants.setForeground(styleOperator, Color.BLUE);
    StyleConstants.setBold(styleOperator, true);

    styleComments = sc.addStyle("comments", parent);
    StyleConstants.setForeground(styleComments, new Color(128, 128, 128));// Hooker's green

    styleCData = sc.addStyle("cdata", parent);
    StyleConstants.setForeground(styleCData, new Color(30, 30, 0));
    StyleConstants.setBackground(styleCData, new Color(250, 250, 240));

    styleProcessingInstructions = sc.addStyle("processingIntruction", parent);
    styleDOCTYPE = sc.addStyle("doctype", styleComments);
  }

  @Override
  public boolean colorizingNeeded(String message) {
    return soapFinder.findSoaps(message).size() > 0;
  }

  @Override
  public void colorize(StyledDocument styledDocument, int offset, int length) throws BadLocationException {
    String text = styledDocument.getText(offset, length);
    SortedSet<SubText> findSoaps = soapFinder.findSoaps(text);
    for (SubText subText : findSoaps) {
      int fragmentStart = offset + subText.getStart();
      int fragmentEnd = offset + subText.getEnd();
      colorizeFragment(styledDocument, fragmentStart, fragmentEnd);
    }
  }

  private void colorizeFragment(StyledDocument styledDocument, int fragmentStart, int fragmentEnd) throws BadLocationException {
    String soap = styledDocument.getText(fragmentStart, fragmentEnd - fragmentStart);

    colorizeRegex(styledDocument, styleOperator, soap, fragmentStart, pOperator, 1);
    colorizeRegex(styledDocument, styleContent, soap, fragmentStart, pContent, 1);
    colorizeRegex(styledDocument, styleElementName, soap, fragmentStart, pElementName, 1);
    colorizeRegex(styledDocument, styleComments, soap, fragmentStart, pComment, 1);
    colorizeRegex(styledDocument, styleAttribtuteName, soap, fragmentStart, pAttributeName, 1);
    colorizeRegex(styledDocument, styleAttribtuteValue, soap, fragmentStart, pAttributeValue, 1);
    colorizeRegex(styledDocument, styleComments, soap, fragmentStart, pXmlHeader, 1);
    colorizeRegex(styledDocument, styleCData, soap, fragmentStart, pCdata, 1);
    colorizeRegex(styledDocument, styleDOCTYPE, soap, fragmentStart, pDoctype, 1);
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }

  @Override
  public String getPluginableId() {
    return this.getClass().getName();
  }

}
