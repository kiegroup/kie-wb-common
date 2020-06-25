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

package org.kie.workbench.common.stunner.standalone.client.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import elemental2.promise.Promise;
import org.kie.workbench.common.stunner.bpmn.client.forms.DataTypeNamesService;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class DataTypeNamesStandaloneService implements DataTypeNamesService {

    List<String> testString = new ArrayList<>(Arrays.asList("org.standalone.Test1",
                                                            "org.standalone.Test2",
                                                            "org.standalone.Test3"));

    @Override
    public Promise<List<String>> call(final Path path) {
        return Promise.resolve(testString);
    }

    @Override
    public void add(String value, String oldValue) {

        if (testString.contains(oldValue)) {
            testString.remove(oldValue);
        }
        testString.add(value);
    }
}
