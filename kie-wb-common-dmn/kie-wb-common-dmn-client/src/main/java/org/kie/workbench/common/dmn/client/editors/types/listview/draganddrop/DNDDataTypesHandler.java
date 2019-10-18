/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.Node;
import elemental2.dom.NodeList;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.Position;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;

import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler.ShiftStrategy.INSERT_INTO_HOVERED_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler.ShiftStrategy.INSERT_NESTED_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler.ShiftStrategy.INSERT_SIBLING_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler.ShiftStrategy.INSERT_TOP_LEVEL_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler.ShiftStrategy.INSERT_TOP_LEVEL_DATA_TYPE_AT_THE_TOP;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.HIDDEN_Y_POSITION;

public class DNDDataTypesHandler {

    private final DataTypeStore dataTypeStore;

    private final DataTypeManager dataTypeManager;

    private final ItemDefinitionStore itemDefinitionStore;

    private DataTypeList dataTypeList;

    @Inject
    public DNDDataTypesHandler(final DataTypeStore dataTypeStore,
                               final DataTypeManager dataTypeManager,
                               final ItemDefinitionStore itemDefinitionStore) {
        this.dataTypeStore = dataTypeStore;
        this.dataTypeManager = dataTypeManager;
        this.itemDefinitionStore = itemDefinitionStore;
    }

    public void init(final DataTypeList dataTypeList) {
        this.dataTypeList = dataTypeList;
    }

    public void onDropDataType(final Element currentElement,
                               final Element hoverElement) {
        try {

            final DNDContext dndContext = makeDndContext(currentElement, hoverElement);
            final Optional<DataType> current = dndContext.getCurrentDataType();
            final Optional<DataType> reference = dndContext.getReference();

            if (current.isPresent() && reference.isPresent()) {
                shiftCurrentByReference(current.get(), reference.get(), dndContext.getStrategy());
            }
        } catch (final Exception e) {
            logError("Drag-n-Drop error. Check '" + DNDDataTypesHandler.class.getSimpleName() + "'.");
        }
    }

    void shiftCurrentByReference(final DataType current,
                                 final DataType reference,
                                 final ShiftStrategy shiftStrategy) {

        final String referenceHash = getDataTypeList().calculateHash(reference);
        final DataType clone = cloneDataType(current);
        final Optional<DataTypeListItem> currentItem = getDataTypeList().findItem(current);
        final Boolean isCurrentItemCollapsed = currentItem.map(DataTypeListItem::isCollapsed).orElse(false);

        // destroy current data type
        currentItem.ifPresent(item -> item.destroy().execute());

        // create new data type by using shift strategy
        getDataTypeList().findItemByDataTypeHash(referenceHash).ifPresent(ref -> {
            shiftStrategy.consumer.accept(ref, clone);
        });

        // keep the state of the new data type item consistent
        getDataTypeList().findItem(clone).ifPresent(item -> {
            if (isCurrentItemCollapsed) {
                item.collapse();
            } else {
                item.expand();
            }
        });
    }

    DataType cloneDataType(final DataType current) {

        final String currentUUID = current.getUUID();
        final ItemDefinition itemDefinition = itemDefinitionStore.get(currentUUID);

        return dataTypeManager.from(itemDefinition).get();
    }

    DNDContext makeDndContext(final Element currentElement,
                              final Element hoverElement) {
        return new DNDContext(currentElement, hoverElement);
    }

    private DataTypeList getDataTypeList() {
        return Optional
                .ofNullable(dataTypeList)
                .orElseThrow(() -> {
                    final String errorMessage = "'DNDDataTypesHandler' must be initialized with a 'DataTypeList' instance.";
                    return new UnsupportedOperationException(errorMessage);
                });
    }

    private DNDListComponent getDndListComponent() {
        return Optional
                .ofNullable(getDataTypeList().getDNDListComponent())
                .orElseThrow(() -> {
                    final String errorMessage = "'DNDDataTypesHandler' must be initialized with a 'DNDListComponent' instance.";
                    return new UnsupportedOperationException(errorMessage);
                });
    }

