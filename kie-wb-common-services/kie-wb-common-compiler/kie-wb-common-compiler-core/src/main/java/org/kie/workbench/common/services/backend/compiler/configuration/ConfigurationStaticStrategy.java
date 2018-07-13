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

package org.kie.workbench.common.services.backend.compiler.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationKey;
import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Strategy implementation to create the Configuration from hard coded values, this must be the fallback impl
 * if the other strategies fails
 */
public class ConfigurationStaticStrategy implements ConfigurationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationStaticStrategy.class);

    public final String KIE_VERSION_FILE = "kie-version.properties";
    public final String KIE_VERSION_KEY = "kie_version";

    protected Map<ConfigurationKey, String> conf;

    private Boolean valid ;

    public ConfigurationStaticStrategy() {
        conf = new HashMap<>();
        Properties props = loadKieVersionProperties();
        if(!props.isEmpty()) {
            conf.put(ConfigurationKey.KIE_VERSION, props.getProperty(KIE_VERSION_KEY));
        }
        conf.put(ConfigurationKey.COMPILER, "jdt");
        conf.put(ConfigurationKey.SOURCE_VERSION, "1.8");
        conf.put(ConfigurationKey.TARGET_VERSION, "1.8");
        conf.put(ConfigurationKey.MAVEN_COMPILER_PLUGIN_GROUP, "org.apache.maven.plugins");
        conf.put(ConfigurationKey.MAVEN_COMPILER_PLUGIN_ARTIFACT, "maven-compiler-plugin");
        conf.put(ConfigurationKey.MAVEN_COMPILER_PLUGIN_VERSION, "3.7.0");
        conf.put(ConfigurationKey.FAIL_ON_ERROR, "false");
        conf.put(ConfigurationKey.TAKARI_COMPILER_PLUGIN_GROUP, "io.takari.maven.plugins");
        conf.put(ConfigurationKey.TAKARI_COMPILER_PLUGIN_ARTIFACT, "takari-lifecycle-plugin");
        conf.put(ConfigurationKey.TAKARI_COMPILER_PLUGIN_VERSION, "1.13.5");
        conf.put(ConfigurationKey.KIE_MAVEN_PLUGINS, "org.kie");
        conf.put(ConfigurationKey.KIE_MAVEN_PLUGIN, "kie-maven-plugin");
        conf.put(ConfigurationKey.KIE_TAKARI_PLUGIN, "kie-takari-plugin");
        valid = Boolean.TRUE;
    }

    @Override
    public Map<ConfigurationKey, String> loadConfiguration() {
        return Collections.unmodifiableMap(conf);
    }

    @Override
    public Boolean isValid() {
        return valid && ( conf.size() == ConfigurationKey.values().length );
    }

    @Override
    public Integer getOrder() {
        return Integer.valueOf(1000);
    }


    public Properties loadKieVersionProperties() {
        Properties prop = new Properties();
        InputStream in = getClass().getClassLoader().getResourceAsStream(KIE_VERSION_FILE);
        if (in == null) {
            logger.info("{} not available with the classloader, unable to initialize the StaticConfigurationStrategy. \n",
                        KIE_VERSION_FILE);
            valid = Boolean.FALSE;
        } else {
            try {
                prop.load(in);
                in.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return prop;
    }
}
