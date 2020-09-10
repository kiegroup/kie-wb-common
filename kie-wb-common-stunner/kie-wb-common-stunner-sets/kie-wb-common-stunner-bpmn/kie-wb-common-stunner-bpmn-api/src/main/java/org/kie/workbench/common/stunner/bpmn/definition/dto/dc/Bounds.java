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

package org.kie.workbench.common.stunner.bpmn.definition.dto.dc;

import javax.xml.bind.annotation.XmlAttribute;

public class Bounds {

    @XmlAttribute
    private double height;
    @XmlAttribute
    private double width;
    @XmlAttribute
    private double x;
    @XmlAttribute
    private double y;

    public Bounds() {

    }

    public Bounds(double height, double width, double x, double y) {
        this.height = height;
        this.width = width;
        this.x = x;
        this.y = y;
    }

    public Bounds(org.kie.workbench.common.stunner.core.graph.content.Bounds bounds) {
        this.height = bounds.getHeight();
        this.width = bounds.getWidth();
        this.x = bounds.getX();
        this.y = bounds.getY();
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Bounds{" +
                "height=" + height +
                ", width=" + width +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
