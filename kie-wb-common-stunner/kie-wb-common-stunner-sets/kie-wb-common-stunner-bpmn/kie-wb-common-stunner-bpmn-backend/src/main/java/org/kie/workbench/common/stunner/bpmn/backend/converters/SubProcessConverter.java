package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.SubProcess;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.Scripts;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CalledElement;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Independent;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.WaitForCompletion;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.Scripts.onEntry;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.Scripts.onExit;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.Scripts.scriptLanguage;

public class SubProcessConverter {

    private final TypedFactoryManager factoryManager;

    public SubProcessConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(SubProcess subProcess) {
        Node<View<EmbeddedSubprocess>, Edge> node = factoryManager.newNode(subProcess.getId(), EmbeddedSubprocess.class);

        EmbeddedSubprocess definition = node.getContent().getDefinition();

        definition.setGeneral(new BPMNGeneralSet(
                new Name(subProcess.getName()),
                Properties.documentation(subProcess.getDocumentation())
        ));

        List<ExtensionAttributeValue> extensionValues = subProcess.getExtensionValues();
        definition.getOnEntryAction().setValue(onEntry(extensionValues));
        definition.getOnExitAction().setValue(onExit(extensionValues));

        String joinedVariables = subProcess.getProperties()
                .stream()
                .map(p -> p.getId() + ":" + p.getItemSubjectRef().getStructureRef())
                .collect(Collectors.joining(","));
        definition.getProcessData().getProcessVariables().setValue(joinedVariables);


        definition.getScriptLanguage().setValue(Scripts.scriptLanguage(extensionValues));
        definition.setGeneral(new BPMNGeneralSet(
                new Name(subProcess.getName()),
                Properties.documentation(subProcess.getDocumentation())
        ));
        definition.getIsAsync().setValue(Properties.findMetaBoolean(subProcess, "customAsync"));
        
        return node;
    }
}
