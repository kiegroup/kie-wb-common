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

package org.kie.workbench.common.screens.home.client;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.kie.workbench.common.screens.home.client.widgets.shortcut.utils.HomeModelAuthorizationManager;
import org.uberfire.client.workbench.events.ApplicationReadyEvent;

@EntryPoint
@Bundle("resources/i18n/HomeConstants.properties")
public class HomeEntryPoint {

    private final HomeModelAuthorizationManager authManager;

    @Inject
    public HomeEntryPoint(final HomeModelAuthorizationManager authManager) {
        this.authManager = authManager;
    }

    public void setup(@Observes ApplicationReadyEvent event) {
        this.authManager.setup();
    }
}
