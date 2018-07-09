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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponseOffProcess;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieMavenCompilerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Paths;

public class ServerIPC {

    private static String prefixInfoChars = "/tmp/info-";
    private static String prefixInfoObjs = "/tmp/obj-";
    private static Logger logger = LoggerFactory.getLogger(ServerIPC.class);

    public static void main(String[] args) throws Throwable {
        String bufferSize = args[0];
        String uuid = args[1];
        String workingDir = args[2];
        String mavenRepo = args[3];
        String alternateSettingsAbsPath = args[4];
        String threadName = Thread.currentThread().getName();
        byte[] response = staticListenForChars(Integer.valueOf(bufferSize), workingDir, mavenRepo, alternateSettingsAbsPath, uuid);
        Thread.currentThread().setName(threadName);// restore the previous name to avoid the override of the maven output
        staticListenForObject(response, uuid);
    }

    public static byte[] staticListenForChars(int bufferSize, String workingDir, String mavenRepo, String alternateSettingsAbsPath, String uuid) throws Exception {
        logger.info("Server listen for chars....");
        KieCompilationResponse res = build(workingDir, mavenRepo, alternateSettingsAbsPath, uuid);
        byte[] resBytes = serialize(res);
        int bufferSizeRes = resBytes.length;
        File f = new File(prefixInfoChars + uuid);
        FileChannel channel = FileChannel.open(f.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize);
        CharBuffer charBuf = byteBuffer.asCharBuffer();
        StringBuffer sb = new StringBuffer(String.valueOf(bufferSizeRes)).append("\0");
        logger.info("Sent msg chars in the buffer:" + sb.toString());
        charBuf.put(sb.toString().toCharArray());
        logger.info("Waiting server for client.");
        char c;
        while (charBuf.get(0) != '\0') {
        }
        logger.info("Finished waiting.");
        return resBytes;
    }

    public static void staticListenForObject(byte[] response, String uuid) throws Exception {
        logger.info("Server write response bytes[]....");
        int bufferSizeRes = response.length;
        File f = new File(prefixInfoObjs + uuid);
        FileChannel channel = FileChannel.open(f.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, bufferSizeRes);
        for (int i = 0; i < bufferSizeRes; i++) {
            byteBuffer.put((byte) response[i]);
        }
        logger.info("Finished writing obj.");
    }

    private static KieCompilationResponse build(String prjPath, String mavenRepo, String alternateSettingsAbsPath, String uuid) {
        AFCompiler compiler = KieMavenCompilerFactory.getCompiler(KieDecorator.KIE_AND_LOG_AFTER);
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(prjPath));
        CompilationRequest req;
        if (alternateSettingsAbsPath != null && alternateSettingsAbsPath.length() > 1) {
            req = new DefaultCompilationRequest(mavenRepo,
                                                info,
                                                new String[]{
                                                        MavenCLIArgs.DEPENDENCY_RESOLVE,
                                                        MavenCLIArgs.COMPILE,
                                                        MavenCLIArgs.ALTERNATE_USER_SETTINGS + alternateSettingsAbsPath
                                                },
                                                Boolean.FALSE,
                                                uuid);
        } else {
            req = new DefaultCompilationRequest(mavenRepo,
                                                info,
                                                new String[]{
                                                        MavenCLIArgs.DEPENDENCY_RESOLVE,
                                                        MavenCLIArgs.COMPILE
                                                },
                                                Boolean.FALSE,
                                                uuid);
        }
        KieCompilationResponse res = (KieCompilationResponse) compiler.compile(req);
        KieCompilationResponse resConverted = new DefaultKieCompilationResponseOffProcess(res);
        return resConverted;
    }

    private static byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
            try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                o.writeObject(obj);
            }
            return b.toByteArray();
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
            Files.delete(Paths.get(staticFilePath));
        } catch(Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }*/
}
