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

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

public class JTAPersistenceManager extends AbstractJPAPersistenceManager<UserTransaction> {

    public JTAPersistenceManager(EntityManagerFactory entityManagerFactory, EntityManager entityManager) {
        super(entityManagerFactory, entityManager);
    }

    @Override
    public UserTransaction openTransaction() throws Exception {
        UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        transaction.begin();
        return transaction;
    }

    @Override
    public void commitTransaction(UserTransaction transaction) throws Exception {
        transaction.commit();
    }

    @Override
    public void rollbackTransaction(UserTransaction transaction) throws Exception {
        transaction.rollback();
    }
}
