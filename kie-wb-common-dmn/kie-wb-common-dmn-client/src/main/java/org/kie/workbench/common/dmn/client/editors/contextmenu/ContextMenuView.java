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

package org.kie.workbench.common.dmn.client.editors.contextmenu;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.google.gwt.dom.client.BrowserEvents;
import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelector;

@Templated
public class ContextMenuView implements ContextMenu.View,
                                        HasListSelectorControl {

    private ContextMenu presenter;

    @Inject
    @DataField("list-selector")
    private ListSelector listSelector;

    @Override
    public void init(final ContextMenu presenter) {
        this.presenter = presenter;
    }

    @PreDestroy
    public void removeDOMEventListeners() {
        DomGlobal.document.removeEventListener(BrowserEvents.MOUSEDOWN,
                                               hideContextMenuHandler(),
                                      false);
        DomGlobal.document.removeEventListener(BrowserEvents.MOUSEWHEEL,
                                      hideContextMenuHandler(),
                                      false);
    }

    @Override
    public void show() {
        listSelector.bind(this, 0, 0);
        listSelector.show();

        DomGlobal.document.addEventListener(BrowserEvents.MOUSEDOWN,
                                            hideContextMenuHandler(),
                                            false);
        DomGlobal.document.addEventListener(BrowserEvents.MOUSEWHEEL,
                                            hideContextMenuHandler(),
                                            false);
    }

    @Override
    public void hide() {
        listSelector.hide();
        removeDOMEventListeners();
    }

    private EventListener hideContextMenuHandler() {
        return event -> {
             if (!Arrays.asList(event.path).contains(getElement())) {
                 listSelector.hide();
             }
        };
    }

    @Override
    public List<ListSelectorItem> getItems(int uiRowIndex, int uiColumnIndex) {
        return presenter.getItems();
    }

    @Override
    public void onItemSelected(ListSelectorItem item) {
        ((ListSelectorTextItem) item).getCommand().execute();
        hide();
    }
}
