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
import org.kie.workbench.common.services.backend.compiler.offprocess.ClientIPC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientIPCImpl implements ClientIPC {

    private ResponseSharedMap map;
    private QueueProvider provider;
    private Logger logger = LoggerFactory.getLogger(ClientIPCImpl.class);

    public ClientIPCImpl(ResponseSharedMap map, QueueProvider provider) {
        this.map = map;
        this.provider = provider;
    }

    public KieCompilationResponse getResponse(String uuid) {
        if(isLoaded(uuid)) {
            return (KieCompilationResponse)map.getResponse(uuid);
        }else {
            return new DefaultKieCompilationResponse(false, "");
        }
    }

    private boolean isLoaded(String uuid) {
        ExcerptTailer tailer = provider.getQueue().createTailer();
        DefaultKieCompilationResponseOffProcess res = getLastKieResponse(tailer);
        DefaultKieCompilationResponse kres = new DefaultKieCompilationResponse(res);

        if (uuid.equals(kres.getRequestUUID())) {
            if (!map.contains(kres.getRequestUUID())) {
                map.addResponse(uuid, kres);
                return true;
            }
        } else {
            logger.info("loop in the queue");
            //we loop in the queue to find our Response by UUID
            while (!uuid.equals(kres.getRequestUUID())) {
                res = getLastKieResponse(tailer);
                kres = new DefaultKieCompilationResponse(res);
            }
            if (!uuid.equals(kres.getRequestUUID())) {
                return false;
            }
        }
        if (!map.contains(kres.getRequestUUID())) {
            map.addResponse(uuid, new DefaultKieCompilationResponse(res));
            return true;
        } else {
            return false;
        }
    }

    private DefaultKieCompilationResponseOffProcess getLastKieResponse(ExcerptTailer tailer) {
        logger.info("reading lastKieResponse");
        DefaultKieCompilationResponseOffProcess res = new DefaultKieCompilationResponseOffProcess(false, "");
        try (DocumentContext dc = tailer.readingDocument()) {
            if (dc.isPresent()) {
                Wire wire = dc.wire();
                Bytes bytes = wire.bytes();
                if (!bytes.isEmpty()) {
                    try {
                        Object obj = deserialize(bytes.toByteArray());
                        res = (DefaultKieCompilationResponseOffProcess) obj;
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
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
