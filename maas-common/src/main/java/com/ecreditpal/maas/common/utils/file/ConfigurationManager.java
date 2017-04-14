package com.ecreditpal.maas.common.utils.file;

import com.ecreditpal.maas.common.kafka.MaasKafkaConfig;
import org.apache.commons.configuration.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * maas 配置管理
 *
 * @author lifeng
 * @version 2017/4/10.
 */
public class ConfigurationManager {
    private static Logger logger = LoggerFactory
            .getLogger(ConfigurationManager.class);

    private static final CompositeConfiguration cc = new CompositeConfiguration();
    private static final String basePath = null;

    static {
        try {
            logger.info("loading system properties ...");
            cc.addConfiguration(new SystemConfiguration());
            //判断是否为本地
            String productConfigDir = cc.getString("config.dir");
            String applicationProp;
            if (productConfigDir == null) {
                //从本地获取配置文件
                applicationProp = "maas-web/target/classes/application.properties";
            } else {
                //从服务器的目录获取配置文件
                applicationProp = productConfigDir + "/application.properties";
            }

            logger.info("loading  property in directory {}.", applicationProp);
            PropertiesConfiguration conf = new PropertiesConfiguration(
                    applicationProp);

            /*
                递归获得配置目录下的所有文件的路径
             */
            if (productConfigDir != null) {
                File file = new File(productConfigDir);
                listFile(file, conf);
            } else{
                List subModels = conf.getList("maven.submodel");
                for (Object subModel : subModels) {
                    File file = new File(subModel.toString()+"/src/main/resources");
                    listFile(file,conf);
                }
            }

            conf.addProperty("defaultKafkaConfig",new MaasKafkaConfig());

            cc.addConfiguration(conf);
        } catch (Exception e) {
            logger.error("Failed to load configuration files", e);
        }
    }
    private ConfigurationManager() {
    }

    public static Configuration getConfiguration() {
        return cc;
    }

    public static void main(String[] args) {
        System.out.println(FileUtil.getFilePath("application.properties"));
    }

    private static void listFile(File file, PropertiesConfiguration conf) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    listFile(f, conf);
                }
            }
        } else {
            conf.addProperty(file.getName(), file.getAbsolutePath());
        }
    }
}