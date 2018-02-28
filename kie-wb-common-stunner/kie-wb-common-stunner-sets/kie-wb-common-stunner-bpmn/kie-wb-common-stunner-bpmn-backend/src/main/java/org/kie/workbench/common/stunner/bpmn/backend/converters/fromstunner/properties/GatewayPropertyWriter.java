package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.List;

import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.GatewayDirection;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

public class GatewayPropertyWriter extends PropertyWriter {

    private final Gateway gateway;
    private String defaultGatewayId;

    public GatewayPropertyWriter(Gateway gateway, VariableScope variableScope) {
        super(gateway, variableScope);
        this.gateway = gateway;
    }

    public void setDefaultRoute(String defaultRouteExpression) {
        if (defaultRouteExpression == null) {
            return;
        }
        CustomAttribute.dg.of(gateway).set(defaultRouteExpression);

        String[] split = defaultRouteExpression.split(" : ");
        this.defaultGatewayId = (split.length == 1) ? split[0] : split[1];
    }

    public void setSource(BasePropertyWriter source) {
        setDefaultGateway(source);
    }

    public void setTarget(BasePropertyWriter target) {
        setDefaultGateway(target);
    }

    private void setDefaultGateway(BasePropertyWriter propertyWriter) {
        if (propertyWriter.getElement().getId().equals(defaultGatewayId)) {
            if (gateway instanceof ExclusiveGateway) {
                ((ExclusiveGateway) gateway).setDefault((SequenceFlow) propertyWriter.getElement());
            } else if (gateway instanceof InclusiveGateway) {
                ((InclusiveGateway) gateway).setDefault((SequenceFlow) propertyWriter.getElement());
            }
        }
    }

    public void setGatewayDirection(Node n) {
        long incoming = countEdges(n.getInEdges());
        long outgoing = countEdges(n.getOutEdges());

        if (incoming <= 1 && outgoing > 1) {
            gateway.setGatewayDirection(GatewayDirection.DIVERGING);
        } else if (incoming > 1 && outgoing <= 1) {
            gateway.setGatewayDirection(GatewayDirection.CONVERGING);
        }
        // temp. removing support for mixed gateway direction (not supported by runtime yet)
//                else if (incoming > 1 && outgoing > 1) {
//                    gateway.setGatewayDirection(GatewayDirection.MIXED);
//                }
//                else if (incoming == 1 && outgoing == 1) {
//                    // this handles the 1:1 case of the diverging gateways
//                }
        else {
            gateway.setGatewayDirection(GatewayDirection.UNSPECIFIED);
        }
    }

    private long countEdges(List<Edge> inEdges) {
        return inEdges.stream()
                .filter(e -> e.getContent() instanceof ViewConnector)
                .count();
    }
}
