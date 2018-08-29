package org.kie.workbench.common.services.backend.compiler.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenBannedVars {

    private static final Logger logger = LoggerFactory.getLogger(MavenBannedVars.class);
    private static final String BANNED_PROPERTIES_FILE = "BannedEnvVars.properties";

    public static Properties getBannedProperties(){
        return loadProperties(BANNED_PROPERTIES_FILE);
    }

    private static Properties loadProperties(String propName) {
        Properties prop = new Properties();
        InputStream in = MavenBannedVars.class.getClassLoader().getResourceAsStream(propName);
        if (in == null) {
            logger.info("{} not available with the classloader no Banned EnvVars Found . \n", propName);
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
