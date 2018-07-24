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

package org.kie.workbench.common.forms.cms.persistence.service;

import java.util.Collection;

import org.kie.workbench.common.forms.cms.common.backend.services.BackendApplicationRuntime;
import org.kie.workbench.common.forms.cms.persistence.shared.InstanceCreationResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.InstanceDeleteResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.InstanceEditionResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistentInstance;

public interface Storage {

    void init(BackendApplicationRuntime runtime);

    InstanceCreationResponse createInstance(PersistentInstance instance);

    InstanceEditionResponse saveInstance(PersistentInstance instance);

    Collection<PersistentInstance> query(String type);

    PersistentInstance getInstance(String type, Object id);

    InstanceDeleteResponse deleteInstance(String type, Object id);
}
