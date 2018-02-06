/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.kie.workbench.common.dmn.client.events.ExpressionEditorSelectedEvent;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.GridCellTuple;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayer;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.Session;

@ApplicationScoped
public class LiteralExpressionEditorDefinition implements ExpressionEditorDefinition<LiteralExpression> {

    private DMNGridPanel gridPanel;
    private DMNGridLayer gridLayer;
    private SessionManager sessionManager;
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private Event<ExpressionEditorSelectedEvent> editorSelectedEvent;

    public LiteralExpressionEditorDefinition() {
        //CDI proxy
    }

    @Inject
    public LiteralExpressionEditorDefinition(final @DMNEditor DMNGridPanel gridPanel,
                                             final @DMNEditor DMNGridLayer gridLayer,
                                             final SessionManager sessionManager,
                                             final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                             final Event<ExpressionEditorSelectedEvent> editorSelectedEvent) {
        this.gridPanel = gridPanel;
        this.gridLayer = gridLayer;
        this.sessionManager = sessionManager;
        this.sessionCommandManager = sessionCommandManager;
        this.editorSelectedEvent = editorSelectedEvent;
    }

    @Override
    public ExpressionType getType() {
        return ExpressionType.LITERAL_EXPRESSION;
    }

    @Override
    public String getName() {
        return LiteralExpression.class.getSimpleName();
    }

    @Override
    public Optional<LiteralExpression> getModelClass() {
        return Optional.of(new LiteralExpression());
    }

    @Override
    public Optional<BaseExpressionGrid> getEditor(final GridCellTuple parent,
                                                  final HasExpression hasExpression,
                                                  final Optional<LiteralExpression> expression,
                                                  final Optional<HasName> hasName,
                                                  final boolean isNested) {
        return Optional.of(new LiteralExpressionGrid(parent,
                                                     hasExpression,
                                                     expression,
                                                     hasName,
                                                     gridPanel,
                                                     gridLayer,
                                                     sessionManager,
                                                     sessionCommandManager,
                                                     editorSelectedEvent,
                                                     isNested));
    }
}
