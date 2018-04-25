/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.kie.workbench.common.widgets.decoratedgrid.client.widget.cells;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class PopupValueListDropDownEditCellTest {

    private PopupValueListDropDownEditCell cell;

    @Before
    public void setUp() throws Exception {
        cell = new PopupValueListDropDownEditCell(new String[0],
                                                  true,
                                                  false) {
        };
    }

    @Test
    public void testBasic() throws Exception {
        testMultiselectionSelected("\"a,b\"", new String[]{"\"a,b\""}, new Integer[]{0});
    }

    @Test
    public void setMultiselectionSelectedUnquotedBeginning() throws Exception {
        testMultiselectionSelected("a,\"a,b\"",
                                   new String[]{"a", "\"a,b\""},
                                   new Integer[]{0, 1});
    }

    @Test
    public void setMultiselectionSelectedUnquotedEnd() throws Exception {
        testMultiselectionSelected("\"a,b\",a",
                                   new String[]{"\"a,b\"", "a"},
                                   new Integer[]{0, 1});
    }

    @Test
    public void setMultiselectionSelectedUnquotedMiddle() throws Exception {
        testMultiselectionSelected("\"a,b\",a,\"c,d\"",
                                   new String[]{"\"a,b\"", "a", "\"c,d\""},
                                   new Integer[]{0, 1, 2});
    }

    @Test
    public void setMultiselectionSelectedStartQuoted() throws Exception {
        testMultiselectionSelected("\"a,b\"",
                                   new String[]{"\"a,b\"", "a", "\"c,d\""},
                                   new Integer[]{0});
    }

    @Test
    public void setMultiselectionSelectedStartNotQuoted() throws Exception {
        testMultiselectionSelected("a",
                                   new String[]{"a", "\"a,b\"", "\"c,d\""},
                                   new Integer[]{0});
    }

    @Test
    public void setMultiselectionSelectedMiddleQuoted() throws Exception {
        testMultiselectionSelected("\"a,b\"",
                                   new String[]{"a", "\"a,b\"", "\"c,d\""},
                                   new Integer[]{1});
    }

    @Test
    public void setMultiselectionSelectedMiddleNotQuoted() throws Exception {
        testMultiselectionSelected("a",
                                   new String[]{"\"a,b\"", "a", "\"c,d\""},
                                   new Integer[]{1});
    }

    @Test
    public void setMultiselectionSelectedEndQuoted() throws Exception {
        testMultiselectionSelected("\"c,d\"",
                                   new String[]{"a", "\"a,b\"", "\"c,d\""},
                                   new Integer[]{2});
    }

    @Test
    public void setMultiselectionSelectedEndNotQuoted() throws Exception {
        testMultiselectionSelected("a",
                                   new String[]{"\"a,b\"", "\"c,d\"", "a"},
                                   new Integer[]{2});
    }

    @Test
    public void setMultiselectionSelectedTwoOfThreeStart() throws Exception {
        testMultiselectionSelected("\"a,b\",a",
                                   new String[]{"\"a,b\"", "a", "\"c,d\""},
                                   new Integer[]{0, 1});
    }

    @Test
    public void setMultiselectionSelectedTwoOfThreeEnd() throws Exception {
        testMultiselectionSelected("a,\"c,d\"",
                                   new String[]{"\"a,b\"", "a", "\"c,d\""},
                                   new Integer[]{1, 2});
    }

    private void testMultiselectionSelected(final String selectionText,
                                            final String[] selectionItems,
                                            final Integer[] selectedIndexes) {
        doReturn(true).when(cell.listBox).isMultipleSelect();
        doReturn(selectionItems.length).when(cell.listBox).getItemCount();
        for (int i = 0; i < selectionItems.length; i++) {
            doReturn(selectionItems[i]).when(cell.listBox).getValue(i);
        }

        cell.startEditing(mock(Cell.Context.class),
                          mock(Element.class),
                          selectionText);

        for (int i = 0; i < selectedIndexes.length; i++) {

        }
        for (int i = 0; i < selectionItems.length; i++) {
            boolean isSelected = false;
            for (int j = 0; j < selectedIndexes.length; j++) {
                if (i == selectedIndexes[j]) {
                    isSelected = true;
                    break;
                }
            }
            verify(cell.listBox).setItemSelected(i, isSelected);
        }
    }
}