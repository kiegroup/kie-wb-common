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

package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class NodeMatch<In, Out> {

    private final Class<?> outputType;
    LinkedList<NodeMatch.Case<?, Out>> cases = new LinkedList<>();
    Function<Node<? extends View<? extends BPMNViewDefinition>, ?>, Out> orElse;

    public NodeMatch(Class<?> outputType) {
        this.outputType = outputType;
    }

    public static <In, Out> NodeMatch<Node<? extends View<? extends In>, ?>, Out> fromNode(Class<In> inputType, Class<Out> outputType) {
        return new NodeMatch<>(outputType);
    }

    static <T, U> Function<T, Result<U>> reportMissing(Class<?> expectedClass) {
        return t ->
                Result.failure(
                        "Not yet implemented: " +
                                Optional.ofNullable(t)
                                        .map(o -> o.getClass().getCanonicalName())
                                        .orElse("null -- expected " + expectedClass.getCanonicalName()));
    }

    static <T, U> Function<T, Result<U>> ignored(Class<?> expectedClass) {
        return t ->
                Result.ignored(
                        "Ignored: " +
                                Optional.ofNullable(t)
                                        .map(o -> o.getClass().getCanonicalName())
                                        .orElse("null -- expected " + expectedClass.getCanonicalName()));
    }

    public <Sub> NodeMatch<In, Out> when(Class<Sub> type, Function<Node<View<Sub>, ?>, Out> then) {
        Function<Node<View<Sub>, ?>, Result<Out>> thenWrapped = sub -> Result.of(then.apply(sub));
        return when_(type, thenWrapped);
    }

    public <Sub> NodeMatch<In, Out> when_(Class<Sub> type, Function<Node<View<Sub>, ?>, Result<Out>> then) {
        cases.add(new NodeMatch.Case<>(type, then));
        return this;
    }

    /**
     * handle a type by throwing an error.
     * Use when the implementation is still missing, but expected to exist
     */
    public <Sub> NodeMatch<In, Out> missing(Class<Sub> type) {
        return when_(type, reportMissing(type));
    }

    public <Sub> NodeMatch<In, Out> ignore(Class<Sub> type) {
        return when_(type, ignored(type));
    }

    public NodeMatch<In, Out> orElse(Function<Node<? extends View<? extends BPMNViewDefinition>, ?>, Out> then) {
        this.orElse = then;
        return this;
    }

    public Result<Out> apply(Node<? extends View<? extends BPMNViewDefinition>, ?> value) {
        return cases.stream()
                .map(c -> c.match(value))
                .filter(Result::nonFailure)
                .findFirst()
                .orElse(applyFallback(value));
    }

    private Result<Out> applyFallback(Node<? extends View<? extends BPMNViewDefinition>, ?> value) {
        if (orElse == null) {
            return Result.failure(value == null ? "Null" : value.getContent().getDefinition().getClass().getName());
        } else {
            return Result.of(orElse.apply(value));
        }
    }

    private static class Case<T, R> {

        public final Class<T> when;
        public final Function<Node<View<T>, ?>, Result<R>> then;

        public Case(Class<T> when, Function<Node<View<T>, ?>, Result<R>> then) {
            this.when = when;
            this.then = then;
        }

        public Result<R> match(Node<? extends View<? extends BPMNViewDefinition>, ?> value) {
            BPMNViewDefinition definition = value.getContent().getDefinition();
            return when.isAssignableFrom(definition.getClass()) ?
                    then.apply((Node) value) : Result.failure(value.getClass().getName());
        }
    }
}
