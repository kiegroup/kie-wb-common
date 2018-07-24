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

package org.kie.workbench.common.forms.cms.persistence.jpa.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.kie.workbench.common.forms.cms.persistence.jpa.JPAPersistenceManager;
import org.kie.workbench.common.forms.cms.persistence.jpa.impl.commands.DeleteInstanceCommand;
import org.kie.workbench.common.forms.cms.persistence.jpa.impl.commands.GetAllInstancesCommand;
import org.kie.workbench.common.forms.cms.persistence.jpa.impl.commands.GetInstanceByIdCommand;
import org.kie.workbench.common.forms.cms.persistence.jpa.impl.commands.MergeInstanceCommand;
import org.kie.workbench.common.forms.cms.persistence.jpa.impl.commands.PersistInstanceCommand;

public abstract class AbstractJPAPersistenceManager<TRANSACTION> implements JPAPersistenceManager,
                                                                            JPATransactionHandler<TRANSACTION> {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    public AbstractJPAPersistenceManager(EntityManagerFactory entityManagerFactory, EntityManager entityManager) {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = entityManager;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Object createInstance(Object instance) {
        return new MergeInstanceCommand(instance).execute(this);
    }

    @Override
    public Object persistInstance(Object instance) {
        return new PersistInstanceCommand(instance).execute(this);
    }

    @Override
    public Object getInstanceById(Class<?> type, Object id) {
        return new GetInstanceByIdCommand(type, id).execute(this);
    }

    @Override
    public Object deleteInstance(Object instance) {
        return new DeleteInstanceCommand(instance).execute(this);
    }

    @Override
    public List<Object> getAllInstances(Class<?> type) {
        return new GetAllInstancesCommand(type).execute(this);
    }

    @Override
    public void destroy() {
        try {
            entityManager.close();
        } catch (Exception ex) {
        }
        try {
            entityManagerFactory.close();
        } catch (Exception ex) {
        }
    }
}
