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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
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
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.datamodel.backend.server.DataModelOracleUtilities;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;

@Service
@Dependent
public class DataObjectsServiceImpl implements DataObjectsService {

    private DataModelService dataModelService;
    private ModuleClassLoaderHelper moduleClassLoaderHelper;
    private KieModuleService moduleService;

    protected DataObjectsServiceImpl() {
        this(null, null, null);
    }

    @Inject
    public DataObjectsServiceImpl(final DataModelService dataModelService,
                                  final ModuleClassLoaderHelper moduleClassLoaderHelper,
                                  final KieModuleService moduleService) {
        this.dataModelService = dataModelService;
        this.moduleClassLoaderHelper = moduleClassLoaderHelper;
        this.moduleService = moduleService;
    }

    @Override
    public List<DataObject> loadDataObjects(final WorkspaceProject workspaceProject) {
        final KieModule module = moduleService.resolveModule(workspaceProject.getRootPath());
        final ClassLoader classLoader = moduleClassLoaderHelper.getModuleClassLoader(module);

        final ModuleDataModelOracle dmo = dataModelService.getModuleDataModel(workspaceProject.getRootPath());
        final String[] types = DataModelOracleUtilities.getFactTypes(dmo);
        final Map<String, ModelField[]> typesModelFields = dmo.getModuleModelFields();

        return Arrays.stream(types).map(type -> convert(type, typesModelFields, classLoader)).collect(Collectors.toList());
    }

    private DataObject convert(final String type,
                               final Map<String, ModelField[]> typesModelFields,
                               final ClassLoader classLoader) {
        final DataObject dataObject = new DataObject(type);
        final ModelField[] typeModelFields = typesModelFields.getOrDefault(type, new ModelField[]{});
        dataObject.setProperties(Arrays.stream(typeModelFields)
                                         .filter(typeModelField -> !Objects.equals(typeModelField.getName(), DataType.TYPE_THIS))
                                         .map(typeModelField -> convert(typeModelField, classLoader))
                                         .collect(Collectors.toList()));
        return dataObject;
    }

    private DataObjectProperty convert(final ModelField field,
                                       final ClassLoader classLoader) {
        final DataObjectProperty dataObjectProperty = new DataObjectProperty();
        dataObjectProperty.setType(convert(field.getClassName(), classLoader));
        dataObjectProperty.setProperty(field.getName());
        return dataObjectProperty;
    }

    private String convert(final String typeName,
                           final ClassLoader classLoader) {
        try {
            final Class<?> clazz = classLoader.loadClass(unbox(typeName));
            final BuiltInType builtInType = determineBuiltInTypeFromClass(clazz);
            if (Objects.nonNull(builtInType)) {
                return builtInType.getName();
            }
            return typeName;
        } catch (ClassNotFoundException cnfe) {
            return BuiltInType.ANY.getName();
        }
    }

    private String unbox(final String typeName) {
        switch (typeName) {
            case "int":
                return Integer.class.getName();
            case "short":
                return Short.class.getName();
            case "long":
                return Long.class.getName();
            case "byte":
                return Byte.class.getName();
            case "double":
                return Double.class.getName();
            case "char":
                return Character.class.getName();
            case "boolean":
                return Boolean.class.getName();
        }
        return typeName;
    }

    private BuiltInType determineBuiltInTypeFromClass(final Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return BuiltInType.UNDEFINED;
        } else if (Number.class.isAssignableFrom(clazz)) {
            return BuiltInType.NUMBER;
        } else if (String.class.isAssignableFrom(clazz)) {
            return BuiltInType.STRING;
        } else if (LocalDate.class.isAssignableFrom(clazz)) {
            return BuiltInType.DATE;
        } else if (LocalTime.class.isAssignableFrom(clazz) || OffsetTime.class.isAssignableFrom(clazz)) {
            return BuiltInType.TIME;
        } else if (ZonedDateTime.class.isAssignableFrom(clazz) || OffsetDateTime.class.isAssignableFrom(clazz) || LocalDateTime.class.isAssignableFrom(clazz)) {
            return BuiltInType.DATE_TIME;
        } else if (Boolean.class.isAssignableFrom(clazz)) {
            return BuiltInType.BOOLEAN;
        } else if (Map.class.isAssignableFrom(clazz)) {
            return BuiltInType.CONTEXT;
        } else if (TemporalAccessor.class.isAssignableFrom(clazz)) {
            final TemporalAccessor ta = (TemporalAccessor) clazz.cast(TemporalAccessor.class);
            if (!(ta instanceof Temporal) && ta.isSupported(ChronoField.HOUR_OF_DAY)
                    && ta.isSupported(ChronoField.MINUTE_OF_HOUR) && ta.isSupported(ChronoField.SECOND_OF_MINUTE)
                    && ta.query(TemporalQueries.zone()) != null) {
                return BuiltInType.TIME;
            }
        }
        return null;
    }
}
