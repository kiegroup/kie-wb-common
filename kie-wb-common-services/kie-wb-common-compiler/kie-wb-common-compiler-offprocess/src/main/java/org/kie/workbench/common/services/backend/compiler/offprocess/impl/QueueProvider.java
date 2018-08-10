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
package org.kie.workbench.common.services.backend.compiler.offprocess.impl;

import java.io.File;

import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ChronicleQueueBuilder;

public class QueueProvider {

    private ChronicleQueue queue;
    private String basePath;
    private String queueName;

    public QueueProvider(String queueName) {
        this.queueName = queueName;
        init(queueName);
    }

    private void init(String name) {
        basePath = System.getProperty("java.io.tmpdir") + File.separator + name;
        queue = ChronicleQueueBuilder.single(basePath).build();
    }

    public ChronicleQueue getQueue() {
        return queue;
    }

    public String getAbsoultePath() {
        return basePath;
    }

    public String getQueueName() {
        return queueName;
    }

    public void cleanQueue() {
        queue.close();
    }
}
