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

import javax.xml.bind.annotation.XmlRootElement;

import org.kie.workbench.common.stunner.bpmn.definition.dto.handler.TargetRefDemarshaller;
import org.kie.workbench.common.stunner.bpmn.definition.dto.handler.TargetRefMarshaller;
import org.treblereel.gwt.xml.mapper.api.annotation.XmlTypeAdapter;

@XmlTypeAdapter(
        serializer = TargetRefMarshaller.class,
        deserializer = TargetRefDemarshaller.class)
@XmlRootElement(name = "targetRef", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class TargetRef extends Ref<TargetRef>{

    public TargetRef() {

    }

    public TargetRef(String value) {
        super(value);
    }

    public TargetRef(String value, boolean asCDATA) {
        super(value, asCDATA);
    }
}
