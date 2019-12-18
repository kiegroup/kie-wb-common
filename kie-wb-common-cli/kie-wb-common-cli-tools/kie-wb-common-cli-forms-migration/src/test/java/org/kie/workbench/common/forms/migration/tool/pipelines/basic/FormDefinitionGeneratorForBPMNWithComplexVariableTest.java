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

package org.kie.workbench.common.forms.migration.tool.pipelines.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.impl.IntegerMultipleInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.migration.legacy.model.DataHolder;
import org.kie.workbench.common.forms.migration.legacy.model.Field;
import org.kie.workbench.common.forms.migration.legacy.model.Form;
import org.kie.workbench.common.forms.migration.legacy.services.impl.FieldTypeBuilder;
import org.kie.workbench.common.forms.migration.tool.FormMigrationSummary;
import org.kie.workbench.common.forms.migration.tool.Resource;
import org.kie.workbench.common.forms.migration.tool.bpmn.BPMNAnalyzer;
import org.kie.workbench.common.forms.migration.tool.bpmn.BPMNProcess;
import org.kie.workbench.common.forms.migration.tool.pipelines.MigrationContext;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.DataObjectFormAdapter;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.migration.cli.RealSystemAccess;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FormDefinitionGeneratorForBPMNWithComplexVariableTest extends AbstractFormDefinitionGeneratorTest {

    @Mock
    private Path userFormPath;

    @Mock
    private Path lineFormPath;

    @Mock
    private Path processFormPath;

    @Mock
    private Path taskFormPath;

    private Form userForm;
    private Form lineForm;
    private Form processForm;
    private Form taskForm;

    @Override
    protected void doInit() throws Exception {

        List<FormMigrationSummary> summaries = new ArrayList<>();

        initForm(form -> userForm = form, DATAOBJECTS_RESOURCES, USER_FORM, userFormPath);
        summaries.add(new FormMigrationSummary(new Resource<>(userForm, userFormPath)));

        initForm(form -> lineForm = form, DATAOBJECTS_RESOURCES, LINE_FORM, lineFormPath);
        summaries.add(new FormMigrationSummary(new Resource<>(lineForm, lineFormPath)));

        initForm(form -> processForm = form, BPMN_RESOURCES, PROCESS_FORM, processFormPath);
        summaries.add(new FormMigrationSummary(new Resource<>(processForm, processFormPath)));

        initForm(form -> taskForm = form, BPMN_RESOURCES, TASK_FORM, taskFormPath);
        summaries.add(new FormMigrationSummary(new Resource<>(taskForm, taskFormPath)));

        context = new MigrationContext(workspaceProject, weldContainer, formsMigrationServicesCDIWrapper, new RealSystemAccess(), summaries, migrationServicesCDIWrapper);

        generator = new FormDefinitionGenerator(DataObjectFormAdapter::new, this::getBPMNAdapter);
    }

    @Override
    protected List<JBPMFormModel> getProcessFormModels() {
        BPMNAnalyzer analyzer = new BPMNAnalyzer();
        BPMNProcess process = analyzer.read(this.getClass().getResourceAsStream(BPMN_RESOURCES + INVOICES_BPMN));
        return process.getFormModels();
    }

    @Test
    public void testMigration() {
        generator.execute(context);

        assertThat(context.getSummaries()).hasSize(4);

        assertThat(context.getExtraSummaries()).hasSize(2);

        // 4 legacyforms + 4 migrated forms + 2 new forms for nested models
        verify(migrationServicesCDIWrapper, times(10)).write(any(Path.class), anyString(), anyString());

        context.getSummaries().forEach(summary -> {
            assertThat(summary.getResult().isSuccess()).isTrue();
            switch (summary.getBaseFormName() + ".form") {
                case PROCESS_FORM:
                    verifyProcessForm(summary);
                    break;
                case TASK_FORM:
                    verifyTaskForm(summary);
                    break;
                case USER_FORM:
                    verifyUserForm(summary);
                    break;
                case LINE_FORM:
                    verifyLineForm(summary);
                    break;
            }
        });
    }

    private void verifyProcessForm(FormMigrationSummary summary) {
        verifyBPMNForm(summary, BusinessProcessFormModel.class);
    }

    private void verifyTaskForm(FormMigrationSummary summary) {
        verifyBPMNForm(summary, TaskFormModel.class);
    }

    private void verifyBPMNForm(FormMigrationSummary summary, Class<? extends JBPMFormModel> modelType) {
        Form originalForm = summary.getOriginalForm().get();

        assertThat(originalForm.getFormFields()).hasSize(5);

        Field originalInvoiceUser = originalForm.getField(INVOICE_USER);
        Field originalLines = originalForm.getField(INVOICE_LINES);
        Field originalTotal = originalForm.getField(INVOICE_TOTAL);

        DataHolder originalDataHolder = originalForm.getHolders().iterator().next();

        String expectedExtraForm = summary.getBaseFormName() + "-" + originalDataHolder.getUniqeId();

        checkMovedField(originalInvoiceUser, expectedExtraForm);
        checkMovedField(originalLines, expectedExtraForm);
        checkMovedField(originalTotal, expectedExtraForm);

        Field invoiceField = originalForm.getField("invoice");

        assertThat(invoiceField.getBag()).isEqualTo(INVOICE_MODEL);
        assertThat(invoiceField.getSourceLink())
                .isEqualTo(summary.getBaseFormName() + "-" + invoiceField.getFieldName());
        assertThat(invoiceField.getInputBinding()).isEqualTo(originalDataHolder.getInputId());
        assertThat(invoiceField.getOutputBinding()).isEqualTo(originalDataHolder.getOuputId());
        assertThat(invoiceField.getDefaultSubform()).isNotNull();

        assertThat(invoiceField.getFieldType().getCode()).isEqualTo(FieldTypeBuilder.SUBFORM);

        FormDefinition newFormDefinition = summary.getNewForm().get();

        assertThat(newFormDefinition.getModel())
                .isNotNull()
                .isInstanceOf(modelType);

        assertThat(newFormDefinition.getModel().getProperties()).hasSize(1);
        assertThat(newFormDefinition.getModel().getProperties().get(0).getTypeInfo().getClassName())
                .isEqualTo(INVOICE_MODEL);

        assertThat(newFormDefinition.getFields()).hasSize(1);

        FieldDefinition newInvoiceField = newFormDefinition.getFieldByName("invoice");

        assertThat(newInvoiceField)
                .isNotNull()
                .isInstanceOf(SubFormFieldDefinition.class);
        assertThat(newInvoiceField.getStandaloneClassName()).isEqualTo(INVOICE_MODEL);

        LayoutTemplate newFormLayout = newFormDefinition.getLayoutTemplate();

        assertThat(newFormLayout).isNotNull();

        assertThat(newFormLayout.getRows()).hasSize(1);

        LayoutRow newLayoutRow = newFormLayout.getRows().get(0);

        assertThat(newLayoutRow).isNotNull();

        assertThat(newLayoutRow.getLayoutColumns()).hasSize(1);

        LayoutColumn newLayoutColumn = newLayoutRow.getLayoutColumns().get(0);

        assertThat(newLayoutColumn).isNotNull();
        assertThat(newLayoutColumn.getSpan()).isEqualTo("12");

        assertThat(newLayoutColumn.getLayoutComponents()).hasSize(1);

        checkLayoutFormField(newLayoutColumn.getLayoutComponents().get(0), newInvoiceField, newFormDefinition);

        FormMigrationSummary extraSummary = context.getExtraSummaries()
                .stream()
                .filter(extra -> extra.getBaseFormName().equals(expectedExtraForm))
                .findAny()
                .orElse(null);

        FormDefinition newExtraFormDefinition = extraSummary.getNewForm().get();

        checkInvoiceFormDefinition(newExtraFormDefinition, originalForm);
    }

    protected void checkInvoiceFormDefinition(FormDefinition invoiceForm, Form originalForm) {
        assertThat(invoiceForm).isNotNull();

        assertThat(invoiceForm.getModel())
                .isNotNull()
                .isInstanceOf(DataObjectFormModel.class);
        assertThat(((DataObjectFormModel) invoiceForm.getModel()).getClassName()).isEqualTo(INVOICE_MODEL);

        assertThat(invoiceForm.getFields()).hasSize(4);

        IntStream indexStream = IntStream.range(0, invoiceForm.getFields().size());

        LayoutTemplate formLayout = invoiceForm.getLayoutTemplate();

        assertThat(formLayout).isNotNull();

        assertThat(formLayout.getRows()).hasSize(4);

        indexStream.forEach(index -> {
            FieldDefinition fieldDefinition = invoiceForm.getFields().get(index);

            switch (index) {
                case 0:
                    checkFieldDefinition(fieldDefinition, "invoice_user", "User Data:", "user", SubFormFieldDefinition.class, invoiceForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 1:
                    checkFieldDefinition(fieldDefinition, "lines", "Invoice Lines", "lines", MultipleSubFormFieldDefinition.class, invoiceForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 2:
                    checkFieldDefinition(fieldDefinition, "invoice_total", "Invoice Total:", "total", DecimalBoxFieldDefinition.class, invoiceForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 3:
                    checkFieldDefinition(fieldDefinition, "invoice_list", "List of Values:", "list", IntegerMultipleInputFieldDefinition.class, invoiceForm, originalForm.getField(fieldDefinition.getName()));
                    break;
            }

            LayoutRow fieldRow = formLayout.getRows().get(index);

            assertThat(fieldRow).isNotNull();

            assertThat(fieldRow.getLayoutColumns()).hasSize(1);

            LayoutColumn fieldColumn = fieldRow.getLayoutColumns().get(0);

            assertThat(fieldColumn).isNotNull();
            assertThat(fieldColumn.getSpan()).isEqualTo("12");

            assertThat(fieldColumn.getLayoutComponents()).hasSize(1);

            checkLayoutFormField(fieldColumn.getLayoutComponents().get(0), fieldDefinition, invoiceForm);
        });
    }
}
