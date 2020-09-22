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

package org.kie.workbench.common.stunner.kogito.client.marshalling.fromstunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gwt.user.client.Command;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Assignment;
import org.kie.workbench.common.stunner.bpmn.definition.dto.CompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Data;
import org.kie.workbench.common.stunner.bpmn.definition.dto.DataAssociation;
import org.kie.workbench.common.stunner.bpmn.definition.dto.DataInput;
import org.kie.workbench.common.stunner.bpmn.definition.dto.DataInputAssociation;
import org.kie.workbench.common.stunner.bpmn.definition.dto.DataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.dto.DataOutputAssociation;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Definitions;
import org.kie.workbench.common.stunner.bpmn.definition.dto.InputSet;
import org.kie.workbench.common.stunner.bpmn.definition.dto.ItemDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.dto.MultiInstanceLoopCharacteristics;
import org.kie.workbench.common.stunner.bpmn.definition.dto.OutputSet;
import org.kie.workbench.common.stunner.bpmn.definition.dto.PotentialOwner;
import org.kie.workbench.common.stunner.bpmn.definition.dto.ResourceAssignmentExpression;
import org.kie.workbench.common.stunner.bpmn.definition.dto.SourceRef;
import org.kie.workbench.common.stunner.bpmn.definition.dto.StringValue;
import org.kie.workbench.common.stunner.bpmn.definition.dto.TargetRef;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.ExtensionElement;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.MetaData;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.OnEntryScript;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.OnExitScript;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.kogito.client.marshalling.converters.Ids;
import org.kie.workbench.common.stunner.kogito.client.marshalling.converters.associations.AssociationDeclaration;
import org.kie.workbench.common.stunner.kogito.client.marshalling.converters.associations.ParsedAssignmentsInfo;
import org.kie.workbench.common.stunner.kogito.client.marshalling.converters.associations.VariableDeclaration;

public class UserTaskConverter {

    private final UserTask userTask;
    private final Definitions definitions;
    private final UserTaskExecutionSet executionSet;
    private final List<Command> actions = new LinkedList<>();
    private final List<ExtensionElement> extensionElements = new LinkedList<>();
    private final List<PotentialOwner> potentialOwners = new LinkedList<>();

    private final List<Data> ioSpecification = new LinkedList<>();
    private final InputSet inputSet = new InputSet();
    private final OutputSet outputSet = new OutputSet();
    private final List<StringValue> dataInputRefs = new ArrayList<>();
    private final List<StringValue> dataOutputRefs = new ArrayList<>();

    UserTaskConverter(UserTask userTask, Definitions definitions) {
        this.userTask = userTask;
        this.definitions = definitions;
        this.executionSet = userTask.getExecutionSet();

        userTask.setBpmnProperties(new LinkedList<>());
        userTask.setIoSpecification(new LinkedList<>());
        userTask.setExtensionElements(extensionElements);
        userTask.setPotentialOwner(potentialOwners);

        inputSet.setDataInputRefs(new LinkedList<>());
        outputSet.setDataOutputRefs(new LinkedList<>());
        initActions();
    }

    private void initActions() {
        actions.add(this::elementname);
        actions.add(this::skippable);
        actions.add(this::priority);
        actions.add(this::comment);
        actions.add(this::description);
        actions.add(this::createdBy);
        actions.add(this::taskName);
        actions.add(this::groupId);
        actions.add(this::content);
        actions.add(this::customAsync);
        actions.add(this::customAutoStart);
        actions.add(this::customSLADueDate);
        actions.add(this::onExitAction);
        actions.add(this::onEntryAction);
        actions.add(this::actors);
        actions.add(this::doReassign);
        actions.add(this::doNotify);
        actions.add(this::multipleInstance);
        actions.add(this::assignments);
        actions.add(this::setCollections);
    }

    private void elementname() {
        extensionElements.add(new MetaData("elementname", userTask.getName()));
    }

