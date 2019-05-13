/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.assets.dropdown;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.submarine.IsSubmarine;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SubmarineKieAssetsDropdownTest extends AbstractKieAssetsDropdownTest {

    @Mock
    private IsSubmarine isSubmarine;

    @Mock
    private Consumer<List<KieAssetsDropdownItem>> kieAssetsConsumer;

    @Mock
    private SubmarineKieAssetsDropdownView viewlocalMock;

    @Mock
    private KieAssetsDropdown dropdownLocal;

    @Before
    public void setup() {
        when(isSubmarine.get()).thenReturn(false);
        dropdownLocal = spy(new SubmarineKieAssetsDropdown(viewlocalMock, isSubmarine, dataProviderMock) {
            {
                onValueChangeHandler = onValueChangeHandlerMock;
                this.kieAssets.addAll(assetList);
            }
        });
        commonSetup();
    }

    @Test
    public void testRegisterOnChangeHandler() {
        final Command command = mock(Command.class);

        getDropdown().registerOnChangeHandler(command);
        getDropdown().onValueChanged();

        verify(command).execute();
    }

    @Test
    public void testLoadAssetsWhenEnvIsSubmarine() {

        when(isSubmarine.get()).thenReturn(true);

        getDropdown().loadAssets();

        verify(getDropdown()).clear();
        verify(viewlocalMock).enableInputMode();
        verify(getViewMock()).initialize();
    }

    @Test
    public void testLoadAssetsWhenEnvIsNotSubmarine() {

        doReturn(kieAssetsConsumer).when((SubmarineKieAssetsDropdown) getDropdown()).getAssetListConsumer();

        getDropdown().loadAssets();

        verify(getDropdown()).clear();
        verify(viewlocalMock).enableDropdownMode();
        verify(dataProviderMock).getItems(kieAssetsConsumer);
    }

    @Test
    public void testInitialize() {
        getDropdown().initialize();
        verify(getViewMock()).refreshSelectPicker();
    }

    @Test
    public void testInitializeWhenItIsNotSubmarine() {
        when(isSubmarine.get()).thenReturn(true);
        getDropdown().initialize();
        verify(getViewMock(), never()).refreshSelectPicker();
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);
        when(getViewMock().getElement()).thenReturn(expectedElement);

        final HTMLElement actualElement = getDropdown().getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testGetValue() {
        final List<KieAssetsDropdownItem> kieAssets = IntStream.range(0, 4).mapToObj(i -> {
            final KieAssetsDropdownItem toReturn = mock(KieAssetsDropdownItem.class);
            when(toReturn.getValue()).thenReturn("item" + i);
            return toReturn;
        }).collect(Collectors.toList());

        when(getViewMock().getValue()).thenReturn("item2");
        ((SubmarineKieAssetsDropdown) getDropdown()).kieAssets.clear();
        ((SubmarineKieAssetsDropdown) getDropdown()).kieAssets.addAll(kieAssets);
        final Optional<KieAssetsDropdownItem> retrieved = getDropdown().getValue();
        assertTrue(retrieved.isPresent());
        assertEquals("item2", retrieved.get().getValue());
    }

    @Test
    public void testGetValueWhenOptionDoesNotExist() {
        ((SubmarineKieAssetsDropdown) getDropdown()).kieAssets.clear();
        assertFalse(getDropdown().getValue().isPresent());
    }

    @Test
    public void testGetValueWhenItIsSubmarine() {

        final String expectedValue = "value";

        when(isSubmarine.get()).thenReturn(true);
        when(getViewMock().getValue()).thenReturn(expectedValue);

        final Optional<KieAssetsDropdownItem> value = getDropdown().getValue();

        assertTrue(value.isPresent());
        assertEquals(expectedValue, value.get().getValue());
    }

    @Test
    public void getAssetListConsumer() {
        final List<KieAssetsDropdownItem> expectedDropdownItems = new ArrayList<>();
        ((SubmarineKieAssetsDropdown) getDropdown()).getAssetListConsumer().accept(expectedDropdownItems);
        verify(((SubmarineKieAssetsDropdown) getDropdown()), times(1)).assetListConsumerMethod(eq(expectedDropdownItems));
    }

    @Test
    public void assetListConsumerMethod() {
        ((SubmarineKieAssetsDropdown) getDropdown()).assetListConsumerMethod(assetList);
        assetList.forEach(item -> verify(getViewMock()).addValue(item));
        verify(getViewMock()).refreshSelectPicker();
        verify(getViewMock()).initialize();
    }

    @Override
    protected KieAssetsDropdown getDropdown() {
        return dropdownLocal;
    }

    @Override
    protected KieAssetsDropdown.View getViewMock() {
        return viewlocalMock;
    }
}
