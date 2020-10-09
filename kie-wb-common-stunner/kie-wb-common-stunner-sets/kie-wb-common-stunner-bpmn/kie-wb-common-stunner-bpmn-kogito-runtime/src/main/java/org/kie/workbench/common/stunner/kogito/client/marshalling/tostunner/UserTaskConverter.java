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

package org.kie.workbench.common.stunner.kogito.client.marshalling.tostunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gwt.user.client.Command;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.dto.DataInput;
import org.kie.workbench.common.stunner.bpmn.definition.dto.DataInputAssociation;
import org.kie.workbench.common.stunner.bpmn.definition.dto.DataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.dto.DataOutputAssociation;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Definitions;
import org.kie.workbench.common.stunner.bpmn.definition.dto.ItemDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.dto.MultiInstanceLoopCharacteristics;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.ExtensionElement;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.MetaData;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.OnEntryScript;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.OnExitScript;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ExecutionOrder;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.kogito.client.marshalling.converters.associations.AssignmentsInfos;
import org.kie.workbench.common.stunner.kogito.client.marshalling.converters.notifications.ParsedNotificationsInfos;
import org.kie.workbench.common.stunner.kogito.client.marshalling.converters.reassignments.ParsedReassignmentsInfos;

public class UserTaskConverter {

    private final UserTask userTask;
    private final Definitions definitions;
    private final List<Command> actions = new LinkedList<>();

    private final Function<String, ItemDefinition> getItemDefinitionStructureRef = new Function<String, ItemDefinition>() {
        @Override
        public ItemDefinition apply(String id) {
            return definitions.getItemDefinitions()
                    .stream()
                    .filter(item -> item.getId().equals(id))
                    .findFirst().orElseThrow(() -> new Error("Unable to fetch ItemDefinition with id :" + id));
        }
    };

    UserTaskConverter(UserTask userTask, Definitions definitions) {
        this.userTask = userTask;
        this.definitions = definitions;

        initActions();
    }

    private void initActions() {
        actions.add(this::processExtensionElements);
        actions.add(this::potentialOwners);
        actions.add(this::groups);
        actions.add(this::skippableInputX);
        actions.add(this::createdBy);
        actions.add(this::comment);
        actions.add(this::content);
        actions.add(this::priority);
        actions.add(this::content);
        actions.add(this::description);
        actions.add(this::assignment);
        actions.add(this::reassignInput);
        actions.add(this::notifyInput);
        actions.add(this::multipleInstance);
    }

    private void processExtensionElements() {
        //sometimes .forEach is null, maybe gwt bug
        for (ExtensionElement elm : userTask.getExtensionElements()) {
            if (elm instanceof MetaData) {
                MetaData metaData = (MetaData) elm;
                if (metaData.getName().equals("elementname")) {
                    userTask.getExecutionSet().getTaskName().setValue(metaData.getMetaValue().getValue());
                } else if (metaData.getName().equals("customAsync")) {
                    userTask.getExecutionSet()
                            .getIsAsync().setValue(Boolean.parseBoolean(metaData.getMetaValue().getValue()));
                } else if (metaData.getName().equals("customAutoStart")) {
                    userTask.getExecutionSet()
                            .getAdHocAutostart().setValue(Boolean.parseBoolean(metaData.getMetaValue().getValue()));
                } else if (metaData.getName().equals("customSLADueDate")) {
                    userTask.getExecutionSet()
                            .getSlaDueDate().setValue(metaData.getMetaValue().getValue());
                }
            } else if (elm instanceof OnExitScript) {
                OnExitScript metaData = (OnExitScript) elm;
                ScriptTypeValue value = userTask.getExecutionSet().getOnExitAction()
                        .getValue().getValues().get(0);

                value.setScript(metaData.getScript());
                value.setLanguage(Scripts.scriptLanguageFromUri(metaData.getScriptFormat()));
            } else if (elm instanceof OnEntryScript) {
                OnEntryScript metaData = (OnEntryScript) elm;
                ScriptTypeValue value = userTask.getExecutionSet().getOnEntryAction()
                        .getValue().getValues().get(0);

                value.setScript(metaData.getScript());
                value.setLanguage(Scripts.scriptLanguageFromUri(metaData.getScriptFormat()));
            }
        }
    }

