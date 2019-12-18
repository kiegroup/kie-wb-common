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

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Before;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition.MultipleSubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestMetaDataEntryManager;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.migration.legacy.model.Field;
import org.kie.workbench.common.forms.migration.legacy.model.Form;
import org.kie.workbench.common.forms.migration.legacy.services.FormSerializationManager;
import org.kie.workbench.common.forms.migration.legacy.services.impl.FormSerializationManagerImpl;
import org.kie.workbench.common.forms.migration.tool.FormMigrationSummary;
import org.kie.workbench.common.forms.migration.tool.cdi.FormsMigrationServicesCDIWrapper;
import org.kie.workbench.common.forms.migration.tool.pipelines.MigrationContext;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.BPMNFormAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.DataObjectFormAdapter;
import org.kie.workbench.common.forms.migration.tool.pipelines.basic.impl.adapters.fields.AbstractFieldAdapter;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormLayoutComponent;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormModelSerializer;
import org.kie.workbench.common.migration.cli.MigrationServicesCDIWrapper;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public abstract class AbstractFormDefinitionGeneratorTest {

    protected static final String INVOICE_MODEL = "org.jbpm.invoices.Invoice";
    protected static final String USER_MODEL = "org.jbpm.invoices.User";
    protected static final String LINE_MODEL = "org.jbpm.invoices.InvoiceLine";

    protected static final String DATAOBJECTS_RESOURCES = "/forms/dataObjects/";
    protected static final String BPMN_RESOURCES = "/forms/bpmn/";

    protected static final String ROOT_PATH = "default:///src/main/resources/";
    protected static final String INVOICE_FORM = "invoice.form";
    protected static final String USER_FORM = "user.form";
    protected static final String LINE_FORM = "line.form";
    protected static final String PROCESS_FORM = "invoices.invoices-taskform.form";
    protected static final String TASK_FORM = "modify-taskform.form";
    protected static final String INVOICES_BPMN = "invoices.bpmn2";

    protected static final String INVOICE_USER = "invoice_user";
    protected static final String INVOICE_LINES = "lines";
    protected static final String INVOICE_TOTAL = "invoice_total";

    protected static final String USER_LOGIN = "user_login";
    protected static final String USER_PASSWORD = "user_password";

    protected static final String LINE_PRODUCT = "line_product";
    protected static final String LINE_PRICE = "price";
    protected static final String LINE_QUANTITY = "quantity";
    protected static final String LINE_TOTAL = "total";

    @Mock
    protected WorkspaceProject workspaceProject;

    @Mock
    protected WeldContainer weldContainer;

    @Mock
    protected FormsMigrationServicesCDIWrapper formsMigrationServicesCDIWrapper;

    @Mock
    protected MigrationServicesCDIWrapper migrationServicesCDIWrapper;

    @Mock
    protected IOService ioService;

    @Mock
    protected Path path;

    protected FormSerializationManager serializer = new FormSerializationManagerImpl();

    protected FormDefinitionSerializer formDefinitionSerializer;

    protected SimpleFileSystemProvider simpleFileSystemProvider = null;

    protected MigrationContext context;

    protected FormDefinitionGenerator generator;

    @Before
    public void init() throws Exception {
        formDefinitionSerializer = new FormDefinitionSerializerImpl(new FieldSerializer(), new FormModelSerializer(), new TestMetaDataEntryManager());

        when(migrationServicesCDIWrapper.getIOService()).thenReturn(ioService);
        when(formsMigrationServicesCDIWrapper.getFormDefinitionSerializer()).thenReturn(formDefinitionSerializer);

        simpleFileSystemProvider = new SimpleFileSystemProvider();
        simpleFileSystemProvider.forceAsDefault();

        when(path.toURI()).thenReturn(ROOT_PATH);

        when(workspaceProject.getRootPath()).thenReturn(path);

        generator = new FormDefinitionGenerator(DataObjectFormAdapter::new, this::getBPMNAdapter);

        doInit();
    }

    protected abstract void doInit() throws Exception;

    protected List<JBPMFormModel> getProcessFormModels() {
        return new ArrayList<>();
    }

    protected void initForm(Consumer<Form> formConsumer, String resourcePath, String name, Path formPath) throws Exception {
        Form form = serializer.loadFormFromXML(IOUtils.toString(new InputStreamReader(this.getClass().getResourceAsStream(resourcePath + name))));
        formConsumer.accept(form);
        when(formPath.toURI()).thenReturn(ROOT_PATH + name);
        when(formPath.getFileName()).thenReturn(name);
    }

    protected BPMNFormAdapter getBPMNAdapter(MigrationContext context) {
        return new BPMNFormAdapter(context) {
            @Override
            protected void readWorkspaceBPMNModels() {
                workspaceBPMNFormModels.addAll(getProcessFormModels());
            }
        };
    }

    protected void checkMovedField(Field field, String targetForm) {
        assertThat(field).isNotNull();
        assertThat(field.getMovedToForm()).isEqualTo(targetForm);
    }

    protected void checkLayoutFormField(final LayoutComponent layoutComponent, final FieldDefinition fieldDefinition, final FormDefinition formDefinition) {
        assertThat(layoutComponent).isNotNull();
        assertThat(layoutComponent.getDragTypeName()).isEqualTo(AbstractFieldAdapter.DRAGGABLE_TYPE);

        assertThat(layoutComponent.getProperties())
                .containsEntry(FormLayoutComponent.FORM_ID, formDefinition.getId())
                .containsEntry(FormLayoutComponent.FIELD_ID, fieldDefinition.getId());
    }

    protected void checkFieldDefinition(
            FieldDefinition fieldDefinition,
            String name,
            String label,
            String binding,
            Class<? extends FieldDefinition> expectedClass,
            FormDefinition formDefinition,
            Field field
    ) {
        assertThat(fieldDefinition).isNotNull();
        assertThat(fieldDefinition.getName()).isEqualTo(name);
        assertThat(fieldDefinition.getLabel()).isEqualTo(label);
        assertThat(fieldDefinition.getBinding()).isEqualTo(binding);
        assertThat(fieldDefinition.getId()).isEqualTo(String.valueOf(field.getId()));
        assertThat(fieldDefinition.getRequired()).isEqualTo(field.getFieldRequired());
        assertThat(fieldDefinition.getReadOnly()).isEqualTo(field.getReadonly());
        assertThat(fieldDefinition).isInstanceOf(expectedClass);

        ModelProperty property = formDefinition.getModel().getProperty(fieldDefinition.getBinding());
        assertThat(property).isNotNull();
        assertThat(property.getTypeInfo()).isEqualTo(fieldDefinition.getFieldTypeInfo());
    }

    protected void verifyInvoiceForm(FormMigrationSummary summary) {
        Form originalForm = summary.getOriginalForm().get();

        assertThat(originalForm.getFormFields()).hasSize(3);

        FormDefinition newForm = summary.getNewForm().get();

        assertThat(newForm.getFields()).hasSize(3);

        assertThat(newForm.getModel())
                .isNotNull()
                .isInstanceOf(DataObjectFormModel.class);
        assertThat(((DataObjectFormModel) newForm.getModel()).getClassName()).isEqualTo(INVOICE_MODEL);

        IntStream indexStream = IntStream.range(0, newForm.getFields().size());

        LayoutTemplate formLayout = newForm.getLayoutTemplate();

        assertThat(formLayout).isNotNull();

        assertThat(formLayout.getRows())
                .isNotEmpty()
                .hasSize(newForm.getFields().size());

        indexStream.forEach(index -> {
            FieldDefinition fieldDefinition = newForm.getFields().get(index);

            switch (index) {
                case 0:
                    checkFieldDefinition(fieldDefinition, INVOICE_USER, "user (invoice)", "user", SubFormFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 1:
                    checkFieldDefinition(fieldDefinition, INVOICE_LINES, "lines (invoice)", "lines", MultipleSubFormFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 3:
                    checkFieldDefinition(fieldDefinition, INVOICE_LINES, "lines (invoice)", "lines", MultipleSubFormFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
                    break;
            }

            LayoutRow fieldRow = formLayout.getRows().get(index);

            assertThat(fieldRow).isNotNull();

            assertThat(fieldRow.getLayoutColumns()).hasSize(1);

            LayoutColumn fieldColumn = fieldRow.getLayoutColumns().get(0);

            assertThat(fieldColumn).isNotNull();
            assertThat(fieldColumn.getSpan()).isEqualTo("12");

            assertThat(fieldColumn.getLayoutComponents()).hasSize(1);

            checkLayoutFormField(fieldColumn.getLayoutComponents().get(0), fieldDefinition, newForm);
        });
    }

    protected void verifyUserForm(FormMigrationSummary summary) {
        Form originalForm = summary.getOriginalForm().get();

        assertThat(originalForm.getFormFields()).hasSize(2);

        FormDefinition newForm = summary.getNewForm().get();

        assertThat(newForm.getFields()).hasSize(2);

        assertThat(newForm.getModel())
                .isNotNull()
                .isInstanceOf(DataObjectFormModel.class);
        assertThat(((DataObjectFormModel) newForm.getModel()).getClassName()).isEqualTo(USER_MODEL);

        IntStream indexStream = IntStream.range(0, newForm.getFields().size());

        LayoutTemplate formLayout = newForm.getLayoutTemplate();

        assertThat(formLayout).isNotNull();

        assertThat(formLayout.getRows()).hasSize(2);

        indexStream.forEach(index -> {
            FieldDefinition fieldDefinition = newForm.getFields().get(index);

            switch (index) {
                case 0:
                    checkFieldDefinition(fieldDefinition, USER_LOGIN, "login", "login", TextBoxFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 1:
                    checkFieldDefinition(fieldDefinition, USER_PASSWORD, "password", "password", TextBoxFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
                    break;
            }

            LayoutRow fieldRow = formLayout.getRows().get(index);

            assertThat(fieldRow).isNotNull();

            assertThat(fieldRow.getLayoutColumns()).hasSize(1);

            LayoutColumn fieldColumn = fieldRow.getLayoutColumns().get(0);

            assertThat(fieldColumn).isNotNull();
            assertThat(fieldColumn.getSpan()).isEqualTo("12");

            assertThat(fieldColumn.getLayoutComponents()).hasSize(1);

            checkLayoutFormField(fieldColumn.getLayoutComponents().get(0), fieldDefinition, newForm);
        });
    }

    protected void verifyLineForm(FormMigrationSummary summary) {
        Form originalForm = summary.getOriginalForm().get();

        assertThat(originalForm.getFormFields()).hasSize(4);

        FormDefinition newForm = summary.getNewForm().get();

        assertThat(newForm.getFields()).hasSize(4);

        assertThat(newForm.getModel())
                .isNotNull()
                .isInstanceOf(DataObjectFormModel.class);
        assertThat(((DataObjectFormModel) newForm.getModel()).getClassName()).isEqualTo(LINE_MODEL);

        IntStream indexStream = IntStream.range(0, newForm.getFields().size());

        LayoutTemplate formLayout = newForm.getLayoutTemplate();

        assertThat(formLayout).isNotNull();

        assertThat(formLayout.getRows()).hasSize(1);

        LayoutRow fieldRow = formLayout.getRows().get(0);

        indexStream.forEach(index -> {
            FieldDefinition fieldDefinition = newForm.getFields().get(index);

            switch (index) {
                case 0:
                    checkFieldDefinition(fieldDefinition, LINE_PRODUCT, "product", "product", TextBoxFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 1:
                    checkFieldDefinition(fieldDefinition, LINE_PRICE, "price", "price", DecimalBoxFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 2:
                    checkFieldDefinition(fieldDefinition, LINE_QUANTITY, "quantity", "quantity", IntegerBoxFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
                    break;
                case 3:
                    checkFieldDefinition(fieldDefinition, LINE_TOTAL, "total", "total", DecimalBoxFieldDefinition.class, newForm, originalForm.getField(fieldDefinition.getName()));
                    break;
            }

            assertThat(fieldRow).isNotNull();

            assertThat(fieldRow.getLayoutColumns()).hasSize(4);

            LayoutColumn fieldColumn = fieldRow.getLayoutColumns().get(index);

            assertThat(fieldColumn).isNotNull();
            assertThat(fieldColumn.getSpan()).isEqualTo("3");

            assertThat(fieldColumn.getLayoutComponents()).hasSize(1);

            checkLayoutFormField(fieldColumn.getLayoutComponents().get(0), fieldDefinition, newForm);
        });
    }
}
