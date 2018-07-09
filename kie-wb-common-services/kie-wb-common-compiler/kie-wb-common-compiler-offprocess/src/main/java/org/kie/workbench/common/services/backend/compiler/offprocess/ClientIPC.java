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
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponseOffProcess;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientIPC {

    private static Logger logger = LoggerFactory.getLogger(ClientIPC.class);

    public static int staticListenChars(int bufferSize, String filePath) throws IOException {
        logger.info("Reading buffer from client....");
        File f = new File(filePath);
        FileChannel channel = FileChannel.open(f.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize);
        CharBuffer charBuf = byteBuffer.asCharBuffer();
        StringBuffer sb = new StringBuffer();
        char c;
        while ((c = charBuf.get()) != 0) {
            sb.append(c);
        }
        logger.info("Received from server:{}.", sb.toString());
        charBuf.put(0, '\0');
        return Integer.valueOf(sb.toString());
    }

    public static KieCompilationResponse staticListenObjs(int bufferSize, String filePath) throws Exception {
        logger.info("Reading buffer obj from client....");
        File f = new File(filePath);
        if (f.exists()) {
            FileChannel channel = FileChannel.open(f.toPath(), StandardOpenOption.READ);
            MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, bufferSize);
            byte[] bytez = new byte[bufferSize];
            for (int i = 0; i < bufferSize; i++) {
                bytez[i] = byteBuffer.get();
            }
            Object obj = deserialize(bytez);
            KieCompilationResponse res = (DefaultKieCompilationResponseOffProcess) obj;
            return res;
        } else {
            return new DefaultKieCompilationResponse(false);
        }
    }

    private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
            try (ObjectInputStream o = new ObjectInputStream(b)) {
                return o.readObject();
            }
        }
    }
/*
    public static void cleanMappedFile(){
        try {
            Method cleanerMethod = staticBuffer.getClass().getMethod("cleaner");
            cleanerMethod.setAccessible(true);
            Object cleaner = cleanerMethod.invoke(staticBuffer);
            Method cleanMethod = cleaner.getClass().getMethod("clean");
            cleanMethod.setAccessible(true);
            cleanMethod.invoke(cleaner);
            Files.delete(Paths.get(staticPathInfo));
        } catch(Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }*/
}
