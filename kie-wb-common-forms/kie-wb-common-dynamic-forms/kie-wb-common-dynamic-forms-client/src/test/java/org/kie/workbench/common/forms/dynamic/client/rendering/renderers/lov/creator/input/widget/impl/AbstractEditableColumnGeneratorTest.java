/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl;

import com.google.gwt.user.cellview.client.Column;
import org.assertj.core.api.Assertions;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.CellEditionCallback;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.TableEntry;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.ext.widgets.table.client.UberfirePagedTable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public abstract class AbstractEditableColumnGeneratorTest<TYPE, GENERATOR extends AbstractEditableColumnGenerator<TYPE>> {

    @Mock
    protected UberfirePagedTable<TableEntry<TYPE>> pagedTable;

    @Mock
    protected CellEditionCallback<TYPE> callback;

    @Mock
    protected TranslationService translationService;

    protected GENERATOR generator;

    @Before
    public void init() {
        generator = spy(getGeneratorInstance(translationService));
    }

    @Test
    public void testGetSupportedTypes() {
        Assertions.assertThat(generator.getTypes())
                .isNotNull()
                .isNotEmpty()
                .contains(getSupportedTypes());
    }

    @Test
    public void testGetEditableColumn() {

        generator.registerColumn(pagedTable, callback,false);

        verify(generator, never()).getReadOnlyColumn();
        verify(generator).getEditableColumn(pagedTable, callback);

        verify(translationService).getTranslation(FormRenderingConstants.EditableColumnGeneratorValueHeader);

        ArgumentCaptor<Column> columnArgumentCaptor = ArgumentCaptor.forClass(Column.class);

        verify(pagedTable).addColumn(columnArgumentCaptor.capture(), anyString());

        Column column = columnArgumentCaptor.getValue();

        assertNotNull(column);
        assertNotNull(column.getFieldUpdater());

    }

    @Test
    public void testGetReadOnlyColumn() {

        generator.registerColumn(pagedTable, callback,true);

        verify(generator, never()).getEditableColumn(pagedTable, callback);
        verify(generator).getReadOnlyColumn();

        verify(translationService).getTranslation(FormRenderingConstants.EditableColumnGeneratorValueHeader);

        ArgumentCaptor<Column> columnArgumentCaptor = ArgumentCaptor.forClass(Column.class);

        verify(pagedTable).addColumn(columnArgumentCaptor.capture(), anyString());

        Column column = columnArgumentCaptor.getValue();

        assertNotNull(column);
        assertNull(column.getFieldUpdater());

    }

    abstract GENERATOR getGeneratorInstance(TranslationService translationService);

    abstract String[] getSupportedTypes();
}
