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

package org.kie.workbench.common.stunner.bpmn.client.canvas.controls;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ContextUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.AbstractActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxView;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.forms.client.components.toolbox.FormGenerationToolboxAction;

public abstract class AbstractCommonActionsToolboxFactory extends AbstractActionsToolboxFactory {

    private final ActionsToolboxFactory commonActionToolbox;
    private final ManagedInstance<FormGenerationToolboxAction> generateFormsActions;
    private final ManagedInstance<ActionsToolboxView> views;

    protected AbstractCommonActionsToolboxFactory() {
        this.commonActionToolbox = null;
        this.generateFormsActions = null;
        this.views = null;
    }

    public AbstractCommonActionsToolboxFactory(final ActionsToolboxFactory commonActionToolbox,
                                               final ManagedInstance<FormGenerationToolboxAction> generateFormsActions,
                                               final ManagedInstance<ActionsToolboxView> views) {
        this.commonActionToolbox = commonActionToolbox;
        this.generateFormsActions = generateFormsActions;
        this.views = views;
    }

    @Override
    protected ActionsToolboxView<?> newViewInstance() {
        return views.get();
    }

    @Override
    @SuppressWarnings("all")
    public Collection<ToolboxAction<AbstractCanvasHandler>> getActions(final AbstractCanvasHandler canvasHandler,
                                                                       final Element<?> e) {
        final List<ToolboxAction<AbstractCanvasHandler>> actions = new LinkedList<>();
        actions.addAll(commonActionToolbox.getActions(canvasHandler,
                                                      e));
        if (ContextUtils.isFormGenerationSupported(e)) {
            actions.add(generateFormsActions.get());
        }
        return actions;
    }

    protected void destroy() {
        generateFormsActions.destroyAll();
        views.destroyAll();
    }
}
