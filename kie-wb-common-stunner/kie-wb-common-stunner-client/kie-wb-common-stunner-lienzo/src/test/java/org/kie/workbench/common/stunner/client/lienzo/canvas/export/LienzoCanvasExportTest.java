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

import java.util.Optional;

import com.ait.lienzo.client.core.Context2D;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.WiresManager;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.util.ScratchPad;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresLayer;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasExportSettings;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasURLExportSettings;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.mockito.Mock;
import org.uberfire.ext.editor.commons.client.file.exports.svg.IContext2D;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.workbench.common.stunner.core.client.canvas.export.CanvasExport.URLDataType.JPG;
import static org.kie.workbench.common.stunner.core.client.canvas.export.CanvasExport.URLDataType.PNG;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoCanvasExportTest {

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private WiresCanvas canvas;

    @Mock
    private WiresCanvasView canvasView;

    @Mock
    private WiresLayer lienzoLayer;

    @Mock
    private Layer layer;

    @Mock
    private ScratchPad scratchPad;

    @Mock
    private Context2D context2D;

    private LienzoCanvasExport tested;

    @Mock
    private LienzoCanvasExport.BoundsProvider boundsProvider;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    private final String DEF_SET_ID = "DEF_SET_ID";

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private TypeDefinitionSetRegistry definitionSets;

    @Mock
    private Object defSet;

    @Mock
    private AdapterManager adapters;

    @Mock
    private DefinitionSetAdapter<Object> definitionSetAdapter;

    @Before
    public void setup() {
        when(canvasHandler.getCanvas()).thenReturn(canvas);
        when(canvas.getView()).thenReturn(canvasView);
        when(canvasView.getLayer()).thenReturn(lienzoLayer);
        when(lienzoLayer.getLienzoLayer()).thenReturn(layer);
        when(layer.uuid()).thenReturn("someLayer");
        when(layer.getScratchPad()).thenReturn(scratchPad);
        when(layer.getWidth()).thenReturn(100);
        when(layer.getHeight()).thenReturn(200);
        when(boundsProvider.compute(eq(lienzoLayer), any(CanvasExportSettings.class))).thenReturn(new int[]{100, 200});
        when(scratchPad.getContext()).thenReturn(context2D);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(DEF_SET_ID);
        when(canvasHandler.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.definitionSets()).thenReturn(definitionSets);
        when(definitionSets.getDefinitionSetById(DEF_SET_ID)).thenReturn(defSet);
        when(definitionManager.adapters()).thenReturn(adapters);
        when(adapters.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(definitionSetAdapter.getSvgNodeId(defSet)).thenReturn(Optional.of("id"));

        this.tested = new LienzoCanvasExport(boundsProvider);
    }

    @Test
    public void testToJpgImageData() {
        tested.toImageData(canvasHandler,
                           CanvasURLExportSettings.build(JPG));
        verify(context2D,
               times(1)).setFillColor(eq(LienzoCanvasExport.BG_COLOR));
        verify(context2D,
               times(1)).fillRect(eq(0d),
                                  eq(0d),
                                  eq(100d),
                                  eq(200d));
        verify(layer,
               times(1)).drawWithTransforms(eq(context2D),
                                            eq(1d),
                                            any(BoundingBox.class));
        verify(scratchPad,
               times(1)).toDataURL(eq(DataURLType.JPG),
                                   eq(1d));
        verify(scratchPad,
               times(1)).clear();
    }

    @Test
    public void testToPngImageData() {
        tested.toImageData(canvasHandler,
                           CanvasURLExportSettings.build(PNG));
        verify(context2D,
               times(1)).setFillColor(eq(LienzoCanvasExport.BG_COLOR));
        verify(context2D,
               times(1)).fillRect(eq(0d),
                                  eq(0d),
                                  eq(100d),
                                  eq(200d));
        verify(layer,
               times(1)).drawWithTransforms(eq(context2D),
                                            eq(1d),
                                            any(BoundingBox.class));
        verify(scratchPad,
               times(1)).toDataURL(eq(DataURLType.PNG),
                                   eq(1d));
        verify(scratchPad,
               times(1)).clear();
    }

    @Test
    public void testWiresLayerBoundsProviderEmpty() {
        layer = new Layer();
        when(lienzoLayer.getLienzoLayer()).thenReturn(layer);
        WiresManager.get(layer);
        LienzoCanvasExport.WiresLayerBoundsProvider provider = new LienzoCanvasExport.WiresLayerBoundsProvider();
        int[] size0 = provider.compute(lienzoLayer, CanvasExportSettings.build());
        assertEquals(25, size0[0]);
        assertEquals(25, size0[1]);
    }

    @Test
    public void testWiresLayerBoundsProvider() {
        layer = new Layer();
        when(lienzoLayer.getLienzoLayer()).thenReturn(layer);
        WiresManager wiresManager = WiresManager.get(layer);
        com.ait.lienzo.client.core.shape.wires.WiresLayer wiresLayer = wiresManager.getLayer();
        wiresLayer.add(new WiresShape(new MultiPath().rect(0, 0, 50, 50)).setLocation(new Point2D(12, 44)));
        wiresLayer.add(new WiresShape(new MultiPath().rect(0, 0, 100, 150)).setLocation(new Point2D(1, 3.4)));
        LienzoCanvasExport.WiresLayerBoundsProvider provider = new LienzoCanvasExport.WiresLayerBoundsProvider();
        int[] size0 = provider.compute(lienzoLayer, CanvasExportSettings.build());
        assertEquals(151, size0[0]);
        assertEquals(203, size0[1]);
    }

    @Test
    public void testWiresLayerBoundsProviderWithSize() {
        LienzoCanvasExport.WiresLayerBoundsProvider provider = new LienzoCanvasExport.WiresLayerBoundsProvider();
        int[] size0 = provider.compute(lienzoLayer, CanvasExportSettings.build(11, 33));
        assertEquals(36, size0[0]);
        assertEquals(58, size0[1]);
    }

    @Test
    public void testToContext2D() {
        IContext2D iContext2D = tested.toContext2D(canvasHandler, CanvasExportSettings.build());
        assertNotNull(iContext2D);
        verify(layer, times(1)).draw(any(Context2D.class));
        verify(layer, times(2)).draw();
    }
}
