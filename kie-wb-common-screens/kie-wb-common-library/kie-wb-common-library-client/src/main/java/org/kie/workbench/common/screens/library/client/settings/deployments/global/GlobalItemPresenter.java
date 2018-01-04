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

package org.kie.workbench.common.screens.library.client.settings.deployments.global;

import org.kie.workbench.common.screens.library.client.settings.deployments.DeploymentsPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.ListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.UberElementalListItem;

public class GlobalItemPresenter extends ListItemPresenter<Object, DeploymentsPresenter, GlobalItemView> {

    private Object object;

    protected GlobalItemPresenter(final GlobalItemView view) {
        super(view);
    }

    @Override
    public GlobalItemPresenter setup(final Object object,
                                     final DeploymentsPresenter parentPresenter) {
        this.object = object;
        return this;
    }

    @Override
    public Object getObject() {
        return object;
    }

    public interface View extends UberElementalListItem<GlobalItemPresenter> {

    }
}
