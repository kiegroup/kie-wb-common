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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.NoSuchElementException;

import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;

public class MultipleInstanceSubProcessPropertyReader extends SubProcessPropertyReader {

    public MultipleInstanceSubProcessPropertyReader(SubProcess element, BPMNPlane plane, DefinitionResolver definitionResolver) {
        super(element, plane, definitionResolver);
    }

    public String getCollectionInput() {
        MultiInstanceLoopCharacteristics loopCharacteristics = getMultiInstanceLoopCharacteristics();
        ItemAwareElement ieDataInput = loopCharacteristics.getLoopDataInputRef();
        return process.getDataInputAssociations().stream()
                .filter(dia -> dia.getTargetRef().equals(ieDataInput))
                .map(dia -> dia.getSourceRef().get(0).getId())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cannot find collection input"));
    }

    public String getCollectionOutput() {
        MultiInstanceLoopCharacteristics loopCharacteristics = getMultiInstanceLoopCharacteristics();
        ItemAwareElement ieDataOutput = loopCharacteristics.getLoopDataOutputRef();
        return process.getDataOutputAssociations().stream()
                .filter(doa -> doa.getSourceRef().get(0).equals(ieDataOutput))
                .map(doa -> doa.getTargetRef().getId())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cannot find collection output"));
    }

    public String getDataInput() {
        MultiInstanceLoopCharacteristics miloop = getMultiInstanceLoopCharacteristics();
        return miloop.getInputDataItem().getId();
    }

    public String getDataOutput() {
        MultiInstanceLoopCharacteristics miloop = getMultiInstanceLoopCharacteristics();
        return miloop.getOutputDataItem().getId();
    }

    public String getCompletionCondition() {
        MultiInstanceLoopCharacteristics miloop = getMultiInstanceLoopCharacteristics();
        FormalExpression completionCondition = (FormalExpression) miloop.getCompletionCondition();
        return completionCondition.getBody();
    }

    private MultiInstanceLoopCharacteristics getMultiInstanceLoopCharacteristics() {
        return (MultiInstanceLoopCharacteristics) process.getLoopCharacteristics();
    }
}
