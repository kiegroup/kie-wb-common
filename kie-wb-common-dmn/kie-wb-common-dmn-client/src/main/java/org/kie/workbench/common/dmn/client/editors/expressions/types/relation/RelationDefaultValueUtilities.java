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

package org.kie.workbench.common.dmn.client.editors.expressions.types.relation;

import java.util.stream.Collectors;

import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.Relation;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.property.dmn.DefaultValueUtilities;

public class RelationDefaultValueUtilities {

    public static final String PREFIX = "column-";

    public static String getNewColumnName(final Relation relation) {
        return PREFIX + DefaultValueUtilities.getMaxUnusedIndex(relation.getColumn().stream()
                                                                        .filter(informationItem -> informationItem != null)
                                                                        .map(InformationItem::getName)
                                                                        .filter(name -> name != null)
                                                                        .map(Name::getValue)
                                                                        .collect(Collectors.toList()),
                                                                PREFIX);
    }
}
