/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.MetaDataAttribute;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.replaceIllegalCharsAttribute;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StringUtilsTest {

    @Mock
    private URL url;

    private String emptyString = "";
    private String testString = "some string";
    private String modifiedString = "some string";

    @Before
    public void setUp() {
        StringUtils.setURL(url);
        when(url.encode(anyString())).thenReturn(modifiedString);
        when(url.decode(anyString())).thenReturn(modifiedString);

        when(url.encodeQueryString(anyString())).thenReturn(modifiedString);
        when(url.decodeQueryString(anyString())).thenReturn(modifiedString);
    }

    @Test
    public void testCreateDataTypeDisplayName() {
        assertEquals("Chairs [com.test]",
                     StringUtils.createDataTypeDisplayName("com.test.Chairs"));
    }

    @Test
    public void testRegexSequence() {

        String test1 = "123Test";
        assertTrue(test1.matches(StringUtils.ALPHA_NUM_REGEXP));

        String test2 = "123Test ";
        assertFalse(test2.matches(StringUtils.ALPHA_NUM_REGEXP));

        String test3 = "123Test #";
        assertFalse(test3.matches(StringUtils.ALPHA_NUM_REGEXP));

        String test4 = "123Test";
        assertTrue(test4.matches(StringUtils.ALPHA_NUM_SPACE_REGEXP));

        String test5 = "123Test ";
        assertTrue(test5.matches(StringUtils.ALPHA_NUM_SPACE_REGEXP));

        String test6 = "123Test #";
        assertFalse(test6.matches(StringUtils.ALPHA_NUM_SPACE_REGEXP));
    }

    @Test
    public void testgetStringForList() {
        List<Variable> variables = new ArrayList<>();
        Variable inputVariable1 = new Variable("input1",
                                               Variable.VariableType.INPUT,
                                               "Boolean",
                                               null);
        Variable inputVariable2 = new Variable("input2",
                                               Variable.VariableType.INPUT,
                                               "Object",
                                               null);
        variables.add(inputVariable1);
        variables.add(inputVariable2);

        List<MetaDataAttribute> attributes = new ArrayList<>();
        MetaDataAttribute metaDataAttribute1 = new MetaDataAttribute("input1", "value");
        MetaDataAttribute metaDataAttribute2 = new MetaDataAttribute("input2", "value");
        attributes.add(metaDataAttribute1);
        attributes.add(metaDataAttribute2);

        assertEquals("input1:Boolean,input2:Object", StringUtils.getStringForList(variables));
        assertEquals("input1ßvalue,input2ßvalue", StringUtils.getStringForList(attributes, null));
        assertEquals("input1ßvalue,input2ßvalue", StringUtils.getStringForList(attributes, ""));
        assertEquals("input1ßvalueØinput2ßvalue", StringUtils.getStringForList(attributes, "Ø"));
    }

    @Test
    public void testEmptyEncode() {
        assertNull(StringUtils.encode(null));

        assertSame(emptyString, StringUtils.encode(emptyString));
        verify(url, never()).encode(anyString());
    }

    @Test
    public void testEncode() {
        assertEquals(testString, StringUtils.encode(testString));
        verify(url).encode(testString);
    }

    @Test
    public void testEmptyDecode() {
        assertNull(StringUtils.decode(null));

        assertSame(emptyString, StringUtils.decode(emptyString));
        verify(url, never()).decode(anyString());
    }

    @Test
    public void testDecode() {
        assertEquals(testString, StringUtils.decode(testString));
        verify(url).decode(testString);
    }

    @Test
    public void testUrlDecode() {
        assertEquals(testString, StringUtils.urlDecode(testString));
        verify(url).decodeQueryString(testString);
    }

    @Test
    public void testEmptyUrlDecode() {
        assertNull(StringUtils.urlDecode(null));

        assertSame(emptyString, StringUtils.urlDecode(emptyString));
        verify(url, never()).decodeQueryString(anyString());
    }

    @Test
    public void testUrlEncode() {
        assertEquals(testString, StringUtils.urlEncode(testString));
        verify(url).encodeQueryString(testString);
    }

    @Test
    public void testEmptyUrlEncode() {
        assertNull(StringUtils.urlEncode(null));

        assertSame(emptyString, StringUtils.urlEncode(emptyString));
        verify(url, never()).encodeQueryString(anyString());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty("Hello"));
    }

    @Test
    public void testNonEmpty() {
        assertFalse(StringUtils.nonEmpty(null));
        assertFalse(StringUtils.nonEmpty(""));
        assertTrue(StringUtils.nonEmpty("Hello"));
    }

    @Test
    public void testReplaceIllegalCharsAttribute() {
        String emptyString = "";
        assertSame(emptyString, replaceIllegalCharsAttribute(emptyString));
        assertEquals(null, replaceIllegalCharsAttribute(null));

        String stringToEncode = "< Valid \"&\" Symbols >";
        assertEquals("&lt; Valid &quot;&amp;&quot; Symbols &gt;", replaceIllegalCharsAttribute(stringToEncode));
    }
}
