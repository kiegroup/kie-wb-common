package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.PotentialOwner;
import org.eclipse.bpmn2.ResourceRole;
import org.eclipse.bpmn2.Task;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.OnEntryScriptType;
import org.jboss.drools.OnExitScriptType;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptableExecutionSet;

public class Properties {

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

    public static void setScriptProperties(Task task, ScriptableExecutionSet executionSet) {
        @SuppressWarnings("unchecked")
        List<OnEntryScriptType> onEntryExtensions =
                (List<OnEntryScriptType>) task.getExtensionValues().get(0).getValue()
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, true);
        @SuppressWarnings("unchecked")
        List<OnExitScriptType> onExitExtensions =
                (List<OnExitScriptType>) task.getExtensionValues().get(0).getValue()
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, true);

        if (!onEntryExtensions.isEmpty()) {
            executionSet.getOnEntryAction().setValue(onEntryExtensions.get(0).getScript());
            executionSet.getScriptLanguage().setValue(ScriptLanguages.fromUri(onEntryExtensions.get(0).getScriptFormat()));
        }

        if (!onExitExtensions.isEmpty()) {
            executionSet.setOnExitAction(new OnExitAction(onExitExtensions.get(0).getScript()));
        }
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
