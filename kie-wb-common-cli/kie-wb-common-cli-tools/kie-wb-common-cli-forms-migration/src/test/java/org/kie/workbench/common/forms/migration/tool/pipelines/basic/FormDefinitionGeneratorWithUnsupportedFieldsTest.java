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
import java.util.Formatter;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.migration.legacy.model.Field;
import org.kie.workbench.common.forms.migration.legacy.model.Form;
import org.kie.workbench.common.forms.migration.tool.FormMigrationSummary;
import org.kie.workbench.common.forms.migration.tool.Resource;
import org.kie.workbench.common.forms.migration.tool.pipelines.MigrationContext;
import org.kie.workbench.common.forms.migration.tool.util.FormsMigrationConstants;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.migration.cli.RealSystemAccess;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FormDefinitionGeneratorWithUnsupportedFieldsTest extends AbstractFormDefinitionGeneratorTest {

    private static final String USER_WITH_UNSUPPORTED_FIELDS = "user_unsupported_fields.form";

    @Mock
    private Path userFormPath;

    private Form userForm;

    @Override
    protected void doInit() throws Exception {
        List<FormMigrationSummary> summaries = new ArrayList<>();

        initForm(form -> userForm = form, DATAOBJECTS_RESOURCES, USER_WITH_UNSUPPORTED_FIELDS, userFormPath);
        summaries.add(new FormMigrationSummary(new Resource<>(userForm, userFormPath)));

        context = new MigrationContext(workspaceProject, weldContainer, formsMigrationServicesCDIWrapper, new RealSystemAccess(), summaries, migrationServicesCDIWrapper);
    }

    @Test
    public void testMigration() {
        generator.execute(context);

        assertThat(context.getSummaries()).hasSize(1);

        assertThat(context.getExtraSummaries()).isEmpty();

        // 1 legacyforms + 1 migrated forms
        verify(migrationServicesCDIWrapper, times(2)).write(any(Path.class), anyString(), anyString());

        FormMigrationSummary summary = context.getSummaries().iterator().next();

        Form originalForm = summary.getOriginalForm().get();

        FormDefinition newForm = summary.getNewForm().get();

        assertThat(newForm).isNotNull();

        assertThat(newForm.getFields()).hasSize(1);

        LayoutTemplate newLayout = newForm.getLayoutTemplate();

        assertThat(newLayout).isNotNull();

        assertThat(newLayout.getRows()).hasSize(2);

        // Checking first field (login), althought the original field type isn't supported it can be migrated to a textbox
        Field originalLogin = originalForm.getField(USER_LOGIN);
        FieldDefinition newLogin = newForm.getFieldByName(USER_LOGIN);

        assertThat(newLogin).isNotNull();

        checkFieldDefinition(newLogin, USER_LOGIN, "login", "login", TextBoxFieldDefinition.class, newForm, originalLogin);

        LayoutRow loginRow = newLayout.getRows().get(0);

        assertThat(loginRow).isNotNull();

        assertThat(loginRow.getLayoutColumns()).hasSize(1);

        LayoutColumn loginColumn = loginRow.getLayoutColumns().get(0);

        assertThat(loginColumn).isNotNull();
        assertThat(loginColumn.getSpan()).isEqualTo("12");

        assertThat(loginColumn.getLayoutComponents()).hasSize(1);

        checkLayoutFormField(loginColumn.getLayoutComponents().get(0), newLogin, newForm);

        // Checking second field (password), the original field type isn't supported and it cannot be migrated to any
        // other form control. There shouldn't be any FieldDefinition for it but it should be an HTML component on
        // the layout warning about the error
        assertThat(newForm.getFieldByName(USER_PASSWORD)).isNull();

        LayoutRow passwordRow = newLayout.getRows().get(1);

        assertThat(passwordRow).isNotNull();

        assertThat(passwordRow.getLayoutColumns()).hasSize(1);

        LayoutColumn passwordColumn = passwordRow.getLayoutColumns().get(0);

        assertThat(passwordColumn).isNotNull();
        assertThat(passwordColumn.getSpan()).isEqualTo("12");

        assertThat(passwordColumn.getLayoutComponents()).hasSize(1);

        LayoutComponent passwordComponent = passwordColumn.getLayoutComponents().get(0);

        assertThat(passwordComponent.getDragTypeName()).isEqualTo(FormsMigrationConstants.HTML_COMPONENT);

        Field originalPassword = originalForm.getField(USER_PASSWORD);

        Formatter formatter = new Formatter();
        formatter.format(FormsMigrationConstants.UNSUPORTED_FIELD_HTML_TEMPLATE, originalPassword.getFieldName(), originalPassword.getFieldType().getCode());

        final String expectedHtmlMessage = formatter.toString();

        assertThat(passwordComponent.getProperties())
                .containsEntry(FormsMigrationConstants.HTML_CODE_PARAMETER, expectedHtmlMessage);

        formatter.close();
    }
}
