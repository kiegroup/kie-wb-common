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

package org.kie.workbench.common.forms.cms.components.service.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.kie.workbench.common.forms.cms.common.backend.services.BackendApplicationRuntime;
import org.kie.workbench.common.forms.editor.service.shared.VFSFormFinderService;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;

@Dependent
public class ProvidersHelperService {

    public static final String MASTER = "master";

    private LibraryService libraryService;

    private KieModuleService moduleService;

    private DataModelerService dataModelerService;

    private VFSFormFinderService formFinderService;

    // Hardcoded only for POC TODO: remove it
    private BackendApplicationRuntime applicationRuntime;

    @Inject
    public ProvidersHelperService(LibraryService libraryService,
                                  KieModuleService moduleService,
                                  DataModelerService dataModelerService,
                                  VFSFormFinderService formFinderService,
                                  BackendApplicationRuntime applicationRuntime) {
        this.libraryService = libraryService;
        this.moduleService = moduleService;
        this.dataModelerService = dataModelerService;
        this.formFinderService = formFinderService;
        this.applicationRuntime = applicationRuntime;
    }

    public Collection<OrganizationalUnit> getOrganizationalUnits() {
        return libraryService.getOrganizationalUnits();
    }

    public Collection<Module> getOrganizationalUnitModules(String ouId) {
        Collection<OrganizationalUnit> ous = getOrganizationalUnits();

        Optional<OrganizationalUnit> ouOptional = ous.stream().filter(ou -> ou.getIdentifier().equals(ouId)).findFirst();

        List<Module> modules = new ArrayList<>();

        if(ouOptional.isPresent()) {
            OrganizationalUnit ou = ouOptional.get();

            ou.getRepositories().stream()
                    .filter(repository -> repository.getBranch(MASTER).isPresent())
                    .map(repository -> repository.getBranch(MASTER).get())
                    .forEach(branch -> modules.addAll(moduleService.getAllModules(branch)));
        }

        return modules;
    }

    public Collection<DataObject> getModuleDataObjects(String ouId, String projectName) {

        Optional<Module> moduleOptional = getModule(ouId, projectName);

        if(moduleOptional.isPresent()) {
            applicationRuntime.initRuntime(moduleOptional.get());
            return dataModelerService.loadModel((KieModule) moduleOptional.get()).getDataObjects();
        }

        return Collections.EMPTY_LIST;
    }

    public Collection<FormDefinition> getFormsForDataObject(String ouId, String projectName, String dataObject) {
        Optional<Module> moduleOptional = getModule(ouId, projectName);

        if(moduleOptional.isPresent()) {
            return formFinderService.findFormsForType(dataObject, moduleOptional.get().getRootPath());
        }

        return Collections.EMPTY_LIST;
    }

    public FormDefinition getFormById(String ouId, String projectName, String formId) {
        if(applicationRuntime.isInitialized()) {
            return applicationRuntime.getFormService().getFormById(formId);
        }

        Optional<Module> projectOptional = getModule(ouId, projectName);

        if(projectOptional.isPresent()) {
            return formFinderService.findFormById(formId, projectOptional.get().getRootPath());
        }

        return null;
    }

    private Optional<Module> getModule(String ouId, String moduleName) {
        Collection<Module> projects = getOrganizationalUnitModules(ouId);

        return projects.stream().filter(kieProject -> kieProject.getModuleName().equals(moduleName)).findFirst();
    }

    // TODO: remove
    public BackendApplicationRuntime getApplicationRuntime() {
        return applicationRuntime;
    }
}
