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

import com.ait.lienzo.client.core.shape.wires.ILayoutHandler;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresUtils;
import org.kie.workbench.common.stunner.cm.client.shape.CaseManagementShape;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgDiagramShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgNullShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgSubprocessShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgTaskShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;
import org.kie.workbench.common.stunner.cm.client.wires.HorizontalStackLayoutManager;
import org.kie.workbench.common.stunner.cm.client.wires.VerticalStackLayoutManager;
import org.kie.workbench.common.stunner.cm.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFactory;
import org.kie.workbench.common.stunner.svg.client.shape.SVGShape;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeFactory;

@Dependent
public class CaseManagementShapeDefFactory implements ShapeDefFactory<BPMNDefinition, CaseManagementSvgShapeDef, Shape> {

    private final SVGShapeFactory svgShapeFactory;
    private final CaseManagementShapeDefFunctionalFactory<BPMNDefinition, CaseManagementSvgShapeDef, Shape> functionalFactory;

    private static final String CM_STAGE = "stage";
    private static final String CM_TASK = "task";
    private static final String CM_SUBCASE = "subcase";
    private static final String CM_SUBPROCESS = "subprocess";

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
        CaseManagementShapeView cmShapeView = (CaseManagementShapeView) shape.getShapeView();
        cmShapeView.setLabel("");
        cmShapeView.setLayoutHandler(new HorizontalStackLayoutManager());
        cmShapeView.setUserData(new WiresUtils.UserData());
        return new CaseManagementShape(cmShapeView);
    }

    @SuppressWarnings("unchecked")
    private Shape newSubprocessDerivedShape(final Object instance, final CaseManagementSvgShapeDef svgShapeDef) {
        SVGShape shape = svgShapeFactory.newShape(instance, svgShapeDef);
        CaseManagementShapeView cmShapeView = (CaseManagementShapeView) shape.getShapeView();
        cmShapeView.setUserData(new WiresUtils.UserData());
        String label = null;
        ILayoutHandler layoutHandler = null;
        if (instance instanceof AdHocSubprocess) {
            label = CM_STAGE;
            layoutHandler = new VerticalStackLayoutManager();
        } else {
            label = instance instanceof ReusableSubprocess ? CM_SUBCASE : CM_SUBPROCESS;
        }
        cmShapeView.setLabel(label);
        cmShapeView.setLayoutHandler(layoutHandler);
        return new CaseManagementShape(cmShapeView);
    }

    @SuppressWarnings("unchecked")
    private Shape newStageChildShape(final Object instance, final CaseManagementSvgShapeDef svgShapeDef) {
        SVGShape shape = svgShapeFactory.newShape(instance, svgShapeDef);
        CaseManagementShapeView cmShapeView = (CaseManagementShapeView) shape.getShapeView();
        cmShapeView.setLabel(CM_TASK);
        cmShapeView.setUserData(new WiresUtils.UserData());
        return new CaseManagementShape(cmShapeView);
    }
}