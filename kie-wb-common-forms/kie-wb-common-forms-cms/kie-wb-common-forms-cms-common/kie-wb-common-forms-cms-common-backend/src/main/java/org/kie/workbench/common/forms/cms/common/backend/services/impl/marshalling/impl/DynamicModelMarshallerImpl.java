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

package org.kie.workbench.common.forms.cms.common.backend.services.impl.marshalling.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.kie.soup.project.datamodel.commons.oracle.ModuleDataModelOracleImpl;
import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.soup.project.datamodel.oracle.TypeSource;
import org.kie.workbench.common.forms.cms.common.backend.services.BackendApplicationRuntime;
import org.kie.workbench.common.forms.cms.common.backend.services.DynamicModelMarshaller;
import org.kie.workbench.common.forms.model.util.formModel.FormModelPropertiesUtil;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ClassFactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.FactBuilder;
import org.kie.workbench.common.services.datamodel.backend.server.builder.projects.ModuleDataModelOracleBuilder;

@Dependent
public class DynamicModelMarshallerImpl implements DynamicModelMarshaller {

    private ClassLoader classLoader;

    private Map<String, Marshaller> marshallers = new HashMap<>();

    private MVELEvaluator evaluator;

    @Inject
    public DynamicModelMarshallerImpl(MVELEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public void init(BackendApplicationRuntime runtime) {
        this.classLoader = runtime.getModuleClassLoader();
    }

    @Override
    public Object newInstance(String modelType) {

        Class<?> clazz = loadClass(modelType);

        if (clazz != null) {
            try {
                return ConstructorUtils.invokeConstructor(clazz, null);
            } catch (Exception e) {
            }
        }

        return null;
    }

    private Class<?> loadClass(String modelType) {
        try {
            return classLoader.loadClass(modelType);
        } catch (ClassNotFoundException e1) {
            try {
                return getClass().getClassLoader().loadClass(modelType);
            } catch (ClassNotFoundException e) {

            }
        }

        return null;
    }

    @Override
    public Map<String, Object> marshall(Object bean) {
        Marshaller marshaller = getMarshallerForType(bean.getClass().getName());

        if (marshaller != null) {
            return marshaller.marshal(bean);
        }

        return null;
    }

    @Override
    public Object unMarshall(String type, Map<String, Object> marshalled) {
        return unMarshall(newInstance(type), marshalled);
    }

    @Override
    public Object unMarshall(Object bean, Map<String, Object> marshalled) {
        Marshaller marshaller = getMarshallerForType(bean.getClass().getName());

        if (marshaller != null) {
            return marshaller.unMarshal(bean, marshalled);
        }

        return null;
    }

    private Marshaller getMarshallerForType(String modelType) {
        Marshaller marshaller = marshallers.get(modelType);

        if (marshaller == null) {
            initMarshallers(modelType);
            marshaller = marshallers.get(modelType);
        }

        return marshaller;
    }

    private void initMarshallers(final String modelType) {
        Class<?> clazz = loadClass(modelType);

        if (clazz != null) {
            ModuleDataModelOracle oracle = getModuleOracle(clazz);

            if (oracle != null) {
                registerMarshaller(modelType, oracle);
            }
        }
    }

    private void registerMarshaller(String modelType, ModuleDataModelOracle oracle) {
        ModelField[] fields = oracle.getModuleModelFields().get(modelType);

        List<PropertyMarshaller> propertyMarshallers = new ArrayList<>();

        Arrays.stream(fields)
                .filter(field -> !field.getName().equals("this"))
                .forEach(modelField -> {

                    String fieldName = modelField.getName();
                    String fieldType = modelField.getClassName();

                    boolean isEnum = oracle.getModuleJavaEnumDefinitions().get(modelType + "#" + fieldName) != null;

                    boolean isList = DataType.TYPE_COLLECTION.equals(modelField.getType());

                    if (isList) {
                        fieldType = oracle.getModuleFieldParametersType().get(modelType + "#" + fieldName);
                    }

                    if (FormModelPropertiesUtil.isBaseType(fieldType) || isEnum) {
                        propertyMarshallers.add(new PropertyMarshaller<>(fieldName, bean -> readPropertyValue(fieldName, bean), (bean, marshalled) -> writePropertyValue(fieldName, bean, marshalled)));
                    } else {
                        registerMarshaller(fieldType, oracle);

                        final String nestedType = fieldType;

                        if (isList) {
                            propertyMarshallers.add(new PropertyMarshaller<>(fieldName, bean -> readListValues(fieldName, nestedType, bean), (bean, marshalled) -> writeListValues(fieldName, nestedType, bean, marshalled)));
                        } else {
                            propertyMarshallers.add(new PropertyMarshaller<>(fieldName, bean -> readModelValue(fieldName, nestedType, bean), (bean, marshalled) -> writeModelValue(fieldName, nestedType, bean, marshalled)));
                        }
                    }
                });

        marshallers.put(modelType, new Marshaller(modelType, () -> newInstance(modelType), propertyMarshallers));
    }

    private List<Map<String, Object>> readListValues(final String property, final String nestedType, final Object bean) {
        Marshaller marshaller = getMarshallerForType(nestedType);
        if (marshaller != null && bean != null) {
            List<Object> values = (List) readPropertyValue(property, bean);
            if (values != null) {
                return values.stream()
                        .map(nested -> marshaller.marshal(nested))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private void writeListValues(final String property, final String nestedType, Object bean, final List<Map<String, Object>> marshalledValues) {
        Marshaller marshaller = getMarshallerForType(nestedType);
        if (marshaller != null) {
            if (bean != null) {
                List<Object> list = marshalledValues.stream()
                        .map(marshalledValue -> marshaller.unMarshal(newInstance(nestedType), marshalledValue))
                        .collect(Collectors.toList());

                writePropertyValue(property, bean, list);
            }
        }
    }

    private Map<String, Object> readModelValue(final String property, final String nestedType, final Object bean) {
        Marshaller marshaller = getMarshallerForType(nestedType);
        if (marshaller != null && bean != null) {
            return marshaller.marshal(readPropertyValue(property, bean));
        }
        return new HashMap<>();
    }

    private void writeModelValue(final String property, final String nestedType, final Object bean, final Map<String, Object> marshalled) {
        Marshaller marshaller = getMarshallerForType(nestedType);
        if (marshaller != null) {

            final Object resul = newInstance(nestedType);

            if (resul != null) {
                writePropertyValue(property, bean, marshaller.unMarshal(resul, marshalled));
            }
        }
    }

    private Object readPropertyValue(final String property, final Object bean) {
        try {
            if (PropertyUtils.getPropertyDescriptor(bean, property) != null) {
                return PropertyUtils.getProperty(bean, property);
            }
        } catch (Exception e) {

        }
        return null;
    }

    private void writePropertyValue(final String property, final Object bean, final Object value) {
        try {
            if (PropertyUtils.getPropertyDescriptor(bean, property) != null) {
                BeanUtils.setProperty(bean, property, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected ModuleDataModelOracle getModuleOracle(Class clazz) {
        try {
            final ModuleDataModelOracleBuilder builder = ModuleDataModelOracleBuilder.newModuleOracleBuilder(evaluator);

            final ClassFactBuilder modelFactBuilder = new ClassFactBuilder(builder, clazz, false, TypeSource.JAVA_PROJECT);

            ModuleDataModelOracle oracle = modelFactBuilder.getDataModelBuilder().build();

            Map<String, FactBuilder> builders = new HashMap<>();

            for (FactBuilder factBuilder : modelFactBuilder.getInternalBuilders().values()) {
                if (factBuilder instanceof ClassFactBuilder) {
                    builders.put(((ClassFactBuilder) factBuilder).getType(),
                                 factBuilder);
                    factBuilder.build((ModuleDataModelOracleImpl) oracle);
                }
            }
            builders.put(modelFactBuilder.getType(),
                         modelFactBuilder);

            modelFactBuilder.build((ModuleDataModelOracleImpl) oracle);

            return oracle;
        } catch (IOException ex) {

        }
        return null;
    }
}
