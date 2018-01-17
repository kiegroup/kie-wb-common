package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class Match<In, Out> {

    private final Class<?> outputType;

    private static class MatchCase<T, R> {

        public final Class<T> when;
        public final Function<T, R> then;

        public MatchCase(Class<T> when, Function<T, R> then) {
            this.when = when;
            this.then = then;
        }

        public Result<R> match(Object value) {
            return when.isAssignableFrom(value.getClass()) ?
                    Result.of(then.apply((T) value)) : Result.empty(value.getClass().getName());
        }
    }

    public interface Result<T> {

        T value();

        boolean isEmpty();

        default boolean nonEmpty() {
            return !isEmpty();
        }

        static <R> Result<R> of(R value) {
            return new NonEmptyResult<>(value);
        }

        static <R> Result<R> empty(String type) {
            return new EmptyResult<R>(type);
        }
    }

    private static class NonEmptyResult<T> implements Result<T> {

        private final T value;

        public NonEmptyResult(T value) {
            Objects.requireNonNull(value);
            this.value = value;
        }

        @Override
        public T value() {
            return value;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    private static class EmptyResult<T> implements Result<T> {

        private final String reason;

        public EmptyResult(String reason) {
            this.reason = reason;
        }

        @Override
        public T value() {
            throw new NoSuchElementException("Could not match value of type " + reason);
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }

    LinkedList<MatchCase<?, Out>> cases = new LinkedList<>();
    Function<In, Out> orElse;

    public static <In, Out> Match<In, Node<? extends View<? extends Out>, ?>> ofNode(Class<In> inputType, Class<Out> outputType) {
        return new Match<>(outputType);
    }

    public static <In, Out> Match<In, Edge<? extends View<? extends Out>, ?>> ofEdge(Class<In> inputType, Class<Out> outputType) {
        return new Match<>(outputType);
    }

    public Match(Class<?> outputType) {
        this.outputType = outputType;
    }

    public <Sub> Match<In, Out> when(Class<Sub> type, Function<Sub, Out> then) {
        cases.add(new MatchCase<>(type, then));
        return this;
    }

    public Match<In, Out> orElse(Function<In, Out> then) {
        this.orElse = then;
        return this;
    }

    public Result<Out> apply(In value) {
        return cases.stream()
                .map(c -> c.match(value))
                .filter(Result::nonEmpty)
                .findFirst()
                .orElse(applyFallback(value));
    }

    private Result<Out> applyFallback(In value) {
        if (orElse == null) {
            return Result.empty(value == null ? "Null" : value.getClass().getName());
        } else {
            return Result.of(orElse.apply(value));
        }
    }
}
