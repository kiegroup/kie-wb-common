/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.imports.listitems;

import java.io.IOException;

import com.google.gwt.text.shared.Renderer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultClassNamesRendererTest {

    private static final String EMPTY_STRING = "";
    private static final String TEST_STRING = "test string";

    @Test
    public void render() {
        Renderer<String> renderer = new DefaultClassNamesRenderer();
        assertEquals(EMPTY_STRING, renderer.render(null));
        assertEquals(TEST_STRING, renderer.render(TEST_STRING));
    }

    @Test
    public void testRender() throws IOException {
        Renderer<String> renderer = new DefaultClassNamesRenderer();

        Appendable appendable = mock(Appendable.class);
        renderer.render(null, appendable);
        verify(appendable).append(EMPTY_STRING);

        renderer.render(TEST_STRING, appendable);
        verify(appendable).append(TEST_STRING);
    }
}