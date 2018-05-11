package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.List;

import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.dd.dc.Point;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.MagnetConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertEquals;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.dc;

public class SequenceFlowPropertyWriterTest {

    @Test
    public void setConnectionMagnetsNullLocation() {
        PropertyWriterFactory propertyWriter = new PropertyWriterFactory();

        org.eclipse.bpmn2.SequenceFlow sequenceFlow = bpmn2.createSequenceFlow();
        sequenceFlow.setId("SEQ");
        SequenceFlowPropertyWriter p = propertyWriter.of(sequenceFlow);
        BoundsImpl bounds = BoundsImpl.build(0, 0, 1000, 1000);

        ScriptTask s = bpmn2.createScriptTask();
        s.setId("SOURCE");
        BoundsImpl sb = BoundsImpl.build(10, 10, 50, 50);
        PropertyWriter source = propertyWriter.of(s);
        source.setBounds(sb);

        ScriptTask t = bpmn2.createScriptTask();
        t.setId("TARGET");
        BoundsImpl tb = BoundsImpl.build(100, 100, 20, 20);
        PropertyWriter target = propertyWriter.of(t);
        target.setBounds(tb);

        p.setSource(source);
        p.setTarget(target);

        // magnets have null location
        ViewConnectorImpl<SequenceFlow> connector = new ViewConnectorImpl<>(new SequenceFlow(), bounds);
        connector.setSourceConnection(new MagnetConnection.Builder().build());
        connector.setTargetConnection(new MagnetConnection.Builder().build());
        p.setConnection(connector);

        BPMNEdge edge = p.getEdge();
        List<Point> expected = asList(
                pointOf((sb.getLowerRight().getX() - sb.getUpperLeft().getX()) / 2 + sb.getUpperLeft().getX(),
                        (sb.getLowerRight().getY() - sb.getUpperLeft().getY()) / 2 + sb.getUpperLeft().getY()),
                pointOf((tb.getLowerRight().getX() - tb.getUpperLeft().getX()) / 2 + tb.getUpperLeft().getX(),
                        (tb.getLowerRight().getY() - tb.getUpperLeft().getY()) / 2 + tb.getUpperLeft().getY()));
        List<Point> waypoints = edge.getWaypoint();

        assertPointsEqual(expected, waypoints);
    }

    private static void assertPointsEqual(List<Point> expected, List<Point> given) {
        assertEquals(expected.size(), given.size());
        for (int i = 0; i < expected.size(); i++) {
            Point pe = expected.get(i);
            Point pg = given.get(i);
            if (pe.getX() != pg.getX() || pe.getY() != pg.getY()) {
                assertEquals(expected, given); // if any of those differ, call usual assertions, just to we get the usual error message
            }
        }
    }

    private Point pointOf(double x, double y) {
        Point point = dc.createPoint();
        point.setX((float) x);
        point.setY((float) y);
        return point;
    }
}