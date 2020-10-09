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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNProperty;

@XmlRootElement(name = "multiInstanceLoopCharacteristics", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class MultiInstanceLoopCharacteristics implements BPMNProperty {

    @XmlAttribute
    private boolean isSequential;

    @XmlElement(namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    private String loopDataInputRef;
    @XmlElement(namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    private String loopDataOutputRef;

    private DataInput inputDataItem;
    private DataOutput outputDataItem;

    private CompletionCondition completionCondition;

    public boolean isIsSequential() {
        return isSequential;
    }

    public void setIsSequential(boolean sequential) {
        isSequential = sequential;
    }

    public String getLoopDataInputRef() {
        return loopDataInputRef;
    }

    public void setLoopDataInputRef(String loopDataInputRef) {
        this.loopDataInputRef = loopDataInputRef;
    }

    public String getLoopDataOutputRef() {
        return loopDataOutputRef;
    }

    public void setLoopDataOutputRef(String loopDataOutputRef) {
        this.loopDataOutputRef = loopDataOutputRef;
    }

    public DataInput getInputDataItem() {
        return inputDataItem;
    }

    public void setInputDataItem(DataInput inputDataItem) {
        this.inputDataItem = inputDataItem;
    }

    public DataOutput getOutputDataItem() {
        return outputDataItem;
    }

    public void setOutputDataItem(DataOutput outputDataItem) {
        this.outputDataItem = outputDataItem;
    }

    public CompletionCondition getCompletionCondition() {
        return completionCondition;
    }

    public void setCompletionCondition(CompletionCondition completionCondition) {
        this.completionCondition = completionCondition;
    }

}
