package org.kie.workbench.common.stunner.bpmn.backend.converters.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.PotentialOwner;
import org.eclipse.bpmn2.ResourceRole;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.ThrowEvent;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.OnEntryScriptType;
import org.jboss.drools.OnExitScriptType;
import org.kie.workbench.common.stunner.bpmn.backend.converters.AssignmentsInfos;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.ScriptLanguages;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptableExecutionSet;

public class Properties {

    public static String getAssignmentsInfo(Activity activity) {
        InputOutputSpecification ioSpecification = activity.getIoSpecification();
        if (ioSpecification == null) {
            return (
                    AssignmentsInfos.makeString(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            activity.getDataInputAssociations(),
                            Collections.emptyList(),
                            Collections.emptyList(),
                            activity.getDataOutputAssociations()
                    )
            );
        } else {
            return (
                    AssignmentsInfos.makeString(
                            ioSpecification.getDataInputs(),
                            ioSpecification.getInputSets(),
                            activity.getDataInputAssociations(),
                            ioSpecification.getDataOutputs(),
                            ioSpecification.getOutputSets(),
                            activity.getDataOutputAssociations()
                    )
            );

        }
    }


    public static String getAssignmentsInfo(ThrowEvent event) {
        return (
                AssignmentsInfos.makeString(
                        event.getDataInputs(),
                        Collections.singletonList(event.getInputSet()),
                        event.getDataInputAssociation(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                )
        );
    }

    public static String getAssignmentsInfo(CatchEvent event) {
        return (
                AssignmentsInfos.makeString(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        event.getDataOutputs(),
                        Collections.singletonList(event.getOutputSet()),
                        event.getDataOutputAssociation()
                )
        );
    }
    public static boolean findAnyAttributeBoolean(BaseElement element, String attributeId) {
        return Boolean.parseBoolean(findAnyAttribute(element, attributeId));
    }

    public static String findAnyAttribute(BaseElement element, String attributeId) {
        return element.getAnyAttribute().stream()
                .filter(e -> e.getEStructuralFeature().getName().equals(attributeId))
                .map(e -> e.getValue().toString())
                .findFirst().orElse("");
    }

    public static String actors(Task task) {
        // get the user task actors
        List<ResourceRole> roles = task.getResources();
        List<String> users = new ArrayList<>();
        for (ResourceRole role : roles) {
            if (role instanceof PotentialOwner) {
                FormalExpression fe = (FormalExpression) role.getResourceAssignmentExpression().getExpression();
                users.add(fe.getBody());
            }
        }
        return users.stream().collect(Collectors.joining(","));
    }

    public static boolean findMetaBoolean(BaseElement element, String name) {
        return findMetaBoolean(element.getExtensionValues(), name);
    }

    public static boolean findMetaBoolean(List<ExtensionAttributeValue> extensions, String name) {
        return Boolean.parseBoolean(findMetaValue(extensions, name));
    }

    public static String findMetaValue(BaseElement element, String name) {
        return Utils.getMetaDataValue(element.getExtensionValues(), name);
    }

    public static String findMetaValue(List<ExtensionAttributeValue> extensions, String name) {
        return Utils.getMetaDataValue(extensions, name);
    }

    public static boolean findInputBooleans(Activity activity, String name) {
        return findInputBooleans(activity.getDataInputAssociations(), name);
    }

    public static boolean findInputBooleans(List<DataInputAssociation> associations, String name) {
        return Boolean.parseBoolean(findInputValue(associations, name));
    }

    public static String findInputValue(Activity activity, String name) {
        return findInputValue(activity.getDataInputAssociations(), name);
    }

    public static String findInputValue(List<DataInputAssociation> associations, String name) {
        for (DataInputAssociation din : associations) {
            DataInput targetRef = (DataInput) (din.getTargetRef());
            if (targetRef.getName().equalsIgnoreCase(name)) {
                Assignment assignment = din.getAssignment().get(0);
                return evaluate(assignment).toString();
            }
        }
        return "";
    }

    private static Object evaluate(Assignment assignment) {
        return ((FormalExpression) assignment.getFrom()).getMixed().getValue(0);
    }

    private static Optional<DataInput> findByName(List<DataInput> dataInputs, String name) {
        return dataInputs.stream().filter(in -> in.getName().equals(name)).findFirst();
    }
}
