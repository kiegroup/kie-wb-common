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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.editors;

import java.util.List;
import java.util.function.Consumer;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.handler.property.PropertyChangeHandler;
import org.jboss.errai.databinding.client.components.ListComponent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.listitems.ImportListItemView;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public abstract class ImportsEditorViewImplTest<T, U extends ImportListItemView<T>> {

    protected ImportsEditorViewImpl<T, U> tested;
    protected DataBinder<List<T>> dataBinder;
    protected ListComponent<T, U> listComponent;
    protected TableElement tableElement;
    protected Button button;
    protected ImportsEditorView.Presenter<T> presenter;
    List<T> emptyList;
    List<T> filledList;

    @Before
    public void setUp() {
        GwtMockito.initMocks(this);

        emptyList = mock(List.class);
        when(emptyList.isEmpty()).thenReturn(true);
        filledList = mock(List.class);
        when(filledList.isEmpty()).thenReturn(false);

        dataBinder = mock(DataBinder.class);
        when(dataBinder.getModel()).thenReturn(emptyList);

        listComponent = mock(ListComponent.class);

        Style style = mock(Style.class);
        tableElement = mock(TableElement.class);
        when(tableElement.getStyle()).thenReturn(style);

        button = mock(Button.class);
        presenter = mockPresenter();

        tested = spyEditor();
    }

    @Test
    public void init() {
        tested.init();

        verify(tested).getPresenter();
        verify(tested.getPresenter()).setView(tested);

        verify(tested).getAddButton();
        verify(tested.getAddButton()).addClickHandler(any(ClickHandler.class));

        verify(tested).getImportComponents();
        verify(tested.getImportComponents()).addComponentCreationHandler(any(Consumer.class));

        verify(tested).getImportsDataBinder();
        verify(tested.getImportsDataBinder()).addPropertyChangeHandler(any(PropertyChangeHandler.class));
    }

    @Test
    public void getImports() {
        tested.getImports();
        verify(tested).getImportsDataBinder();
        verify(tested.getImportsDataBinder()).getModel();
    }

    @Test
    public void setImports() {
        tested.setImports(emptyList);
        verify(tested, times(2)).getImportsDataBinder();
        verify(tested.getImportsDataBinder()).setModel(emptyList);
        verify(tested).updateImportsTable();
    }

    @Test
    public void getImportsDataBinder() {
        DataBinder<List<T>> result = tested.getImportsDataBinder();
        Assert.assertEquals(dataBinder, result);
    }

    @Test
    public void getImportComponents() {
        ListComponent<T, U> result = tested.getImportComponents();
        assertEquals(listComponent, result);
    }

    @Test
    public void getImportsTable() {
        TableElement result = tested.getImportsTable();
        assertEquals(tableElement, result);
    }

    @Test
    public void getAddButton() {
        Button result = tested.getAddButton();
        assertEquals(button, result);
    }

    @Test
    public void getPresenter() {
        ImportsEditorView.Presenter<T> result = tested.getPresenter();
        assertEquals(presenter, result);
    }

    @Test
    public void hideImportsTable() {
        tested.hideImportsTable();
        verify(tested).getImportsTable();
        verify(tested.getImportsTable()).getStyle();
        verify(tested.getImportsTable().getStyle()).setDisplay(Style.Display.NONE);
    }

    @Test
    public void showImportsTable() {
        tested.showImportsTable();
        verify(tested).getImportsTable();
        verify(tested.getImportsTable()).getStyle();
        verify(tested.getImportsTable().getStyle()).setDisplay(Style.Display.TABLE);
    }

    @Test
    public void updateImportsTable() {
        when(dataBinder.getModel()).thenReturn(emptyList);
        tested.updateImportsTable();
        when(dataBinder.getModel()).thenReturn(filledList);
        tested.updateImportsTable();
        verify(tested).hideImportsTable();
        verify(tested).showImportsTable();
    }

    protected abstract ImportsEditorViewImpl<T, U> spyEditor();

    protected abstract ImportsEditorView.Presenter<T> mockPresenter();
}