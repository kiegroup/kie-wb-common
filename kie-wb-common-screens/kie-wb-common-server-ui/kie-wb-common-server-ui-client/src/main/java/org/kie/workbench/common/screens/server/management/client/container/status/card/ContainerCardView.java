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

package org.kie.workbench.common.screens.server.management.client.container.status.card;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.server.management.client.widget.Div;
import org.kie.workbench.common.screens.server.management.client.widget.card.CardPresenter;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

@Templated
@Dependent
public class ContainerCardView extends Composite
        implements ContainerCardPresenter.View {

    @Inject
    @DataField("container")
    Div container;

    @Override
    public void setCard(final CardPresenter.View cardView) {
        container.add(checkNotNull("cardView",
                                   cardView).asWidget());
    }

    @Override
    public void removeCard(final CardPresenter.View cardView) {
        container.remove(checkNotNull("cardView",
                                   cardView).asWidget());
    }

    @Override
    public void delete() {
        removeFromParent();
    }

    @Override
    public Widget asWidget() {
        return container;
    }
}
