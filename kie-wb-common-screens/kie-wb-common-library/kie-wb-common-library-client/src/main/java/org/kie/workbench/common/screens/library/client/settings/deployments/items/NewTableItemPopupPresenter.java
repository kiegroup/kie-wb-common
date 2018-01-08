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

package org.kie.workbench.common.screens.library.client.settings.deployments.items;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.screens.datamodeller.model.kiedeployment.KieDeploymentDescriptorContent.BlergsModel;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class NewTableItemPopupPresenter {

    private Consumer<BlergsModel> onSave;

    public void show(final Consumer<BlergsModel> onSave) {
        this.onSave = onSave;
        ok();
    }

    public void ok() {

        final BlergsModel newBlergsModel = new BlergsModel();
        newBlergsModel.setName("Test name" + System.currentTimeMillis());
        newBlergsModel.setResolver("Test resolver" + System.currentTimeMillis());

        final Map<String, String> parameters = new HashMap<>();
        parameters.put("Test param 1" + System.currentTimeMillis(), "foo" + System.currentTimeMillis());
        parameters.put("Test param 2" + System.currentTimeMillis(), "bar" + System.currentTimeMillis());
        newBlergsModel.setParameters(parameters);

        onSave.accept(newBlergsModel);
    }

    public void cancel() {

    }

    public interface View extends UberElemental<NewTableItemPopupPresenter> {

    }
}