    private void potentialOwners() {
        if (userTask.getPotentialOwner() != null) {
            String value = userTask.getPotentialOwner()
                    .stream()
                    .map(elm -> elm.getResourceAssignmentExpression()
                            .getFormalExpression()).collect(Collectors.joining(","));
            userTask.getExecutionSet()
                    .getActors()
                    .setValue(value);
        }
    }

    private void groups() {
        getDataInputAssociationByTargetRef("GroupIdInputX")
                .ifPresent(elm -> userTask.getExecutionSet().
                        getGroupid()
                        .setValue(elm.getAssignment().getFrom().getValue()));
    }

    private void skippableInputX() {
        getDataInputAssociationByTargetRef("SkippableInputX")
                .ifPresent(elm -> userTask.getExecutionSet().
                        getSkippable()
                        .setValue(Boolean.parseBoolean(elm.getAssignment().getFrom().getValue())));
    }

    private void createdBy() {
        getDataInputAssociationByTargetRef("CreatedByInputX")
                .ifPresent(elm -> userTask.getExecutionSet().
                        getCreatedBy()
                        .setValue(elm.getAssignment().getFrom().getValue()));
    }

    private void comment() {
        getDataInputAssociationByTargetRef("CommentInputX")
                .ifPresent(elm -> userTask.getExecutionSet().
                        getSubject()
                        .setValue(elm.getAssignment().getFrom().getValue()));
    }

    private void content() {
        getDataInputAssociationByTargetRef("ContentInputX")
                .ifPresent(elm -> userTask.getExecutionSet().
                        getContent()
                        .setValue(elm.getAssignment().getFrom().getValue()));
    }

    private void priority() {
        getDataInputAssociationByTargetRef("PriorityInputX")
                .ifPresent(elm -> userTask.getExecutionSet().
                        getPriority()
                        .setValue(elm.getAssignment().getFrom().getValue()));
    }

    private void description() {
        getDataInputAssociationByTargetRef("DescriptionInputX")
                .ifPresent(elm -> userTask.getExecutionSet().
                        getDescription()
                        .setValue(elm.getAssignment().getFrom().getValue()));
    }

    private void assignment() {
        List<DataInput> datainput = new ArrayList<>();
        List<DataOutput> dataoutput = new ArrayList<>();
        List<DataInputAssociation> inputAssociations = new ArrayList<>();
        List<DataOutputAssociation> outputAssociations = new ArrayList<>();

        userTask.getIoSpecification().stream().forEach(io -> {
            if (io instanceof DataInput) {
                datainput.add((DataInput) io);
            } else if (io instanceof DataOutput) {
                dataoutput.add((DataOutput) io);
            }
        });

        userTask.getBpmnProperties().stream().forEach(bpmnProperty -> {
            if (bpmnProperty instanceof DataInputAssociation) {
                inputAssociations.add((DataInputAssociation) bpmnProperty);
            } else if (bpmnProperty instanceof DataOutputAssociation) {
                outputAssociations.add((DataOutputAssociation) bpmnProperty);
            }
        });

        AssignmentsInfo assignmentsInfo = AssignmentsInfos.of(definitions,
                                                              datainput,
                                                              inputAssociations,
                                                              dataoutput,
                                                              outputAssociations,
                                                              definitions.getItemDefinitions(),
                                                              false);
        userTask.getExecutionSet().getAssignmentsinfo().setValue(assignmentsInfo.getValue());
    }

    private void reassignInput() {
        Arrays.asList("NotStartedReassign", "NotCompletedReassign").stream().forEach(type -> {
            getDataInputAssociationByTargetRef(type + "InputX")
                    .ifPresent(elm -> Arrays.stream(elm.getAssignment()
                                                            .getFrom()
                                                            .getValue()
                                                            .split("\\^"))
                            .forEach(value -> userTask.getExecutionSet().
                                    getReassignmentsInfo()
                                    .getValue()
                                    .addValue(ParsedReassignmentsInfos.of(type, value))
                            ));
        });
    }

