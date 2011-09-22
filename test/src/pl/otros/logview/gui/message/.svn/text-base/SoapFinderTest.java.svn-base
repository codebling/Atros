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

import java.io.FileInputStream;
import java.util.SortedSet;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class SoapFinderTest {

  @Test
  public void testFindSoapTag() throws Exception {
    String soap1Formatted = IOUtils.toString(new FileInputStream("test/resources/soap/1-soapMessageFormmated.txt"));
    String soap1UnFormatted = IOUtils.toString(new FileInputStream("test/resources/soap/1-soapMessage.txt"));
    String soap2Formatted = IOUtils.toString(new FileInputStream("test/resources/soap/2-soapMessageFormmated.txt"));
    String soap2UnFormatted = IOUtils.toString(new FileInputStream("test/resources/soap/2-soapMessage.txt"));
    SoapFinder soapFinder = new SoapFinder();
    Assert.assertEquals("SoAp", soapFinder.findSoapTag(soap1Formatted));
    Assert.assertEquals("SoAp", soapFinder.findSoapTag(soap1UnFormatted));
    Assert.assertEquals("soap", soapFinder.findSoapTag(soap2Formatted));
    Assert.assertEquals("soap", soapFinder.findSoapTag(soap2UnFormatted));
  }

  @Test
  public void testFindSoap() throws Exception {
    String stringWithSoaps = IOUtils.toString(new FileInputStream("test/resources/soap/stringWithSoap.txt"));
    SoapFinder finder = new SoapFinder();
    SortedSet<SubText> findSoaps = finder.findSoaps(stringWithSoaps);
    // 60,300
    Assert.assertEquals(2, findSoaps.size());
    SubText first = findSoaps.first();
    stringWithSoaps.substring(first.getStart(), first.getEnd());
    Assert.assertEquals(38, first.getStart());
    Assert.assertEquals(299, first.getEnd());
    SubText last = findSoaps.last();
    stringWithSoaps.substring(last.getStart(), last.getEnd());
    Assert.assertEquals(369, last.getStart());
    Assert.assertEquals(645, last.getEnd());

  }
}
