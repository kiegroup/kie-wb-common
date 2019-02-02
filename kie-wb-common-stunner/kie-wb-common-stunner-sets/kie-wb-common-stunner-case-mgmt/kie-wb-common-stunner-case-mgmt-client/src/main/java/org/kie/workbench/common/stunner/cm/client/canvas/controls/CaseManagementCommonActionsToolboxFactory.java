/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.canvas.controls;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.client.canvas.controls.AbstractCommonActionsToolboxFactory;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolbox;
import org.kie.workbench.common.stunner.forms.client.components.toolbox.FormGenerationToolboxAction;

@Dependent
@CaseManagementEditor
public class CaseManagementCommonActionsToolboxFactory extends AbstractCommonActionsToolboxFactory {

    protected CaseManagementCommonActionsToolboxFactory() {
        super();
    }

    @Inject
    public CaseManagementCommonActionsToolboxFactory(final @CommonActionsToolbox ActionsToolboxFactory commonActionToolbox,
                                                     final @Any ManagedInstance<FormGenerationToolboxAction> generateFormsActions,
                                                     final @Any @CommonActionsToolbox ManagedInstance<ActionsToolboxView> views) {
        super(commonActionToolbox, generateFormsActions, views);
    }

    @PreDestroy
    public void destroy() {
        super.destroy();
    }
}
