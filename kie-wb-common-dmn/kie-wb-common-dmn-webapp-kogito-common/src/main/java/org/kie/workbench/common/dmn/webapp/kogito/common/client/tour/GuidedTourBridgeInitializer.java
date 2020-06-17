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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.tour;

import javax.inject.Inject;

import org.appformer.kogito.bridge.client.guided.tour.GuidedTourBridge;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.observers.GuidedTourGraphObserver;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.observers.GuidedTourGridObserver;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers.GuidedTourGraphElementPositionUtils;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers.GuidedTourHTMLElementPositionUtils;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial.DMNTutorial;

public class GuidedTourBridgeInitializer {

    private final GuidedTourGraphObserver graphObserver;

    private final GuidedTourGridObserver gridObserver;

    private final GuidedTourGraphElementPositionUtils graphPositionUtils;

    private final GuidedTourHTMLElementPositionUtils htmlPositionUtils;

    private final GuidedTourBridge monitorBridge;

    private final DMNTutorial dmnTutorial;

    @Inject
    public GuidedTourBridgeInitializer(final GuidedTourGraphObserver graphObserver,
                                       final GuidedTourGridObserver gridObserver,
                                       final GuidedTourGraphElementPositionUtils graphPositionUtils,
                                       final GuidedTourHTMLElementPositionUtils htmlPositionUtils,
                                       final GuidedTourBridge monitorBridge,
                                       final DMNTutorial dmnTutorial) {
        this.graphObserver = graphObserver;
        this.gridObserver = gridObserver;
        this.graphPositionUtils = graphPositionUtils;
        this.htmlPositionUtils = htmlPositionUtils;
        this.monitorBridge = monitorBridge;
        this.dmnTutorial = dmnTutorial;
    }

    public void initialize() {
        registerPositionProviders();
        registerObservers();
        registerTutorials();
    }

    private void registerPositionProviders() {
        monitorBridge.registerPositionProvider("DMNEditorGraph", graphPositionUtils.getPositionProviderFunction());
        monitorBridge.registerPositionProvider("DMNEditorHTMLElement", htmlPositionUtils.getPositionProviderFunction());
    }

    private void registerObservers() {
        monitorBridge.registerObserver(graphObserver);
        monitorBridge.registerObserver(gridObserver);
    }

    private void registerTutorials() {
        monitorBridge.registerTutorial(dmnTutorial.getTutorial());
    }
}
