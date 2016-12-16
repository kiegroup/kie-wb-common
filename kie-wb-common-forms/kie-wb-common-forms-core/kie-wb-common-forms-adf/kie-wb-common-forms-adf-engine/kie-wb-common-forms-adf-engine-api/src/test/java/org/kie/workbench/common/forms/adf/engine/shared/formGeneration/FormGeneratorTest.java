/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.adf.engine.shared.formGeneration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.model.Person;
import org.kie.workbench.common.forms.adf.engine.shared.test.AbstractFormGenerationTest;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.mockito.runners.MockitoJUnitRunner;

;

@RunWith(MockitoJUnitRunner.class)
public class FormGeneratorTest extends AbstractFormGenerationTest {

    @Before
    @Override
    public void init() {
        super.init();
    }

    @Test
    public void testGenerateFormForModel() {
        FormDefinition form = generator.generateFormForModel(model);

        testGeneratedForm(form,
                          Person.class.getName());
    }

    @Test
    public void testGenerateFormForClass() {
        FormDefinition form = generator.generateFormForClass(Person.class);

        testGeneratedForm(form,
                          Person.class.getName());
    }

    @Test
    public void testGenerateFormForClassName() {
        FormDefinition form = generator.generateFormForClassName(Person.class.getName());

        testGeneratedForm(form,
                          Person.class.getName());
    }
}
