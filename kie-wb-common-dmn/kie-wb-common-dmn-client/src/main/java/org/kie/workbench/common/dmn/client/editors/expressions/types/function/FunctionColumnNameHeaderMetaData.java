/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.ValueAndDataTypePopoverView;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.NameAndDataTypeHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;

public class FunctionColumnNameHeaderMetaData extends NameAndDataTypeHeaderMetaData {

    private static final String NAME_DATA_TYPE_COLUMN_GROUP = "FunctionColumnNameHeaderMetaData$NameAndDataTypeColumn";

    public FunctionColumnNameHeaderMetaData(final HasExpression hasExpression,
                                            final Optional<HasName> hasValue,
                                            final Consumer<HasName> clearValueConsumer,
                                            final BiConsumer<HasName, Name> setValueConsumer,
                                            final BiConsumer<HasTypeRef, QName> setTypeRefConsumer,
                                            final TranslationService translationService,
                                            final CellEditorControlsView.Presenter cellEditorControls,
                                            final ValueAndDataTypePopoverView.Presenter editor) {
        super(hasExpression,
              hasValue,
              clearValueConsumer,
              setValueConsumer,
              setTypeRefConsumer,
              translationService,
              cellEditorControls,
              editor);
    }

    @Override
    public String getColumnGroup() {
        return NAME_DATA_TYPE_COLUMN_GROUP;
    }

    @Override
    public String getPopoverTitle() {
        return translationService.getTranslation(DMNEditorConstants.FunctionEditor_EditExpression);
    }
}
