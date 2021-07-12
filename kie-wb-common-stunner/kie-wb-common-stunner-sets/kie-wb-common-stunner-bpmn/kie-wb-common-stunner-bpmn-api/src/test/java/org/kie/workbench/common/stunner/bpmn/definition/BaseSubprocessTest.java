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

package org.kie.workbench.common.stunner.bpmn.definition;

import java.util.Set;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.ReflectionAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BaseSubprocessTest {

    @Mock
    private AdvancedData advancedData;

    @Test
    public void testBaseSubprocessCanBeContainedByALane() throws Exception {

        final FakeBaseSubprocess baseSubprocess = new FakeBaseSubprocess();
        final Set<String> labels = ReflectionAdapterUtils.getAnnotatedFieldValue(baseSubprocess, Labels.class);

        assertNotNull(labels);
        assertTrue(labels.contains("lane_child"));
    }

    @Test
    public void testGetAdvancedData() {
        final FakeBaseSubprocess baseSubprocess = new FakeBaseSubprocess(null,
                                                                         null,
                                                                         null,
                                                                         null,
                                                                         null,
                                                                         advancedData);
        AdvancedData result = baseSubprocess.getAdvancedData();
        assertEquals(advancedData, result);
    }

    @Test
    public void testSetAdvancedData() {
        final FakeBaseSubprocess baseSubprocess = new FakeBaseSubprocess();
        assertNull(baseSubprocess.advancedData);
        baseSubprocess.setAdvancedData(advancedData);
        assertEquals(advancedData, baseSubprocess.advancedData);
    }

    private class FakeBaseSubprocess extends BaseSubprocess {

        FakeBaseSubprocess() {
            super();
        }

        FakeBaseSubprocess(final BPMNGeneralSet general,
                           final BackgroundSet backgroundSet,
                           final FontSet fontSet,
                           final RectangleDimensionsSet dimensionsSet,
                           final SimulationSet simulationSet,
                           final AdvancedData advancedData) {
            super(general,
                  backgroundSet,
                  fontSet,
                  dimensionsSet,
                  simulationSet,
                  advancedData);
        }
    }
}
