/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.jgroups.util.Util.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AbstractNodeBuilderTest {

    private NodeBuilderImpl nodeBuilderStartEvent = new NodeBuilderImpl(StartNoneEvent.class);
    private NodeImpl nodeStartEvent = new NodeImpl<>(UUID);
    private StartNoneEvent startNoneEvent = new StartNoneEvent();

    private NodeBuilderImpl nodeBuilderUserTask = new NodeBuilderImpl(UserTask.class);
    private NodeImpl nodeUserTask = new NodeImpl(UUID);
    private UserTask userTask = new UserTask();

    private static final String UUID = "UUID";

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        ViewImpl<StartNoneEvent> viewStartNoneEvent = new ViewImpl<>(startNoneEvent,
                                                                     new BoundsImpl(new BoundImpl(40.0d,
                                                                                                  10.0d),
                                                                                    new BoundImpl(58.0d,
                                                                                                  28.0d)));
        nodeStartEvent.setContent(viewStartNoneEvent);

        ViewImpl<UserTask> viewUserTask = new ViewImpl<>(userTask,
                                                         new BoundsImpl(new BoundImpl(100.0d,
                                                                                      300.0d),
                                                                        new BoundImpl(236.0d,
                                                                                      348.0d)));
        nodeUserTask.setContent(viewUserTask);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSourceMagnet0() {
        Double[] docker = {7d, 7d};

        int magnetIndex = nodeBuilderStartEvent.getSourceConnectionMagnetIndex(null,
                                                                               nodeStartEvent,
                                                                               null,
                                                                               docker);
        assertEquals(0,
                     magnetIndex);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testintSourceMagnet1() {
        Double[] docker = {14d, 0d};

        int magnetIndex = nodeBuilderStartEvent.getSourceConnectionMagnetIndex(null,
                                                                               nodeStartEvent,
                                                                               null,
                                                                               docker);
        assertEquals(1,
                     magnetIndex);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSourceMagnet2() {
        Double[] docker = {28d, 0d};

        int magnetIndex = nodeBuilderStartEvent.getSourceConnectionMagnetIndex(null,
                                                                               nodeStartEvent,
                                                                               null,
                                                                               docker);
        assertEquals(2,
                     magnetIndex);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSourceMagnet3() {
        Double[] docker = {28d, 14d};

        int magnetIndex = nodeBuilderStartEvent.getSourceConnectionMagnetIndex(null,
                                                                               nodeStartEvent,
                                                                               null,
                                                                               docker);
        assertEquals(3,
                     magnetIndex);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSourceMagnet4() {
        Double[] docker = {28d, 28d};

        int magnetIndex = nodeBuilderStartEvent.getSourceConnectionMagnetIndex(null,
                                                                               nodeStartEvent,
                                                                               null,
                                                                               docker);
        assertEquals(4,
                     magnetIndex);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSourceMagnet5() {
        Double[] docker = {14d, 28d};

        int magnetIndex = nodeBuilderStartEvent.getSourceConnectionMagnetIndex(null,
                                                                               nodeStartEvent,
                                                                               null,
                                                                               docker);
        assertEquals(5,
                     magnetIndex);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSourceMagnet6() {
        Double[] docker = {0d, 28d};

        int magnetIndex = nodeBuilderStartEvent.getSourceConnectionMagnetIndex(null,
                                                                               nodeStartEvent,
                                                                               null,
                                                                               docker);
        assertEquals(6,
                     magnetIndex);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSourceMagnet7() {
        Double[] docker = {0d, 14d};

        int magnetIndex = nodeBuilderStartEvent.getSourceConnectionMagnetIndex(null,
                                                                               nodeStartEvent,
                                                                               null,
                                                                               docker);
        assertEquals(7,
                     magnetIndex);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSourceMagnet8() {
        Double[] docker = {0d, 0d};

        int magnetIndex = nodeBuilderStartEvent.getSourceConnectionMagnetIndex(null,
                                                                               nodeStartEvent,
                                                                               null,
                                                                               docker);
        assertEquals(8,
                     magnetIndex);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTargetMagnet0() {
        Double[] docker = {68d, 24d};

        int magnetIndex = nodeBuilderUserTask.getTargetConnectionMagnetIndex(null,
                                                                             nodeUserTask,
                                                                             null,
                                                                             docker);
        assertEquals(0,
                     magnetIndex);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTargetMagnet2() {
        Double[] docker = {136d, 0d};

        int magnetIndex = nodeBuilderUserTask.getTargetConnectionMagnetIndex(null,
                                                                             nodeUserTask,
                                                                             null,
                                                                             docker);
        assertEquals(2,
                     magnetIndex);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTargetMagnet6() {
        Double[] docker = {0d, 48d};

        int magnetIndex = nodeBuilderUserTask.getTargetConnectionMagnetIndex(null,
                                                                             nodeUserTask,
                                                                             null,
                                                                             docker);
        assertEquals(6,
                     magnetIndex);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTargetMagnet8() {
        Double[] docker = {0d, 0d};

        int magnetIndex = nodeBuilderUserTask.getTargetConnectionMagnetIndex(null,
                                                                             nodeUserTask,
                                                                             null,
                                                                             docker);
        assertEquals(8,
                     magnetIndex);
    }
}
