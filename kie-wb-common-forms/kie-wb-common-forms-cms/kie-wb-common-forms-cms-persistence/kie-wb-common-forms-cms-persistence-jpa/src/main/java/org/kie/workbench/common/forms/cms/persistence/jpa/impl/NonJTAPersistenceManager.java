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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

public class NonJTAPersistenceManager extends AbstractJPAPersistenceManager<EntityTransaction> {

    public NonJTAPersistenceManager(EntityManagerFactory entityManagerFactory, EntityManager entityManager) {
        super(entityManagerFactory, entityManager);
    }

    @Override
    public EntityTransaction openTransaction() throws Exception {
        EntityTransaction transaction = getEntityManager().getTransaction();
        transaction.begin();
        return transaction;
    }

    @Override
    public void commitTransaction(EntityTransaction transaction) throws Exception {
        transaction.commit();
    }

    @Override
    public void rollbackTransaction(EntityTransaction transaction) throws Exception {
        transaction.rollback();
    }
}
