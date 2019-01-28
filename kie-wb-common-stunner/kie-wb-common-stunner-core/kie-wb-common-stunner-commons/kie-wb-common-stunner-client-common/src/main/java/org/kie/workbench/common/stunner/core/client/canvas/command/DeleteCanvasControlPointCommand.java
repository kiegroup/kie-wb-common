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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasManageableControlPoints;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.util.ControlPointValidations;

import static org.kie.workbench.common.stunner.core.client.canvas.command.AddCanvasControlPointCommand.getManageableControlPoints;
import static org.kie.workbench.common.stunner.core.client.canvas.command.AddCanvasControlPointCommand.getViewControlPoints;

public class DeleteCanvasControlPointCommand extends AbstractCanvasCommand {

    private final Edge candidate;
    private final int index;
    private ControlPoint deletedControlPoint;

    public DeleteCanvasControlPointCommand(final Edge candidate,
                                           final int index) {
        this.candidate = candidate;
        this.index = index;
    }

    @Override
    public CommandResult<CanvasViolation> allow(final AbstractCanvasHandler context) {
        ControlPointValidations.checkDeleteControlPoint(getViewControlPoints(context, candidate), index);
        return CanvasCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        allow(context);
        final HasManageableControlPoints<?> view = getManageableControlPoints(context, candidate);
        this.deletedControlPoint = view.getManageableControlPoints()[index];
        // Hide control points.
        view.hideControlPoints();
        // Delete the control point at the given index.
        view.deleteControlPoint(index);
        // Show control points.
        view.showControlPoints(HasControlPoints.ControlPointType.POINTS);
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new AddCanvasControlPointCommand(candidate, deletedControlPoint, index).execute(context);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [candidate=" + getUUID(candidate) + "," +
                "index=" + index + "]";
    }
}
