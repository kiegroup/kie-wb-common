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

@XmlSchema(
        namespace = "http://www.omg.org/bpmn20",
        elementFormDefault = XmlNsForm.QUALIFIED,
        xmlns = {
                @XmlNs(prefix = "xsi",
                        namespaceURI = "http://www.w3.org/2001/XMLSchema-instance"),

                @XmlNs(prefix = "bpmn2",
                        namespaceURI = "http://www.omg.org/spec/BPMN/20100524/MODEL"),
                @XmlNs(prefix = "bpmndi",
                        namespaceURI = "http://www.omg.org/spec/BPMN/20100524/DI"),
                @XmlNs(prefix = "bpsim",
                        namespaceURI = "http://www.bpsim.org/schemas/1.0"),
                @XmlNs(prefix = "dc",
                        namespaceURI = "http://www.omg.org/spec/DD/20100524/DC"),
                @XmlNs(prefix = "drools",
                        namespaceURI = "http://www.jboss.org/drools")
        },
        location = "http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd http://www.omg.org/spec/DD/20100524/DC DC.xsd http://www.omg.org/spec/DD/20100524/DI DI.xsd"
)
package org.kie.workbench.common.stunner.bpmn.definition.property.diagram;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;