package org.kie.workbench.common.stunner.bpmn.backend.converters;

import com.fasterxml.jackson.core.JsonGenerationException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.Expression;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.Point;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.Bpmn2OryxManager;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.Colors.defaultSequenceflowColor;
import static org.kie.workbench.common.stunner.bpmn.backend.legacy.Bpmn2JsonPropertyIds.*;
import static org.kie.workbench.common.stunner.bpmn.backend.legacy.Bpmn2JsonPropertyIds.ISSELECTABLE;

public class SequenceFlowConverter {
    private static final Logger _logger = LoggerFactory.getLogger(SequenceFlowConverter.class);
    private TypedFactoryManager factoryManager;

    public SequenceFlowConverter(TypedFactoryManager factoryManager) {

        this.factoryManager = factoryManager;
    }
    public Edge<View<BPMNViewDefinition>, ?> convert(SequenceFlow seq) {
        return factoryManager.newEdge(seq.getId(), org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow.class);
    }

    public static void convert(SequenceFlow sequenceFlow,
                                        BPMNGraphGenerator generator) throws JsonGenerationException, IOException {
        // dont marshal "dangling" sequence flow..better to just omit than fail
        if (sequenceFlow.getSourceRef() == null || sequenceFlow.getTargetRef() == null) {
            return;
        }
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        // check null for sequence flow name
        if (sequenceFlow.getName() != null && !"".equals(sequenceFlow.getName())) {
            properties.put(NAME,
                    StringEscapeUtils.unescapeXml(sequenceFlow.getName()));
        } else {
            properties.put(NAME,
                    "");
        }
        // overwrite name if elementname extension element is present
        String elementName = Utils.getMetaDataValue(sequenceFlow.getExtensionValues(),
                "elementname");
        if (elementName != null) {
            properties.put(NAME,
                    elementName);
        }

        Properties.putDocumentationProperty(sequenceFlow, properties);

        if (sequenceFlow.isIsImmediate()) {
            properties.put(ISIMMEDIATE,
                    "true");
        } else {
            properties.put(ISIMMEDIATE,
                    "false");
        }
        Expression conditionExpression = sequenceFlow.getConditionExpression();
        if (conditionExpression instanceof FormalExpression) {
            if (((FormalExpression) conditionExpression).getBody() != null) {
                properties.put(CONDITIONEXPRESSION,
                        ((FormalExpression) conditionExpression).getBody());
            }
            if (((FormalExpression) conditionExpression).getLanguage() != null) {
                String cd = ((FormalExpression) conditionExpression).getLanguage();
                String cdStr = "";
                if (cd.equalsIgnoreCase("http://www.java.com/java")) {
                    cdStr = "java";
                } else if (cd.equalsIgnoreCase("http://www.jboss.org/drools/rule")) {
                    cdStr = "drools";
                } else if (cd.equalsIgnoreCase("http://www.mvel.org/2.0")) {
                    cdStr = "mvel";
                } else if (cd.equalsIgnoreCase("http://www.javascript.com/javascript")) {
                    cdStr = "javascript";
                } else {
                    // default to mvel
                    cdStr = "mvel";
                }
                properties.put(CONDITIONEXPRESSIONLANGUAGE,
                        cdStr);
            }
        }
        boolean foundBgColor = false;
        boolean foundBrColor = false;
        boolean foundFontColor = false;
        boolean foundSelectable = false;
        Iterator<FeatureMap.Entry> iter = sequenceFlow.getAnyAttribute().iterator();
        while (iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if (entry.getEStructuralFeature().getName().equals("priority")) {
                String priorityStr = String.valueOf(entry.getValue());
                if (priorityStr != null) {
                    try {
                        Integer priorityInt = Integer.parseInt(priorityStr);
                        if (priorityInt >= 1) {
                            properties.put(PRIORITY,
                                    entry.getValue());
                        } else {
                            _logger.error("Priority must be equal or greater than 1.");
                        }
                    } catch (NumberFormatException e) {
                        _logger.error("Priority must be a number.");
                    }
                }
            }
            if (entry.getEStructuralFeature().getName().equals("background-color") || entry.getEStructuralFeature().getName().equals("bgcolor")) {
                properties.put(BGCOLOR,
                        entry.getValue());
                foundBgColor = true;
            }
            if (entry.getEStructuralFeature().getName().equals("border-color") || entry.getEStructuralFeature().getName().equals("bordercolor")) {
                properties.put(BORDERCOLOR,
                        entry.getValue());
                foundBrColor = true;
            }
            if (entry.getEStructuralFeature().getName().equals("fontsize")) {
                properties.put(FONTSIZE,
                        entry.getValue());
                foundBrColor = true;
            }
            if (entry.getEStructuralFeature().getName().equals("color") || entry.getEStructuralFeature().getName().equals("fontcolor")) {
                properties.put(FONTCOLOR,
                        entry.getValue());
                foundFontColor = true;
            }
            if (entry.getEStructuralFeature().getName().equals("selectable")) {
                properties.put(ISSELECTABLE,
                        entry.getValue());
                foundSelectable = true;
            }
        }
        if (!foundBgColor) {
            properties.put(BGCOLOR,
                    defaultSequenceflowColor);
        }
        if (!foundBrColor) {
            properties.put(BORDERCOLOR,
                    defaultSequenceflowColor);
        }
        if (!foundFontColor) {
            properties.put(FONTCOLOR,
                    defaultSequenceflowColor);
        }
        if (!foundSelectable) {
            properties.put(ISSELECTABLE,
                    "true");
        }
        // simulation properties
//        setSimulationProperties(sequenceFlow.getId(),
//                properties);

        // Custom attributes for Stunner's connectors - source/target auto connection flag.
        String sourcePropertyName = Bpmn2OryxManager.MAGNET_AUTO_CONNECTION +
                Bpmn2OryxManager.SOURCE;
        String sourceConnectorAuto = Utils.getMetaDataValue(sequenceFlow.getExtensionValues(),
                sourcePropertyName);
        if (sourceConnectorAuto != null && sourceConnectorAuto.trim().length() > 0) {
            properties.put(sourcePropertyName,
                    sourceConnectorAuto);
        }
        String targetPropertyName = Bpmn2OryxManager.MAGNET_AUTO_CONNECTION +
                Bpmn2OryxManager.TARGET;
        String targetConnectorAuto = Utils.getMetaDataValue(sequenceFlow.getExtensionValues(),
                targetPropertyName);
        if (targetConnectorAuto != null && targetConnectorAuto.trim().length() > 0) {
            properties.put(targetPropertyName,
                    targetConnectorAuto);
        }

        Properties.convert(generator, properties);

        generator.writeObjectFieldStart("stencil");
        generator.writeObjectField("id",
                "SequenceFlow");
        generator.writeEndObject();
        generator.writeArrayFieldStart("childShapes");
        generator.writeEndArray();
        generator.writeArrayFieldStart("outgoing");
        generator.writeStartObject();
        generator.writeObjectField("resourceId",
                sequenceFlow.getTargetRef().getId());
        generator.writeEndObject();
        generator.writeEndArray();
//        Bounds sourceBounds = ((BPMNShape) Misc.findDiagramElement(plane,
//                sequenceFlow.getSourceRef())).getBounds();
//        Bounds targetBounds = ((BPMNShape) Misc.findDiagramElement(plane,
//                sequenceFlow.getTargetRef())).getBounds();
//        generator.writeArrayFieldStart("dockers");
//        List<Point> waypoints = ((BPMNEdge) Misc.findDiagramElement(plane,
//                sequenceFlow)).getWaypoint();
//
//        if (waypoints.size() > 1) {
//            Point waypoint = waypoints.get(0);
//            Misc.writeWaypointObject(generator,
//                    waypoint.getX() - sourceBounds.getX(),
//                    waypoint.getY() - sourceBounds.getY());
//        } else {
//            Misc.writeWaypointObject(generator,
//                    sourceBounds.getWidth() / 2,
//                    sourceBounds.getHeight() / 2);
//        }
//
//        for (int i = 1; i < waypoints.size() - 1; i++) {
//            Point waypoint = waypoints.get(i);
//            Misc.writeWaypointObject(generator,
//                    waypoint.getX(),
//                    waypoint.getY());
//        }
//
//        if (waypoints.size() > 1) {
//            Point waypoint = waypoints.get(waypoints.size() - 1);
//            Misc.writeWaypointObject(generator,
//                    waypoint.getX() - targetBounds.getX(),
//                    waypoint.getY() - targetBounds.getY());
//        } else {
//            Misc.writeWaypointObject(generator,
//                    targetBounds.getWidth() / 2,
//                    targetBounds.getHeight() / 2);
//        }
//        generator.writeEndArray();
    }

}
