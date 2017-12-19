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

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import elemental2.promise.Promise;
import org.jboss.errai.common.client.api.Caller;

public class Promises {

    static <T> Promise<T> reduce(final T identity, final Stream<Promise<T>> promises) {
        return promises.reduce(Promise.resolve(identity), (p, q) -> p.then(b -> q));
    }

    public static <T, S, E> Promise<S> promisify(final Caller<T> caller, final Function<T, S> call, final E rejectObject, Predicate<S> validate) {
        return new Promise<>((resolve, reject) -> {
            call.apply(caller.call((S resolveObject) -> {
                if (validate.test(resolveObject)) {
                    resolve.onInvoke(resolveObject);
                } else {
                    reject.onInvoke(rejectObject);
                }
            }, (message, throwable) -> {
                reject.onInvoke(rejectObject);
                return true;
            }));
        });
    }
}
