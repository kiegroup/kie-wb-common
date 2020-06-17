/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.observers;

import java.util.Optional;

import org.appformer.kogito.bridge.client.guided.tour.GuidedTourBridge;
import org.appformer.kogito.bridge.client.guided.tour.service.api.UserInteraction;
import org.jboss.errai.ioc.client.api.Disposer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourActions.CREATED;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourActions.UPDATED;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GuidedTourGridObserverTest {

    @Mock
    private Disposer<GuidedTourGridObserver> disposer;

    @Mock
    private GuidedTourBridge bridge;

    private GuidedTourGridObserverFake observer;

    @Before
    public void setup() {
        observer = spy(new GuidedTourGridObserverFake(disposer));
    }

    @Test
    public void testOnEditExpressionEvent() {
        final EditExpressionEvent event = mock(EditExpressionEvent.class);
        final UserInteraction userInteraction = mock(UserInteraction.class);

        doReturn(userInteraction).when(observer).buildUserInteraction(CREATED.name());

        observer.onEditExpressionEvent(event);

        verify(bridge).refresh(userInteraction);
    }

    @Test
    public void testOnExpressionEditorChanged() {
        final ExpressionEditorChanged event = mock(ExpressionEditorChanged.class);
        final UserInteraction userInteraction = mock(UserInteraction.class);

        doReturn(userInteraction).when(observer).buildUserInteraction(UPDATED.name());

        observer.onExpressionEditorChanged(event);

        verify(bridge).refresh(userInteraction);
    }

    class GuidedTourGridObserverFake extends GuidedTourGridObserver {

        GuidedTourGridObserverFake(final Disposer<GuidedTourGridObserver> disposer) {
            super(disposer);
        }

        @Override
        protected Optional<GuidedTourBridge> getMonitorBridge() {
            return Optional.of(bridge);
        }
    }
}
