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
package org.kie.workbench.common.forms.migration.legacy.model;

import java.io.Serializable;

public class DataFieldHolder implements Comparable,
                                        Serializable {

    DataHolder holder;
    String id;
    String className;
    String bag;

    public DataFieldHolder(DataHolder holder, String id, String className) {
        this.holder = holder;
        this.id = id;
        this.className = className;
    }

    public DataFieldHolder(DataHolder holder, String id, String className, String bag) {
        this.holder = holder;
        this.id = id;
        this.className = className;
        this.bag = bag;
    }

    public DataHolder getHolder() {
        return holder;
    }

    public void setHolder(DataHolder holder) {
        this.holder = holder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public int compareTo(Object o) {
        return id.compareTo(((DataFieldHolder) o).getId());
    }

    public String getBag() {
        return bag;
    }

    public void setBag(String bag) {
        this.bag = bag;
    }
}
