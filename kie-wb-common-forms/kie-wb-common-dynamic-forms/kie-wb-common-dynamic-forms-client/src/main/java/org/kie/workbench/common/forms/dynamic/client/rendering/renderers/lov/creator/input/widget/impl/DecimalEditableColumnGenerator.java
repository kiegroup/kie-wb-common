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

import java.math.BigDecimal;

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
public class DecimalEditableColumnGenerator extends AbstractEditableColumnGenerator<Double> {

    @Inject
    public DecimalEditableColumnGenerator(TranslationService translationService) {
        super(translationService = translationService);
    }

    @Override
    public String[] getTypes() {
        return new String[]{BigDecimal.class.getName(), Double.class.getName(), Float.class.getName()};
    }

    @Override
    protected Column<TableEntry<Double>, String> getEditableColumn(UberfirePagedTable<TableEntry<Double>> table,
                                                                   CellEditionCallback<Double> callback) {
        Column<TableEntry<Double>, String> column = new Column<TableEntry<Double>, String>(new EditTextCell()) {
            @Override
            public String getValue(TableEntry<Double> model) {
                if (model.getValue() == null) {
                    return "";
                }
                return model.getValue().toString();
            }
        };

        ColumnFieldUpdater<Double, String> updater = new ColumnFieldUpdater<Double, String>(table,
                                                                                            column) {

            @Override
            protected boolean validate(String value,
                                       TableEntry<Double> model) {
                if (value != null && !value.isEmpty()) {
                    try {
                        convert(value);
                        return true;
                    } catch (Exception ex) {
                        Window.alert(translationService.getTranslation(FormRenderingConstants.DecimalEditableColumnGeneratorInvalidNumber));
                        return false;
                    }
                }
                return true;
            }

            @Override
            protected Double convert(String flatValue) {
                if (flatValue == null || flatValue.isEmpty()) {
                    return null;
                }
                return Double.valueOf(flatValue);
            }
        };

        updater.setCallback(callback);

        column.setFieldUpdater(updater);

        return column;
    }

    @Override
    protected Column<TableEntry<Double>, String> getReadOnlyColumn() {
        Column<TableEntry<Double>, String> column = new Column<TableEntry<Double>, String>(new TextCell()) {
            @Override
            public String getValue(TableEntry<Double> model) {
                if (model.getValue() == null) {
                    return "";
                }
                return model.getValue().toString();
            }
        };

        return column;
    }
}
