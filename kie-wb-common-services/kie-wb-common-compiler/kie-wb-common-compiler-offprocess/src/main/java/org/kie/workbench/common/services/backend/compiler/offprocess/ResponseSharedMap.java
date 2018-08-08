/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.backend.compiler.offprocess;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.workbench.common.services.backend.compiler.CompilationResponse;

/***
 * Map to hold the Response of the compilations using the UUID key to retrieve and store
 */
public class ResponseSharedMap {

    private static Map<String, CompilationResponse> map = new ConcurrentHashMap();

    public static CompilationResponse getResponse(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return null;
        }
    }

    public static void removeResponse(String key) {
        map.remove(key);
    }

    public static void addResponse(String key, CompilationResponse res) {
        if (!map.containsKey(key)) {
            map.put(key, res);
        }
    }

    public static void purgeAll(){
        map.clear();
    }
}
