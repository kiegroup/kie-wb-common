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

package org.kie.workbench.common.stunner.bpmn.backend.legacy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.codehaus.jackson.JsonGenerator;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DynamicDefinitionsBpmnMarshallerHelper implements BpmnMarshallerHelper {

    private Map<String, DynamicDefinitionMarshaller> marshallerMap;
    private static final Logger logger = LoggerFactory.getLogger(DynamicDefinitionsBpmnMarshallerHelper.class);
    public static final String DYNAMIC_DEFINITION_PROPERTY = "dynamicdefinition";

    @Inject
    public DynamicDefinitionsBpmnMarshallerHelper() {
        marshallerMap = new HashMap<>();
        initDefinitions();
    }

    @Override
    public void applyProperties(BaseElement baseElement,
                                Map<String, String> properties) {
        if (properties.containsKey(DYNAMIC_DEFINITION_PROPERTY)) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    DYNAMIC_DEFINITION_PROPERTY,
                    false,
                    false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                                                                                                                           properties.get(DYNAMIC_DEFINITION_PROPERTY));
            baseElement.getAnyAttribute().add(extensionEntry);
            marshallerMap.get(properties.get(DYNAMIC_DEFINITION_PROPERTY)).applyProperties(baseElement, properties);
        }
    }

    public void marshallProperties(FlowElement baseElement,
                                   Map<String, Object> properties,
                                   JsonGenerator generator) {
        if (properties.containsKey(DYNAMIC_DEFINITION_PROPERTY)) {
            marshallerMap.get(properties.get(DYNAMIC_DEFINITION_PROPERTY)).marshallProperties(baseElement, properties,
                                                                                              generator);
            properties.remove(DYNAMIC_DEFINITION_PROPERTY);
        }
    }

    private void initDefinitions() {
        logger.info("----------------------------Adding dynamic marshallers------------------------------------------");
        List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                                                          .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
                                                          .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                                                          .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(""))));
        reflections.getSubTypesOf(DynamicDefinitionMarshaller.class)
                .forEach((t) -> {
            try {
                DynamicDefinitionMarshaller marshaller= t.newInstance();
                logger.info("Adding marshaller for " + marshaller.getClassName());
                marshallerMap.put(marshaller.getClassName(), marshaller);
            }
            catch (Exception e) {
                logger.error(e.getMessage());
            }
        });
        logger.info("--------------------------Done adding dynamic marshallers---------------------------------------");
    }
}
