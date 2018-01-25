package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;

public class VoidMatch<In> {

    private static class Case<T> {

        public final Class<T> when;
        public final Consumer<T> then;

        public Case(Class<T> when, Consumer<T> then) {
            this.when = when;
            this.then = then;
        }

        public Result<Void> match(Object value) {
            if (when.isAssignableFrom(value.getClass())) {
                then.accept((T) value);
                return Result.success(null);
            } else {
                return Result.failure(value.getClass().getName());
            }
        }
    }

    LinkedList<VoidMatch.Case<?>> cases = new LinkedList<>();
    Consumer<In> orElse;

    public static <In> VoidMatch<In> of(Class<In> inputType) {
        return new VoidMatch<>();
    }

    public static <In> VoidMatch<In> ofNode(Class<In> inputType) {
        return new VoidMatch<>();
    }

    public static <In> VoidMatch<In> ofEdge(Class<In> inputType) {
        return new VoidMatch<>();
    }

    public VoidMatch() {
    }

    public <Sub> VoidMatch<In> when(Class<Sub> type, Consumer<Sub> then) {
        cases.add(new VoidMatch.Case<>(type, then));
        return this;
    }

    /**
     * handle a type by throwing an error.
     * Use when the implementation is still missing, but expected to exist
     */
    public <Sub> VoidMatch<In> missing(Class<Sub> type) {
        return when(type, reportMissing(type));
    }

    public VoidMatch<In> orElse(Consumer<In> then) {
        this.orElse = then;
        return this;
    }

    public Result<Void> apply(In value) {
         return cases.stream()
                .map(c -> c.match(value))
                .filter(Result::isSuccess)
                .findFirst()
                .orElse(applyFallback(value));
    }

    private Result<Void> applyFallback(In value) {
        if (orElse == null) {
            return Result.failure(value == null ? "Null" : value.getClass().getName());
        } else {
            orElse.accept(value);
            return Result.success(null);
        }
    }

    static <T> Consumer<T> reportMissing(Class<?> expectedClass) {
        return t -> {
            throw new UnsupportedOperationException(
                    "Not yet implemented: " +
                            Optional.ofNullable(t)
                                    .map(o -> o.getClass().getCanonicalName())
                                    .orElse("null -- expected " + expectedClass.getCanonicalName()));
        };
    }
}