    private void notifyInput() {
        Arrays.asList("NotStartedNotify", "NotCompletedNotify").forEach(type -> {
            getDataInputAssociationByTargetRef(type + "InputX")
                    .ifPresent(elm -> Arrays.stream(elm.getAssignment()
                                                            .getFrom()
                                                            .getValue()
                                                            .split("\\^"))
                            .forEach(value -> userTask.getExecutionSet().
                                    getNotificationsInfo()
                                    .getValue()
                                    .addValue(ParsedNotificationsInfos.of(type, value))
                            ));
        });
    }

    private void multipleInstance() {
        Optional<MultiInstanceLoopCharacteristics> result = userTask.getBpmnProperties()
                .stream().filter(elm -> elm instanceof MultiInstanceLoopCharacteristics)
                .findFirst()
                .map(instance -> ((MultiInstanceLoopCharacteristics) instance));
        if (result.isPresent()) {
            MultiInstanceLoopCharacteristics instance = result.get();
            userTask.getExecutionSet().getIsMultipleInstance().setValue(true);
            userTask.getExecutionSet().getMultipleInstanceExecutionMode()
                    .setValue(instance.isIsSequential() ? ExecutionOrder.SEQUENTIAL.value()
                                      : ExecutionOrder.PARALLEL.value());
            if (instance.getCompletionCondition() != null) {
                userTask.getExecutionSet()
                        .getMultipleInstanceCompletionCondition()
                        .setValue(instance.getCompletionCondition().getValue());
            }

            if (instance.getInputDataItem() != null) {
                DataInput dataInput = instance.getInputDataItem();
                String type = getItemDefinitionStructureRef.apply(dataInput.getItemSubjectRef()).getStructureRef();
                userTask.getExecutionSet()
                        .getMultipleInstanceDataInput()
                        .setValue(dataInput.getName() + ":" + type);
            }

            if (instance.getOutputDataItem() != null) {
                DataOutput dataOutput = instance.getOutputDataItem();
                String type = getItemDefinitionStructureRef.apply(dataOutput.getItemSubjectRef()).getStructureRef();
                userTask.getExecutionSet()
                        .getMultipleInstanceDataOutput()
                        .setValue(dataOutput.getName() + ":" + type);
            }

            if (instance.getLoopDataInputRef() != null) {
                userTask.getBpmnProperties().stream().filter(elm -> elm instanceof DataInputAssociation)
                        .map(elm -> (DataInputAssociation) elm)
                        .filter(dia -> dia.getTargetRef() != null)
                        .filter(dia -> dia.getTargetRef().getValue().equals(instance.getLoopDataInputRef()))
                        .findFirst()
                        .ifPresent(dataInputAssociation -> {
                            userTask.getExecutionSet()
                                    .getMultipleInstanceCollectionInput()
                                    .setValue(dataInputAssociation.getSourceRef()
                                                      .getValue());
                        });
            }

            if (instance.getLoopDataOutputRef() != null) {
                userTask.getBpmnProperties().stream().filter(elm -> elm instanceof DataOutputAssociation)
                        .map(elm -> (DataOutputAssociation) elm)
                        .filter(dia -> dia.getSourceRef() != null)
                        .filter(dia -> dia.getSourceRef()
                                .getValue().equals(instance.getLoopDataOutputRef()))
                        .findFirst()
                        .ifPresent(dataOutputAssociation -> {
                            userTask.getExecutionSet()
                                    .getMultipleInstanceCollectionOutput()
                                    .setValue(dataOutputAssociation.getTargetRef()
                                                      .getValue());
                        });
            }
        }
    }

    private Optional<DataInputAssociation> getDataInputAssociationByTargetRef(String name) {
        return userTask.getBpmnProperties()
                .stream().filter(elm -> elm instanceof DataInputAssociation)
                .map(elm -> ((DataInputAssociation) elm))
                .filter(elm -> (elm.getTargetRef() != null && elm.getTargetRef()
                        .getValue()
                        .endsWith(name)))
                .map(Optional::ofNullable)
                .findFirst().flatMap(Function.identity());
    }

    void convert() {
        for (Command command : actions) {
            command.execute();
        }
    }
}