/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellTableDropDownDataValueMapProvider;
import org.mockito.Mock;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class PopupDropDownEditCellMultipleSelectTest {

    @Mock
    private AsyncPackageDataModelOracle dmo;

    private PopupDropDownEditCell cell;

    @Before
    public void setUp() throws Exception {
        cell = new PopupDropDownEditCell("Person",
                                         "name",
                                         dmo,
                                         mock(CellTableDropDownDataValueMapProvider.class),
                                         false) {
        };
    }

    @Test
    public void setMultiselectionSelected() throws Exception {
        doReturn(mock(DropDownData.class)).when(dmo).getEnums(anyString(),
                                                              anyString(),
                                                              anyMap());
        doReturn(true).when(cell.listBox).isMultipleSelect();
        doReturn(1).when(cell.listBox).getItemCount();
        doReturn("\"a,b\"").when(cell.listBox).getValue(0);

        cell.startEditing(mock(Cell.Context.class),
                          mock(Element.class),
                          "\"a,b\"");

        verify(cell.listBox).setItemSelected(0, true);
    }
}