    private void skippable() {
        definitions.getItemDefinitions().add(new ItemDefinition(Ids.dataInputItem(userTask.getId(), "Skippable")));

        DataInputAssociation inputAssociation = new DataInputAssociation()
                .setTargetRef(new TargetRef(Ids.dataInput(userTask.getId(), "Skippable"), false));

        inputAssociation.setAssignment(new Assignment().from(userTask.getExecutionSet()
                                                                     .getSkippable().getValue().toString())
                                               .to(Ids.dataInput(userTask.getId(), "Skippable")));
        userTask.getBpmnProperties().add(inputAssociation);

        ioSpecification.add(new DataInput().setId(Ids.dataInput(userTask.getId(), "Skippable"))
                                    .setItemSubjectRef(Ids.dataInputItem(userTask.getId(), "Skippable"))
                                    .setName("Skippable")
                                    .setDtype("Object")
        );
        dataInputRefs.add(new StringValue(userTask.getId() + "_SkippableInputX"));
    }

    private void priority() {
        definitions.getItemDefinitions().add(new ItemDefinition(Ids.dataInputItem(userTask.getId(), "Priority")));
        if (!executionSet.getPriority().getValue().isEmpty()) {
            userTask.getBpmnProperties().add(new DataInputAssociation(userTask, userTask.getExecutionSet()
                    .getPriority().getValue(), "PriorityInputX"));
            ioSpecification.add(new DataInput(userTask.getId(), "PriorityInputX", "Priority"));
            dataInputRefs.add(new StringValue(userTask.getId() + "_PriorityInputX"));
        }
    }

    private void comment() {
        definitions.getItemDefinitions().add(new ItemDefinition(Ids.dataInputItem(userTask.getId(), "Comment")));
        if (!executionSet.getSubject().getValue().isEmpty()) {
            dataInputRefs.add(new StringValue(userTask.getId() + "_CommentInputX"));
            ioSpecification.add(new DataInput(userTask.getId(), "CommentInputX", "Comment"));
            userTask.getBpmnProperties().add(new DataInputAssociation(userTask, userTask.getExecutionSet()
                    .getSubject().getValue(), "CommentInputX"));
        }
    }

    private void description() {
        definitions.getItemDefinitions().add(new ItemDefinition(Ids.dataInputItem(userTask.getId(), "Description")));
        if (!executionSet.getDescription().getValue().isEmpty()) {
            userTask.getBpmnProperties().add(new DataInputAssociation(userTask, userTask.getExecutionSet()
                    .getDescription().getValue(), "DescriptionInputX"));
            ioSpecification.add(new DataInput(userTask.getId(), "DescriptionInputX", "Description"));
            dataInputRefs.add(new StringValue(userTask.getId() + "_DescriptionInputX"));
        }
    }

    private void createdBy() {
        definitions.getItemDefinitions().add(new ItemDefinition(Ids.dataInputItem(userTask.getId(), "CreatedBy")));
        if (!executionSet.getCreatedBy().getValue().isEmpty()) {
            userTask.getBpmnProperties().add(new DataInputAssociation(userTask,
                                                                      userTask.getExecutionSet()
                                                                              .getCreatedBy().getValue(),
                                                                      "CreatedByInputX"));

            ioSpecification.add(new DataInput(userTask.getId(), "CreatedByInputX", "CreatedBy"));
            dataInputRefs.add(new StringValue(userTask.getId() + "_CreatedByInputX"));
        }
    }

    private void taskName() {
        if (!executionSet.getTaskName().getValue().isEmpty()) {
            definitions.getItemDefinitions().add(new ItemDefinition(Ids.dataInputItem(userTask.getId(), "TaskName")));

            DataInputAssociation inputAssociation = new DataInputAssociation().setTargetRef(new TargetRef(Ids.dataInput(userTask.getId(), "TaskName"), false));
            inputAssociation.setAssignment(new Assignment().from(userTask.getExecutionSet()
                                                                         .getTaskName().getValue())
                                                   .to(Ids.dataInput(userTask.getId(), "TaskName")));
            userTask.getBpmnProperties().add(inputAssociation);

            ioSpecification.add(new DataInput().setId(Ids.dataInput(userTask.getId(), "TaskName"))
                                        .setItemSubjectRef(Ids.dataInputItem(userTask.getId(), "TaskName"))
                                        .setName("TaskName")
                                        .setDtype("Object"));
            dataInputRefs.add(new StringValue(userTask.getId() + "_TaskNameInputX"));
        }
    }

