/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.settings.util.select;

import java.util.Arrays;
import java.util.List;

import elemental2.dom.Element;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.util.select.KieSelectElement.Option;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class KieEnumSelectElementTest {

    @Mock
    private KieSelectElement kieSelectElement;

    @Mock
    private TranslationService translationService;

    private KieEnumSelectElement<TestEnum> kieEnumSelectElement;

    enum TestEnum {
        FOO,
        BAR;
    }

    @Before
    public void before() {
        kieEnumSelectElement = Mockito.spy(new KieEnumSelectElement<TestEnum>(kieSelectElement, translationService));
    }

    @Test
    public void testSetup() {

        final Element container = Mockito.spy(new Element());
        final List<Option> options = Arrays.asList(new Option("FOO", "foo"), new Option("Bar", "bar"));

        Mockito.doReturn(options).when(kieEnumSelectElement).buildOptions(Matchers.any());

        kieEnumSelectElement.setup(
                container,
                TestEnum.values(),
                TestEnum.FOO,
                value -> {
                });

        assertEquals(TestEnum.class, kieEnumSelectElement.componentType);
        Mockito.verify(kieSelectElement).setup(Matchers.eq(container), Matchers.eq(options), Matchers.eq("FOO"), Matchers.any());
    }

    @Test
    public void testBuildOptions() {
        Mockito.doReturn(new Option("A", "a")).when(kieEnumSelectElement).newOption(Matchers.any());

        final List<Option> options = kieEnumSelectElement.buildOptions(TestEnum.values());

        Assert.assertEquals(2, options.size());
        Assert.assertEquals("A", options.get(0).label);
        Assert.assertEquals("a", options.get(0).value);
        Assert.assertEquals("A", options.get(1).label);
        Assert.assertEquals("a", options.get(1).value);
    }

    @Test
    public void testNewOption() {
        Mockito.doReturn("A").when(kieEnumSelectElement).getLabel(Matchers.eq(TestEnum.FOO));

        final Option option = kieEnumSelectElement.newOption(TestEnum.FOO);

        Assert.assertEquals("A", option.label);
        Assert.assertEquals("FOO", option.value);
    }

    @Test
    public void testGetLabel() {
        Mockito.doReturn("A").when(translationService).format(Matchers.any());

        final String label = kieEnumSelectElement.getLabel(TestEnum.FOO);

        Assert.assertEquals("A", label);
    }

    @Test
    public void testToEnum() {
        kieEnumSelectElement.componentType = TestEnum.class;

        assertEquals(TestEnum.FOO, kieEnumSelectElement.toEnum("FOO"));
        assertEquals(TestEnum.BAR, kieEnumSelectElement.toEnum("BAR"));
    }

    @Test
    public void testGetValue() {
        Mockito.doReturn("FOO").when(kieSelectElement).getValue();
        kieEnumSelectElement.componentType = TestEnum.class;

        final TestEnum value = kieEnumSelectElement.getValue();

        Assert.assertEquals(TestEnum.FOO, value);
    }
}
