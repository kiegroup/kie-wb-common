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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.user.cellview.client.Column;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.CellEditionCallback;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.ColumnFieldUpdater;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.TableEntry;
import org.uberfire.ext.widgets.table.client.CheckboxCellImpl;
import org.uberfire.ext.widgets.table.client.UberfirePagedTable;

@Dependent
public class BooleanEditableColumnGenerator extends AbstractEditableColumnGenerator<Boolean> {

    @Inject
    public BooleanEditableColumnGenerator(TranslationService translationService) {
        super(translationService);
    }

    @Override
    public String[] getTypes() {
        return new String[]{Boolean.class.getName()};
    }

    @Override
    protected Column<TableEntry<Boolean>, Boolean> getEditableColumn(UberfirePagedTable<TableEntry<Boolean>> table,
                                                                     CellEditionCallback<Boolean> callback) {

        Column<TableEntry<Boolean>, Boolean> column = new Column<TableEntry<Boolean>, Boolean>(new CheckboxCell()) {
            @Override
            public Boolean getValue(TableEntry<Boolean> model) {
                if (model.getValue() == null) {
                    model.setValue(Boolean.FALSE);
                }
                return Boolean.TRUE.equals(model.getValue());
            }
        };

        ColumnFieldUpdater<Boolean, Boolean> updater = new ColumnFieldUpdater<Boolean, Boolean>(table,
                                                                                                column) {

            @Override
            protected boolean validate(Boolean value,
                                       TableEntry<Boolean> model) {
                return true;
            }
        };

        updater.setCallback(callback);

        column.setFieldUpdater(updater);

        return column;
    }

    @Override
    protected Column<TableEntry<Boolean>, Boolean> getReadOnlyColumn() {
        Column<TableEntry<Boolean>, Boolean> column = new Column<TableEntry<Boolean>, Boolean>(new CheckboxCellImpl(true)) {
            @Override
            public Boolean getValue(TableEntry<Boolean> model) {
                return Boolean.TRUE.equals(model.getValue());
            }
        };

        return column;
    }
}
