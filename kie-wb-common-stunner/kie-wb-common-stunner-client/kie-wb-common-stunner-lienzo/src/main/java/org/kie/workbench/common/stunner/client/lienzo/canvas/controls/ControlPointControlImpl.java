/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.canvas.controls;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.wires.IControlHandleList;
import com.ait.lienzo.client.core.shape.wires.IControlPointsAcceptor;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresConnectorView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

@Dependent
@Default
public class ControlPointControlImpl
        extends AbstractCanvasHandlerRegistrationControl<AbstractCanvasHandler>
        implements ControlPointControl<AbstractCanvasHandler>,
                   CanvasControl.SessionAware<EditorSession> {

    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private final IControlPointsAcceptor cpAcceptor;
    private CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;

    @Inject
    public ControlPointControlImpl(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory) {
        this.canvasCommandFactory = canvasCommandFactory;
        this.cpAcceptor = new StunnerControlPointsAcceptor(this,
                                                           this::getEdge);
    }

    @Override
    protected void doInit() {
        super.doInit();
        getCanvasView().setControlPointsAcceptor(cpAcceptor);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(final Element element) {
    }

    @Override
    public void setCommandManagerProvider(final CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.commandManagerProvider = provider;
    }

    @Override
    public void addControlPoints(Edge candidate, ControlPoint... controlPoint) {
        checkAndExecuteCommand(canvasCommandFactory.addControlPoint(candidate, controlPoint));
    }

    @Override
    public void bind(final EditorSession session) {
    }

    private CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        return commandManagerProvider.getCommandManager();
    }

    @Override
    public void moveControlPoints(final Edge candidate,
                                  final ControlPoint[] cps,
                                  final Point2D[] locations) {
        if (cps.length != locations.length) {
            throw new IllegalArgumentException("Arguments length differ for ControlPointControlImpl.moveControlPoints()");
        }
        if (cps.length > 0) {
            Command<AbstractCanvasHandler, CanvasViolation> command = null;
            if (cps.length == 1) {
                command = canvasCommandFactory.updateControlPointPosition(candidate,
                                                                          cps[0],
                                                                          locations[0]);
            } else {
                final CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation> builder =
                        new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>()
                                .forward();
                for (int i = 0; i < cps.length; i++) {
                    final Point2D location = locations[i];
                    final CanvasCommand<AbstractCanvasHandler> c =
                            canvasCommandFactory.updateControlPointPosition(candidate,
                                                                            cps[i],
                                                                            Point2D.create(location.getX(),
                                                                                           location.getY()));
                    builder.addCommand(c);
                }
                command = builder.build();
            }
            checkAndExecuteCommand(command);
        }
    }

    public void removeControlPoint(final Edge candidate,
                                   final ControlPoint controlPoint) {
        if (validateControlPointState(candidate)) {
            checkAndExecuteCommand(canvasCommandFactory.deleteControlPoint(candidate, controlPoint));
        }
    }

    public static class StunnerControlPointsAcceptor implements IControlPointsAcceptor {

        private final ControlPointControl control;
        private final Function<String, Edge> connectorSupplier;

        public StunnerControlPointsAcceptor(final ControlPointControl control,
                                            final Function<String, Edge> connectorSupplier) {
            this.control = control;
            this.connectorSupplier = connectorSupplier;
        }

        @Override
        public boolean add(final WiresConnector connector,
                           final int index,
                           final double x,
                           final double y) {
            final Edge edge = getEdge(connector);
            if (validateControlPointState(edge)) {
                control.addControlPoints(edge, ControlPoint.build(x, y, index - 1));
                return true;
            }
            return false;
        }

        @Override
        public boolean move(final WiresConnector connector,
                            final int index,
                            final double tx,
                            final double ty) {
            final Edge edge = getEdge(connector);
            if (validateControlPointState(edge)) {
                final Optional<ControlPoint> cp = getControlPointyByIndex(edge,
                                                                          index);
                cp.ifPresent(instance -> control.moveControlPoints(edge,
                                                                   new ControlPoint[]{instance},
                                                                   new Point2D[]{new Point2D(tx, ty)}));
                return cp.isPresent();
            }
            return false;
        }

        @Override
        public void update(final WiresConnector connector) {
            final Edge edge = getEdge(connector);
            if (validateControlPointState(edge)) {
                final ViewConnector viewConnector = (ViewConnector) edge.getContent();
                final List<ControlPoint> controlPoints = viewConnector.getControlPoints();
                final IControlHandleList pointHandles = connector.getPointHandles();
                final int size = pointHandles.size();
                if (size > 2) {
                    final ControlPoint[] points = new ControlPoint[size - 2];
                    final Point2D[] locations = new Point2D[size - 2];
                    for (int i = 1; i < size - 1; i++) {
                        final com.ait.lienzo.client.core.types.Point2D location =
                                pointHandles.getHandle(i).getControl().getLocation();
                        final ControlPoint controlPoint = controlPoints.get(i - 1);
                        locations[i - 1] = Point2D.create(location.getX(),
                                                          location.getY());
                        points[i - 1] = controlPoint;
                    }
                    control.moveControlPoints(edge,
                                              points,
                                              locations);
                }
            }
        }

        @Override
        public boolean delete(final WiresConnector connector,
                              final int index) {
            final Edge edge = getEdge(connector);
            if (validateControlPointState(edge)) {
                final Optional<ControlPoint> cp = getControlPointyByIndex(edge,
                                                                          index);
                cp.ifPresent(instance -> control.removeControlPoint(edge, instance));
            }
            return false;
        }

        private Edge getEdge(final WiresConnector connector) {
            return getEdge(getUUID(connector));
        }

        private Edge getEdge(final String uuid) {
            return connectorSupplier.apply(uuid);
        }

        private static String getUUID(final WiresConnector connector) {
            return connector instanceof WiresConnectorView ?
                    ((WiresConnectorView) connector).getUUID() :
                    connector.uuid();
        }
    }

    CommandManagerProvider<AbstractCanvasHandler> getCommandManagerProvider() {
        return commandManagerProvider;
    }

    private CommandResult<CanvasViolation> checkAndExecuteCommand(Command<AbstractCanvasHandler, CanvasViolation> command) {
        CommandResult<CanvasViolation> allowResult = getCommandManager().allow(canvasHandler, command);
        if (CommandUtils.isError(allowResult)) {
            return allowResult;
        }
        return getCommandManager().execute(canvasHandler, command);
    }

    private static Optional<ControlPoint> getControlPointyByIndex(final Edge edge,
                                                                  final int index) {
        ViewConnector viewConnector = (ViewConnector) edge.getContent();
        return viewConnector.getControlPoints().stream()
                .filter(cp -> Objects.nonNull(cp.getIndex()))
                .filter(cp -> cp.getIndex() == index - 1)
                .findFirst();
    }

    private static boolean validateControlPointState(final Edge edge) {
        return (Objects.nonNull(edge) && (edge.getContent() instanceof ViewConnector));
    }

    private Edge getEdge(final String uuid) {
        return canvasHandler.getGraphIndex().getEdge(uuid);
    }

    private WiresCanvas.View getCanvasView() {
        return (WiresCanvas.View) canvasHandler.getAbstractCanvas().getView();
    }

    @Override
    protected void doDestroy() {
        super.doDestroy();
        getCanvasView().setControlPointsAcceptor(IControlPointsAcceptor.ALL);
        commandManagerProvider = null;
    }
}