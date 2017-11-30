/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.impl.utils;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class LogUtils {

    protected static String FILE_URI = "file://";

    public static List<String> getOutput(String prj,
                                         String uuid) {
        StringBuilder sb = new StringBuilder(prj.trim()).append("/").append("log").append(".").append(uuid).append(".log");
        return readTmpLog(sb.toString());
    }

    public static List<String> readTmpLog(String logFile) {
        Path logPath = Paths.get(URI.create(FILE_URI + logFile));
        List<String> log = new ArrayList<>();
        try {
            if (Files.isReadable(logPath)) {
                for (String line : Files.readAllLines(logPath, Charset.defaultCharset())) {
                    log.add(line);
                }
                return log;
            }
            return Collections.emptyList();
        } finally {
            Files.delete(logPath);
        }
    }
}
