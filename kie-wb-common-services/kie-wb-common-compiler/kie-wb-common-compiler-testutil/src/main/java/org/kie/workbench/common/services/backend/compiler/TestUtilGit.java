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
package org.kie.workbench.common.services.backend.compiler;

import java.io.IOException;
import java.net.ServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtilGit {

    private static Logger logger = LoggerFactory.getLogger(TestUtilGit.class);
    private static int SELECT_RANDOM_PORT_OPTION = 0;

    public static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(SELECT_RANDOM_PORT_OPTION)) {
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            logger.debug("Found free port {}", port);
            return port;
        } catch (IOException e) {
            // nop = ok
        }
        throw new IllegalStateException("Could not find a free TCP/IP port to start git-daemon.");
    }
}
