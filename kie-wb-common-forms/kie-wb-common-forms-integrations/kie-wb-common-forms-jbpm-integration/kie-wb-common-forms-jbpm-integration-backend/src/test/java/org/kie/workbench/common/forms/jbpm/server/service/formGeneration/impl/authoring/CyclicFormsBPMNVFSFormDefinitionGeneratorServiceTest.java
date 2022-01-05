/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.jbpm.server.service.formGeneration.impl.authoring;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.model.Address;
import org.kie.workbench.common.forms.jbpm.server.service.formGeneration.model.Person;
import org.kie.workbench.common.forms.model.TypeKind;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CyclicFormsBPMNVFSFormDefinitionGeneratorServiceTest extends BPMNVFSFormDefinitionGeneratorServiceTest {

    @Override
    public void setup() throws IOException {
        super.setup();

        when(modelFinderService.getModel(Mockito.<String>any(), any())).then(this::getModel);
        when(modelReader.readFormModel(Mockito.<String>any())).then(this::getModel);
    }

    @Test
    public void testCreateNewProcessFormNestedFormsWithCyclicReference() {

        when(ioService.exists(any())).thenReturn(false);

        launchNestedFormWithCyclicReference();
    }

    private DataObjectFormModel getModel(InvocationOnMock invocationOnMock) {
        String className = invocationOnMock.getArguments()[0].toString();

        if (Person.class.getName().equals(className)) {
            return getPersonFormModel();
        }

        if (Address.class.getName().equals(className)) {
            return getAddressFormModel();
        }

        return null;
    }

    protected DataObjectFormModel getPersonFormModel() {
        DataObjectFormModel model = new DataObjectFormModel(Person.class.getSimpleName(), Person.class.getName());

        model.addProperty("name", String.class.getName(), TypeKind.BASE, false);
        model.addProperty("address", Address.class.getName(), TypeKind.OBJECT, false);

        return model;
    }

    protected DataObjectFormModel getAddressFormModel() {
        DataObjectFormModel model = new DataObjectFormModel(Address.class.getSimpleName(), Address.class.getName());

        model.addProperty("owner", Person.class.getName(), TypeKind.OBJECT, false);

        return model;
    }
}
