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

package org.kie.workbench.common.dmn.client.editors.drd.contextmenu;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.uberfire.client.mvp.UberElemental;

public class DRDContextMenu {

    private View view;

    @Inject
    public DRDContextMenu(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public List<HasListSelectorControl.ListSelectorItem> getItems() {
        return Collections.singletonList(
                HasListSelectorControl.ListSelectorTextItem.build("ITEM NAME", true, () -> DomGlobal.console.log(">>>>> COMMAND: item selected!"))
        );
    }

    public void onItemSelected(HasListSelectorControl.ListSelectorItem item) {
        final HasListSelectorControl.ListSelectorTextItem li = (HasListSelectorControl.ListSelectorTextItem) item;
        li.getCommand().execute();
    }

    public interface View extends UberElemental<DRDContextMenu>, IsElement {
    }
}
