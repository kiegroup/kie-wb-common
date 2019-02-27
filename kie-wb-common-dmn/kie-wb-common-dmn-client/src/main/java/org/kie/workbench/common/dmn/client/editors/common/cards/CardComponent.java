/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.common.cards;

import com.google.gwt.dom.client.Style.HasCssName;
import elemental2.dom.HTMLElement;

public interface CardComponent {

    HasCssName getIcon();

    String getTitle();

    HTMLElement getContent();

    default String getUUID() {
        return "";
    }

    /**
     * This method is invoked when the card title is changing.
     * @param newTitle represents the new title
     * @return <code>true</code> when the new title must be applied,
     * otherwise <code>false</code> and the new title must be discarded.
     */
    default boolean onTitleChanged(final String newTitle) {
        return true;
    }
}
