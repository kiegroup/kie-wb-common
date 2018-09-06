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
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

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
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.KeyValueRow;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated("RolesEditorWidget.html")
public class RolesEditorWidgetViewImpl extends Composite implements RolesEditorWidgetView,
                                                                    HasValue<String> {

    private String serializedRoles;

    private Optional<Presenter> presenter;

    @Inject
    @DataField("addButton")
    protected Button addButton;

    private boolean readOnly = false;

    public RolesEditorWidgetViewImpl() {
        this.presenter = Optional.empty();
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
            ValueChangeEvent.fireIfNotEqual(this, oldValue, serializedRoles);
        }
        setReadOnly(readOnly);
    }

    @Override
    public void doSave() {
        presenter.map(p -> p.serialize(getRows())).ifPresent(newValue -> setValue(newValue, true));
    }

    @Override
    public void notifyModelChanged() {
        doSave();
    }

    protected void initView() {
        setRows(presenter.map(p -> p.deserialize(serializedRoles)).orElse(null));
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = Optional.ofNullable(presenter);
        addButton.setIcon(IconType.PLUS);
        addButton.addClickHandler((e) -> handleAddVarButton());
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
        return Optional.ofNullable(rows.getValue()).map(List::size).orElse(0);
    }

    @Override
    public void setRows(final List<KeyValueRow> rows) {
        this.rows.setValue(rows);
        for (int i = 0; i < getRowsCount(); i++) {
            RolesListItemWidgetView widget = getWidget(i);
            widget.setParentWidget(this);
        }
    }

    @Override
    public List<KeyValueRow> getRows() {
        return rows.getValue();
    }

    @Override
    public RolesListItemWidgetView getWidget(KeyValueRow row) {
        return rows.getComponent(row);
    }

    @Override
    public RolesListItemWidgetView getWidget(int index) {
        return rows.getComponent(index);
    }

    protected void handleAddVarButton() {
        if (!getRows().isEmpty() && getRows().get(getRowsCount() - 1).getKey() == null) {
            return;
        }

        final int index = getRowsCount();
        getRows().add(new KeyValueRow());
        final RolesListItemWidgetView widget = getWidget(index);
        widget.setParentWidget(this);
    }

    @Override
    public void remove(final KeyValueRow row) {
        getRows().remove(row);
        doSave();
    }

    @Override
    public boolean isDuplicateName(String name) {
        return getRows().stream().filter(row -> Objects.equals(row.getKey(), name)).count() > 1;
    }
}
