/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.cms.common.backend.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Module;
import org.kie.workbench.common.forms.cms.common.backend.services.BackendApplicationRuntime;
import org.kie.workbench.common.forms.cms.common.backend.services.BackendPersistenceService;
import org.kie.workbench.common.forms.cms.common.backend.services.DynamicModelMarshaller;
import org.kie.workbench.common.forms.cms.common.shared.events.FormsDeployedEvent;
import org.kie.workbench.common.forms.cms.common.shared.services.FormService;
import org.kie.workbench.common.forms.cms.common.shared.services.impl.AbstractApplicationRuntime;
import org.kie.workbench.common.forms.editor.service.shared.VFSFormFinderService;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModule;

@ApplicationScoped
public class BackendApplicationRuntimeImpl extends AbstractApplicationRuntime implements BackendApplicationRuntime {

    private ModuleClassLoaderHelper classLoaderHelper;

    private VFSFormFinderService formFinderService;

    private DynamicModelMarshaller marshaller;

    private Module deployedModule;

    private Event<FormsDeployedEvent> formsDeployed;

    private ClassLoader classLoader;

    private BackendPersistenceService runtimePersistence;

    private boolean initialized = false;

    @Inject
    public BackendApplicationRuntimeImpl(ModuleClassLoaderHelper classLoaderHelper,
                                         FormService formService,
                                         VFSFormFinderService formFinderService,
                                         DynamicModelMarshaller marshaller,
                                         BackendPersistenceService runtimePersistence,
                                         Event<FormsDeployedEvent> formsDeployed) {
        super(formService);
        this.classLoaderHelper = classLoaderHelper;
        this.formFinderService = formFinderService;
        this.marshaller = marshaller;
        this.runtimePersistence = runtimePersistence;
        this.formsDeployed = formsDeployed;
    }

    @Override
    public void initRuntime(Module module) {
        if(initialized) {
            return;
        }
        this.deployedModule = module;

        this.classLoader = classLoaderHelper.getModuleClassLoader((KieModule) module);

        marshaller.init(this);

        FormsDeployedEvent event = new FormsDeployedEvent(formFinderService.findAllForms(module.getRootPath()));

        formsDeployed.fire(event);

        runtimePersistence.init(this);

        initialized = true;
    }

    @Override
    public Module getDeployedModule() {
        return deployedModule;
    }

    @Override
    public DynamicModelMarshaller getModuleMarshaller() {
        return marshaller;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public ClassLoader getModuleClassLoader() {
        return classLoader;
    }
}
