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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

public class PathConverter {

    public final static String FILE_URI = "file://";
    protected static final Logger logger = LoggerFactory.getLogger(PathConverter.class);

    public static org.uberfire.java.nio.file.Path getNioPath(org.guvnor.common.services.project.model.Project project) {
        org.uberfire.java.nio.file.Path nioPath;
        if (!project.getRootPath().toString().startsWith("file://")) {
            nioPath = org.uberfire.backend.server.util.Paths.convert(project.getRootPath());
        } else {
            nioPath = PathConverter.createPathFromVFS(project.getRootPath());
        }
        return nioPath;
    }

    public static Path createPathFromString(String path) {
        if (path.startsWith(FILE_URI)) {
            return Paths.get(URI.create(path));
        } else {
            return Paths.get(URI.create(FILE_URI + path));
        }
    }

    public static Path createPathFromVFS(org.uberfire.backend.vfs.Path path) {
        Path nioPath = org.uberfire.backend.server.util.Paths.convert(path);
        if (nioPath.startsWith(FILE_URI)) {
            return nioPath;
        } else {
            return Paths.get(URI.create(FILE_URI + path));
        }
    }

    public static List<URL> createURLSFromString(List<String> items) {
        List<URL> urls = new ArrayList<>(items.size());
        try {
            for (String item : items) {
                if (FilenameUtils.getName(item).startsWith(".")) {
                    continue;
                }
                if (item.startsWith(FILE_URI)) {
                    urls.add(new URL(item));
                } else {
                    urls.add(new URL(FILE_URI + item));
                }
            }
        } catch (MalformedURLException ex) {
            logger.error(ex.getMessage());
        }
        return urls;
    }

    public static List<URI> createURISFromString(List<String> items) {
        List<URI> uris = new ArrayList<>(items.size());
        for (String item : items) {
            if (FilenameUtils.getName(item).startsWith(".")) {
                continue;
            }
            if (item.startsWith(FILE_URI)) {
                uris.add(URI.create(item));
            } else {
                uris.add(URI.create(FILE_URI + item));
            }
        }
        return uris;
    }
}