    private void groupId() {
        definitions.getItemDefinitions().add(new ItemDefinition(Ids.dataInputItem(userTask.getId(), "GroupId")));
        if (!executionSet.getGroupid().getValue().isEmpty()) {
            userTask.getBpmnProperties().add(new DataInputAssociation(userTask, userTask.getExecutionSet()
                    .getGroupid().getValue(), "GroupIdInputX"));
            ioSpecification.add(new DataInput(userTask.getId(), "GroupIdInputX", "GroupId"));
            dataInputRefs.add(new StringValue(userTask.getId() + "_GroupIdInputX"));
        }
    }

    private void content() {
        definitions.getItemDefinitions().add(new ItemDefinition(Ids.dataInputItem(userTask.getId(), "Content")));
        if (!executionSet.getContent().getValue().isEmpty()) {
            ioSpecification.add(new DataInput(userTask.getId(), "ContentInputX", "Content"));
            dataInputRefs.add(new StringValue(userTask.getId() + "_ContentInputX"));
            userTask.getBpmnProperties().add(new DataInputAssociation(userTask, userTask.getExecutionSet()
                    .getContent().getValue(), "ContentInputX"));
        }
    }

    private void customAsync() {
        if (userTask.getExecutionSet()
                .getIsAsync().getValue()) {
            extensionElements.add(new MetaData("customAsync", userTask.getExecutionSet()
                    .getIsAsync().getValue().toString()));
        }
    }

    private void customAutoStart() {
        if (userTask.getExecutionSet()
                .getAdHocAutostart().getValue()) {
            extensionElements.add(new MetaData("customAutoStart", userTask.getExecutionSet()
                    .getAdHocAutostart().getValue().toString()));
        }
    }

    private void customSLADueDate() {
        if (!userTask.getExecutionSet()
                .getSlaDueDate().getValue().isEmpty()) {
            extensionElements.add(new MetaData("customSLADueDate", userTask.getExecutionSet()
                    .getSlaDueDate().getValue()));
        }
    }

    private void onExitAction() {
        for (ScriptTypeValue value : userTask.getExecutionSet().getOnExitAction().getValue().getValues()) {
            if (!value.getScript().isEmpty()) {
                extensionElements.add(new OnExitScript(value.getScript(), value.getLanguage()));
            }
        }
    }

    private void onEntryAction() {
        for (ScriptTypeValue value : userTask.getExecutionSet().getOnEntryAction().getValue().getValues()) {
            if (!value.getScript().isEmpty()) {
                extensionElements.add(new OnEntryScript(value.getScript(), value.getLanguage()));
            }
        }
    }

    private void actors() {
        if (!executionSet.getActors().getValue().isEmpty()) {
            fromActorString(executionSet.getActors().getValue())
                    .stream()
                    .map(actor -> new PotentialOwner(UUID.generate(),
                                                     new ResourceAssignmentExpression(UUID.generate(), actor)))
                    .forEach(actor -> potentialOwners.add(actor));
        }
    }

