/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasValue;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.RuleAnnotationClauseColumnHeaderMetaData.COLUMN_GROUP;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuleAnnotationClauseColumnHeaderMetaDataTest {

    private RuleAnnotationClauseColumnHeaderMetaData column;
    @Mock
    private HasValue hasValue;

    @Mock
    private BiFunction<Integer, Integer, List<HasListSelectorControl.ListSelectorItem>> listSelectorItemsSupplier;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private ValuePopoverView.Presenter editor;

    @Mock
    private Consumer<HasListSelectorControl.ListSelectorItem> listSelectorItemConsumer;

    @Mock
    private BiConsumer<HasValue, Name> setValueConsumer;

    @Before
    public void setup() {
        column = new RuleAnnotationClauseColumnHeaderMetaData(hasValue,
                                                              listSelectorItemsSupplier,
                                                              listSelector,
                                                              cellEditorControls,
                                                              editor,
                                                              listSelectorItemConsumer,
                                                              setValueConsumer);
    }

    @Test
    public void testGetColumnGroup() {

        final String actual = column.getColumnGroup();

        assertEquals(COLUMN_GROUP, actual);
    }

    @Test
    public void testGetTitle() {

        final String title = "the title";
        final Name name = mock(Name.class);

        when(hasValue.getValue()).thenReturn(name);
        when(name.getValue()).thenReturn(title);

        final String actual = column.getTitle();

        assertEquals(actual, title);
    }

    @Test
    public void testGetItems() {

        final int uiRowIndex = 5;
        final int uiColumnIndex = 6;
        final List<HasListSelectorControl.ListSelectorItem> expectedItems = mock(List.class);
        when(listSelectorItemsSupplier.apply(uiRowIndex, uiColumnIndex)).thenReturn(expectedItems);

        final List<HasListSelectorControl.ListSelectorItem> actualItems = column.getItems(uiRowIndex, uiColumnIndex);

        assertEquals(expectedItems, actualItems);
        verify(listSelectorItemsSupplier).apply(uiRowIndex, uiColumnIndex);
    }

    @Test
    public void testGetEditor() {

        final Optional<HasCellEditorControls.Editor> actual = column.getEditor();

        assertEquals(listSelector, actual.get());
    }

    @Test
    public void testSetValue() {

        final Name name = mock(Name.class);

        when(hasValue.getValue()).thenReturn(name);

        final String value = "the value";

        column.setValue(value);

        verify(name).setValue(value);
        verify(setValueConsumer).accept(hasValue, name);
    }

    @Test
    public void testGetValue() {

        final Name name = mock(Name.class);
        final String value = "the value";

        when(name.getValue()).thenReturn(value);
        when(hasValue.getValue()).thenReturn(name);

        final String actual = column.getValue();

        assertEquals(value, actual);
    }

    @Test
    public void testOnItemSelected() {

        final HasListSelectorControl.ListSelectorItem item = mock(HasListSelectorControl.ListSelectorItem.class);

        column.onItemSelected(item);

        verify(listSelectorItemConsumer).accept(item);
    }
}
