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
package org.kie.workbench.common.services.backend.compiler.offprocess;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.wire.DocumentContext;
import net.openhft.chronicle.wire.Wire;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponseOffProcess;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientIPC {

    private static Logger logger = LoggerFactory.getLogger(ClientIPC.class);

    public static KieCompilationResponse listenObjs(String uuid) throws Exception {
        logger.info("Reading buffer obj from client....");
        ExcerptTailer tailer = QueueProvider.getQueue().createTailer();
        KieCompilationResponse res = new DefaultKieCompilationResponse(false);
        ;
        try (DocumentContext dc = tailer.readingDocument()) {
            if (dc.isPresent()) {// this will tell you if there is any data  to read{
                Wire wire = dc.wire();
                Bytes bytes = wire.bytes();
                if (!bytes.isEmpty()) {
                    Object obj = deserialize(bytes.toByteArray());
                    res = (DefaultKieCompilationResponseOffProcess) obj;
                }
            }
        }
        return res;
    }

    private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
            try (ObjectInputStream o = new ObjectInputStream(b)) {
                return o.readObject();
            }
        }
    }
}
