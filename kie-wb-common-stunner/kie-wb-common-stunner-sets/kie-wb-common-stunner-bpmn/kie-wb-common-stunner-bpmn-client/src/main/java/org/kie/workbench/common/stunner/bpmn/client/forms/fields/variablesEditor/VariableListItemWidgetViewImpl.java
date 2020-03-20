/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.text.shared.Renderer;
import elemental2.dom.CSSProperties;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.StunnerSpecific;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable.VariableType;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.VariableRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBoxView;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.workbench.events.NotificationEvent;

import static jsinterop.annotations.JsPackage.GLOBAL;

/**
 * A templated widget that will be used to display a row in a table of
 * {@link VariableRow}s.
 * <p>
 * The Name field of VariableRow is Bound, but other fields are not bound because
 * they use a combination of ListBox and TextBox to implement a drop-down combo
 * to hold the values.
 */
@Templated(value = "VariablesEditorWidget.html#variableRow", stylesheet = "VariablesEditorWidget.css")
public class VariableListItemWidgetViewImpl implements VariableListItemWidgetView,
                                                       ComboBoxView.ModelPresenter {

    /**
     * Errai's data binding module will automatically bind the provided instance
     * of the model (see {@link #setModel(VariableRow)}) to all fields annotated
     * with {@link Bound}. If not specified otherwise, the bindings occur based on
     * matching field names (e.g. variableRow.name will automatically be kept in
     * sync with the data-field "name")
     */
    @Inject
    @AutoBound
    protected DataBinder<VariableRow> variableRow;

    @Inject
    @Bound
    @DataField
    @StunnerSpecific
    protected VariableNameTextBox name;

    @Inject
    protected ErrorPopupPresenter errorPopupPresenter;

    private String currentValue;
    private String currentName;

    protected Set<String> tagSet = new HashSet<>();

    @Inject
    private org.jboss.errai.common.client.dom.Document document;

    @Inject
    @DataField("variable-tags-settings")
    private HTMLAnchorElement variableTagsSettings;

    @Inject
    @DataField("tags-div")
    HTMLDivElement tagsDiv;

    @Inject
    @DataField
    HTMLDivElement tagsContainer;

    @Inject
    @DataField("some-check")
    protected HTMLInputElement somecheck;

    @Inject
    @DataField
    protected Button closeButton;

    @Inject
    @DataField
    private Span acceptButton;

    @Inject
    @DataField
    protected Span tagCount;

    @Inject
    @DataField
    protected CustomDataTypeTextBox customTagName;

    @Inject
    protected ComboBox tagNamesComboBox;

    protected List<String> tagNamesList = new ArrayList<>();

    @DataField
    protected ValueListBox<String> dataType = new ValueListBox<>(new Renderer<String>() {
        public String render(final String object) {
            String s = "";
            if (object != null) {
                s = object.toString();
            }
            return s;
        }

        public void render(final String object,
                           final Appendable appendable) throws IOException {
            String s = render(object);
            appendable.append(s);
        }
    });

    @Inject
    @DataField
    protected CustomDataTypeTextBox customDataType;

    @Inject
    protected ComboBox dataTypeComboBox;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    @DataField
    protected Button deleteButton;

    @Inject
    @DataField
    protected HTMLDivElement tagsTD;

    @DataField
    protected ValueListBox<String> defaultTagNames = new ValueListBox<>(new Renderer<String>() {
        public String render(final String object) {
            return object != null ? object : "";
        }

        public void render(final String object,
                           final Appendable appendable) throws IOException {
            String s = render(object);
            appendable.append(s);
        }
    });

    /**
     * Required for implementation of Delete button.
     */
    private VariablesEditorWidgetView.Presenter parentWidget;

    public void setParentWidget(final VariablesEditorWidgetView.Presenter parentWidget) {
        this.parentWidget = parentWidget;
    }

    @Override
    public void setTextBoxModelValue(final TextBox textBox,
                                     final String value) {
        setCustomDataType(value);
    }

    @Override
    public void setListBoxModelValue(final ValueListBox<String> listBox,
                                     final String value) {
        setDataTypeDisplayName(value);
    }

    @Override
    public String getModelValue(final ValueListBox<String> listBox) {
        String value = getCustomDataType();
        if (value == null || value.isEmpty()) {
            value = getDataTypeDisplayName();
        }
        return value;
    }

    @PostConstruct
    public void init() {
        name.setRegExp(StringUtils.ALPHA_NUM_REGEXP,
                       StunnerFormsClientFieldsConstants.INSTANCE.Removed_invalid_characters_from_name(),
                       StunnerFormsClientFieldsConstants.INSTANCE.Invalid_character_in_name());

        name.addChangeHandler(event -> {
            String value = name.getText();
            if (isDuplicateName(value)) {
                notification.fire(new NotificationEvent(StunnerFormsClientFieldsConstants.INSTANCE.DuplicatedVariableNameError(value),
                                                        NotificationEvent.NotificationType.ERROR));
                name.setValue(currentName);
                ValueChangeEvent.fire(name, currentName);
            } else if (isBoundToNodes(currentName)) {
                errorPopupPresenter.showMessage(StunnerFormsClientFieldsConstants.INSTANCE.RenameDiagramVariableError());
                name.setValue(currentName);
                ValueChangeEvent.fire(name, currentName);
            }
            notifyModelChanged();
        });
        dataTypeComboBox.init(this,
                              true,
                              dataType,
                              customDataType,
                              false,
                              true,
                              CUSTOM_PROMPT,
                              ENTER_TYPE_PROMPT);
        customDataType.setRegExp(StringUtils.ALPHA_NUM_UNDERSCORE_DOT_REGEXP,
                                 StunnerFormsClientFieldsConstants.INSTANCE.Removed_invalid_characters_from_name(),
                                 StunnerFormsClientFieldsConstants.INSTANCE.Invalid_character_in_name());
        customDataType.addKeyDownHandler(event -> {
            int iChar = event.getNativeKeyCode();
            if (iChar == ' ') {
                event.preventDefault();
            }
        });

        PopOver.$(variableTagsSettings).popovers();

        setTagTittle("This is a new title");
        if (variableTagsSettings != null) {
            variableTagsSettings.onclick = e -> {
                GWT.log("Item has been clicked");
                GWT.log("Next Sibling: " + variableTagsSettings.nextElementSibling.innerHTML);
                ((HTMLDivElement) variableTagsSettings.nextElementSibling).style.left = "130px";

                GWT.log("Next Sibling Child Count: " + variableTagsSettings.nextElementSibling.childElementCount);
                Element lastNode = variableTagsSettings.nextElementSibling.lastElementChild;
                GWT.log("Last Sibling Child Count: " + lastNode);
                GWT.log("some check value is: " + somecheck.checked);
                lastNode.innerHTML = "";
                lastNode.appendChild(tagsDiv);
                // ((HTMLDivElement) variableTagsSettings.nextElementSibling.lastElementChild).style.maxWidth = CSSProperties.MaxWidthUnionType.of("320px");

                GWT.log("Inner Html: " + variableTagsSettings.nextElementSibling.lastElementChild.innerHTML);
                GWT.log("Style: " + variableTagsSettings.nextElementSibling.lastElementChild.getAttribute("style"));
                GWT.log("Style: " + variableTagsSettings.nextElementSibling.lastElementChild.getAttribute("class"));

                ((HTMLDivElement) variableTagsSettings.nextElementSibling).style.maxWidth = CSSProperties.MaxWidthUnionType.of("280px");

                tagsDiv.style.display = "block";
                return null;
            };
        }

        customTagName.addKeyDownHandler(event -> {
            int iChar = event.getNativeKeyCode();
            if (iChar == ' ') {
                event.preventDefault();
            }
        });

        loadDefaultTagNames();
        setTagsListItems();
    }

    protected void loadDefaultTagNames() {
        List<String> tagNames = new ArrayList<>();
        tagNames.add("internal");
        tagNames.add("required");
        tagNames.add("readonly");
        tagNames.add("input");
        tagNames.add("output");
        tagNames.add("business relevant");
        tagNamesList.addAll(tagNames);
    }

    private void setTagsListItems() {

        ListBoxValues classNameListBoxValues = new ListBoxValues(VariableListItemWidgetView.CUSTOM_PROMPT,
                                                                 "Edit" + " ",
                                                                 null);

        classNameListBoxValues.addValues(tagNamesList);
        tagNamesComboBox.setShowCustomValues(true);
        tagNamesComboBox.setListBoxValues(classNameListBoxValues);

        defaultTagNames.setValue("");

        tagNamesComboBox.init(this,
                              true,
                              defaultTagNames,
                              customTagName,
                              false,
                              true,
                              CUSTOM_PROMPT,
                              ENTER_TYPE_PROMPT);
    }

    private void setTagTittle(final String title) {
        variableTagsSettings.setAttribute("title", title);
        tagCount.setAttribute("title", title);
    }

    @Override
    public VariableRow getModel() {
        return variableRow.getModel();
    }

    @Override
    public void setModel(final VariableRow model) {
        variableRow.setModel(model);
        initVariableControls();
        currentValue = getModel().toString();
        currentName = getModel().getName();
    }

    @Override
    public VariableType getVariableType() {
        return getModel().getVariableType();
    }

    @Override
    public String getDataTypeDisplayName() {
        return getModel().getDataTypeDisplayName();
    }

    @Override
    public void setDataTypeDisplayName(final String dataTypeDisplayName) {
        getModel().setDataTypeDisplayName(dataTypeDisplayName);
    }

    @Override
    public String getCustomDataType() {
        return getModel().getCustomDataType();
    }

    @Override
    public void setCustomDataType(final String customDataType) {
        getModel().setCustomDataType(customDataType);
    }

    @Override
    public void setDataTypes(final ListBoxValues dataTypeListBoxValues) {
        dataTypeComboBox.setCurrentTextValue("");
        dataTypeComboBox.setListBoxValues(dataTypeListBoxValues);
        dataTypeComboBox.setShowCustomValues(true);
        String cdt = getCustomDataType();
        if (cdt != null && !cdt.isEmpty()) {
            dataTypeComboBox.addCustomValueToListBoxValues(cdt,
                                                           "");
        }
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        deleteButton.setEnabled(!readOnly);
        dataTypeComboBox.setReadOnly(readOnly);
        name.setEnabled(!readOnly);
    }

    private boolean isDuplicateName(final String name) {
        return parentWidget.isDuplicateName(name);
    }

    private boolean isBoundToNodes(final String name) {
        return parentWidget.isBoundToNodes(name);
    }

    @EventHandler("deleteButton")
    public void handleDeleteButton(final ClickEvent e) {
        parentWidget.removeVariable(getModel());
    }

    @EventHandler("closeButton")
    public void handleCloseButton(final ClickEvent e) {
        variableTagsSettings.click();
    }

    @EventHandler("acceptButton")
    public void handleAcceptButton(final ClickEvent e) {
        final String tag = tagNamesComboBox.getValue();
        if (tag != null && !tag.isEmpty() && !tagSet.contains(tag)) {
            GWT.log("Adding Message: " + tag);

            final Span tagSpan = (Span) document.createElement("Span");
            tagSpan.setTextContent(tag);
            tagSpan.setClassName("badge tagBadge  tagBadges");

            final Span closeSpan = (Span) document.createElement("Span");
            closeSpan.setTextContent("x");
            closeSpan.setClassName("close tagCloseButton tagBadges");

            final org.jboss.errai.common.client.dom.Button closeSpan2 = (org.jboss.errai.common.client.dom.Button) document.createElement("button");
            closeSpan2.setTextContent("x");
            closeSpan2.setClassName("close tagCloseButton tagBadges");

            tagSpan.appendChild(closeSpan);
            tagSpan.appendChild(closeSpan2);


            closeSpan2.setOnclick(ex ->     {
                GWT.log("Item has been clicked");
                ex.preventDefault();
                //tagSet.remove(tag);
                //tagSpan.getParentElement().removeChild(tagSpan);
            });

            HTMLAnchorElement someButton = (HTMLAnchorElement) document.createElement("a");

            someButton.textContent = "XXX";

            someButton.onclick = ex -> {
                GWT.log("Item has been clicked 2");
                ex.preventDefault();
                tagSet.remove(tag);
          //      tagSpan.getParentElement().removeChild(tagSpan);
                tagsContainer.innerHTML = "";
                return null;
            };

            tagsContainer.innerHTML += tagSpan.getOuterHTML();
            tagsContainer.appendChild(someButton);



            tagSet.add(tag);

        }
    }

    /**
     * Updates the display of this row according to the state of the
     * corresponding {@link VariableRow}.
     */
    private void initVariableControls() {
        deleteButton.setIcon(IconType.TRASH);
        String cdt = getCustomDataType();
        if (cdt != null && !cdt.isEmpty()) {
            customDataType.setValue(cdt);
            dataType.setValue(cdt);
        } else if (getDataTypeDisplayName() != null) {
            dataType.setValue(getDataTypeDisplayName());
        }
    }

    @Override
    public void notifyModelChanged() {
        String oldValue = currentValue;
        currentValue = getModel().toString();
        currentName = getModel().getName();
        if (oldValue == null) {
            if (currentValue != null && currentValue.length() > 0) {
                parentWidget.notifyModelChanged();
            }
        } else if (!oldValue.equals(currentValue)) {
            parentWidget.notifyModelChanged();
        }
    }

    @Override
    public void setTagsNotEnabled() {
        this.tagsTD.remove();
    }

    @JsType(isNative = true)
    private static abstract class PopOver {

        @JsMethod(namespace = GLOBAL, name = "jQuery")
        public native static PopOver $(final elemental2.dom.Node selector);

        public native void popovers();
    }
}
