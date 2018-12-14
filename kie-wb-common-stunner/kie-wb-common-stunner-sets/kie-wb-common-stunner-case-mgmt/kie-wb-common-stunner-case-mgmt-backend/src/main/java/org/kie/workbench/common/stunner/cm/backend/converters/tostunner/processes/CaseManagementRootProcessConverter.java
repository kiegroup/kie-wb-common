/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.cm.backend.converters.tostunner.processes;

import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BaseConverterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.processes.BaseRootProcessConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessInstanceDescription;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Version;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.definition.property.diagram.CaseManagementDiagramSet;

public class CaseManagementRootProcessConverter extends BaseRootProcessConverter<CaseManagementDiagram> {

    public CaseManagementRootProcessConverter(TypedFactoryManager typedFactoryManager,
                                              PropertyReaderFactory propertyReaderFactory,
                                              DefinitionResolver definitionResolver,
                                              BaseConverterFactory<CaseManagementDiagram, ?, ?> factory) {
        super(typedFactoryManager, propertyReaderFactory, definitionResolver, factory);
    }

    @Override
    public Class<CaseManagementDiagram> getDiagramClass() {
        return CaseManagementDiagram.class;
    }

    @Override
    protected CaseManagementDiagramSet createDiagramSet(Name name,
                                                        Documentation documentation,
                                                        Id id,
                                                        Package pkg,
                                                        Version version,
                                                        AdHoc adHoc,
                                                        ProcessInstanceDescription processInstanceDescription,
                                                        Executable executable) {
        return new CaseManagementDiagramSet(name, documentation, id, pkg, version, new AdHoc(true), processInstanceDescription, executable);
    }
}
