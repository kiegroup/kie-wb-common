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
package org.kie.workbench.common.stunner.cm.client.shape.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.ILayoutHandler;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.tooling.nativetools.client.collection.NFastArrayList;
import org.kie.workbench.common.stunner.core.client.shape.view.HasSize;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitiveShape;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGPrimitiveFactory;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;

public class CaseManagementShapeView extends SVGShapeViewImpl implements HasSize<SVGShapeViewImpl> {

    public static final String VALUE_STAGE__CRHT = " M 163 51 H 2.6 c -0.9 0 -1.7 -0.5 -2.2 -1.2 c -0.5 -0.8 -0.5 -1.7 0 -2.5 l 12.8 -23 c 0.3 -0.5 0.2 -1.1 -0.1 -1.5 L 0.8 3.9 c -0.5 -0.8 -0.6 -1.8 -0.1 -2.6 c 0.4 -0.8 1.3 -1.3 2.2 -1.3 h 160.2 c 0.9 0 1.6 0.4 2.1 1.2 l 13.8 21.6 c 0.5 0.8 0.5 1.8 0.1 2.6 l -13.9 24.3 C 164.8 50.5 163.9 51 163 51 z";

    private final double minWidth;
    private final double minHeight;
    private final Optional<MultiPath> optDropZone;
    private double currentWidth;
    private double currentHeight;
    private String shapeLabel;

    public CaseManagementShapeView(final String name,
                                   final SVGPrimitiveShape svgPrimitive,
                                   final double width,
                                   final double height,
                                   final boolean resizable) {
        super(name, svgPrimitive, width, height, resizable);
        this.minWidth = width;
        this.minHeight = height;
        this.currentWidth = minWidth;
        this.currentHeight = minHeight;
        this.optDropZone = makeDropZone();
        this.optDropZone.ifPresent((dz) -> dz.setDraggable(false));
    }

    public void setLabel(String shapeLabel) {
        this.shapeLabel = shapeLabel;
        setTitle(shapeLabel);
        getTextViewDecorator().moveTitleToTop();
        refresh();
    }

    // TODO (Jeremy): Remove this constructor - "wrapping" the shape this way doesnt' works fine...
    public CaseManagementShapeView(SVGShapeViewImpl svgShapeView, String shapeLabel, ILayoutHandler layoutHandler) {
        super(svgShapeView.getName(),
              (SVGPrimitiveShape) svgShapeView.getPrimitive(),
              ((SVGPrimitiveShape) svgShapeView.getPrimitive()).getBoundingBox().getWidth(),
              ((SVGPrimitiveShape) svgShapeView.getPrimitive()).getBoundingBox().getHeight(),
              false);
        this.minWidth = svgShapeView.getBoundingBox().getWidth();
        this.minHeight = svgShapeView.getBoundingBox().getHeight();
        this.currentWidth = minWidth;
        this.currentHeight = minHeight;
        this.optDropZone = makeDropZone();
        this.optDropZone.ifPresent((dz) -> dz.setDraggable(false));
        setLabel(shapeLabel);
        setLayoutHandler(layoutHandler);
    }

    public double getWidth() {
        return currentWidth;
    }

    public double getHeight() {
        return currentHeight;
    }

    public void logicallyReplace(final WiresShape original, final WiresShape replacement) {

        if (original == null || replacement == null || replacement.getParent() == this) {
            return;
        }
        getChildShapes().set(getIndex(original), replacement);
        getContainer().getChildNodes().set(getNodeIndex(original.getGroup()), replacement.getGroup());

        original.setParent(null);
        replacement.setParent(this);

        getLayoutHandler().requestLayout(this);
    }

    public void addShapeAtNextIndex(final WiresShape shape) {
        addShape(shape, getChildShapes().size());
    }

    public void addShape(final WiresShape shape, final int targetIndex) {

        if (shape == null || (targetIndex < 0 || targetIndex > getChildShapes().size())) {
            return;
        }
        final List<WiresShape> existingChildShapes = new ArrayList<>();

        existingChildShapes.addAll(getChildShapes().toList());
        existingChildShapes.forEach(WiresShape::removeFromParent);

        existingChildShapes.remove(shape);
        existingChildShapes.add(targetIndex, shape);

        //call to add(..) causes ILayoutHandler to be invoked
        existingChildShapes.forEach(this::add);
    }

    public int getIndex(final WiresShape shape) {

        final NFastArrayList<WiresShape> children = getChildShapes();
        int i = 0;
        for (WiresShape child : children) {
            if (child == shape || isUUIDSame(shape, child)) {
                return i;
            }
            i++;
        }
        return i;
    }

    private boolean isUUIDSame(WiresShape shape, WiresShape child) {

        if (!(shape instanceof CaseManagementShapeView) || !(child instanceof CaseManagementShapeView)) {
            return false;
        }
        CaseManagementShapeView shapeCMView = (CaseManagementShapeView) shape;
        CaseManagementShapeView childCMView = (CaseManagementShapeView) child;

        return shapeCMView.getUUID().equals(childCMView.getUUID());
    }

    private int getNodeIndex(final Group group) {
        return getContainer().getChildNodes().toList().indexOf(group);
    }

    protected Optional<MultiPath> makeDropZone() {
        return Optional.empty();
    }

    public Optional<MultiPath> getDropZone() {
        return optDropZone;
    }

    public CaseManagementShapeView getGhost() {
        final CaseManagementShapeView ghost = createGhost();
        if (null != ghost) {
            ghost.setFillAlpha(0.5d);
            ghost.setStrokeAlpha(0.5d);
            ghost.setUUID(getUUID());
        }
        return ghost;
    }

    protected CaseManagementShapeView createGhost() {

        // TODO (Jeremy):
        // - No need to create a SVGShapeView etc here, a ghost can be any WiresShape.
        // - Do not hardcode the ghost shape here, as this class is being used for all canvas shapes generated from SVG.
        SVGPrimitiveShape mainShape = SVGPrimitiveFactory.newSVGPrimitiveShape(
                new com.ait.lienzo.client.core.shape.MultiPath(VALUE_STAGE__CRHT)
                        .setDraggable(false)
                        .setID("stage__RYU6")
                        .setX(0.00)
                        .setY(0.00)
                        .setAlpha(1.00)
                        .setListening(true)
                        .setScale(1.00, 1.00)
                        .setOffset(0.00, 0.00)
                        .setFillColor("#ffffff")
                        .setStrokeColor("#393f44")
                        .setStrokeWidth(1.50), true, null);

        SVGShapeViewImpl newView = new SVGShapeViewImpl("stage",
//                                                        SVGPrimitiveFactory.newSVGPrimitiveShape(getShape(), false, null),
                                                        mainShape,
                                                        0d,
                                                        0d,
                                                        false);

        CaseManagementShapeView ghost = new CaseManagementShapeView(newView, shapeLabel, getLayoutHandler());

        for (WiresShape wiresShape : getChildShapes()) {
            final CaseManagementShapeView shapeView = ((CaseManagementShapeView) wiresShape).getGhost();
            ghost.add(shapeView);
        }
        return ghost;
    }
}
