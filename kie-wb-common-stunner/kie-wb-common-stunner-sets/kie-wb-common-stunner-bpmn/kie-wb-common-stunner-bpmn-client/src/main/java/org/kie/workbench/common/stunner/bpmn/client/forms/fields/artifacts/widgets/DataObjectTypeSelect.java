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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.artifacts.widgets;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.KeyboardEvent;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.AbstractValidatingTextBox;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.NotificationEvent.NotificationType;

import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants.INSTANCE;

@Templated
@Dependent
public class DataObjectTypeSelect extends Composite implements HasValue<String> {

    public static final String CUSTOM_PROMPT = "Custom ...";
    public static final String EDIT_PROMPT = "Edit ";
    private static final RegExp regExp = RegExp.compile(StringUtils.ALPHA_NUM_REGEXP);
    @Inject
    @DataField
    private HTMLSelectElement select;
    @Inject
    @DataField
    private HTMLInputElement addOrEdit;
    @Inject
    private javax.enterprise.event.Event<NotificationEvent> notification;
    private List<String> buildin = Arrays.asList("Boolean", "Float", "Integer", "Object", "String");
    private String selectedValue = "";
    private EventListener onEditEvent;
    private Mode currentAction = Mode.NONE;

    @PostConstruct
    public void init() {
        addDataTypeToSelect("Custom ...", "Custom ...", Mode.ADD, true, false, false);
        buildin.forEach((e) -> addDataTypeToSelect(e, e, Mode.LABEL, false, false, false));
        select.addEventListener("change", this::onChange);
        addOrEdit.addEventListener("focusout", this::onFocusout);
        addOrEdit.addEventListener("keyup", this::onKeyup);
    }

    void onKeyup(Event e) {
        KeyboardEvent event = (KeyboardEvent) e;
        if (event.key.equals("Enter")) {
            event.preventDefault();
            hide(addOrEdit.value);
        } else if (event.key.equals("Escape")) {
            getOptionByText(selectedValue).selected = true;
            hide();
        }
    }

    private void onFocusout(Event event) {
        hide(addOrEdit.value);
    }

    private void hide(String value) {
        if (validateValue(value)) {
            if (!currentAction.equals(Mode.NONE)) {
                if (currentAction.equals(Mode.ADD)) {
                    addDataTypeToSelect(value, value, Mode.LABEL, true, true, true);
                } else if (currentAction.equals(Mode.EDIT)) {
                    HTMLOptionElement selected = (HTMLOptionElement) select.options.item(select.selectedIndex);
                    String remove = selected.value;

                    for (int i = (int) select.options.length - 1; i >= 0; --i) {
                        if (((HTMLOptionElement) select.options.item(i)).value.equals(remove)) {
                            select.remove(i);
                        }
                    }
                    addDataTypeToSelect(value, value, Mode.ADD, true, true, true);
                }

                setValue(value, true);
            }

            hide();
        }
    }

    private void hide() {
        this.select.style.display = "block";
        this.addOrEdit.style.display = "none";
        this.addOrEdit.removeEventListener("keyup", this.onEditEvent);
        this.addOrEdit.value = "";
        this.currentAction = Mode.NONE;
    }

    protected void fireValidationError(String validationError) {
        this.notification.fire(new NotificationEvent(INSTANCE.Invalid_character_in_name()
                                                             + " :" + validationError,
                                                     NotificationType.ERROR));
    }

    protected void onChange(Event event) {
        HTMLOptionElement selected = (HTMLOptionElement) select.options.item(select.selectedIndex);
        if (isAddCustom(selected)) {
            onAdd();
        } else if (isEdit(selected)) {
            onEdit();
        } else {
            setValue(selected.text, true);
        }
    }

    private EventListener createEventListener() {
        return (event) -> validateValue(addOrEdit.value);
    }

    private boolean validateValue(String value) {
        if (Mode.NONE.equals(currentAction)) {
            return true;
        } else if (!value.isEmpty() && regExp.test(value)) {
            return true;
        } else {
            String invalidChars = AbstractValidatingTextBox.getInvalidCharsInName(regExp, value);
            fireValidationError(invalidChars);
            return false;
        }
    }

    private void onAdd() {
        currentAction = Mode.ADD;
        showInput("");
    }

    private void onEdit() {
        currentAction = Mode.EDIT;
        HTMLOptionElement selected = (HTMLOptionElement) select.options.item(select.selectedIndex);
        showInput(selected.value);
    }

    private void showInput(String text) {
        this.onEditEvent = this.createEventListener();
        this.select.style.display = "none";
        this.addOrEdit.style.display = "block";
        this.addOrEdit.focus();
        this.addOrEdit.value = text;
        this.addOrEdit.addEventListener("keyup", this.onEditEvent);
    }

    private boolean isAddCustom(HTMLOptionElement element) {
        return element.getAttribute("role").equals(DataObjectTypeSelect.Mode.ADD.name());
    }

    private boolean isEdit(HTMLOptionElement element) {
        return element.getAttribute("role").equals(DataObjectTypeSelect.Mode.EDIT.name());
    }

    public String getValue() {
        return selectedValue;
    }

    public void setValue(String value) {
        this.setValue(value.isEmpty() ? String.class.getSimpleName() : value, false);
    }

    public void setValue(String value, boolean fireEvents) {
        if (!selectedValue.equals(value)) {
            String oldValue = selectedValue;
            doSetValue(value);
            if (fireEvents) {
                ValueChangeEvent.fireIfNotEqual(this, oldValue, selectedValue);
            }
        }
    }

    private void doSetValue(String value) {
        if (!value.isEmpty()) {
            selectedValue = value;
            if (checkIfNotExists(value)) {
                addDataTypeToSelect(value, value, DataObjectTypeSelect.Mode.LABEL, true, false, true);
            }
            getOptionByText(value).selected = true;
        }
    }

    protected boolean checkIfNotExists(String name) {
        for (int i = 0; (double) i < select.options.length; ++i) {
            if (((HTMLOptionElement) select.options.item(i)).text.equals(name)) {
                return false;
            }
        }
        return true;
    }

    protected void addDataTypeToSelect(String name, String value, DataObjectTypeSelect.Mode mode, boolean custom, boolean editable, boolean selected) {
        if (!checkForDuplicates(name)) {
            if (editable) {
                name = createEditPrompt(name);
                mode = Mode.EDIT;
                custom = true;
                editable = false;
                selected = false;
            }
            select.add(createDataObjectType(name, value, mode, custom, editable, selected));
        }
    }

    protected HTMLOptionElement getOptionByText(String text) {
        for (int i = 0; (double) i < select.options.length; ++i) {
            if (((HTMLOptionElement) select.options.item(i)).text.equals(text)) {
                return (HTMLOptionElement) select.options.item(i);
            }
        }
        throw new IllegalArgumentException();
    }

    private boolean checkForDuplicates(String name) {
        for (int i = 0; (double) i < select.options.length; ++i) {
            if (((HTMLOptionElement) select.options.item(i)).text.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private String createEditPrompt(String name) {
        return EDIT_PROMPT + name + " ...";
    }

    protected HTMLOptionElement createDataObjectType(String name, String value, DataObjectTypeSelect.Mode mode, boolean custom, boolean editable, boolean selected) {
        return new DataObjectType(name, value, mode.toString(), custom, editable, selected).asElement();
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    public void setReadOnly(boolean readOnly) {
        select.disabled = readOnly;
    }

    enum Mode {
        LABEL,
        EDIT,
        ADD,
        NONE;
    }
}
