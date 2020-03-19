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

import org.kie.workbench.common.dmn.api.definition.HasValue;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditablePopupHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;

public class RuleAnnotationClauseColumnHeaderMetaData extends EditablePopupHeaderMetaData<HasValueSelectorControl, ValuePopoverView.Presenter> implements HasListSelectorControl,
                                                                                                                                                          HasCellEditorControls,
                                                                                                                                                          HasValueSelectorControl {

    public static final String COLUMN_GROUP = "RuleAnnotationClauseColumnHeaderMetaData$RuleAnnotationClauseColumn";

    private final BiFunction<Integer, Integer, List<ListSelectorItem>> listSelectorItemsSupplier;
    private final ListSelectorView.Presenter listSelector;
    private final Consumer<ListSelectorItem> listSelectorItemConsumer;
    private final HasValue<Name> hasValue;
    private final BiConsumer<HasValue, Name> setValueConsumer;

    public RuleAnnotationClauseColumnHeaderMetaData(final HasValue<Name> hasValue,
                                                    final BiFunction<Integer, Integer, List<ListSelectorItem>> listSelectorItemsSupplier,
                                                    final ListSelectorView.Presenter listSelector,
                                                    final CellEditorControlsView.Presenter cellEditorControls,
                                                    final ValuePopoverView.Presenter editor,
                                                    final Consumer<HasListSelectorControl.ListSelectorItem> listSelectorItemConsumer,
                                                    final BiConsumer<HasValue, Name> setValueConsumer) {
        super(cellEditorControls, editor);
        this.editor = editor;
        this.listSelectorItemsSupplier = listSelectorItemsSupplier;
        this.listSelector = listSelector;
        this.hasValue = hasValue;
        this.listSelectorItemConsumer = listSelectorItemConsumer;
        this.setValueConsumer = setValueConsumer;
    }

    @Override
    public String getColumnGroup() {
        return COLUMN_GROUP;
    }

    @Override
    public String getTitle() {
        return hasValue.getValue().getValue();
    }

    @Override
    public List<ListSelectorItem> getItems(final int uiRowIndex,
                                           final int uiColumnIndex) {
        return listSelectorItemsSupplier.apply(uiRowIndex, uiColumnIndex);
    }

    @Override
    public Optional<Editor> getEditor() {
        return Optional.of(listSelector);
    }

    @Override
    protected HasValueSelectorControl getPresenter() {
        return this;
    }

    @Override
    public void setValue(final String value) {
        hasValue.getValue().setValue(value);
        setValueConsumer.accept(hasValue, hasValue.getValue());
    }

    @Override
    public String getValue() {
        return hasValue.getValue().getValue();
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        listSelectorItemConsumer.accept(item);
    }
}
