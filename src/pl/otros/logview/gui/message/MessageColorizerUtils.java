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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Style;
import javax.swing.text.StyledDocument;

public class MessageColorizerUtils {

  /**
   * Apply styles on StyledDocument using regular expression
   * 
   * @param styledDocument
   *          styleDocument to decorate
   * @param style
   *          style to apply
   * @param text
   *          text to be tested by regular expression, fragment from document
   * @param offset
   *          offset of text in styledDocument
   * @param regex
   *          regular expression
   * @param group
   *          group to apply
   */
  public static void colorizeRegex(StyledDocument styledDocument, Style style, String text, int offset, Pattern regex, int group) {
    Matcher matcher = regex.matcher(text);
    while (matcher.find()) {
      int start = matcher.start(group);
      int end = matcher.end(group);
      if (end - start > 0) {
        styledDocument.setCharacterAttributes(offset + start, end - start, style, false);
      }
    }
  }
}
