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

package org.kie.workbench.common.stunner.bpmn.backend.forms.conditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.workbench.common.stunner.bpmn.forms.conditions.Condition;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.ConditionEditorService;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.FieldMetadata;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.FunctionDef;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.GenerateConditionResult;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.ParseConditionResult;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadata;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadataQuery;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadataQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;

// TODO: (Submarine) - Re-implement

public abstract class BaseConditionEditorServiceImpl implements ConditionEditorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseConditionEditorServiceImpl.class);

    @Override
    public List<FunctionDef> findAvailableFunctions(Path path, String clazz) {
        return Collections.emptyList();
    }

    @Override
    public ParseConditionResult parseCondition(String conditionStr) {
        return null;
    }

    @Override
    public GenerateConditionResult generateCondition(Condition condition) {
        return null;
    }

    @Override
    public TypeMetadataQueryResult findMetadata(TypeMetadataQuery query) {
        return findMetadata(query, resolveClassLoader(query.getPath()));
    }

    protected TypeMetadataQueryResult findMetadata(TypeMetadataQuery query, ClassLoader classLoader) {
        Set<TypeMetadata> typeMetadatas = new HashSet<>();
        Set<String> missingTypes = new HashSet<>();
        for (String type : query.getTypes()) {
            try {
                TypeMetadata typeMetadata = buildTypeMetadata(type, classLoader);
                typeMetadatas.add(typeMetadata);
            } catch (ClassNotFoundException e) {
                missingTypes.add(type);
            }
        }
        return new TypeMetadataQueryResult(typeMetadatas, missingTypes);
    }

    protected TypeMetadata buildTypeMetadata(String type, ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> clazz = classLoader.loadClass(type);
        List<FieldMetadata> fields = new ArrayList<>();
        return new TypeMetadata(type, fields);
    }

    protected abstract ClassLoader resolveClassLoader(Path path);
}
