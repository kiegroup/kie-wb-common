package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.List;
import java.util.Optional;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Task;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.OnEntryScriptType;
import org.jboss.drools.OnExitScriptType;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptLanguage;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class TaskConverter {

    private TypedFactoryManager factoryManager;

    public TaskConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public Node<? extends View<? extends BPMNViewDefinition>, ?> convert(org.eclipse.bpmn2.Task task) {
        return Match.ofNode(Task.class, BPMNViewDefinition.class)
                .when(org.eclipse.bpmn2.BusinessRuleTask.class, t ->
                        factoryManager.newNode(t.getId(), BusinessRuleTask.class))
                .when(org.eclipse.bpmn2.ScriptTask.class, t ->
                        factoryManager.newNode(t.getId(), ScriptTask.class)
                )
                //.when(org.eclipse.bpmn2.ServiceTask.class,      t -> null)
                //.when(org.eclipse.bpmn2.ManualTask.class,       t -> null)
                .when(org.eclipse.bpmn2.UserTask.class, t -> {
                    Node<View<UserTask>, Edge> node = factoryManager.newNode(t.getId(), UserTask.class);
                    UserTaskExecutionSet executionSet = node.getContent().getDefinition().getExecutionSet();
                    AssignmentsInfoStringBuilder.setAssignmentsInfo(
                            task, executionSet.getAssignmentsinfo());

                    executionSet.getTaskName().setValue(t.getName());
                    executionSet.getIsAsync().setValue(findMetaBoolean(t, "customAsync"));
                    executionSet.getAdHocAutostart().setValue(findMetaBoolean(t, "customAutoStart"));

                    executionSet.getSubject().setValue(findValue(task, "Comment"));
                    executionSet.getTaskName().setValue(findValue(task, "TaskName"));
                    executionSet.getSkippable().setValue(findBoolean(task, "Skippable"));
                    executionSet.getDescription().setValue(findValue(task, "Description"));
                    executionSet.getPriority().setValue(findValue(task, "Priority"));
                    executionSet.getCreatedBy().setValue(findValue(task, "CreatedBy"));

                    @SuppressWarnings("unchecked")
                    List<OnEntryScriptType> onEntryExtensions =
                            (List<OnEntryScriptType>) task.getExtensionValues().get(0).getValue()
                                    .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, true);
                    @SuppressWarnings("unchecked")
                    List<OnExitScriptType> onExitExtensions =
                            (List<OnExitScriptType>) task.getExtensionValues().get(0).getValue()
                                    .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, true);

                    executionSet.setOnEntryAction(new OnEntryAction(onEntryExtensions.get(0).getScript()));
                    executionSet.setOnExitAction(new OnExitAction(onExitExtensions.get(0).getScript()));
                    executionSet.setScriptLanguage(new ScriptLanguage(extractScriptLanguage(onEntryExtensions.get(0).getScriptFormat())));

//                executionSet.getSubject().setValue("");
                    //executionSet.getAdHocAutostart().setValue(Boolean.parseBoolean(findValue(task, "adhocautostart")));
                    return node;
                })
                .orElse(t ->
                                factoryManager.newNode(t.getId(), NoneTask.class)
                )
                .apply(task)
                .value();
    }

    private String extractScriptLanguage(String format) {
        if (format.equals("http://www.java.com/java")) {
            return "java";
        } else if (format.equals("http://www.mvel.org/2.0")) {
            return "mvel";
        } else if (format.equals("http://www.javascript.com/javascript")) {
            return "javascript";
        } else {
            return "java";
        }
    }

    private boolean findMetaBoolean(org.eclipse.bpmn2.Task task, String name) {
        return Boolean.parseBoolean(findMetaValue(task, name));
    }

    private String findMetaValue(org.eclipse.bpmn2.Task t, String name) {
        return Utils.getMetaDataValue(t.getExtensionValues(), name);
    }

    private boolean findBoolean(org.eclipse.bpmn2.Task task, String name) {
        return Boolean.parseBoolean(findValue(task, name));
    }

    private String findValue(org.eclipse.bpmn2.Task task, String name) {
        for (DataInputAssociation din : task.getDataInputAssociations()) {
            DataInput targetRef = (DataInput) (din.getTargetRef());
            if (targetRef.getName().equalsIgnoreCase(name)) {
                Assignment assignment = din.getAssignment().get(0);
                return evaluate(assignment).toString();
            }
        }
        return "";
    }

    private Object evaluate(Assignment assignment) {
        return ((FormalExpression) assignment.getFrom()).getMixed().getValue(0);
    }

    private Optional<DataInput> findByName(List<DataInput> dataInputs, String name) {
        return dataInputs.stream().filter(in -> in.getName().equals(name)).findFirst();
    }
}
