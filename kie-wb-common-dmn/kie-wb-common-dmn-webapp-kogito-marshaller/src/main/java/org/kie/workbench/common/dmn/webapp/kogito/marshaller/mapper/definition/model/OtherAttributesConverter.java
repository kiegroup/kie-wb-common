/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model;

import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

public class OtherAttributesConverter {

    public static Map<QName, String> fromMap(Map<String, String> toConvert) {
        return toConvert.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> javax.xml.namespace.QName.valueOf(entry.getKey()),
                                          Map.Entry::getValue));
    }

    public static void addEntry(Map<QName, String> toPopulate, String key, String value) {
        toPopulate.put(javax.xml.namespace.QName.valueOf(key), value);
    }

}
