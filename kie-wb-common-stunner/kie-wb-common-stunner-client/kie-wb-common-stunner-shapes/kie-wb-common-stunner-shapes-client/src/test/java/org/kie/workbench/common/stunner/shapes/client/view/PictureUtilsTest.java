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
package org.kie.workbench.common.stunner.shapes.client.view;

import com.ait.lienzo.client.core.image.ImageProxy;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwtmockito.WithClassesToStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@WithClassesToStub({ImageElement.class})
@RunWith(LienzoMockitoTestRunner.class)
public class PictureUtilsTest {

    @Mock
    private Picture picture;

    @Mock
    private ImageProxy imageProxy;

    @Mock
    private ImageElement imageElement;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        //Fake image loading...
        when(picture.getImageProxy()).thenReturn(imageProxy);
        when(imageProxy.getImage()).thenReturn(imageElement);
    }

    @Test
    public void checkDestructionRemovesResourcesFromDOMWhenPictureIsLoaded() {
        when(picture.isLoaded()).thenReturn(true);

        PictureUtils.tryDestroy(picture,
                                PictureUtils::retryDestroy);

        verify(picture).removeFromParent();
        verify(imageElement).removeFromParent();
    }

    @Test
    public void checkDestructionRemovesResourcesFromDOMWhenPictureLoadedIsDelayed() {
        when(picture.isLoaded()).thenReturn(false);

        PictureUtils.tryDestroy(picture,
                                (p) -> {
                                    when(picture.isLoaded()).thenReturn(true);
                                    PictureUtils.retryDestroy(p);
                                });

        verify(picture).removeFromParent();
        verify(imageElement).removeFromParent();
    }
}
