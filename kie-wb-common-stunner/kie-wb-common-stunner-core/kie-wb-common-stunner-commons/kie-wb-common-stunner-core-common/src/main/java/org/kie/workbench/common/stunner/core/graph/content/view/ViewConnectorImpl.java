/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.graph.content.view;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;

@Portable
public final class ViewConnectorImpl<W> implements ViewConnector<W> {

    protected W definition;
    protected Bounds bounds;
    protected Integer sourceMagnetIndex;
    protected Double sourceMagnetX;
    protected Double sourceMagnetY;
    protected Integer targetMagnetIndex;
    protected Double targetMagnetX;
    protected Double targetMagnetY;

    public ViewConnectorImpl(final @MapsTo("definition") W definition,
                             final @MapsTo("bounds") Bounds bounds) {
        this.definition = definition;
        this.bounds = bounds;
        this.sourceMagnetIndex = 0;
        this.sourceMagnetX = 0d;
        this.sourceMagnetY = 0d;
        this.targetMagnetIndex = 0;
        this.targetMagnetX = 0d;
        this.targetMagnetY = 0d;
    }

    @Override
    public W getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition(final W definition) {
        this.definition = definition;
    }

    @Override
    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(final Bounds bounds) {
        this.bounds = bounds;
    }

    @Override
    public Integer getSourceMagnetIndex() {
        return sourceMagnetIndex;
    }

    @Override
    public Double getSourceMagnetX() {
        return sourceMagnetX;
    }

    @Override
    public Double getSourceMagnetY() {
        return sourceMagnetY;
    }

    @Override
    public Integer getTargetMagnetIndex() {
        return targetMagnetIndex;
    }

    @Override
    public Double getTargetMagnetX() {
        return targetMagnetX;
    }

    @Override
    public Double getTargetMagnetY() {
        return targetMagnetY;
    }

    @Override
    public void setSourceMagnet(final Integer index,
                                final Double x,
                                final Double y) {
        this.sourceMagnetIndex = index;
        this.sourceMagnetX = x;
        this.sourceMagnetY = y;
    }

    @Override
    public void setSourceMagnetIndex(final Integer index) {
        this.sourceMagnetIndex = index;
    }

    @Override
    public void setTargetMagnet(final Integer index,
                                final Double x,
                                final Double y) {
        this.targetMagnetIndex = index;
        this.targetMagnetX = x;
        this.targetMagnetY = y;
    }

    @Override
    public void setTargetMagnetIndex(final Integer index) {
        this.targetMagnetIndex = index;
    }

    @Override
    public boolean hasValidSourceMagnetCoords() {
        return areValidMagnetCoords(sourceMagnetX.intValue(),
                                    sourceMagnetY.intValue(),
                                    sourceMagnetIndex);
    }

    @Override
    public boolean hasValidTargetMagnetCoords() {
        return areValidMagnetCoords(targetMagnetX.intValue(),
                                    targetMagnetY.intValue(),
                                    targetMagnetIndex);
    }

    private boolean areValidMagnetCoords(int magnetX,
                                         int magnetY,
                                         int magnetIndex) {
        // X and Y both 0 only valid if magnetIndex is 8
        if (magnetX == 0 && magnetY == 0) {
            if (magnetIndex == 8) {
                return true;
            } else {
                return false;
            }
        }
        // All other coordinates are valid
        return true;
    }
}
