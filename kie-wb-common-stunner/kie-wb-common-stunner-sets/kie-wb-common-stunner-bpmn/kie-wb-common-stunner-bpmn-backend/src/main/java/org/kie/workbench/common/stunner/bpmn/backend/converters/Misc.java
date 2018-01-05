package org.kie.workbench.common.stunner.bpmn.backend.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import org.eclipse.bpmn2.*;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.di.DiagramElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class Misc {
    private static final Logger _logger = LoggerFactory.getLogger(Misc.class);

    public static void writeAssociations(Process process,
                                   String elementId,
                                   JsonGenerator generator) throws IOException {
        for (Artifact artifact : process.getArtifacts()) {
            if (artifact instanceof Association) {
                Association association = (Association) artifact;
                if (association.getSourceRef().getId().equals(elementId)) {
                    generator.writeStartObject();
                    generator.writeObjectField("resourceId",
                            association.getId());
                    generator.writeEndObject();
                }
            }
        }
    }

    public static void marshallDataOutputAssociations(StringBuilder associationBuff,
                                                List<DataOutputAssociation> outputAssociations) {
        if (outputAssociations != null) {
            for (DataOutputAssociation dataout : outputAssociations) {
                if (dataout.getSourceRef().size() > 0) {
                    String lhsAssociation = ((DataOutput) dataout.getSourceRef().get(0)).getName();
                    String rhsAssociation = dataout.getTargetRef().getId();
                    if (dataout.getTransformation() != null && dataout.getTransformation().getBody() != null) {
                        rhsAssociation = encodeAssociationValue(dataout.getTransformation().getBody());
                    }
                    if (lhsAssociation != null && lhsAssociation.length() > 0) {
                        associationBuff.append("[dout]" + lhsAssociation).append("->").append(rhsAssociation);
                        associationBuff.append(",");
                    }
                }
            }
        }
    }


    public static String encodeAssociationValue(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        try {
            return URLEncoder.encode(s,
                    "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    public static DiagramElement findDiagramElement(
//            Map<String, DiagramElement> _diagramElements,
            BPMNPlane plane,
            BaseElement baseElement) {
//
//        DiagramElement result = _diagramElements.get(baseElement.getId());
//        if (result != null) {
//            return result;
//        }
//        for (DiagramElement element : plane.getPlaneElement()) {
//            if ((element instanceof BPMNEdge && ((BPMNEdge) element).getBpmnElement() == baseElement) ||
//                    (element instanceof BPMNShape && ((BPMNShape) element).getBpmnElement() == baseElement)) {
//                _diagramElements.put(baseElement.getId(),
//                        element);
//                return element;
//            }
//        }
        _logger.debug("Could not find BPMNDI information for " + baseElement);
        return null;
    }

    public static void writeWaypointObject(final JsonGenerator generator,
                                     final float x,
                                     final float y) throws IOException {
        generator.writeStartObject();
        generator.writeObjectField("x",
                x);
        generator.writeObjectField("y",
                y);
        generator.writeEndObject();
    }


    public static void findBoundaryEvents(FlowElementsContainer flc,
                                    List<BoundaryEvent> boundaryList) {
        for (FlowElement fl : flc.getFlowElements()) {
            if (fl instanceof BoundaryEvent) {
                boundaryList.add((BoundaryEvent) fl);
            }
            if (fl instanceof FlowElementsContainer) {
                findBoundaryEvents((FlowElementsContainer) fl,
                        boundaryList);
            }
        }
    }


}