    private void doReassign() {
        definitions.getItemDefinitions().add(new ItemDefinition(Ids.dataInputItem(userTask.getId(), "NotStartedReassign")));
        definitions.getItemDefinitions().add(new ItemDefinition(Ids.dataInputItem(userTask.getId(), "NotCompletedReassign")));

        if (!executionSet.getReassignmentsInfo().getValue().isEmpty()) {
            executionSet.getReassignmentsInfo().getValue()
                    .getValues()
                    .stream()
                    .map(reassignment -> reassignment.getType())
                    .collect(Collectors.toSet()).forEach(type -> {
                ioSpecification.add(new DataInput().setId(Ids.dataInput(userTask.getId(), type))
                                            .setItemSubjectRef(Ids.dataInputItem(userTask.getId(), type))
                                            .setName(type));
                dataInputRefs.add(new StringValue(Ids.dataInput(userTask.getId(), type)));
            });

            String notStartedReassign = executionSet.getReassignmentsInfo()
                    .getValue()
                    .getValues()
                    .stream()
                    .filter(reassignment -> reassignment.getType().equals("NotStartedReassign"))
                    .map(reassignment -> reassignment.toCDATAFormat())
                    .collect(Collectors.joining("^"));

            if (!notStartedReassign.isEmpty()) {
                userTask.getBpmnProperties()
                        .add(new DataInputAssociation()
                                     .setTargetRef(new TargetRef(Ids.dataInput(userTask.getId(), "NotStartedReassign")).setAsCDATA(false))
                                     .setAssignment(new Assignment().from(notStartedReassign).to(Ids.dataInput(userTask.getId(), "NotStartedReassign"))));
            }

            String notCompletedReassign = executionSet.getReassignmentsInfo()
                    .getValue()
                    .getValues()
                    .stream()
                    .filter(reassignment -> reassignment.getType().equals("NotCompletedReassign"))
                    .map(reassignment -> reassignment.toCDATAFormat())
                    .collect(Collectors.joining("^"));

            if (!notCompletedReassign.isEmpty()) {
                userTask.getBpmnProperties()
                        .add(new DataInputAssociation()
                                     .setTargetRef(new TargetRef(Ids.dataInput(userTask.getId(), "NotCompletedReassign")).setAsCDATA(false))
                                     .setAssignment(new Assignment().from(notCompletedReassign).to(Ids.dataInput(userTask.getId(), "NotCompletedReassign"))));
            }
        }
    }

    private void doNotify() {
        definitions.getItemDefinitions().add(new ItemDefinition(Ids.dataInputItem(userTask.getId(), "NotStartedNotify")));
        definitions.getItemDefinitions().add(new ItemDefinition(Ids.dataInputItem(userTask.getId(), "NotCompletedNotify")));

        if (!executionSet.getNotificationsInfo().getValue().isEmpty()) {
            executionSet.getNotificationsInfo().getValue()
                    .getValues()
                    .stream()
                    .map(notification -> notification.getType())
                    .collect(Collectors.toSet()).forEach(type -> {
                ioSpecification.add(new DataInput().setId(Ids.dataInput(userTask.getId(), type))
                                            .setItemSubjectRef(Ids.dataInputItem(userTask.getId(), type))
                                            .setName(type));
                dataInputRefs.add(new StringValue(Ids.dataInput(userTask.getId(), type)));
            });

            String notStartedNotify = executionSet.getNotificationsInfo()
                    .getValue()
                    .getValues()
                    .stream()
                    .filter(notification -> notification.getType().equals("NotStartedNotify"))
                    .map(notification -> notification.toCDATAFormat())
                    .collect(Collectors.joining("^"));

            if (!notStartedNotify.isEmpty()) {
                userTask.getBpmnProperties()
                        .add(new DataInputAssociation()
                                     .setTargetRef(new TargetRef(Ids.dataInput(userTask.getId(), "NotStartedNotify")).setAsCDATA(false))
                                     .setAssignment(new Assignment().from(notStartedNotify).to(Ids.dataInput(userTask.getId(), "NotStartedNotify"))));
            }

            String notCompletedNotify = executionSet.getNotificationsInfo()
                    .getValue()
                    .getValues()
                    .stream()
                    .filter(notification -> notification.getType().equals("NotCompletedNotify"))
                    .map(notification -> notification.toCDATAFormat())
                    .collect(Collectors.joining("^"));

            if (!notCompletedNotify.isEmpty()) {
                userTask.getBpmnProperties()
                        .add(new DataInputAssociation()
                                     .setTargetRef(new TargetRef(Ids.dataInput(userTask.getId(), "NotCompletedNotify")).setAsCDATA(false))
                                     .setAssignment(new Assignment().from(notCompletedNotify).to(Ids.dataInput(userTask.getId(), "NotCompletedNotify"))));
            }
        }
    }

