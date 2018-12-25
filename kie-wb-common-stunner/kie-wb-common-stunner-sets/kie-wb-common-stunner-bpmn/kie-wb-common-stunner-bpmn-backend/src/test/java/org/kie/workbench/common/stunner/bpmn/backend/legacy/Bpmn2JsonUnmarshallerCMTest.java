/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.legacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.jboss.drools.MetaDataType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Bpmn2JsonUnmarshallerCMTest {

    private Bpmn2JsonUnmarshaller tested;

    @Before
    public void init() throws Exception {
        tested = new Bpmn2JsonUnmarshaller();
    }

    @Test
    public void testApplyCallActivityProperties_case() throws Exception {
        final CallActivity callActivity = mock(CallActivity.class);
        when(callActivity.getExtensionValues()).thenReturn(new ArrayList<>());

        final String propertyName = "isCase";
        final String propertyValue = "true";
        final String resultName = "case";
        final String resultValue = "<![CDATA[" + propertyValue + "]]>";

        final Map<String, String> properties = new HashMap<>();
        properties.put(propertyName, propertyValue);

        tested.applyCallActivityProperties(callActivity, properties);

        assertFalse(callActivity.getExtensionValues().isEmpty());
        final ExtensionAttributeValue extenstionValue = callActivity.getExtensionValues().get(0);

        final Optional<String> value = extenstionValue.getValue().stream()
                .filter(v -> resultName.equals(((MetaDataType) v.getValue()).getName()))
                .map(v -> ((MetaDataType) v.getValue()).getMetaValue()).findAny();

        assertTrue(value.isPresent() && resultValue.equals(value.get()));
    }

    @Test
    public void testApplyCallActivityProperties_autostart() throws Exception {
        final CallActivity callActivity = mock(CallActivity.class);
        when(callActivity.getExtensionValues()).thenReturn(new ArrayList<>());

        final String propertyName = "customautostart";
        final String propertyValue = "true";
        final String resultName = "customAutoStart";
        final String resultValue = "<![CDATA[" + propertyValue + "]]>";

        final Map<String, String> properties = new HashMap<>();
        properties.put(propertyName, propertyValue);

        tested.applyCallActivityProperties(callActivity, properties);

        assertFalse(callActivity.getExtensionValues().isEmpty());
        final ExtensionAttributeValue extenstionValue = callActivity.getExtensionValues().get(0);

        final Optional<String> value = extenstionValue.getValue().stream()
                .filter(v -> resultName.equals(((MetaDataType) v.getValue()).getName()))
                .map(v -> ((MetaDataType) v.getValue()).getMetaValue()).findAny();

        assertTrue(value.isPresent() && resultValue.equals(value.get()));
    }

    @Test
    public void testApplyAdHocSubProcessProperties() throws Exception {
        final AdHocSubProcess adHocSubProcess = mock(AdHocSubProcess.class);
        when(adHocSubProcess.getExtensionValues()).thenReturn(new ArrayList<>());

        final String propertyName = "customautostart";
        final String propertyValue = "true";
        final String resultName = "customAutoStart";
        final String resultValue = "<![CDATA[" + propertyValue + "]]>";

        final Map<String, String> properties = new HashMap<>();
        properties.put(propertyName, propertyValue);

        tested.applyAdHocSubProcessProperties(adHocSubProcess, properties);

        assertFalse(adHocSubProcess.getExtensionValues().isEmpty());
        final ExtensionAttributeValue extenstionValue = adHocSubProcess.getExtensionValues().get(0);

        final Optional<String> value = extenstionValue.getValue().stream()
                .filter(v -> resultName.equals(((MetaDataType) v.getValue()).getName()))
                .map(v -> ((MetaDataType) v.getValue()).getMetaValue()).findAny();

        assertTrue(value.isPresent() && resultValue.equals(value.get()));
    }
}
