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
package org.kie.workbench.common.services.backend.compiler.impl.classloader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FilterClassesByPackageCollector implements Collector<String, List<String>, List<String>> {

    protected static final String DOT = ".";
    protected static final String JAVA_CLASS_EXT = ".class";
    protected static final String META_INF = "META-INF";
    protected static final String SEPARATOR = "/";
    protected static final String MAVEN_TARGET = "target/classes/";
    private String packageNameWithSlash;

    public FilterClassesByPackageCollector(String packageName) {
        this.packageNameWithSlash = packageName.replace(DOT, SEPARATOR) + SEPARATOR;//fix for the wildcard
    }

    @Override
    public Supplier<List<String>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<String>, String> accumulator() {
        return (filtered, item) -> {
            if (!item.contains(META_INF) && item.endsWith(JAVA_CLASS_EXT) && item.contains(packageNameWithSlash)) {
                filtered.add(getFiltered(item));
            }
        };
    }

    @Override
    public BinaryOperator<List<String>> combiner() {
        return (items, filtered) -> {
            filtered.addAll(items);
            return filtered;
        };
    }

    @Override
    public Function<List<String>, List<String>> finisher() {
        return (inputList)->inputList;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.IDENTITY_FINISH);
    }

    private String getFiltered(String item) {
        String one = item.substring(item.lastIndexOf(MAVEN_TARGET) + MAVEN_TARGET.length(), item.lastIndexOf(DOT));
        return one.contains(SEPARATOR) ? one.replace(SEPARATOR, DOT) : one ;
    }
}
