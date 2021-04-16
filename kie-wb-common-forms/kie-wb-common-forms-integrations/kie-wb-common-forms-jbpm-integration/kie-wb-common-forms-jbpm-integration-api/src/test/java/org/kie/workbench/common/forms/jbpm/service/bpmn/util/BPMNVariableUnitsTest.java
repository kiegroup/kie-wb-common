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

package org.kie.workbench.common.forms.jbpm.service.bpmn.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BPMNVariableUnitsTest {

    @Test
    public void testGenerateVariableProperty() {
        ModelProperty modelProperty = BPMNVariableUtils.generateVariableProperty("Object",
                                                                                 "java.lang.Object",
                                                                                 new ClassLoader() {
                                                                                     @Override
                                                                                     public Class<?> loadClass(String name) throws ClassNotFoundException {
                                                                                         return Object.class;
                                                                                     }
                                                                                 });
        Assert.assertNull(modelProperty.getMetaData().getEntry("field-required"));
        Assert.assertNull(modelProperty.getMetaData().getEntry("field-readOnly"));

        modelProperty = BPMNVariableUtils.generateVariableProperty("Object",
                                                                   "java.lang.Object",
                                                                   true,
                                                                   new ClassLoader() {
                                                                       @Override
                                                                       public Class<?> loadClass(String name) throws ClassNotFoundException {
                                                                           return Object.class;
                                                                       }
                                                                   });

        Assert.assertTrue((Boolean)modelProperty.getMetaData().getEntry("field-readOnly").getValue());

        modelProperty = BPMNVariableUtils.generateVariableProperty("Object",
                                                                   "java.lang.Object",
                                                                   true,
                                                                   true,
                                                                   new ClassLoader() {
                                                                       @Override
                                                                       public Class<?> loadClass(String name) throws ClassNotFoundException {
                                                                           return Object.class;
                                                                       }
                                                                   });

        Assert.assertTrue((Boolean)modelProperty.getMetaData().getEntry("field-required").getValue());
        Assert.assertTrue((Boolean)modelProperty.getMetaData().getEntry("field-readOnly").getValue());
    }
}
