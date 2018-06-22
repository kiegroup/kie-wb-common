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

package org.kie.workbench.common.stunner.cm.client.shape.factory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.cm.client.shape.CaseManagementShape;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgDiagramShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgNullShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgSubprocessShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgTaskShapeDef;
import org.kie.workbench.common.stunner.cm.client.wires.HorizontalStackLayoutManager;
import org.kie.workbench.common.stunner.cm.client.wires.VerticalStackLayoutManager;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFactory;
import org.kie.workbench.common.stunner.svg.client.shape.SVGShape;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeFactory;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;

@Dependent
public class CaseManagementShapeDefFactory implements ShapeDefFactory<BPMNDefinition, CaseManagementSvgShapeDef, Shape> {

    private final SVGShapeFactory svgShapeFactory;
    private final CaseManagementShapeDefFunctionalFactory<BPMNDefinition, CaseManagementSvgShapeDef, Shape> functionalFactory;

    // CDI Proxy
    @SuppressWarnings("unused")
    protected CaseManagementShapeDefFactory() {
        this(null, null);
    }

    @Inject
    public CaseManagementShapeDefFactory(final SVGShapeFactory svgShapeFactory,
                                         final @CaseManagementEditor CaseManagementShapeDefFunctionalFactory
                                                 <BPMNDefinition, CaseManagementSvgShapeDef, Shape> functionalFactory) {
        this.svgShapeFactory = svgShapeFactory;
        this.functionalFactory = functionalFactory;
    }

    @PostConstruct
    public void init() {
        functionalFactory
                .set(CaseManagementSvgDiagramShapeDef.class,
                     this::newDiagramShape)
                .set(CaseManagementSvgTaskShapeDef.class,
                     this::newStageChildShape)
                .set(CaseManagementSvgSubprocessShapeDef.class,
                     this::newSubprocessDerivedShape)
                .set(CaseManagementSvgNullShapeDef.class,
                     this::newStageChildShape);
    }

    @Override
    public Shape newShape(final BPMNDefinition instance, final CaseManagementSvgShapeDef svgShapeDef) {
        return functionalFactory.newShape(instance, svgShapeDef);
    }

    @SuppressWarnings("unchecked")
    private Shape newDiagramShape(final Object instance, final CaseManagementSvgShapeDef svgShapeDef) {

        SVGShape shape = svgShapeFactory.newShape(instance, svgShapeDef);
        SVGShapeViewImpl svgShapeView = (SVGShapeViewImpl) shape.getShapeView();
        svgShapeView.setLayoutHandler(new HorizontalStackLayoutManager());

        return new CaseManagementShape(svgShapeView);
    }

    @SuppressWarnings("unchecked")
    private Shape newSubprocessDerivedShape(final Object instance, final CaseManagementSvgShapeDef svgShapeDef) {

        SVGShape shape = svgShapeFactory.newShape(instance, svgShapeDef);
        SVGShapeViewImpl svgShapeView = (SVGShapeViewImpl) shape.getShapeView();

        if (instance instanceof AdHocSubprocess) {
            svgShapeView.setLayoutHandler(new VerticalStackLayoutManager());
        }
        return new CaseManagementShape(svgShapeView);
    }

    @SuppressWarnings("unchecked")
    private Shape newStageChildShape(final Object instance, final CaseManagementSvgShapeDef svgShapeDef) {

        SVGShape shape = svgShapeFactory.newShape(instance, svgShapeDef);
        SVGShapeViewImpl svgShapeView = (SVGShapeViewImpl) shape.getShapeView();

        return new CaseManagementShape(svgShapeView);
    }
}