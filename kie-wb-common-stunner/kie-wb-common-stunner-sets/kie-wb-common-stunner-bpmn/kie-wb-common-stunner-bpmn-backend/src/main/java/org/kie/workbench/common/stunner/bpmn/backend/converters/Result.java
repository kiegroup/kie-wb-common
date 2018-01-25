package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

public interface Result<T> {

    boolean isFailure();
    boolean isIgnored();
    boolean isSuccess();
    default boolean nonFailure() { return !isFailure(); }
    default boolean notIgnored() { return !isIgnored(); }

    default T value() {
        return asSuccess().value();
    }

    default void ifSuccess(Consumer<T> consumer) {
        if (isSuccess()) {
            consumer.accept(asSuccess().value());
        }
    }

    default void ifFailure(Consumer<String> consumer) {
        if (isFailure()) {
            consumer.accept(asFailure().reason());
        }
    }


    Success<T> asSuccess();

    Failure<T> asFailure();

    static <R> Result<R> of(R value) {
        return new Success<>(value);
    }

    static <R> Result<R> success(R value) {
        return new Success<>(value);
    }

    static <R> Result<R> failure(String reason) {
        return new Failure<>(reason);
    }

    static <U> Result<U> ignored(String reason) {
        return new Ignored<>(reason);
    }

    class Success<T> implements Result<T> {

        private final T value;

        Success(T value) {
            this.value = value;
        }

        public T value() {
            return value;
        }

        public Success<T> asSuccess() {
            return this;
        }

        public Ignored<T> asIgnored() {
            throw new ClassCastException("Could not convert Success to Ignored");
        }

        public Failure<T> asFailure() {
            throw new ClassCastException("Could not convert Success to Failure");
        }

        @Override
        public boolean isSuccess() {
            return true;
        }
        public boolean isIgnored() { return false; }
        public boolean isFailure() { return false; }
    }

    class Ignored<T> implements Result<T> {

        private final String reason;

        Ignored(String reason) {
            this.reason = reason;
        }

        public String reason() {
            return reason;
        }

        public Success<T> asSuccess() {
            throw new NoSuchElementException(reason);
        }

        public Ignored<T> asIgnored() {
            return this;
        }

        public Failure<T> asFailure() {
            throw new ClassCastException("Could not convert Ignored to Success");
        }

        @Override
        public boolean isSuccess() {
            return false;
        }
        public boolean isIgnored() { return true; }
        public boolean isFailure() { return false; }

    }

    class Failure<T> implements Result<T> {

        private final String reason;

        Failure(String reason) {
            this.reason = reason;
        }

        public String reason() {
            return reason;
        }

        public Success<T> asSuccess() {
            throw new NoSuchElementException(reason);
        }

        public Ignored<T> asIgnored() {
            throw new ClassCastException("Could not convert Failure to Ignored");
        }

        public Failure<T> asFailure() {
            return this;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }
        public boolean isIgnored() { return false; }
        public boolean isFailure() { return true; }

    }
}


