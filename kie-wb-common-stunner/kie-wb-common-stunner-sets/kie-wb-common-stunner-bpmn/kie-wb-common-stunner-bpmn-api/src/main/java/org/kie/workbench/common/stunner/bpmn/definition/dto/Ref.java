/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition.dto;

public class Ref<T extends Ref> {

    private String value;

    private transient boolean asCDATA = true;

    public Ref() {

    }

    public Ref(String value) {
        this.value = value;
    }

    public Ref(String value, boolean asCDATA) {
        this.value = value;
        this.asCDATA = asCDATA;
    }

    public String getValue() {
        return value;
    }

    public T setValue(String value) {
        this.value = value;
        return (T) this;
    }

    public boolean isAsCDATA() {
        return asCDATA;
    }

    public T setAsCDATA(boolean asCDATA) {
        this.asCDATA = asCDATA;
        return (T) this;
    }
}
