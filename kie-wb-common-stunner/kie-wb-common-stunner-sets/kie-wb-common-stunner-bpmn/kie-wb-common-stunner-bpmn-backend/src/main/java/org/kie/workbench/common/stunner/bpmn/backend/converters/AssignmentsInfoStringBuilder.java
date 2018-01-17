package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;

public class AssignmentsInfoStringBuilder {
    public static void setAssignmentsInfo(Activity activity, AssignmentsInfo assignmentsInfo) {
        InputOutputSpecification ioSpecification = activity.getIoSpecification();
        if (ioSpecification == null) {
            assignmentsInfo.setValue(
                    AssignmentsInfoStringBuilder.makeString(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            activity.getDataInputAssociations(),
                            Collections.emptyList(),
                            Collections.emptyList(),
                            activity.getDataOutputAssociations()
                    )
            );
        } else {
            assignmentsInfo.setValue(
                    AssignmentsInfoStringBuilder.makeString(
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


    public static void setAssignmentsInfo(ThrowEvent event, AssignmentsInfo assignmentsInfo) {
        assignmentsInfo.setValue(
                AssignmentsInfoStringBuilder.makeString(
                        event.getDataInputs(),
                        Collections.singletonList(event.getInputSet()),
                        event.getDataInputAssociation(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList()
                )
        );
    }

    public static void setAssignmentsInfo(CatchEvent event, AssignmentsInfo assignmentsInfo) {
        assignmentsInfo.setValue(
                AssignmentsInfoStringBuilder.makeString(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        Collections.emptyList(),
                        event.getDataOutputs(),
                        Collections.singletonList(event.getOutputSet()),
                        event.getDataOutputAssociation()
                )
        );
    }

    public static String makeString(
            final List<DataInput> datainput,
            final List<InputSet> inputSets,
            final List<DataInputAssociation> inputAssociations,
            final List<DataOutput> dataoutput,
            final List<OutputSet> dataoutputset,
            final List<DataOutputAssociation> outputAssociations) {

        String dataInputString = dataInputsToString(datainput);
        String inputSetsToString = inputSetsToString(inputSets);
        List<String> dataInputAssociationsToString = inAssociationsToString(inputAssociations);

        String dataOutputString = dataOutputsToString(dataoutput);
        String outputSetsToString = outputSetsToString(dataoutputset);
        List<String> dataOutputAssociationsToString = outAssociationsToString(outputAssociations);

        String associationString =
                Stream.concat(dataInputAssociationsToString.stream(), dataOutputAssociationsToString.stream())
                .collect(Collectors.joining(","));

        return Stream.of(dataInputString,
                         inputSetsToString,
                         dataOutputString,
                         outputSetsToString,
                         associationString)
                .collect(Collectors.joining("|"));
    }

    private static String inputSetsToString(List<InputSet> inputSets) {
        return inputSets.stream()
                .map(AssignmentsInfoStringBuilder::toString)
                .collect(Collectors.joining(","));
    }

    private static String outputSetsToString(List<OutputSet> outputSets) {
        return outputSets.stream()
                .map(AssignmentsInfoStringBuilder::toString)
                .collect(Collectors.joining(","));
    }

    public static String dataInputsToString(List<DataInput> dataInputs) {
        return dataInputs.stream()
                .filter(o -> !extractDtype(o).isEmpty())
                .map(AssignmentsInfoStringBuilder::toString)
                .collect(Collectors.joining(","));
    }

    public static String dataOutputsToString(List<DataOutput> dataInputs) {
        return dataInputs.stream()
                .filter(o -> !extractDtype(o).isEmpty())
                .map(AssignmentsInfoStringBuilder::toString)
                .collect(Collectors.joining(","));
    }

    private static String toString(OutputSet outputSet) {
        return "";
    }

    public static String toString(InputSet dataInput) {
        return "";
    }

    @Deprecated
    private void marshallItemAwareElements(Activity activity,
                                           List<? extends ItemAwareElement> elements,
                                           StringBuilder buffer,
                                           List<String> disallowedNames) {
        for (ItemAwareElement element : elements) {
            String name = null;
            if (element instanceof DataInput) {
                name = ((DataInput) element).getName();
            }
            if (element instanceof DataOutput) {
                name = ((DataOutput) element).getName();
            }
            if (name != null && !name.isEmpty() && !disallowedNames.contains(name)) {
                buffer.append(name);
                if (element.getItemSubjectRef() != null && element.getItemSubjectRef().getStructureRef() != null && !element.getItemSubjectRef().getStructureRef().isEmpty()) {
                    buffer.append(":").append(element.getItemSubjectRef().getStructureRef());
                } else if (activity.eContainer() instanceof SubProcess) {
                    // BZ1247105: for Outputs on Tasks inside sub-processes
                    String dtype = extractDtype(element);
                    if (dtype != null && !dtype.isEmpty()) {
                        buffer.append(":").append(dtype);
                    }
                }
                buffer.append(",");
            }
        }
    }

    public static List<String> outAssociationsToString(List<DataOutputAssociation> outputAssociations) {
        List<String> result = new ArrayList<>();
        for (DataOutputAssociation doa : outputAssociations) {
            String doaName = ((DataOutput) doa.getSourceRef().get(0)).getName();
            if (doaName != null && doaName.length() > 0) {

                if (doaName != null && doaName.length() > 0) {
                    result.add(
                            String.format("[dout]%s->%s", doaName, doa.getTargetRef().getId()));
                }

            }
        }
        return result;
    }

    public static List<String> inAssociationsToString(List<DataInputAssociation> inputAssociations) {
        List<String> result = new ArrayList<>();

        for (DataInputAssociation dia : inputAssociations) {
            List<ItemAwareElement> sourceRef = dia.getSourceRef();
            if (sourceRef.isEmpty()) continue;
            String doaName = sourceRef.get(0).getId();
            if (doaName != null && doaName.length() > 0) {
                result.add(
                        String.format("[din]%s->%s", doaName, ((DataInput) dia.getTargetRef()).getName()));
            }
        }

        return result;
    }

    public static String toString(DataInput dataInput) {
        String name = dataInput.getName();
        String dtype = extractDtype(dataInput);
        return dtype.isEmpty() ? name : name + ':' + dtype;
    }

    public static String toString(DataOutput dataInput) {
        String name = dataInput.getName();
        String dtype = extractDtype(dataInput);
        return dtype.isEmpty() ? name : name + ':' + dtype;
    }

    private static String extractDtype(BaseElement el) {
        return getAnyAttributeValue(el, "dtype"); // fixme: look for a safer way to do this
    }

    static String getAnyAttributeValue(BaseElement el, String attrName) {
        for (FeatureMap.Entry entry : el.getAnyAttribute()) {
            if (attrName.equals(entry.getEStructuralFeature().getName())) {
                return entry.getValue().toString();
            }
        }
        return "";
    }
}
