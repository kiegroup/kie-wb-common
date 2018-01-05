package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;

public class Match<In,Out> {
    private static class MatchCase<T,R> {
        public final Class<T> when;
        public final Function<T, R> then;

        public MatchCase(Class<T> when, Function<T, R> then) {
            this.when = when;
            this.then = then;
        }

        public Optional<R> match(Object value) {
            return when.isAssignableFrom(value.getClass()) ?
                Optional.ofNullable(then.apply((T) value)) : Optional.empty();
        }
    }

    LinkedList<MatchCase<?, Out>> cases = new LinkedList<>();
    Function<In, Out> orElse = (x -> null);

    public static <In,Out> Match<In, Node<View<Out>, ?>> ofNode(Class<In> inputType, Class<Out> outputType) {
        return new Match<>();
    }
    public static <In,Out> Match<In, Edge<View<Out>, ?>> ofEdge(Class<In> inputType, Class<Out> outputType) {
        return new Match<>();
    }
    public Match()  {}

    public <Sub> Match<In, Out> when(Class<Sub> type, Function<Sub,Out> then) {
        cases.add(new MatchCase<>(type, then));
        return this;
    }

    public Match<In, Out> orElse(Function<In,Out> then) {
        this.orElse = then;
        return this;
    }

    public Optional<Out> apply(In value) {
        return cases.stream()
                .map(c -> c.match(value))
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.ofNullable(orElse.apply(value)));
    }
}