    void logError(final String message) {
        DomGlobal.console.error(message);
    }

    class DNDContext {

        private final Element currentElement;

        private final Element hoverElement;

        private Element previousElement;

        private DataType current;

        private DataType hovered;

        private DataType previous;

        DNDContext(final Element currentElement,
                   final Element hoverElement) {

            this.currentElement = currentElement;
            this.hoverElement = hoverElement;
            this.current = getDataType(currentElement);
        }

        Optional<DataType> getReference() {

            if (reloadHoveredDataType().isPresent()) {
                return getHoveredDataType();
            }

            if (reloadPreviousDataType().isPresent()) {
                return getPreviousDataType();
            }

            if (getCurrentDataType().isPresent()) {
                return getFirstDataType(getCurrentDataType().get());
            }

            return Optional.empty();
        }

        ShiftStrategy getStrategy() {

            if (getHoveredDataType().isPresent()) {
                return INSERT_INTO_HOVERED_DATA_TYPE;
            }

            if (!getPreviousDataType().isPresent()) {
                return INSERT_TOP_LEVEL_DATA_TYPE_AT_THE_TOP;
            }

            final int currentElementLevel = Position.getX(currentElement);
            final int previousElementLevel = Position.getX(previousElement);

            if (currentElementLevel == 0) {
                return INSERT_TOP_LEVEL_DATA_TYPE;
            } else if (previousElementLevel < currentElementLevel) {
                return INSERT_NESTED_DATA_TYPE;
            } else {
                return INSERT_SIBLING_DATA_TYPE;
            }
        }

        private Optional<DataType> reloadHoveredDataType() {
            hovered = getDataType(hoverElement);
            return getHoveredDataType();
        }

        private Optional<DataType> reloadPreviousDataType() {
            previousElement = getPreviousElement(currentElement);
            previous = getDataType(previousElement);
            return getPreviousDataType();
        }

        Optional<DataType> getCurrentDataType() {
            return Optional.ofNullable(current);
        }

        private Optional<DataType> getHoveredDataType() {
            return Optional.ofNullable(hovered);
        }

        private Optional<DataType> getPreviousDataType() {
            return Optional.ofNullable(previous);
        }

        private DataType getDataType(final Element element) {
            return element == null ? null : dataTypeStore.get(element.getAttribute(UUID_ATTR));
        }

        private Optional<DataType> getFirstDataType(final DataType current) {

            final NodeList<Node> nodes = getDndListComponent().getDragArea().childNodes;

            for (int i = 0; i < nodes.length; i++) {
                final Element element = (Element) nodes.getAt(i);
                if (Position.getY(element) > HIDDEN_Y_POSITION && Position.getX(element) == 0) {
                    final DataType dataType = getDataType(element);
                    if (dataType != null && !Objects.equals(current.getName(), dataType.getName())) {
                        return Optional.of(dataType);
                    }
                }
            }

            return Optional.empty();
        }

        private Element getPreviousElement(final Element reference) {
            return getDndListComponent()
                    .getPreviousElement(reference, element -> Position.getX(element) <= Position.getX(reference))
                    .orElse(null);
        }
    }

    enum ShiftStrategy {

        INSERT_TOP_LEVEL_DATA_TYPE_AT_THE_TOP(DataTypeListItem::insertFieldAbove),
        INSERT_INTO_HOVERED_DATA_TYPE(DataTypeListItem::insertNestedField),
        INSERT_TOP_LEVEL_DATA_TYPE(DataTypeListItem::insertFieldBelow),
        INSERT_SIBLING_DATA_TYPE(DataTypeListItem::insertFieldBelow),
        INSERT_NESTED_DATA_TYPE(DataTypeListItem::insertNestedField);

        private final BiConsumer<DataTypeListItem, DataType> consumer;

        ShiftStrategy(final BiConsumer<DataTypeListItem, DataType> consumer) {
            this.consumer = consumer;
        }
    }
}
