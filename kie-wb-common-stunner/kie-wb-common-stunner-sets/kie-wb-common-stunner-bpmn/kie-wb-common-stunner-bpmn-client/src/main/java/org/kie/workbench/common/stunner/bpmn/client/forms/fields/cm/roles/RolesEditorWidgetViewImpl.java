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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.cm.roles;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.client.widget.Table;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.KeyValueRow;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated("RolesEditorWidget.html#widget")
public class RolesEditorWidgetViewImpl extends Composite implements RolesEditorWidgetView,
                                                                    HasValue<String> {

    public static final String ROLE = "Role";
    public static final String CARDINALITY = "Cardinality";
    private String serializedRoles;

    private Presenter presenter;

    @Inject
    @DataField("addButton")
    protected Button addButton;

    @DataField("table")
    protected TableElement table = Document.get().createTableElement();

    @DataField("nameth")
    protected TableCellElement nameCol = Document.get().createTHElement();

    @DataField("datatypeth")
    protected TableCellElement cardinalityCol = Document.get().createTHElement();

    private boolean readOnly = false;

    public RolesEditorWidgetViewImpl() {

    }

    @Inject
    @DataField("rows")
    @Table(root = "tbody")
    protected ListWidget<KeyValueRow, RolesListItemWidgetView> rows;

    @Inject
    protected Event<NotificationEvent> notification;

    @Override
    public String getValue() {
        return serializedRoles;
    }

    @Override
    public void setValue(final String value) {
        doSetValue(value, false, true);
    }

    @Override
    public void setValue(final String value,
                         final boolean fireEvents) {
        doSetValue(value, fireEvents, false);
    }

    protected void doSetValue(final String value,
                              final boolean fireEvents,
                              final boolean initializeView) {
        final String oldValue = serializedRoles;
        serializedRoles = value;
        if (initializeView) {
            initView();
        }
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this,
                                            oldValue,
                                            serializedRoles);
        }
        setReadOnly(readOnly);
    }

    @Override
    public void doSave() {
        String newValue = presenter.serialize(getRows());
        setValue(newValue, true);
    }

    protected void initView() {
        setRows(presenter.deserialize(serializedRoles));
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
        addButton.setIcon(IconType.PLUS);
        nameCol.setInnerText(ROLE);
        cardinalityCol.setInnerText(CARDINALITY);
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
        addButton.setEnabled(!readOnly);
        for (int i = 0; i < getRowsCount(); i++) {
            getWidget(i).setReadOnly(readOnly);
        }
    }

    @Override
    public int getRowsCount() {
        return rows.getValue().size();
    }

    @Override
    public void setTableDisplayStyle() {
        table.getStyle().setDisplay(Style.Display.TABLE);
    }

    @Override
    public void setNoneDisplayStyle() {
        table.getStyle().setDisplay(Style.Display.NONE);
    }

    @Override
    public void setRows(final List<KeyValueRow> rows) {
        this.rows.setValue(rows);
        for (int i = 0; i < getRowsCount(); i++) {
            RolesListItemWidgetView widget = getWidget(i);
            widget.setParentWidget(presenter);
        }
    }

    @Override
    public List<KeyValueRow> getRows() {
        return rows.getValue();
    }

    @Override
    public RolesListItemWidgetView getWidget(final int index) {
        return rows.getComponent(index);
    }

    @EventHandler("addButton")
    public void handleAddVarButton(final ClickEvent e) {
        presenter.add();
    }

    @Override
    public void remove(final KeyValueRow row) {
        presenter.remove(row);
        if (getRows().isEmpty()) {
            setNoneDisplayStyle();
        }
    }
}
