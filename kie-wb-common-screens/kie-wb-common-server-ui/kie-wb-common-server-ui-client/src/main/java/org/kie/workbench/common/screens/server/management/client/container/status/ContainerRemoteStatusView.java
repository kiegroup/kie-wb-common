/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.server.management.client.container.status;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.server.management.client.widget.Div;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

@Dependent
@Templated
public class ContainerRemoteStatusView extends Composite
        implements ContainerRemoteStatusPresenter.View {

    @Inject
    @DataField("card-container")
    Div cardContainer;

    @Override
    public void addCard(final IsWidget widget) {
        cardContainer.add(checkNotNull("widget",  widget));
    }

    @Override
    public void removeCard(final IsWidget widget) {
        cardContainer.remove(checkNotNull("widget",  widget));
    }

    @Override
    public void clear() {
        cardContainer.clear();
    }
}
