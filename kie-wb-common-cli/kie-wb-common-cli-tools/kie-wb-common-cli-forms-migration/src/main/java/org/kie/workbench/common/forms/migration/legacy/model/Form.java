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
package org.kie.workbench.common.forms.migration.legacy.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Form {

    public static final String RENDER_MODE_FORM = "form";
    public static final String RENDER_MODE_DISPLAY = "display";
    public static final String RENDER_MODE_TEMPLATE_EDIT = "templateEdit";
    public static final String RENDER_MODE_WYSIWYG_FORM = "wysiwyg-form";
    public static final String RENDER_MODE_WYSIWYG_DISPLAY = "wysiwyg-display";

    public static final String LABEL_MODE_UNDEFINED = "undefined";
    public static final String LABEL_MODE_BEFORE = "before";
    public static final String LABEL_MODE_AFTER = "after";
    public static final String LABEL_MODE_RIGHT = "right";
    public static final String LABEL_MODE_LEFT = "left";
    public static final String LABEL_MODE_HIDDEN = "hidden";
    public static final String DISPLAY_MODE_DEFAULT = "default";
    public static final String DISPLAY_MODE_ALIGNED = "aligned";
    public static final String DISPLAY_MODE_TEMPLATE = "template";
    public static final String DISPLAY_MODE_NONE = "none";
    public static final String TEMPLATE_FIELD = "$field";
    public static final String TEMPLATE_LABEL = "$label";

    private Long id;

    private String subject;

    private String name;

    private String displayMode;

    private String labelMode;

    private String showMode;

    private Long status;

    private Set<FormDisplayInfo> formDisplayInfos;

    private Set<Field> formFields = new TreeSet<Field>();

    private Set<DataHolder> holders;

    private HashMap dataHolderRenderInfo = new HashMap();

    private int migrationStep = 0;

    public Form() {
        formDisplayInfos = new TreeSet<FormDisplayInfo>();
        holders = new TreeSet<DataHolder>();
    }

    public Long getId() {
        return this.id;
    }

    public String getItemClassName() {
        return Form.class.getName();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayMode() {
        return this.displayMode;
    }

    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }

    public String getLabelMode() {
        return labelMode;
    }

    public void setLabelMode(String labelMode) {
        this.labelMode = labelMode;
    }

    public String getShowMode() {
        return showMode;
    }

    public void setShowMode(String showMode) {
        this.showMode = showMode;
    }

    public Long getStatus() {
        return this.status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Set<FormDisplayInfo> getFormDisplayInfos() {
        return this.formDisplayInfos;
    }

    public void setFormDisplayInfos(Set<FormDisplayInfo> formDisplayInfos) {
        this.formDisplayInfos = formDisplayInfos;
    }

    public Set<Field> getFormFields() {
        return this.formFields;
    }

    public void setFormFields(Set<Field> formFields) {
        this.formFields = formFields;
    }

    public Set<Field> getFieldsForDataHolder(DataHolder dataHolder) {
        return new TreeSet<>(formFields.stream()
                                     .filter(field -> dataHolder.ownsField(field)).collect(Collectors.toSet()));
    }

    public void setDataHolder(DataHolder holder) {
        if (holder == null) {
            return;
        }

        if ((holder.getInputId() == null || holder.getInputId().trim().length() == 0) && (holder.getOuputId() == null || holder.getOuputId().trim().length() == 0)) {
            return;
        }

        if (getDataHolderById(holder.getInputId()) != null || getDataHolderById(holder.getOuputId()) != null) {
            holders.remove(holder);
        }
        holders.add(holder);
    }

    public void removeDataHolder(String id) {
        if (id == null || id.trim().length() == 0) {
            return;
        }
        if (getDataHolderById(id) != null) {
            for (Iterator it = holders.iterator(); it.hasNext(); ) {
                DataHolder holder = (DataHolder) it.next();
                if (id.equals(holder.getUniqeId())) {
                    it.remove();
                }
            }
        }
    }

    public DataHolder getDataHolderByIds(String inputId, String outputId) {
        if (inputId == null || inputId.trim().length() == 0) {
            return null;
        }
        if (outputId == null || outputId.trim().length() == 0) {
            return null;
        }
        if (getHolders() != null) {
            for (DataHolder dataHolder : holders) {
                if (inputId.equals(dataHolder.getInputId()) || outputId.equals(dataHolder.getOuputId())) {
                    return dataHolder;
                }
            }
        }
        return null;
    }

    public DataHolder getDataHolderById(String srcId) {
        if (srcId == null || srcId.trim().length() == 0) {
            return null;
        }
        if (getHolders() != null) {
            for (DataHolder dataHolder : holders) {
                if (srcId.equals(dataHolder.getUniqeId())) {
                    return dataHolder;
                }
            }
        }
        return null;
    }

    public String toString() {
        return "Form [" + getName() + "]";
    }

    public boolean equals(Object other) {
        if (!(other instanceof Form)) {
            return false;
        }
        Form castOther = (Form) other;
        return this.getId().equals(castOther.getId());
    }

    public int hashCode() {
        return getId().hashCode();
    }

    /**
     * Get field by name
     * @param name Desired field name, must be not null
     * @return field by given name or null if it doesn't exist.
     */
    public Field getField(String name) {
        if (name == null || name.trim().length() == 0) {
            return null;
        }
        if (getFormFields() != null) {

            for (Field field : formFields) {
                if (name.equals(field.getFieldName())) {
                    return field;
                }
            }
        }
        return null;
    }

    protected String getDisplayModeText(String selector) {
        String text = null;
        if (getFormDisplayInfos() != null) {
            for (Iterator it = getFormDisplayInfos().iterator(); it.hasNext(); ) {
                FormDisplayInfo dInfo = (FormDisplayInfo) it.next();
                if (selector.equals(dInfo.getDisplayMode())) {
                    text = dInfo.getDisplayData();
                    break;
                }
            }
        }
        return text;
    }

    protected void setDisplayModeText(final String selector, final String data) {
        if (getFormDisplayInfos() == null) {
            setFormDisplayInfos(new HashSet());
        }
        FormDisplayInfo theTemplateInfo = null;

        for (Iterator it = getFormDisplayInfos().iterator(); it.hasNext(); ) {
            FormDisplayInfo dInfo = (FormDisplayInfo) it.next();
            if (selector.equals(dInfo.getDisplayMode())) {
                theTemplateInfo = dInfo;
                break;
            }
        }

        if (theTemplateInfo == null) {
            theTemplateInfo = new FormDisplayInfo();
            getFormDisplayInfos().add(theTemplateInfo);
        }
        theTemplateInfo.setDisplayData(data);
        theTemplateInfo.setDisplayMode(selector);
    }

    public Set<DataHolder> getHolders() {
        return holders;
    }

    public void setHolders(Set<DataHolder> holders) {
        this.holders = holders;
    }

    public String getFormTemplate() {
        return getDisplayModeText(DISPLAY_MODE_TEMPLATE);
    }

    public void setFormTemplate(final String data) {
        setDisplayModeText(DISPLAY_MODE_TEMPLATE, data);
    }

    public boolean containsFormField(String fieldName) {
        if (formFields != null && fieldName != null && !"".equals(fieldName)) {
            for (Field formField : formFields) {
                if (formField.getFieldName().equals(fieldName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getSynchronizationObject() {
        return ("JBPM Form " + this.getId()).intern();
    }

    /**
     * Get a Set with all field names (Strings) present in this form
     * @return a Set with all field names present in this form
     */
    public Set getFieldNames() {
        Set s = new TreeSet();
        for (Field formField : formFields) {
            s.add(formField.getFieldName());
        }
        return s;
    }

    public String getDataFieldHolderNameFromBindingStr(String bindingStr) {
        if (bindingStr != null && bindingStr.indexOf('/') != -1 && bindingStr.length() > 1) {
            return bindingStr.substring(bindingStr.indexOf('/') + 1, bindingStr.length() - 1);
        }
        return "";
    }

    public DataHolder getDataHolderFromInputExpression(String inputExpression) {
        return getDataHolderFromExpression(inputExpression, true);
    }

    public DataHolder getDataHolderFromOutputExpression(String outputExpression) {
        return getDataHolderFromExpression(outputExpression, false);
    }

    protected DataHolder getDataHolderFromExpression(String bindingExpression, boolean checkInput) {
        if (bindingExpression == null) {
            return null;
        }

        for (DataHolder holder : holders) {
            if (checkInput && holder.containsInputBinding(bindingExpression)) {
                return holder;
            } else if (!checkInput && holder.containsOutputBinding(bindingExpression)) {
                return holder;
            }
        }
        return null;
    }

    public DataHolder getDataHolderByField(Field field) {
        if (field == null || (field.getInputBinding() == null && field.getOutputBinding() == null)) {
            return null;
        }

        for (DataHolder holder : holders) {
            if (holder.containsBinding(field.getInputBinding())) {
                return holder;
            } else if (holder.containsBinding(field.getOutputBinding())) {
                return holder;
            }
        }
        return null;
    }

    public boolean containsHolder(DataHolder aholder) {
        for (DataHolder holder : holders) {
            if (holder.equals(aholder)) {
                return true;
            }
        }
        return false;
    }

    public int getMigrationStep() {
        return migrationStep;
    }

    public void setMigrationStep(int migrationStep) {
        this.migrationStep = migrationStep;
    }
}
