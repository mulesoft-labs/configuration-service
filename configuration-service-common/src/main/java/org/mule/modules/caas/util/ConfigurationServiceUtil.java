package org.mule.modules.caas.util;

import org.mule.modules.caas.client.ClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConfigurationServiceUtil {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceUtil.class);

    public static InputStream loadFilesystemOrClasspathResource(String resourceName) {

        try {

            File tryFile = new File(resourceName);

            if (tryFile.exists()) {
                logger.debug("Found resource {} in filesystem, loading it...", resourceName);
                return new FileInputStream(tryFile);
            }

        } catch (IOException ex) {
            logger.error("Error while trying to load resource from filesystem or classpath...", ex);
            throw new RuntimeException(ex);
        }

        return loadClasspathResource(resourceName);
    }

    public static InputStream loadClasspathResource(String resourceName) {

        InputStream is = null;

        if (Thread.currentThread().getContextClassLoader() != null) {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        }

        if (is == null) {
            ClientUtils.class.getClassLoader().getResourceAsStream(resourceName);
        }

        if (is == null) {
            logger.warn("Could not find resource: {} either on classpath or filesystem...", resourceName);
        }

        return is;

    }

}
