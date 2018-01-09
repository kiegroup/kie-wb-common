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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.databinding.client.api.Converter;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.RequiresValueConverter;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.converters.LOVConvertersFactory;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.LOVCreationInput;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.AbstractLOVInputFieldDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.LOVInputFieldType;

@Dependent
public class LOVInputFieldRenderer extends FieldRenderer<AbstractLOVInputFieldDefinition, DefaultFormGroup> implements RequiresValueConverter {

    private LOVCreationInput input;

    @Inject
    public LOVInputFieldRenderer(LOVCreationInput input) {
        this.input = input;
    }

    @Override
    protected FormGroup getFormGroup(RenderMode renderMode) {
        DefaultFormGroup formGroup = formGroupsInstance.get();

        input.setPageSize(field.getPageSize());
        input.init(field.getStandaloneClassName());

        if(field.getReadOnly() || !renderMode.equals(RenderMode.EDIT_MODE)) {
            setReadOnly(true);
        }

        formGroup.render(input.asWidget(), field);

        return formGroup;
    }

    @Override
    public String getName() {
        return LOVInputFieldType.NAME;
    }

    @Override
    public String getSupportedCode() {
        return LOVInputFieldType.NAME;
    }

    @Override
    protected void setReadOnly(boolean readOnly) {
        input.setReadOnly(readOnly);
    }

    @Override
    public Converter getConverter() {
        return LOVConvertersFactory.getConverter(field.getStandaloneClassName());
    }
}
