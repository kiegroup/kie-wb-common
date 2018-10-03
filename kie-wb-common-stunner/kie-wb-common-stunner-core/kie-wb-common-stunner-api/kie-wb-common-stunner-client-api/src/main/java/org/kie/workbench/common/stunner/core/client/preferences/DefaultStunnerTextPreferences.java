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

package org.kie.workbench.common.stunner.core.client.preferences;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;

@Dependent
@Default
public class DefaultStunnerTextPreferences implements StunnerTextPreferences {

    private double textAlpha;

    private String textFontFamily;

    private double textFontSize;

    private String textFillColor;

    private String textStrokeColor;

    private double textStrokeWidth;

    public double getTextAlpha() {
        return textAlpha;
    }

    public void setTextAlpha(double textAlpha) {
        this.textAlpha = textAlpha;
    }

    public String getTextFontFamily() {
        return textFontFamily;
    }

    public void setTextFontFamily(String textFontFamily) {
        this.textFontFamily = textFontFamily;
    }

    public double getTextFontSize() {
        return textFontSize;
    }

    public void setTextFontSize(double textFontSize) {
        this.textFontSize = textFontSize;
    }

    public String getTextFillColor() {
        return textFillColor;
    }

    public void setTextFillColor(String textFillColor) {
        this.textFillColor = textFillColor;
    }

    public String getTextStrokeColor() {
        return textStrokeColor;
    }

    public void setTextStrokeColor(String textStrokeColor) {
        this.textStrokeColor = textStrokeColor;
    }

    public double getTextStrokeWidth() {
        return textStrokeWidth;
    }

    public void setTextStrokeWidth(double textStrokeWidth) {
        this.textStrokeWidth = textStrokeWidth;
    }
}
