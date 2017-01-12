/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.UpdateElementPropertyValueCommand;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public final class UpdateElementPropertyCommand extends AbstractCanvasGraphCommand {

    private final Element element;
    private final String propertyId;
    private final Object value;

    public UpdateElementPropertyCommand( final Element element,
                                         final String propertyId,
                                         final Object value ) {
        this.element = element;
        this.propertyId = propertyId;
        this.value = value;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand( final AbstractCanvasHandler context ) {
        return new UpdateElementPropertyValueCommand( ( Node ) element,
                                                      propertyId,
                                                      value );
    }

    @Override
    protected AbstractCanvasCommand newCanvasCommand( final AbstractCanvasHandler context ) {
        return new UpdateCanvasElementPropertyCommand( element );
    }
}
