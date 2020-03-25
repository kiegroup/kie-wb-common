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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.text.shared.Renderer;
import elemental2.dom.CSSProperties;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
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

    protected static Button lastOverlayOpened = null;

    protected boolean isOpen = false;

    @Inject
    private org.jboss.errai.common.client.dom.Document document;

    @Inject
    @DataField("variable-tags-settings")
    protected HTMLAnchorElement variableTagsSettings;

    @Inject
    @DataField("tags-div")
    HTMLDivElement tagsDiv;

    @Inject
    @DataField
    HTMLDivElement tagsContainer;

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

    private Map<String, HTMLAnchorElement> removeButtons = new HashMap<>();

    @Inject
    protected ComboBox tagNamesComboBox;

    protected List<String> tagNamesList = new ArrayList<>();

    private String overlayTopPosition = null;

    final String[] defaultTags = {"internal", "required", "readonly", "input", "output", "business_relevant"};

    final Set<String> defaultTagsSet = new HashSet<>(Arrays.asList(defaultTags));

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

    @DataField
    protected TableCellElement tagsTD = Document.get().createTDElement();

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
        GWT.log("Setting Textbox Model Value Textbox: " + textBox);

        GWT.log("Setting Textbox Model Value: " + value);
        if (textBox == customDataType) {
            setCustomDataType(value);
            GWT.log("It is custom Data Type");
        } else {
            GWT.log("Not Custom Data Type");
        }
    }

    @Override
    public void setListBoxModelValue(final ValueListBox<String> listBox,
                                     final String value) {
        GWT.log("Setting Listbox Model Value Listbox: " + listBox);

        GWT.log("Setting Listbox Model Value: " + value);

        if (listBox == dataType) {
            GWT.log("Is Data Type Listbox");
            setDataTypeDisplayName(value);
        } else {
            GWT.log("Is not Data Type Listbox");
        }
    }

    @Override
    public String getModelValue(final ValueListBox<String> listBox) {

        if (true || listBox == dataType) {
            GWT.log("Not setting Model Value");
            String value = getCustomDataType();
            if (value == null || value.isEmpty()) {
                value = getDataTypeDisplayName();
            }
            return value;
        } else {
            GWT.log("Not setting Model Value");
            return "";
        }
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
        GWT.log("Value of Data Types: " + dataType);
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

        GWT.log("On Init value of Model Tags: " + getModel().getTags());
        PopOver.$(variableTagsSettings).popovers();

        setTagTittle("Tags: ");

        variableTagsSettings.onclick = e -> {
            // This is needed when opened

            if (isOpen) {
                isOpen = false;
                GWT.log("Pop Over is Open Must be closed");
                lastOverlayOpened = null;
                return null;
            } else {
                isOpen = true;
                GWT.log("Pop Over is Closed Must be Opened");
            }

            GWT.log("Item has been clicked");
            GWT.log("Last Overlay: " + lastOverlayOpened);

            final HTMLDivElement overlayDiv = ((HTMLDivElement) variableTagsSettings.nextElementSibling);
            GWT.log("Opening");
            GWT.log("Next Sibling: " + overlayDiv.innerHTML);

            if (lastOverlayOpened != null && lastOverlayOpened != this.closeButton) {
                GWT.log("Closing Anchor Overlay");
                GWT.log("Closing");
                GWT.log("Performing Click");
                lastOverlayOpened.click();
            }

            overlayDiv.style.display = "block";

            GWT.log("Should Have opened: " + tagNamesComboBox.getValue());
            overlayDiv.style.left = "130px";
            GWT.log("Next Sibling Child Count: " + overlayDiv.childElementCount);
            final Element lastNode = overlayDiv.lastElementChild;
            GWT.log("Last Sibling Child Count: " + lastNode);
            lastNode.innerHTML = "";
            lastNode.appendChild(tagsDiv);

            GWT.log("Variable Tag Settings Left: " + variableTagsSettings.style.left);
            GWT.log("Variable Tag Settings Top: " + variableTagsSettings.style.left);

            GWT.log("Variable Tag Settings Client Left: " + variableTagsSettings.clientLeft);
            GWT.log("Variable Tag Settings Client Top: " + variableTagsSettings.clientTop);

            GWT.log("Variable Tag Settings Bounding Rect Left: " + variableTagsSettings.getBoundingClientRect().left);
            GWT.log("Variable Tag Settings Bounding Rect Top: " + variableTagsSettings.getBoundingClientRect().top);

            GWT.log("Overlay Left: " + overlayDiv.style.left);
            GWT.log("Overlay Top: " + overlayDiv.style.top);

            if (overlayTopPosition == null) {
                overlayTopPosition = overlayDiv.style.top;
            }

            overlayDiv.style.top = overlayTopPosition;

            GWT.log("Inner Html: " + overlayDiv.lastElementChild.innerHTML);
            GWT.log("Style: " + overlayDiv.lastElementChild.getAttribute("style"));
            GWT.log("Style: " + overlayDiv.lastElementChild.getAttribute("class"));

            overlayDiv.style.maxWidth = CSSProperties.MaxWidthUnionType.of("280px");
            overlayDiv.style.minHeight = CSSProperties.MinHeightUnionType.of("280px");

            tagsDiv.style.display = "block";
            overlayDiv.style.display = "block";

            lastOverlayOpened = this.closeButton;
            GWT.log("This Close Button: " + lastOverlayOpened);

            return null;
        };

        customTagName.addFocusHandler(focusEvent -> {
            previousCustomValue = customTagName.getValue();
            GWT.log("On Focus Value: " + previousCustomValue);
        });

        customTagName.addKeyDownHandler(event -> {
            int iChar = event.getNativeKeyCode();
            if (iChar == ' ') {
                event.preventDefault();
            }
        });

        loadDefaultTagNames();
        setTagsListItems();
    }

    protected String previousCustomValue = "";

    protected void loadDefaultTagNames() {
        final List<String> defaultTagNamesList = new ArrayList<>();
        defaultTagNamesList.addAll(defaultTagsSet);
        tagNamesList.addAll(defaultTagNamesList);
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
                              false,
                              defaultTagNames,
                              customTagName,
                              false,
                              true,
                              CUSTOM_PROMPT,
                              ENTER_TAG_PROMPT);
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
        GWT.log("Setting Model: " + model);
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
        GWT.log("Setting Data Display Name: " + dataTypeDisplayName);
        getModel().setDataTypeDisplayName(dataTypeDisplayName);
    }

    @Override
    public String getCustomDataType() {
        return getModel().getCustomDataType();
    }

    @Override
    public void setCustomDataType(final String customDataType) {
        GWT.log("Setting Custom Data Type: " + customDataType);
        getModel().setCustomDataType(customDataType);
    }

    @Override
    public void setCustomTags(final List<String> tags) {
        GWT.log("Setting Custom Tags: " + tags);
        getModel().setTags(tags);
        tagNamesList = tags;
        GWT.log("Value of TagNamesList: " + tagNamesList);
        GWT.log("Value of Default tag List: " + defaultTagsSet);
    }

    @Override
    public List<String> getCustomTags() {
        return new ArrayList<>(tagSet);
    }

    @Override
    public void setDataTypes(final ListBoxValues dataTypeListBoxValues) {
        GWT.log("Setting Data Types: " + dataTypeListBoxValues);
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
    public void setTagTypes(final List<String> tagTypes) {
        GWT.log("Setting Tag Types: " + tagTypes);
        tagNamesComboBox.setCurrentTextValue("");
        tagNamesComboBox.setShowCustomValues(true);

       for (final String tag : tagTypes) {
           if (!defaultTagsSet.contains(tag)) {
               GWT.log("Adding Custom Type: " + tag);
               tagNamesComboBox.addCustomValueToListBoxValues(tag, "");
           }
       }
       tagSet.clear();
       tagSet.addAll(tagTypes);

        renderTagElementsBadges(true);

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

        final String tagX = tagNamesComboBox.getValue();

        GWT.log("Custom Value: " + customTagName.getValue());

        if (tagX != null && !tagX.isEmpty() && !tagSet.contains(tagX)) {
            GWT.log("Removing Buttons");
            for (final HTMLAnchorElement anchor : removeButtons.values()) {
                anchor.click();
            }
            removeButtons.clear();

            GWT.log("Adding Message: " + tagX);
            GWT.log("Adding Buttons");

            tagSet.add(tagX);
            GWT.log("Value of Tags before notifying: " + tagSet);
            // Update Model
            setCustomTags(new ArrayList<>(tagSet));
            notifyModelChanged();

            GWT.log("Adding Tags : " + tagSet);
            GWT.log("Custom Value: " + customTagName.getValue());
            GWT.log("Previous Value: " + previousCustomValue);

            if (!defaultTagsSet.contains(tagX) && previousCustomValue != null && !tagX.equals(previousCustomValue)) { // Is custom
                GWT.log("Item was Edited, removing old value");
                tagSet.remove(previousCustomValue);
            }

            renderTagElementsBadges(false);
        }
    }

    private void renderTagElementsBadges(final boolean updateSet) {
        for (final String tag : tagSet) {

            final HTMLLabelElement tagLabel = (HTMLLabelElement) document.createElement("label");
            tagLabel.textContent = tag;
            tagLabel.className = "badge tagBadge  tagBadges";
            tagLabel.htmlFor = "closeButton";

            final HTMLAnchorElement closeButton = (HTMLAnchorElement) document.createElement("a");
            closeButton.id = "closeButton";
            closeButton.textContent = "x";
            closeButton.className = "close tagCloseButton tagBadges";

            closeButton.onclick = ex -> {
                GWT.log("Item has been clicked 2");
                tagLabel.remove();
                closeButton.remove();
                ex.preventDefault();

                if (updateSet) {
                    tagSet.remove(tag);
                    setCustomTags(new ArrayList<>(tagSet));
                    notifyModelChanged();
                }

                tagCount.setTextContent(tagSet.size() != 0 ? String.valueOf(tagSet.size()) : "");
                setTagTittle("Tags: " + tagSet.toString());
                setCustomTags(new ArrayList<>(tagSet));
                return null;
            };

            tagLabel.appendChild(closeButton);
            tagsContainer.appendChild(tagLabel);

            tagCount.setTextContent(tagSet.size() != 0 ? String.valueOf(tagSet.size()) : "");
            setTagTittle("Tags: " + tagSet.toString());

            removeButtons.put(tag, closeButton);
        } //
    }

    /**
     * Updates the display of this row according to the state of the
     * corresponding {@link VariableRow}.
     */
    private void initVariableControls() {
        GWT.log("Initializing Variable Controls");
        deleteButton.setIcon(IconType.TRASH);
        String cdt = getCustomDataType();
        if (cdt != null && !cdt.isEmpty()) {
            customDataType.setValue(cdt);
            GWT.log("Initializing Custom Data Type: " + cdt);
            dataType.setValue(cdt);
        } else if (getDataTypeDisplayName() != null) {
            GWT.log("Initializing Data Type: " + getDataTypeDisplayName());
            dataType.setValue(getDataTypeDisplayName());
        }
    }

    @Override
    public void notifyModelChanged() {
        GWT.log("Model Changed");
        GWT.log("Model Changed Tags: " + tagSet);

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

        if (tagSet.size() != 0) {
            GWT.log("There are some tags, notifying model changed");
            GWT.log("Values of tags: " + getModel().getTags());
            parentWidget.notifyModelChanged();
        }
    }

    @Override
    public void setTagsNotEnabled() {
        this.tagsTD.removeFromParent();
    }

    @JsType(isNative = true)
    private static abstract class PopOver {

        @JsMethod(namespace = GLOBAL, name = "jQuery")
        public native static PopOver $(final elemental2.dom.Node selector);

        public native void popovers();
    }
}