    private void multipleInstance() {
        if (Boolean.TRUE.equals(executionSet.getIsMultipleInstance().getValue())) {
            MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = new MultiInstanceLoopCharacteristics();
            multiInstanceLoopCharacteristics.setIsSequential(executionSet.getMultipleInstanceExecutionMode().isSequential());

            if (!executionSet.getMultipleInstanceCompletionCondition().getValue().isEmpty()) {
                multiInstanceLoopCharacteristics.setCompletionCondition(
                        new CompletionCondition(executionSet.getMultipleInstanceCompletionCondition().getValue()));
            }

            if (!executionSet.getMultipleInstanceDataInput().getValue().isEmpty()) {
                String[] value = executionSet.getMultipleInstanceDataInput().getValue().split(":");
                ItemDefinition itemDefinition = new ItemDefinition();
                itemDefinition.setId(Ids.multiInstanceItemType(userTask.getId(), value[0]));
                itemDefinition.setStructureRef(value[1]);
                definitions.getItemDefinitions().add(itemDefinition);

                DataInput dataInput = new DataInput().setId(Ids.dataInput(userTask.getId(), value[0]))
                        .setItemSubjectRef(Ids.multiInstanceItemType(userTask.getId(), value[0]))
                        .setName(value[0]);
                ioSpecification.add(dataInput);

                dataInputRefs.add(new StringValue(Ids.dataInput(userTask.getId(), value[0])));

                DataInput inputDataItem = new DataInput().setId(value[0])
                        .setItemSubjectRef(Ids.multiInstanceItemType(userTask.getId(), value[0]))
                        .setName(value[0]);

                userTask.getBpmnProperties().add(new DataInputAssociation()
                                                         .setSourceRef(new SourceRef(value[0]))
                                                         .setTargetRef(new TargetRef(Ids.dataInput(userTask.getId(), value[0]), false)));

                multiInstanceLoopCharacteristics.setInputDataItem(inputDataItem);
            }

            if (!executionSet.getMultipleInstanceDataOutput().getValue().isEmpty()) {
                String[] value = executionSet.getMultipleInstanceDataOutput().getValue().split(":");
                ItemDefinition itemDefinition = new ItemDefinition();
                itemDefinition.setId(Ids.multiInstanceItemType(userTask.getId(), value[0]));
                itemDefinition.setStructureRef(value[1]);
                definitions.getItemDefinitions().add(itemDefinition);

                DataOutput dataOutput = new DataOutput().setId(Ids.dataOutput(userTask.getId(), value[0]))
                        .setItemSubjectRef(Ids.multiInstanceItemType(userTask.getId(), value[0]))
                        .setName(value[0]);
                ioSpecification.add(dataOutput);

                dataOutputRefs.add(new StringValue(Ids.dataOutput(userTask.getId(), value[0])));

                DataOutput outputDataItem = new DataOutput().setId(value[0])
                        .setItemSubjectRef(Ids.multiInstanceItemType(userTask.getId(), value[0]))
                        .setName(value[0]);

                userTask.getBpmnProperties().add(new DataOutputAssociation()
                                                         .setSourceRef(new SourceRef(Ids.dataOutput(userTask.getId(), value[0])))
                                                         .setTargetRef(new TargetRef(value[0], false)));

                multiInstanceLoopCharacteristics.setOutputDataItem(outputDataItem);
            }

            if (executionSet.getMultipleInstanceCollectionInput().getValue() != null) {
                String id = Ids.inCollectionInput(userTask.getId());

                userTask.getBpmnProperties().add(new DataInputAssociation()
                                                         .setSourceRef(new SourceRef(executionSet.getMultipleInstanceCollectionInput().getValue()))
                                                         .setTargetRef(new TargetRef(id, false)));
                dataInputRefs.add(new StringValue(id));
                Data dataInput = new DataInput().setId(id)
                        .setItemSubjectRef("_" + executionSet.getMultipleInstanceCollectionInput().getValue() + "Item")
                        .setName("IN_COLLECTION");
                ioSpecification.add(dataInput);
                multiInstanceLoopCharacteristics.setLoopDataInputRef(new StringValue(id));
            }

            if (executionSet.getMultipleInstanceCollectionOutput().getValue() != null) {
                String id = Ids.outCollectionOutput(userTask.getId());
                userTask.getBpmnProperties().add(new DataOutputAssociation()
                                                         .setSourceRef(new SourceRef(id))
                                                         .setTargetRef(new TargetRef(executionSet.getMultipleInstanceCollectionOutput()
                                                                                             .getValue(), false)));
                dataOutputRefs.add(new StringValue(id));
                Data dataInput = new DataOutput().setId(id)
                        .setItemSubjectRef("_" + executionSet.getMultipleInstanceCollectionOutput().getValue() + "Item")
                        .setName("OUT_COLLECTION");
                ioSpecification.add(dataInput);
                multiInstanceLoopCharacteristics.setLoopDataOutputRef(new StringValue(id));
            }
            userTask.getBpmnProperties().add(multiInstanceLoopCharacteristics);
        }
    }

