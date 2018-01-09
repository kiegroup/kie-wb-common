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

import java.math.BigInteger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.CellEditionCallback;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.ColumnFieldUpdater;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.TableEntry;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.uberfire.ext.widgets.table.client.UberfirePagedTable;

@Dependent
public class IntegerEditableColumnGenerator extends AbstractEditableColumnGenerator<Long> {

    @Inject
    public IntegerEditableColumnGenerator(TranslationService translationService) {
        super(translationService);
    }

    @Override
    public String[] getTypes() {
        return new String[]{BigInteger.class.getName(), Byte.class.getName(), Integer.class.getName(), Long.class.getName(), Short.class.getName()};
    }

    @Override
    protected Column<TableEntry<Long>, String> getEditableColumn(UberfirePagedTable<TableEntry<Long>> table,
                                                                 CellEditionCallback<Long> callback) {
        Column<TableEntry<Long>, String> column = new Column<TableEntry<Long>, String>(new EditTextCell()) {
            @Override
            public String getValue(TableEntry<Long> model) {
                if (model.getValue() == null) {
                    return "";
                }
                return model.getValue().toString();
            }
        };

        ColumnFieldUpdater<Long, String> updater = new ColumnFieldUpdater<Long, String>(table, column) {

            @Override
            protected boolean validate(String value,
                                       TableEntry<Long> model) {
                boolean validValue = true;
                if (value != null && !value.isEmpty()) {
                    try {
                        convert(value);
                    } catch (Exception ex) {
                        Window.alert(translationService.getTranslation(FormRenderingConstants.IntegerEditableColumnGeneratorInvalidNumber));
                        validValue = false;
                    }
                }
                return validValue;
            }

            @Override
            protected Long convert(String flatValue) {
                if (flatValue == null || flatValue.isEmpty()) {
                    return null;
                }
                return Long.decode(flatValue);
            }
        };

        updater.setCallback(callback);

        column.setFieldUpdater(updater);

        return column;
    }

    @Override
    protected Column<TableEntry<Long>, String> getReadOnlyColumn() {
        Column<TableEntry<Long>, String> column = new Column<TableEntry<Long>, String>(new TextCell()) {
            @Override
            public String getValue(TableEntry<Long> model) {
                if (model.getValue() == null) {
                    return "";
                }
                return model.getValue().toString();
            }
        };

        return column;
    }
}
