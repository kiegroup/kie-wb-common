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

package org.kie.workbench.common.forms.cms.persistence.service.impl;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.forms.cms.common.backend.services.BackendApplicationRuntime;
import org.kie.workbench.common.forms.cms.common.backend.services.BackendPersistenceService;
import org.kie.workbench.common.forms.cms.persistence.service.Storage;
import org.kie.workbench.common.forms.cms.persistence.shared.InstanceCreationResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.InstanceDeleteResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.InstanceEditionResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistenceService;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistentInstance;

@Service
@ApplicationScoped
public class PersistenceServiceImpl implements PersistenceService,
                                               BackendPersistenceService {

    private Storage storage;

    @Inject
    public PersistenceServiceImpl(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void init(BackendApplicationRuntime runtime) {
        storage.init(runtime);
    }

    @Override
    public InstanceCreationResponse createInstance(PersistentInstance instance) {

        if (instance.getId() != null) {
            throw new IllegalArgumentException("Cannot create new \"" + instance.getType() + "\"instance, already has a persistence id (\"" + instance.getId() + "\").");
        }

        return storage.createInstance(instance);
    }

    @Override
    public InstanceEditionResponse saveInstance(PersistentInstance instance) {

        if (instance.getId() == null) {
            throw new IllegalArgumentException("Cannot persist instance, it doesn't have a persistence id.");
        }

        return storage.saveInstance(instance);
    }

    @Override
    public PersistentInstance getInstance(String type, Object id) {

        return storage.getInstance(type, id);
    }

    @Override
    public Collection<PersistentInstance> query(String type) {
        return storage.query(type);
    }

    @Override
    public InstanceDeleteResponse deleteInstance(String type, Object id) {

        return storage.deleteInstance(type, id);
    }
}