    private void assignments() {
        if (!executionSet.getAssignmentsinfo().getValue().isEmpty()) {
            ParsedAssignmentsInfo parsedAssignmentsInfo = ParsedAssignmentsInfo.of(executionSet.getAssignmentsinfo());
            parsedAssignmentsInfo.getAssociations().getInputs().forEach(association -> {
                addAssignments(parsedAssignmentsInfo, association);
            });

            parsedAssignmentsInfo.getAssociations().getOutputs().forEach(association -> {
                addAssignments(parsedAssignmentsInfo, association);
            });
        }
    }

    private void setCollections() {
        inputSet.setDataInputRefs(dataInputRefs);
        outputSet.setDataOutputRefs(dataOutputRefs);

        ioSpecification.add(inputSet);
        ioSpecification.add(outputSet);

        userTask.setIoSpecification(ioSpecification);
        userTask.setPotentialOwner(potentialOwners);
    }

    private List<String> fromActorString(String delimitedActors) {
        String[] split = delimitedActors.split(",");
        if (split.length == 1 && split[0].isEmpty()) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(split);
        }
    }

    private void addAssignments(ParsedAssignmentsInfo parsedAssignmentsInfo,
                                AssociationDeclaration associationDeclaration) {
        boolean input = associationDeclaration.getDirection()
                .equals(AssociationDeclaration.Direction.Input);
        VariableDeclaration variableDeclaration = input ? parsedAssignmentsInfo.getInputs()
                .lookup(associationDeclaration.getTarget()) : parsedAssignmentsInfo.getOutputs()
                .lookup(associationDeclaration.getSource());

        variableDeclaration.getTypeDeclaration()
                .setId(Ids.dataInputItem(userTask.getId(), associationDeclaration.getTarget()));
        definitions.getItemDefinitions()
                .add(variableDeclaration.getTypeDeclaration());

        String id = Ids.dataInput(userTask.getId(), associationDeclaration.getTarget());

        Data data;
        DataAssociation dataAssociation = null;
        if (input) {
            data = new DataInput();
            dataInputRefs.add(new StringValue(id));
            if (associationDeclaration.getSource() != null) {
                dataAssociation = new DataInputAssociation();
                dataAssociation.setTargetRef(new TargetRef(id, false));

                if (associationDeclaration.getType().equals(AssociationDeclaration.Type.FromTo)) {
                    dataAssociation.setAssignment(new Assignment()
                                                          .from(associationDeclaration.getSource())
                                                          .to(id, false));
                } else {
                    dataAssociation.setSourceRef(new SourceRef(associationDeclaration.getSource(), false));
                }
            }
        } else {
            data = new DataOutput();
            dataOutputRefs.add(new StringValue(id));
            if (associationDeclaration.getTarget() != null) {
                dataAssociation = new DataOutputAssociation();
                dataAssociation.setSourceRef(new SourceRef(id, false));
                if (associationDeclaration.getType().equals(AssociationDeclaration.Type.FromTo)) {
                    dataAssociation.setAssignment(new Assignment()
                                                          .from(id, false)
                                                          .to(associationDeclaration.getTarget()));
                } else {
                    dataAssociation.setTargetRef(new TargetRef(associationDeclaration.getTarget(), false));
                }
            }
        }
        data.setId(id);
        data.setDtype(variableDeclaration.getType())
                .setItemSubjectRef(variableDeclaration.getTypeDeclaration().getId())
                .setName(associationDeclaration.getTarget());
        ioSpecification.add(data);

        if (dataAssociation != null) {
            userTask.getBpmnProperties().add(dataAssociation);
        }
    }

    void convert() {
        for (Command command : actions) {
            command.execute();
        }
    }
}
