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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.SortedSet;

import javax.swing.text.Style;
import javax.swing.text.StyleContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class SoapMessageFormatter implements MessageFormatter {

  private static final String NAME = "Soap formatter";
  private static final String DESCRIPTION = "Formatting SOAP messages.";
  private int formattIndend = 2;
  private SoapFinder soapFinder = new SoapFinder();

  /*
   * (non-Javadoc)
   * 
   * @see pl.otros.logview.gui.message.MessageFormatter#formattingNeeded(java.lang.String)
   */
  @Override
  public boolean formattingNeeded(String message) {
    return soapFinder.findSoaps(message).size() > 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see pl.otros.logview.gui.message.MessageFormatter#format(java.lang.String, javax.swing.text.StyledDocument)
   */
  @Override
  public String format(String message) {
    // StyledDocument styledDocument = jTextPane.getStyledDocument();
    StringBuilder sb = new StringBuilder();
    SortedSet<SubText> findSoaps = soapFinder.findSoaps(message);
    StyleContext sc = new StyleContext();
    Style defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);

    int lastEnd = 0;
    // System.out.println("SoapMessageFormatter.format() found: " + findSoaps);
    for (SubText subText : findSoaps) {
      String subString = message.substring(lastEnd, subText.getStart());
      sb.append(subString);
      sb.append("\n");
      String xml = prettyFormat(message.substring(subText.getStart(), subText.getEnd()));
      sb.append(xml);
      lastEnd = subText.getEnd();
    }
    sb.append(message.substring(lastEnd));
    return sb.toString();
  }

  public String prettyFormat(String input, int indent, boolean omitXmlDeclaration) {
    try {
      Source xmlInput = new StreamSource(new StringReader(input));
      StringWriter stringWriter = new StringWriter();
      StreamResult xmlOutput = new StreamResult(stringWriter);
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitXmlDeclaration ? "yes" : "no");
      transformer.transform(xmlInput, xmlOutput);
      return xmlOutput.getWriter().toString();
    } catch (Exception e) {
      throw new RuntimeException(e); // simple exception handling, please
      // review it
    }
  }

  public String prettyFormat(String input) {
    return prettyFormat(input, formattIndend, false);
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
