/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.deployments.eventlisteners;

import javax.inject.Inject;

import org.kie.workbench.common.screens.library.client.settings.deployments.DeploymentsPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.ListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.UberElementalListItem;

public class EventListenerItemPresenter extends ListItemPresenter<Object, DeploymentsPresenter, EventListenerItemView> {

    private Object eventListener;
    private DeploymentsPresenter parentPresenter;

    @Inject
    public EventListenerItemPresenter(final EventListenerItemView view) {
        super(view);
    }

    @Override
    public EventListenerItemPresenter setup(final Object eventListener,
                                            final DeploymentsPresenter parentPresenter) {

        this.eventListener = eventListener;
        this.parentPresenter = parentPresenter;
        return this;
    }

    @Override
    public Object getObject() {
        return eventListener;
    }

    @Override
    public void remove() {
        super.remove();
        parentPresenter.fireChangeEvent();
    }

    public interface View extends UberElementalListItem<EventListenerItemPresenter> {

    }
}
