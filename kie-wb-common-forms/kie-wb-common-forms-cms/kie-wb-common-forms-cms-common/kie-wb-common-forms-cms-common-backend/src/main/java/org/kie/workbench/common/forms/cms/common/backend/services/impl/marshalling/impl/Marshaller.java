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

package org.kie.workbench.common.forms.cms.common.backend.services.impl.marshalling.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Marshaller {

    private String type;

    private Supplier newInsanceSupplier;

    private List<PropertyMarshaller> marshallers;

    public Marshaller(String type, Supplier newInsanceSupplier, List<PropertyMarshaller> marshallers) {
        this.type = type;
        this.newInsanceSupplier = newInsanceSupplier;
        this.marshallers = marshallers;
    }

    public Supplier getNewInsanceSupplier() {
        return newInsanceSupplier;
    }

    public Map<String, Object> marshal(Object bean) {
        Map<String, Object> marshalled = new HashMap<>();

        if (bean != null) {
            marshallers.stream()
                    .forEach(propertyMarshaller -> marshalled.put(propertyMarshaller.getPropertyName(), propertyMarshaller.marshal(bean)));
        }

        return marshalled;
    }

    public Object unMarshal(Object bean, Map<String, Object> marshaled) {
        if (bean == null) {
            bean = newInsanceSupplier.get();
        }

        if (bean == null) {
            throw new IllegalStateException("Impossible to create new instance for type '" + type + "'");
        }

        final Object result = bean;

        marshallers.stream().forEach(propertyMarshaller -> {
            if(marshaled.containsKey(propertyMarshaller.getPropertyName())) {
                propertyMarshaller.unMarshal(result, marshaled.get(propertyMarshaller.getPropertyName()));
            }
        });

        return result;
    }
}
