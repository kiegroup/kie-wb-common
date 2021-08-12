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

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.ReflectionAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class LaneTest {

    @Mock
    private AdvancedData advancedData;

    @Test
    public void testLaneCanContain() {

        final Class<Lane> laneClass = Lane.class;
        final CanContain canContain = laneClass.getAnnotation(CanContain.class);

        final List<String> expectedRoles = singletonList("lane_child");
        final List<String> actualRoles = asList(canContain.roles());

        assertEquals(expectedRoles, actualRoles);
    }

    @Test
    public void testGetAdvancedData() {
        final Lane lane = new Lane(null,
                                   null,
                                   null,
                                   null,
                                   advancedData);
        AdvancedData result = lane.getAdvancedData();
        assertEquals(advancedData, result);
    }

    @Test
    public void testSetAdvancedData() {
        final Lane lane = new Lane();
        assertNotEquals(advancedData, lane.advancedData);
        lane.setAdvancedData(advancedData);
        assertEquals(advancedData, lane.advancedData);
    }

    @Test
    public void testLaneCannotContainAnotherLane() throws Exception {

        final Lane lane = new Lane();
        final Set<String> labels = ReflectionAdapterUtils.getAnnotatedFieldValue(lane, Labels.class);

        assertNotNull(labels);
        assertFalse(labels.contains("lane_child"));
    }
}
