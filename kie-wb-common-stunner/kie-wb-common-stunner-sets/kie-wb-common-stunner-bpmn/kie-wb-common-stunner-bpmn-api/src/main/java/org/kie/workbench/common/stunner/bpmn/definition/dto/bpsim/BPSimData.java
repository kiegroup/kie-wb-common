/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition.dto.bpsim;

import javax.xml.bind.annotation.XmlElement;

public class BPSimData {

    @XmlElement(name = "Scenario")
    private Scenario scenario;

    public BPSimData() {

    }

    public BPSimData(Scenario scenario) {
        this.scenario = scenario;
    }

    @Override
    public int hashCode() {
        return getScenario() != null ? getScenario().hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BPSimData)) {
            return false;
        }

        BPSimData bpSimData = (BPSimData) o;

        return getScenario() != null ? getScenario().equals(bpSimData.getScenario()) : bpSimData.getScenario() == null;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    @Override
    public String toString() {
        return "BPSimData{" +
                "scenario=" + scenario +
                '}';
    }
}
