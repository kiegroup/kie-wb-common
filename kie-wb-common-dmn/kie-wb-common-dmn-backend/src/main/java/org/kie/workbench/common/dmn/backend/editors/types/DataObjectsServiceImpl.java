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

package org.kie.workbench.common.dmn.backend.editors.types;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.dmn.api.editors.types.DataObject;
import org.kie.workbench.common.dmn.api.editors.types.DataObjectProperty;
import org.kie.workbench.common.dmn.api.editors.types.DataObjectsService;
import org.kie.workbench.common.services.datamodel.backend.server.DataModelOracleUtilities;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;

@Service
@Dependent
public class DataObjectsServiceImpl implements DataObjectsService {

    private DataModelService dataModelService;

    protected DataObjectsServiceImpl() {
        this(null);
    }

    @Inject
    public DataObjectsServiceImpl(final DataModelService dataModelService) {
        this.dataModelService = dataModelService;
    }

    @Override
    public List<DataObject> loadDataObjects(final WorkspaceProject workspaceProject) {
        final ModuleDataModelOracle dmo = dataModelService.getModuleDataModel(workspaceProject.getRootPath());
        final String[] types = DataModelOracleUtilities.getFactTypes(dmo);
        final Map<String, ModelField[]> typesModelFields = dmo.getModuleModelFields();

        return Arrays.stream(types).map(type -> convert(type, typesModelFields)).collect(Collectors.toList());
    }

    private DataObject convert(final String type,
                               final Map<String, ModelField[]> typesModelFields) {
        final DataObject dataObject = new DataObject(type);
        final ModelField[] typeModelFields = typesModelFields.getOrDefault(type, new ModelField[]{});
        dataObject.setProperties(Arrays.stream(typeModelFields)
                                         .filter(typeModelField -> !Objects.equals(typeModelField.getName(), DataType.TYPE_THIS))
                                         .map(this::convert)
                                         .collect(Collectors.toList()));
        return dataObject;
    }

    private DataObjectProperty convert(final ModelField field) {
        final DataObjectProperty dataObjectProperty = new DataObjectProperty();
        dataObjectProperty.setType(field.getClassName());
        dataObjectProperty.setProperty(field.getName());
        return dataObjectProperty;
    }
}
