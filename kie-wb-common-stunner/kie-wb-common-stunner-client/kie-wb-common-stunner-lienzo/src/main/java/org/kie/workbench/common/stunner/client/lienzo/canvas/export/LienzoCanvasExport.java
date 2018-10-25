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

package org.kie.workbench.common.stunner.client.lienzo.canvas.export;

import javax.enterprise.context.ApplicationScoped;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.widget.panel.Bounds;
import com.ait.lienzo.shared.core.types.DataURLType;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoLayer;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoLayerUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasExport;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasExportSettings;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasURLExportSettings;
import org.uberfire.ext.editor.commons.client.file.exports.svg.Context2DFactory;
import org.uberfire.ext.editor.commons.client.file.exports.svg.IContext2D;
import org.uberfire.ext.editor.commons.client.file.exports.svg.SvgExportSettings;

@ApplicationScoped
public class LienzoCanvasExport implements CanvasExport<AbstractCanvasHandler> {

    public static final String BG_COLOR = "#FFFFFF";
    public static final int PADDING = 25;
    private final BoundsProvider boundsProvider;

    public LienzoCanvasExport() {
        this(new WiresLayerBoundsProvider());
    }

    LienzoCanvasExport(final BoundsProvider boundsProvider) {
        this.boundsProvider = boundsProvider;
    }

    @Override
    public IContext2D toContext2D(final AbstractCanvasHandler canvasHandler,
                                  final CanvasExportSettings settings) {
        final LienzoLayer layer = getLayer(canvasHandler);
        final com.ait.lienzo.client.core.shape.Layer lienzoLayer = layer.getLienzoLayer();
        final int[] bounds = boundsProvider.compute(layer, settings);
        final IContext2D svgContext2D = Context2DFactory.create(new SvgExportSettings(bounds[0],
                                                                                      bounds[1],
                                                                                      lienzoLayer.getContext()));
        lienzoLayer.draw();
        lienzoLayer.draw(new Context2D(new DelegateNativeContext2D(svgContext2D,
                                                                   canvasHandler)));
        lienzoLayer.draw();
        return svgContext2D;
    }

    @Override
    public String toImageData(final AbstractCanvasHandler canvasHandler,
                              final CanvasURLExportSettings settings) {
        final LienzoLayer layer = getLayer(canvasHandler);
        final int[] bounds = boundsProvider.compute(layer, settings);
        return LienzoLayerUtils.layerToDataURL(layer,
                                               getDataType(settings.getUrlDataType()),
                                               bounds[0],
                                               bounds[1],
                                               BG_COLOR);
    }

    public interface BoundsProvider {

        public int[] compute(LienzoLayer layer,
                             CanvasExportSettings settings);
    }

    public static class WiresLayerBoundsProvider implements BoundsProvider {

        @Override
        public int[] compute(final LienzoLayer layer,
                             final CanvasExportSettings settings) {
            final int[] result = new int[2];
            if (settings.hasSize()) {
                result[0] = settings.getWide();
                result[1] = settings.getHigh();
            } else {
                final Bounds bounds = LienzoLayerUtils.computeBounds(layer);
                result[0] = (int) (bounds.getX() + bounds.getWidth());
                result[1] = (int) (bounds.getY() + bounds.getHeight());
            }
            return new int[]{result[0] + PADDING, result[1] + PADDING};
        }
    }

    private static DataURLType getDataType(final CanvasExport.URLDataType type) {
        switch (type) {
            case JPG:
                return DataURLType.JPG;
            case PNG:
                return DataURLType.PNG;
        }
        throw new UnsupportedOperationException("Export data type [" + type.name() + "] not supported ");
    }

    private static LienzoLayer getLayer(final AbstractCanvasHandler canvasHandler) {
        return ((WiresCanvas) canvasHandler.getCanvas()).getView().getLayer();
    }
}
