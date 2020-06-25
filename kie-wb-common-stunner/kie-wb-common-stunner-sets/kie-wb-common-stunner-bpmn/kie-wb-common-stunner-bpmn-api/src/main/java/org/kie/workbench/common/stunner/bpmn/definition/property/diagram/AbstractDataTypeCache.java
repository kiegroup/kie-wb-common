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

package org.kie.workbench.common.stunner.bpmn.definition.property.diagram;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.GenericServiceTask;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateErrorEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateEscalationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateMessageEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventCatching;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateSignalEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartEscalationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.BaseAdvancedData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.BaseProcessData;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTask;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/** An Abstract Class to Handle Data Type Cache.
 */
public abstract class AbstractDataTypeCache {

    public AbstractDataTypeCache() {
    }

    protected List<String> allDataTypes = new ArrayList<>();

    public void extractFromItem(View view) {
        Object definition = view.getDefinition();

        if (definition instanceof DataObject) {
            DataObject dataObject = (DataObject) view.getDefinition();
            allDataTypes.add(dataObject.getType().getValue().getType());
        } else if (definition instanceof AdHocSubprocess) {
            AdHocSubprocess mi = (AdHocSubprocess) view.getDefinition();
            allDataTypes.addAll(getDataTypes(mi.getProcessData().getProcessVariables().getValue()));
        } else if (definition instanceof BPMNDiagramImpl) {
            BPMNDiagramImpl mi = (BPMNDiagramImpl) view.getDefinition();
            allDataTypes.addAll(getDataTypes(mi.getProcessData().getProcessVariables().getValue()));
        } else if (definition instanceof EmbeddedSubprocess) {
            EmbeddedSubprocess mi = (EmbeddedSubprocess) view.getDefinition();
            allDataTypes.addAll(getDataTypes(mi.getProcessData().getProcessVariables().getValue()));
        } else if (definition instanceof EventSubprocess) {
            EventSubprocess mi = (EventSubprocess) view.getDefinition();
            allDataTypes.addAll(getDataTypes(mi.getProcessData().getProcessVariables().getValue()));
        } else if (definition instanceof MultipleInstanceSubprocess) {
            MultipleInstanceSubprocess mi = (MultipleInstanceSubprocess) view.getDefinition();
            allDataTypes.addAll(getDataTypes(mi.getProcessData().getProcessVariables().getValue()));
            allDataTypes.addAll(getDataTypes(mi.getExecutionSet().getMultipleInstanceDataInput().getValue()));
            allDataTypes.addAll(getDataTypes(mi.getExecutionSet().getMultipleInstanceDataOutput().getValue()));
        } else if (definition instanceof UserTask) {
            UserTask ut = (UserTask) view.getDefinition();
            allDataTypes.addAll(processAssignments(ut.getExecutionSet().getAssignmentsinfo()));
        } else if (definition instanceof GenericServiceTask) {
            GenericServiceTask st = (GenericServiceTask) view.getDefinition();
            allDataTypes.addAll(processAssignments(st.getExecutionSet().getAssignmentsinfo()));
        } else if (definition instanceof BusinessRuleTask) {
            BusinessRuleTask bt = (BusinessRuleTask) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof EndErrorEvent) {
            EndErrorEvent bt = (EndErrorEvent) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof EndEscalationEvent) {
            EndEscalationEvent bt = (EndEscalationEvent) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof EndMessageEvent) {
            EndMessageEvent bt = (EndMessageEvent) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof EndSignalEvent) {
            EndSignalEvent bt = (EndSignalEvent) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof IntermediateErrorEventCatching) {
            IntermediateErrorEventCatching bt = (IntermediateErrorEventCatching) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof IntermediateEscalationEvent) {
            IntermediateEscalationEvent bt = (IntermediateEscalationEvent) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof IntermediateEscalationEventThrowing) {
            IntermediateEscalationEventThrowing bt = (IntermediateEscalationEventThrowing) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof IntermediateMessageEventCatching) {
            IntermediateMessageEventCatching bt = (IntermediateMessageEventCatching) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof IntermediateMessageEventThrowing) {
            IntermediateMessageEventThrowing bt = (IntermediateMessageEventThrowing) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof IntermediateSignalEventCatching) {
            IntermediateSignalEventCatching bt = (IntermediateSignalEventCatching) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof IntermediateSignalEventThrowing) {
            IntermediateSignalEventThrowing bt = (IntermediateSignalEventThrowing) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof ReusableSubprocess) {
            ReusableSubprocess bt = (ReusableSubprocess) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof StartErrorEvent) {
            StartErrorEvent bt = (StartErrorEvent) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof StartEscalationEvent) {
            StartEscalationEvent bt = (StartEscalationEvent) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof StartMessageEvent) {
            StartMessageEvent bt = (StartMessageEvent) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof StartSignalEvent) {
            StartSignalEvent bt = (StartSignalEvent) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else if (definition instanceof CustomTask) {
            CustomTask bt = (CustomTask) view.getDefinition();
            allDataTypes.addAll(processAssignments(bt.getDataIOSet().getAssignmentsinfo()));
        } else {
            // Nothing to do. This simply means that the item does not contain a data type
        }
    }

    protected abstract void cacheDataTypes(Object processRoot);

    protected abstract List<String> processAssignments(AssignmentsInfo info);

    protected abstract List<String> getDataTypes(String variables);

    private void cacheImports(List<DefaultImport> defaultImports) {
        for (DefaultImport imported : defaultImports) {
            allDataTypes.add(imported.getClassName());
        }
    }

    public void initCache(Object diagramRoot, Node<View<? extends BPMNDiagram<? extends BaseDiagramSet, ? extends BaseProcessData, ? extends BaseAdvancedData>>, Edge> value) {
        final BPMNDiagram<? extends BaseDiagramSet, ? extends BaseProcessData, ? extends BaseAdvancedData> definition = value.getContent().getDefinition();
        cacheImports(definition.getDiagramSet().getImports().getValue().getDefaultImports());
        cacheProcessVariables(definition.getProcessData().getProcessVariables().getValue());
        cacheGlobalVariables(definition.getAdvancedData().getGlobalVariables().getValue());
        cacheDataTypes(diagramRoot);
    }

    private void cacheProcessVariables(String processVariables) {
        allDataTypes.addAll(getDataTypes(processVariables));
    }

    private void cacheGlobalVariables(String globalVariables) {
        allDataTypes.addAll(getDataTypes(globalVariables));
    }

    public List<String> getCachedDataTypes() {
        allDataTypes.remove("Object");
        allDataTypes.remove("String");
        allDataTypes.remove("Integer");
        allDataTypes.remove("Boolean");
        allDataTypes.remove("Double");
        return allDataTypes;
    }
}

