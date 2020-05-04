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
package org.kie.workbench.common.dmn.client.widgets.codecompletion;

import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import elemental2.core.JsRegExp;
import jsinterop.base.Js;

public class Populator {

    protected static Logger LOGGER = Logger.getLogger(Populator.class.getName());

    public static final String FEEL_LANGUAGE_ID = "feel-language";
    public static final String FEEL_THEME_ID = "feel-theme";
    protected static final String INSERT_TEXT_RULES_KEY = "insertTextRules";
    protected static final String LABEL_KEY = "label";
    protected static final String INSERT_TEXT_KEY = "insertText";
    protected static final String KIND_KEY = "kind";

    JavaScriptObject makeJavaScriptObject(final String property,
                                          final JSONValue value) {
        final JSONObject jsonObject = makeJSONObject();
        jsonObject.put(property, value);
        return jsonObject.getJavaScriptObject();
    }

    JsRegExp makeRegExp(final String pattern) {
        return new JsRegExp(pattern);
    }

    JSONArray makeJSONArray() {
        return new JSONArray();
    }

    JSONBoolean makeJSONBoolean(final boolean value) {
        return JSONBoolean.getInstance(value);
    }

    JSONString makeJSONString(final String value) {
        return new JSONString(value);
    }

    JSONValue makeJSONNumber(final int value) {
        return new JSONNumber(value);
    }

    JSONObject makeJSONObject(final Object obj) {
        return new JSONObject(Js.uncheckedCast(obj));
    }

    JSONObject makeJSONObject() {
        return new JSONObject();
    }

    void push(final JSONArray jsonArray,
              final JSONValue jsonValue) {
        jsonArray.set(jsonArray.size(), jsonValue);
    }
}
