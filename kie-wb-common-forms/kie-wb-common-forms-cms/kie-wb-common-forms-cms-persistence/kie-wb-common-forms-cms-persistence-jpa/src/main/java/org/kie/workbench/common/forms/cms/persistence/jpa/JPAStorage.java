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

package org.kie.workbench.common.forms.cms.persistence.jpa;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.cms.common.backend.services.BackendApplicationRuntime;
import org.kie.workbench.common.forms.cms.persistence.service.Storage;
import org.kie.workbench.common.forms.cms.persistence.shared.InstanceCreationResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.InstanceDeleteResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.InstanceEditionResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.OperationResult;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistentInstance;

@Dependent
public class JPAStorage implements Storage {

    private static final String ID_PROPERTY = "id";

    private BackendApplicationRuntime runtime;

    private JPAPersistenceManagerBuilder persisnteceManagerBuilder;

    private JPAPersistenceManager persistenceManager;

    @Inject
    public JPAStorage(JPAPersistenceManagerBuilder persisnteceManagerBuilder) {
        this.persisnteceManagerBuilder = persisnteceManagerBuilder;
    }

    @Override
    public void init(BackendApplicationRuntime runtime) {
        this.runtime = runtime;

        if(persistenceManager != null) {
            persistenceManager.destroy();
        }

        persistenceManager = persisnteceManagerBuilder.getPersistenceManager(runtime);

        if (persistenceManager == null) {
            throw new IllegalStateException("Cannot initialize Persistence Manager");
        }
    }

    @Override
    public InstanceCreationResponse createInstance(PersistentInstance instance) {

        Object bean = runtime.getModuleMarshaller().unMarshall(instance.getType(), instance.getModel());

        bean = persistenceManager.createInstance(bean);

        Map<String, Object> marshaled = runtime.getModuleMarshaller().marshall(bean);

        instance = new PersistentInstance(marshaled.get(ID_PROPERTY), bean.getClass().getName(), marshaled);

        return new InstanceCreationResponse(OperationResult.SUCCESS, instance);
    }

    @Override
    public InstanceEditionResponse saveInstance(PersistentInstance instance) {
        Class<?> type = getClassForType(instance.getType());

        Object bean = persistenceManager.getInstanceById(type, instance.getId());

        runtime.getModuleMarshaller().unMarshall(bean, instance.getModel());

        persistenceManager.persistInstance(bean);

        return new InstanceEditionResponse(OperationResult.SUCCESS, instance);
    }

    @Override
    public Collection<PersistentInstance> query(String type) {
        Class<?> clazz = getClassForType(type);

        return persistenceManager.getAllInstances(clazz).stream()
                .map(this::marshall)
                .map(marshaled -> new PersistentInstance(marshaled.get(ID_PROPERTY), type, marshaled))
                .collect(Collectors.toList());
    }

    @Override
    public PersistentInstance getInstance(String type, Object id) {
        Class<?> clazz = getClassForType(type);

        Object bean = persistenceManager.getInstanceById(clazz, id);

        return new PersistentInstance(id, type, marshall(bean));
    }

    private Map<String, Object> marshall(Object bean) {
        return runtime.getModuleMarshaller().marshall(bean);
    }

    @Override
    public InstanceDeleteResponse deleteInstance(String type, Object id) {

        Class<?> clazz = getClassForType(type);

        Object bean = persistenceManager.getInstanceById(clazz, id);

        persistenceManager.deleteInstance(bean);

        return new InstanceDeleteResponse(OperationResult.SUCCESS);
    }

    private Class<?> getClassForType(String type) {
        try {
            return runtime.getModuleClassLoader().loadClass(type);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find class '" + type + "'", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (persistenceManager != null) {
            persistenceManager.destroy();
        }
    }
}
