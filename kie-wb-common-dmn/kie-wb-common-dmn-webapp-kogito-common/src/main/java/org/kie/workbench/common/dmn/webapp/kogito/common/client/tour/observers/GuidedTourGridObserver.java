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

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.appformer.kogito.bridge.client.guided.tour.GuidedTourBridge;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourObserver;
import org.appformer.kogito.bridge.client.guided.tour.service.api.UserInteraction;
import org.jboss.errai.ioc.client.api.Disposer;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;

import static org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourActions.CREATED;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourActions.UPDATED;

public class GuidedTourGridObserver extends GuidedTourObserver<GuidedTourGridObserver> {

    private static final String BOXED_EXPRESSION = "BOXED_EXPRESSION";

    @Inject
    public GuidedTourGridObserver(final Disposer<GuidedTourGridObserver> disposer) {
        super(disposer);
    }

    public void onEditExpressionEvent(final @Observes EditExpressionEvent e) {
        onBoxedExpressionEvent(CREATED.name());
    }

    public void onExpressionEditorChanged(final @Observes ExpressionEditorChanged e) {
        onBoxedExpressionEvent(UPDATED.name());
    }

    private void onBoxedExpressionEvent(final String action) {
        final Optional<GuidedTourBridge> monitorBridge = getMonitorBridge();
        monitorBridge.ifPresent(bridge -> bridge.refresh(buildUserInteraction(action)));
    }

    UserInteraction buildUserInteraction(final String action) {
        final UserInteraction userInteraction = new UserInteraction();
        userInteraction.setAction(action);
        userInteraction.setTarget(BOXED_EXPRESSION);
        return userInteraction;
    }
}
