/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.client.shape.factory;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.v1_1.Association;
import org.kie.workbench.common.dmn.api.definition.v1_1.AuthorityRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationRequirement;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.shape.AssociationShape;
import org.kie.workbench.common.dmn.client.shape.AuthorityRequirementShape;
import org.kie.workbench.common.dmn.client.shape.InformationRequirementShape;
import org.kie.workbench.common.dmn.client.shape.KnowledgeRequirementShape;
import org.kie.workbench.common.dmn.client.shape.def.AssociationShapeDef;
import org.kie.workbench.common.dmn.client.shape.def.AuthorityRequirementShapeDef;
import org.kie.workbench.common.dmn.client.shape.def.InformationRequirementShapeDef;
import org.kie.workbench.common.dmn.client.shape.def.KnowledgeRequirementShapeDef;
import org.kie.workbench.common.dmn.client.shape.view.AssociationView;
import org.kie.workbench.common.dmn.client.shape.view.AuthorityRequirementView;
import org.kie.workbench.common.dmn.client.shape.view.InformationRequirementView;
import org.kie.workbench.common.dmn.client.shape.view.KnowledgeRequirementView;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.GlyphBuilderFactory;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.shapes.client.factory.BasicShapesFactoryImpl;
import org.kie.workbench.common.stunner.shapes.client.view.ShapeViewFactory;
import org.kie.workbench.common.stunner.shapes.def.ConnectorShapeDef;

@DMNEditor
@ApplicationScoped
public class DMNInterceptingShapeFactory extends BasicShapesFactoryImpl {

    protected DMNInterceptingShapeFactory() {
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public DMNInterceptingShapeFactory(final FactoryManager factoryManager,
                                       final @DMNEditor ShapeViewFactory shapeViewFactory,
                                       final DefinitionManager definitionManager,
                                       final GlyphBuilderFactory glyphBuilderFactor) {
        super(factoryManager,
              shapeViewFactory,
              definitionManager,
              glyphBuilderFactor);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Shape<ShapeView> build(final Object definition,
                                  final AbstractCanvasHandler context) {
        final String id = definitionManager.adapters().forDefinition().getId(definition);
        final ShapeDef proxy = getShapeDef(id);

        Optional<Shape<? extends ShapeView>> shape = Optional.empty();

        if (isAssociation(proxy)) {
            final ConnectorShapeDef<Association> shapeDef = (ConnectorShapeDef<Association>) proxy;
            final AssociationView view = ((DMNInterceptingShapeViewFactory) shapeViewFactory).association(0,
                                                                                                          0,
                                                                                                          100,
                                                                                                          100);
            shape = Optional.of(new AssociationShape(shapeDef,
                                                     view));
        } else if (isInformationRequirement(proxy)) {
            final ConnectorShapeDef<InformationRequirement> shapeDef = (ConnectorShapeDef<InformationRequirement>) proxy;
            final InformationRequirementView view = ((DMNInterceptingShapeViewFactory) shapeViewFactory).informationRequirement(0,
                                                                                                                                0,
                                                                                                                                100,
                                                                                                                                100);
            shape = Optional.of(new InformationRequirementShape(shapeDef,
                                                                view));
        } else if (isKnowledgeRequirement(proxy)) {
            final ConnectorShapeDef<KnowledgeRequirement> shapeDef = (ConnectorShapeDef<KnowledgeRequirement>) proxy;
            final KnowledgeRequirementView view = ((DMNInterceptingShapeViewFactory) shapeViewFactory).knowledgeRequirement(0,
                                                                                                                            0,
                                                                                                                            100,
                                                                                                                            100);
            shape = Optional.of(new KnowledgeRequirementShape(shapeDef,
                                                              view));
        } else if (isAuthorityRequirement(proxy)) {
            final ConnectorShapeDef<AuthorityRequirement> shapeDef = (ConnectorShapeDef<AuthorityRequirement>) proxy;
            final AuthorityRequirementView view = ((DMNInterceptingShapeViewFactory) shapeViewFactory).authorityRequirement(0,
                                                                                                                            0,
                                                                                                                            100,
                                                                                                                            100);
            shape = Optional.of(new AuthorityRequirementShape(shapeDef,
                                                              view));
        }

        return (Shape<ShapeView>) shape.orElse(build(definition,
                                                     proxy,
                                                     context));
    }

    private boolean isAssociation(final ShapeDef shapeDef) {
        return shapeDef instanceof AssociationShapeDef;
    }

    private boolean isInformationRequirement(final ShapeDef shapeDef) {
        return shapeDef instanceof InformationRequirementShapeDef;
    }

    private boolean isKnowledgeRequirement(final ShapeDef shapeDef) {
        return shapeDef instanceof KnowledgeRequirementShapeDef;
    }

    private boolean isAuthorityRequirement(final ShapeDef shapeDef) {
        return shapeDef instanceof AuthorityRequirementShapeDef;
    }
}
