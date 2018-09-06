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

import java.util.Objects;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.BlurEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.KeyValueRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable.VariableType;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;
import org.kie.workbench.common.stunner.client.widgets.canvas.actions.IntegerTextBox;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@Templated("RolesEditorWidget.html#tableRow")
public class RolesListItemWidgetViewImpl implements RolesListItemWidgetView,
                                                    IsElement {

    public static final String INVALID_CHARACTERS_MESSAGE = "Invalid characters";
    private static final String DUPLICATE_NAME_ERROR_MESSAGE = "A role with this name already exists";

    @Inject
    @AutoBound
    protected DataBinder<KeyValueRow> row;

    @Inject
    @Bound(property = "key")
    @DataField("roleInput")
    protected VariableNameTextBox role;

    @Inject
    @Bound(property = "value")
    @DataField("cardinalityInput")
    protected IntegerTextBox cardinality;

    private boolean allowDuplicateNames = false;

    private String previousRole;

    private String previousCardinality;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    @DataField
    protected Button deleteButton;

    /**
     * Required for implementation of Delete button.
     */
    private Optional<RolesEditorWidgetView> parentWidget;

    @Inject
    public RolesListItemWidgetViewImpl() {
        parentWidget = Optional.empty();
    }

    public void setParentWidget(final RolesEditorWidgetView parentWidget) {
        this.parentWidget = Optional.ofNullable(parentWidget);
    }

    @PostConstruct
    public void init() {
        role.setRegExp(StringUtils.ALPHA_NUM_REGEXP, INVALID_CHARACTERS_MESSAGE, INVALID_CHARACTERS_MESSAGE);
        role.addBlurHandler((e) -> handleBlur(e));
        cardinality.addBlurHandler((e) -> handleBlur(e));
        cardinality.addFocusHandler((e) -> handleFocus());
        deleteButton.setIcon(IconType.TRASH);
        deleteButton.addClickHandler((e) -> handleDeleteButton());
    }

    private void handleFocus() {
        if (Objects.equals("0", cardinality.getText())) {
            cardinality.clear();
        }
    }

    private void handleBlur(BlurEvent e) {
        String value = role.getText();
        if (!allowDuplicateNames && isDuplicateName(value)) {
            notification.fire(new NotificationEvent(DUPLICATE_NAME_ERROR_MESSAGE,
                                                    NotificationEvent.NotificationType.ERROR));
            role.setValue("");
            return;
        }
        notifyModelChanged();
    }

    @Override
    public VariableType getVariableType() {
        return VariableType.PROCESS;
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        deleteButton.setEnabled(!readOnly);
        role.setEnabled(!readOnly);
        cardinality.setEnabled(!readOnly);
    }

    @Override
    public boolean isDuplicateName(final String name) {
        return parentWidget.map(p -> p.isDuplicateName(name)).orElse(false);
    }

    public void handleDeleteButton() {
        parentWidget.ifPresent(p -> p.remove(getValue()));
    }

    @Override
    public void notifyModelChanged() {
        final String currentRole = row.getModel().getKey();
        final String currentCardinality = row.getModel().getValue();

        //skip in case not modified values
        if ((Objects.equals(previousRole, currentRole) && Objects.equals(previousCardinality, currentCardinality))) {
            return;
        }
        previousRole = currentRole;
        previousCardinality = currentCardinality;
        parentWidget.ifPresent(RolesEditorWidgetView::notifyModelChanged);
    }

    @Override
    public void setValue(KeyValueRow value) {
        row.setModel(value);
        previousRole = value.getKey();
        previousCardinality = value.getValue();
    }

    @Override
    public KeyValueRow getValue() {
        return row.getModel();
    }

    @Override
    public KeyValueRow getModel() {
        return getValue();
    }

    @Override
    public void setModel(KeyValueRow model) {
        setValue(model);
    }
}
