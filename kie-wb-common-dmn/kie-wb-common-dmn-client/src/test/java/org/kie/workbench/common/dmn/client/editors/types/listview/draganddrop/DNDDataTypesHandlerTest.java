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

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.NodeList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler.DNDContext;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler.ShiftStrategy;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItemView.UUID_ATTR;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler.ShiftStrategy.INSERT_INTO_HOVERED_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler.ShiftStrategy.INSERT_NESTED_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler.ShiftStrategy.INSERT_SIBLING_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler.ShiftStrategy.INSERT_TOP_LEVEL_DATA_TYPE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler.ShiftStrategy.INSERT_TOP_LEVEL_DATA_TYPE_AT_THE_TOP;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DATA_X_POSITION;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.DATA_Y_POSITION;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DNDDataTypesHandlerTest {

    @Mock
    private DataTypeStore dataTypeStore;

    @Mock
    private DataTypeManager dataTypeManager;

    @Mock
    private ItemDefinitionStore itemDefinitionStore;

    @Mock
    private DataTypeList dataTypeList;

    @Mock
    private DNDListComponent dndListComponent;

    private DNDDataTypesHandler handler;

    @Before
    public void setup() {
        handler = spy(new DNDDataTypesHandler(dataTypeStore, dataTypeManager, itemDefinitionStore));
        handler.init(dataTypeList);

        when(dataTypeList.getDNDListComponent()).thenReturn(dndListComponent);
    }

    @Test
    public void testOnDropDataType() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);
        final DataType current = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final DNDContext context = mock(DNDContext.class);
        final ShiftStrategy strategy = INSERT_INTO_HOVERED_DATA_TYPE;

        doNothing().when(handler).logError(anyString());
        doReturn(context).when(handler).makeDndContext(currentElement, hoverElement);
        when(context.getCurrentDataType()).thenReturn(Optional.of(current));
        when(context.getReference()).thenReturn(Optional.of(reference));
        when(context.getStrategy()).thenReturn(strategy);

        handler.onDropDataType(currentElement, hoverElement);

        verify(handler).shiftCurrentByReference(current, reference, strategy);
    }

    @Test
    public void testOnDropDataTypeWhenCurrentIsNotPresent() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);
        final DataType reference = mock(DataType.class);
        final DNDContext context = mock(DNDContext.class);

        doNothing().when(handler).logError(anyString());
        doReturn(context).when(handler).makeDndContext(currentElement, hoverElement);
        when(context.getCurrentDataType()).thenReturn(Optional.empty());
        when(context.getReference()).thenReturn(Optional.of(reference));

        handler.onDropDataType(currentElement, hoverElement);

        verify(handler, never()).shiftCurrentByReference(any(), any(), any());
    }

    @Test
    public void testOnDropDataTypeWhenReferenceIsNotPresent() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);
        final DataType current = mock(DataType.class);
        final DNDContext context = mock(DNDContext.class);

        doNothing().when(handler).logError(anyString());
        doReturn(context).when(handler).makeDndContext(currentElement, hoverElement);
        when(context.getCurrentDataType()).thenReturn(Optional.of(current));
        when(context.getReference()).thenReturn(Optional.empty());

        handler.onDropDataType(currentElement, hoverElement);

        verify(handler, never()).shiftCurrentByReference(any(), any(), any());
    }

    @Test
    public void testOnDropDataTypeWhenAnErrorIsRaised() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);

        doNothing().when(handler).logError(anyString());

        handler.onDropDataType(currentElement, hoverElement);

        verify(handler).logError("Drag-n-Drop error. Check 'DNDDataTypesHandler'.");
    }

    @Test
    public void testShiftCurrentByReferenceWhenCurrentIsCollapsedAndItIsTopLevelShiftOperation() {

        final DataType current = mock(DataType.class);
        final DataType clone = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final ShiftStrategy strategy = INSERT_INTO_HOVERED_DATA_TYPE;
        final String referenceHash = "referenceHash";
        final DataTypeListItem oldItem = mock(DataTypeListItem.class);
        final DataTypeListItem referenceItem = mock(DataTypeListItem.class);
        final DataTypeListItem newItem = mock(DataTypeListItem.class);

        doReturn(clone).when(handler).cloneDataType(current);
        doReturn(true).when(handler).isTopLevelShiftOperation(current, strategy);
        when(dataTypeList.calculateHash(reference)).thenReturn(referenceHash);
        when(dataTypeList.findItem(current)).thenReturn(Optional.of(oldItem));
        when(dataTypeList.findItem(clone)).thenReturn(Optional.of(newItem));
        when(dataTypeList.findItemByDataTypeHash(referenceHash)).thenReturn(Optional.of(referenceItem));
        when(oldItem.isCollapsed()).thenReturn(true);

        handler.shiftCurrentByReference(current, reference, strategy);

        verify(newItem).collapse();
        verify(oldItem).destroyWithoutDependentTypes();
        verify(referenceItem).insertNestedField(clone);
    }

    @Test
    public void testShiftCurrentByReferenceWhenCurrentIsNotCollapsedAndItIsNotTopLevelShiftOperation() {

        final DataType current = mock(DataType.class);
        final DataType clone = mock(DataType.class);
        final DataType reference = mock(DataType.class);
        final ShiftStrategy strategy = INSERT_INTO_HOVERED_DATA_TYPE;
        final String referenceHash = "referenceHash";
        final DataTypeListItem oldItem = mock(DataTypeListItem.class);
        final DataTypeListItem referenceItem = mock(DataTypeListItem.class);
        final DataTypeListItem newItem = mock(DataTypeListItem.class);

        doReturn(clone).when(handler).cloneDataType(current);
        doReturn(false).when(handler).isTopLevelShiftOperation(current, strategy);
        when(dataTypeList.calculateHash(reference)).thenReturn(referenceHash);
        when(dataTypeList.findItem(current)).thenReturn(Optional.of(oldItem));
        when(dataTypeList.findItem(clone)).thenReturn(Optional.of(newItem));
        when(dataTypeList.findItemByDataTypeHash(referenceHash)).thenReturn(Optional.of(referenceItem));
        when(oldItem.isCollapsed()).thenReturn(false);

        handler.shiftCurrentByReference(current, reference, strategy);

        verify(newItem).expand();
        verify(oldItem).destroyWithDependentTypes();
        verify(referenceItem).insertNestedField(clone);
    }

    @Test
    public void testDNDContextGetReferenceWhenHoveredDataTypeIsPresent() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);
        final Optional<DataType> hoverDataType = Optional.of(mock(DataType.class));
        final String uuid = "0000-1111-2222-3333";

        when(hoverElement.getAttribute(UUID_ATTR)).thenReturn(uuid);
        when(dataTypeStore.get(uuid)).thenReturn(hoverDataType.get());

        final Optional<DataType> reference = handler.makeDndContext(currentElement, hoverElement).getReference();

        assertEquals(hoverDataType, reference);
    }

    @Test
    public void testDNDContextGetReferenceWhenHoveredDataTypeIsPresentButHoveredDataTypeIsReadOnly() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);
        final Element previousElement = mock(Element.class);
        final Optional<DataType> hoverDataType = Optional.of(mock(DataType.class));
        final Optional<DataType> previousDataType = Optional.of(mock(DataType.class));
        final String hoverUUID = "0000-0000-0000-0000";
        final String previousUUID = "1111-1111-1111-1111";

        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.of(previousElement));
        when(dataTypeStore.get(previousUUID)).thenReturn(previousDataType.get());
        when(dataTypeStore.get(hoverUUID)).thenReturn(hoverDataType.get());
        when(previousElement.getAttribute(UUID_ATTR)).thenReturn(previousUUID);
        when(hoverElement.getAttribute(UUID_ATTR)).thenReturn(hoverUUID);
        when(hoverDataType.get().isReadOnly()).thenReturn(true);

        final Optional<DataType> reference = handler.makeDndContext(currentElement, hoverElement).getReference();

        assertEquals(previousDataType, reference);
    }

    @Test
    public void testDNDContextGetReferenceWhenPreviousDataTypeIsPresent() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);
        final Element previousElement = mock(Element.class);
        final Optional<DataType> previousDataType = Optional.of(mock(DataType.class));
        final String uuid = "0000-1111-2222-3333";

        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.of(previousElement));
        when(dataTypeStore.get(uuid)).thenReturn(previousDataType.get());
        when(previousElement.getAttribute(UUID_ATTR)).thenReturn(uuid);

        final Optional<DataType> reference = handler.makeDndContext(currentElement, hoverElement).getReference();

        assertEquals(previousDataType, reference);
    }

    @Test
    public void testDNDContextGetReferenceWhenCurrentDataTypeIsPresent() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);
        final Element firstElement = mock(Element.class);
        final Element hiddenElement = mock(Element.class);
        final HTMLElement dragArea = mock(HTMLElement.class);
        final Optional<DataType> currentDataType = Optional.of(mock(DataType.class));
        final Optional<DataType> firstDataType = Optional.of(mock(DataType.class));
        final String currentUUID = "0000-0000-0000-0000";
        final String firstUUID = "1111-1111-1111-1111";

        dragArea.childNodes = spy(new NodeList<>());
        dragArea.childNodes.length = 3;

        doReturn(hiddenElement).when(dragArea.childNodes).getAt(0);
        doReturn(currentElement).when(dragArea.childNodes).getAt(1);
        doReturn(firstElement).when(dragArea.childNodes).getAt(2);

        when(hiddenElement.getAttribute(DATA_Y_POSITION)).thenReturn("-1");
        when(currentElement.getAttribute(DATA_Y_POSITION)).thenReturn("0");
        when(firstElement.getAttribute(DATA_Y_POSITION)).thenReturn("1");
        when(hiddenElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(currentElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(firstElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(dndListComponent.getDragArea()).thenReturn(dragArea);
        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.empty());
        when(dataTypeStore.get(currentUUID)).thenReturn(currentDataType.get());
        when(dataTypeStore.get(firstUUID)).thenReturn(firstDataType.get());
        when(currentDataType.get().getName()).thenReturn("Current Data Type");
        when(firstDataType.get().getName()).thenReturn("First Data Type");
        when(currentElement.getAttribute(UUID_ATTR)).thenReturn(currentUUID);
        when(firstElement.getAttribute(UUID_ATTR)).thenReturn(firstUUID);

        final Optional<DataType> reference = handler.makeDndContext(currentElement, hoverElement).getReference();

        assertEquals(firstDataType, reference);
    }

    @Test
    public void testDNDContextGetReferenceWhenDataTypeListIsNotInitialized() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);
        final Element previousElement = mock(Element.class);
        final Optional<DataType> previousDataType = Optional.of(mock(DataType.class));
        final String uuid = "0000-1111-2222-3333";

        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.of(previousElement));
        when(dataTypeStore.get(uuid)).thenReturn(previousDataType.get());
        when(previousElement.getAttribute(UUID_ATTR)).thenReturn(uuid);

        handler.init(null);

        assertThatThrownBy(() -> handler.makeDndContext(currentElement, hoverElement).getReference())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("'DNDDataTypesHandler' must be initialized with a 'DataTypeList' instance.");
    }

    @Test
    public void testDNDContextGetReferenceWhenDNDListComponentIsNotInitialized() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);
        final Element previousElement = mock(Element.class);
        final Optional<DataType> previousDataType = Optional.of(mock(DataType.class));
        final String uuid = "0000-1111-2222-3333";

        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.of(previousElement));
        when(dataTypeStore.get(uuid)).thenReturn(previousDataType.get());
        when(previousElement.getAttribute(UUID_ATTR)).thenReturn(uuid);

        when(dataTypeList.getDNDListComponent()).thenReturn(null);

        assertThatThrownBy(() -> handler.makeDndContext(currentElement, hoverElement).getReference())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("'DNDDataTypesHandler' must be initialized with a 'DNDListComponent' instance.");
    }

    @Test
    public void testDNDContextGetReferenceWhenItIsNotPresent() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);

        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.empty());

        final Optional<DataType> reference = handler.makeDndContext(currentElement, hoverElement).getReference();

        assertFalse(reference.isPresent());
    }

    @Test
    public void testGetStrategyInsertIntoHoveredDataType() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);
        final DNDContext context = handler.makeDndContext(currentElement, hoverElement);
        final Optional<DataType> hoverDataType = Optional.of(mock(DataType.class));
        final String uuid = "0000-1111-2222-3333";

        when(hoverElement.getAttribute(UUID_ATTR)).thenReturn(uuid);
        when(dataTypeStore.get(uuid)).thenReturn(hoverDataType.get());
        loadReferenceContext(context);

        final ShiftStrategy actualShiftStrategy = context.getStrategy();
        final ShiftStrategy expectedShiftStrategy = INSERT_INTO_HOVERED_DATA_TYPE;

        assertEquals(expectedShiftStrategy, actualShiftStrategy);
    }

    @Test
    public void testGetStrategyInsertIntoHoveredDataTypeWhenHoveredDataTypeIsReadOnly() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);
        final DNDContext context = handler.makeDndContext(currentElement, hoverElement);
        final Optional<DataType> hoverDataType = Optional.of(mock(DataType.class));
        final String uuid = "0000-1111-2222-3333";

        when(hoverDataType.get().isReadOnly()).thenReturn(true);
        when(hoverElement.getAttribute(UUID_ATTR)).thenReturn(uuid);
        when(dataTypeStore.get(uuid)).thenReturn(hoverDataType.get());
        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.empty());
        loadReferenceContext(context);

        final ShiftStrategy actualShiftStrategy = context.getStrategy();
        final ShiftStrategy expectedShiftStrategy = INSERT_TOP_LEVEL_DATA_TYPE_AT_THE_TOP;

        assertEquals(expectedShiftStrategy, actualShiftStrategy);
    }

    @Test
    public void testGetStrategyInsertTopLevelDataTypeAtTheTop() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);
        final DNDContext context = handler.makeDndContext(currentElement, hoverElement);

        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.empty());
        loadReferenceContext(context);

        final ShiftStrategy actualShiftStrategy = context.getStrategy();
        final ShiftStrategy expectedShiftStrategy = INSERT_TOP_LEVEL_DATA_TYPE_AT_THE_TOP;

        assertEquals(expectedShiftStrategy, actualShiftStrategy);
    }

    @Test
    public void testGetStrategyInsertTopLevelDataType() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);
        final Element previousElement = mock(Element.class);
        final DNDContext context = handler.makeDndContext(currentElement, hoverElement);
        final String uuid = "0000-1111-2222-3333";
        final Optional<DataType> previousDataType = Optional.of(mock(DataType.class));

        when(dataTypeStore.get(uuid)).thenReturn(previousDataType.get());
        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.of(previousElement));
        when(currentElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(previousElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(previousElement.getAttribute(UUID_ATTR)).thenReturn(uuid);
        loadReferenceContext(context);

        final ShiftStrategy actualShiftStrategy = context.getStrategy();
        final ShiftStrategy expectedShiftStrategy = INSERT_TOP_LEVEL_DATA_TYPE;

        assertEquals(expectedShiftStrategy, actualShiftStrategy);
    }

    @Test
    public void testGetStrategyInsertNestedDataType() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);
        final Element previousElement = mock(Element.class);
        final DNDContext context = handler.makeDndContext(currentElement, hoverElement);
        final String uuid = "0000-1111-2222-3333";
        final Optional<DataType> previousDataType = Optional.of(mock(DataType.class));

        when(dataTypeStore.get(uuid)).thenReturn(previousDataType.get());
        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.of(previousElement));
        when(currentElement.getAttribute(DATA_X_POSITION)).thenReturn("1");
        when(previousElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(previousElement.getAttribute(UUID_ATTR)).thenReturn(uuid);
        loadReferenceContext(context);

        final ShiftStrategy actualShiftStrategy = context.getStrategy();
        final ShiftStrategy expectedShiftStrategy = INSERT_NESTED_DATA_TYPE;

        assertEquals(expectedShiftStrategy, actualShiftStrategy);
    }

    @Test
    public void testGetStrategyInsertNestedDataTypeWhenPreviousDataTypeIsReadOnly() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);
        final Element previousElement = mock(Element.class);
        final DNDContext context = handler.makeDndContext(currentElement, hoverElement);
        final String uuid = "0000-1111-2222-3333";
        final Optional<DataType> previousDataType = Optional.of(mock(DataType.class));

        when(previousDataType.get().isReadOnly()).thenReturn(true);
        when(dataTypeStore.get(uuid)).thenReturn(previousDataType.get());
        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.of(previousElement));
        when(currentElement.getAttribute(DATA_X_POSITION)).thenReturn("1");
        when(previousElement.getAttribute(DATA_X_POSITION)).thenReturn("0");
        when(previousElement.getAttribute(UUID_ATTR)).thenReturn(uuid);
        loadReferenceContext(context);

        final ShiftStrategy actualShiftStrategy = context.getStrategy();
        final ShiftStrategy expectedShiftStrategy = INSERT_SIBLING_DATA_TYPE;

        assertEquals(expectedShiftStrategy, actualShiftStrategy);
    }

    @Test
    public void testGetStrategyInsertSiblingDataType() {

        final Element currentElement = mock(Element.class);
        final Element hoverElement = mock(Element.class);
        final Element previousElement = mock(Element.class);
        final DNDContext context = handler.makeDndContext(currentElement, hoverElement);
        final String uuid = "0000-1111-2222-3333";
        final Optional<DataType> previousDataType = Optional.of(mock(DataType.class));

        when(dataTypeStore.get(uuid)).thenReturn(previousDataType.get());
        when(dndListComponent.getPreviousElement(any(), any())).thenReturn(Optional.of(previousElement));
        when(currentElement.getAttribute(DATA_X_POSITION)).thenReturn("1");
        when(previousElement.getAttribute(DATA_X_POSITION)).thenReturn("1");
        when(previousElement.getAttribute(UUID_ATTR)).thenReturn(uuid);
        loadReferenceContext(context);

        final ShiftStrategy actualShiftStrategy = context.getStrategy();
        final ShiftStrategy expectedShiftStrategy = INSERT_SIBLING_DATA_TYPE;

        assertEquals(expectedShiftStrategy, actualShiftStrategy);
    }

    @Test
    public void testIsTopLevelShiftOperationWhenDataTypeIsNotTopLevel() {

        final DataType dataType = mock(DataType.class);
        final ShiftStrategy shiftStrategy = INSERT_TOP_LEVEL_DATA_TYPE_AT_THE_TOP;

        when(dataType.isTopLevel()).thenReturn(false);

        assertFalse(handler.isTopLevelShiftOperation(dataType, shiftStrategy));
    }

    @Test
    public void testIsTopLevelShiftOperationWhenDataTypeIsTopLevelAndShiftStrategyIsNotTopLevel() {

        final DataType dataType = mock(DataType.class);

        when(dataType.isTopLevel()).thenReturn(true);

        assertFalse(handler.isTopLevelShiftOperation(dataType, INSERT_NESTED_DATA_TYPE));
        assertFalse(handler.isTopLevelShiftOperation(dataType, INSERT_SIBLING_DATA_TYPE));
        assertFalse(handler.isTopLevelShiftOperation(dataType, INSERT_INTO_HOVERED_DATA_TYPE));
    }

    @Test
    public void testIsTopLevelShiftOperationWhenDataTypeIsTopLevelAndShiftStrategyIsTopLevel() {

        final DataType dataType = mock(DataType.class);

        when(dataType.isTopLevel()).thenReturn(true);

        assertTrue(handler.isTopLevelShiftOperation(dataType, INSERT_TOP_LEVEL_DATA_TYPE_AT_THE_TOP));
        assertTrue(handler.isTopLevelShiftOperation(dataType, INSERT_TOP_LEVEL_DATA_TYPE));
    }

    private void loadReferenceContext(final DNDContext context) {
        context.getReference();
    }
}
