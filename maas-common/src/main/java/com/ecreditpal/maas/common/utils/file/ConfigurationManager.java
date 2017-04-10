package com.ecreditpal.maas.common.utils.file;

import org.apache.commons.configuration.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lifeng
 * @CreateTime 2017/4/10.
 */
public class ConfigurationManager {
    private static Logger logger = LoggerFactory
            .getLogger(ConfigurationManager.class);

    private static final String CONFIG_DELIM_REG = "\\.";
    private static final String CONFIG_DELIM = ".";

    private static final CompositeConfiguration cc = new CompositeConfiguration();

    static {
        try {
            logger.info("loading system properties ...");
            cc.addConfiguration(new SystemConfiguration());

            String applicationProp = FileUtil.getFilePath("application.properties");

            logger.info("loading  property in directory {}.",
                    applicationProp);
            PropertiesConfiguration prodConfiguration = new PropertiesConfiguration(
                    applicationProp);
            cc.addConfiguration(prodConfiguration);


//            PropertiesConfiguration swaggerConfiguration = new PropertiesConfiguration(
//                    String.format("%s/swagger.properties", configDir));
//            cc.addConfiguration(swaggerConfiguration);
//
//            PropertiesConfiguration basicConfiguration = new PropertiesConfiguration(
//                    String.format("%s/config.properties", configDir));
//            cc.addConfiguration(basicConfiguration);
        } catch (ConfigurationException e) {
            logger.error("Failed to load configuration files", e);
        }
    }

    private ConfigurationManager() {
    }

    public static Configuration getConfiguration() {
        return cc;
    }
}