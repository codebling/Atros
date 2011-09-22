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

import java.awt.Color;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.apache.commons.lang.StringUtils;

import pl.otros.logview.gui.actions.search.SearchAction.SearchMode;
import pl.otros.logview.importer.AbstractPluginableElement;

public class SearchResultColorizer extends AbstractPluginableElement implements MessageColorizer {

  private Color color = Color.YELLOW;
  private String searchString = "";
  private SearchMode searchMode;

  public SearchResultColorizer() {
    super("Search result", "Marsk search result");
  }

  @Override
  public boolean colorizingNeeded(String message) {
    if (StringUtils.isEmpty(searchString)) {
      return false;
    }
    if (searchMode.equals(SearchMode.STRING_CONTAINS)) {
      return StringUtils.containsIgnoreCase(message, searchString);
    } else if (searchMode.equals(SearchMode.REGEX)) {
      try {
        Pattern p = Pattern.compile(searchString, Pattern.CASE_INSENSITIVE);
        return (p.matcher(message).find());
      } catch (Exception e) {
        return false;
      }
    }
    return false;
  }

  @Override
  public void colorize(StyledDocument styledDocument, int offset, int length) throws BadLocationException {
    if (StringUtils.isEmpty(searchString)) {
      return;
    }
    Style searchStyle = styledDocument.getStyle(StyleContext.DEFAULT_STYLE);
    StyleConstants.setBackground(searchStyle, color);
    if (searchMode.equals(SearchMode.STRING_CONTAINS)) {
      colorizeString(styledDocument, searchStyle, offset, length, searchString);
    } else if (searchMode.equals(SearchMode.REGEX)) {
      String text = styledDocument.getText(offset, length);
      MessageColorizerUtils.colorizeRegex(styledDocument, searchStyle, text, offset, Pattern.compile(searchString, Pattern.CASE_INSENSITIVE), 0);
    }
  }

  private void colorizeString(StyledDocument logDetailsDocument, Style searchStyle, int offset, int length, String toHighlight) {
    try {
      String text = logDetailsDocument.getText(offset, length).toLowerCase();
      toHighlight = toHighlight.toLowerCase();
      int idx = 0;
      while ((idx = text.indexOf(toHighlight, idx)) > -1) {
        logDetailsDocument.setCharacterAttributes(idx + offset, toHighlight.length(), searchStyle, false);
        idx++;
      }
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
  }

  public String getSearchString() {
    return searchString;
  }

  public void setSearchString(String searchString) {
    this.searchString = searchString;
  }

  public SearchMode getSearchMode() {
    return searchMode;
  }

  public void setSearchMode(SearchMode searchMode) {
    this.searchMode = searchMode;
  }

}
