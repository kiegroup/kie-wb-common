/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import elemental2.dom.DomGlobal;
import elemental2.promise.IThenable;
import elemental2.promise.Promise;
import elemental2.promise.Promise.PromiseExecutorCallbackFn.RejectCallbackFn;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

public class Promises {

    // Reducers

    static <T, O> Promise<O> all(final List<T> objects, final Function<T, Promise<O>> f) {
        return objects.stream().map(f).reduce(resolve(), (p1, p2) -> p1.then(ignore -> p2));
    }

    static <T, O> Promise<O> reduceLazily(final O identity,
                                          final List<T> objects,
                                          final Function<T, Promise<O>> f) {
        return objects.stream()
                .<Supplier<Promise<O>>>
                        map(o -> () -> f.apply(o))
                .<Supplier<Promise<O>>>
                        reduce(() -> Promise.resolve(identity),
                               (p1, p2) -> () -> p1.get().then(ignore -> p2.get())
                )
                .get();
    }

    static <T, O> Promise<O> reduceLazilyChaining(final O identity,
                                                  final List<T> objects,
                                                  final BiFunction<Supplier<Promise<O>>, T, Promise<O>> f) {

        return objects.stream()
                .<Function<Supplier<Promise<O>>, Supplier<Promise<O>>>>
                        map(o -> next -> () -> f.apply(next, o))
                .<Function<Supplier<Promise<O>>, Supplier<Promise<O>>>>
                        reduce(next -> () -> Promise.resolve(identity),
                               (p1, p2) -> uberNext -> () -> {
                                   final Supplier<Promise<O>> next = p2.apply(uberNext);
                                   final Supplier<Promise<O>> chain = () -> next.get().then(ignore -> uberNext.get());
                                   return p1.apply(chain).get().then(ignore -> next.get());
                               }
                )
                .apply(Promises::resolve).get();
    }

    // Callers

    public static <T, S> Promise<S> promisify(final Caller<T> caller,
                                              final Function<T, S> call) {

        return promisify(caller, call, Promises::noOpOnError, null, ignore -> true);
    }

    public static <T, S, M> Promise<S> promisify(final Caller<T> caller,
                                                 final Consumer<T> call,
                                                 final BiConsumer<M, Throwable> onError) {

        return promisify(caller, call, onError, null);
    }

    public static <T, S, E, M> Promise<S> promisify(final Caller<T> caller,
                                                    final Function<T, S> call,
                                                    final BiConsumer<M, Throwable> onError,
                                                    final E rejectObject,
                                                    final Predicate<S> validate) {

        return new Promise<>((resolve, reject) -> call.apply(caller.call(
                (S response) -> {
                    if (validate.test(response)) {
                        resolve.onInvoke(response);
                    } else {
                        reject.onInvoke(rejectObject);
                    }
                },
                defaultErrorCallback(onError, rejectObject, reject))));
    }

    public static <T, S, E, M> Promise<S> promisify(final Caller<T> caller,
                                                    final Consumer<T> call,
                                                    final BiConsumer<M, Throwable> onError,
                                                    final E rejectObject) {

        return new Promise<>((resolve, reject) -> call.accept(caller.call(
                (RemoteCallback<S>) resolve::onInvoke,
                defaultErrorCallback(onError, rejectObject, reject))));
    }

    private static <E, M> ErrorCallback<M> defaultErrorCallback(final BiConsumer<M, Throwable> onError,
                                                                final E rejectObject,
                                                                final RejectCallbackFn reject) {
        return (M o, Throwable throwable) -> {
            onError.accept(o, throwable);
            reject.onInvoke(rejectObject);
            return true;
        };
    }

    public static <M> void noOpOnError(final M o, final Throwable t) {
    }

    public static <T> Promise<T> resolve() {
        return Promise.resolve((IThenable<T>) null);
    }

    @SuppressWarnings("unchecked")
    public static <V> Promise<Object> handleExceptionOr(final Object o,
                                                        final Function<V, Promise<Object>> f) {

        DomGlobal.console.info(o.getClass().getCanonicalName() + "");

        if (o instanceof RuntimeException) {
            throw (RuntimeException) o;
        } if (o instanceof Error) {
            throw (Error) o;
        } else {
            return f.apply((V) o);
        }
    }
}
