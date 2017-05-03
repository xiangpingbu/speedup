package com.ecreditpal.maas.common.utils.file;

import com.ecreditpal.maas.common.db.activejdbc.MakeInstrumentationUtil;
import com.ecreditpal.maas.common.kafka.MaasKafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * maas 配置管理
 *
 * @author lifeng
 * @version 2017/4/10.
 */
@Slf4j
public class ConfigurationManager {

    private static final CompositeConfiguration cc = new CompositeConfiguration();

    /**
     * 禁止实例化
     */
    private ConfigurationManager() {
    }

    static {
        try {
            log.info("loading system properties ...");
            cc.addConfiguration(new SystemConfiguration());
            //判断是否为本地
            String productConfigDir = cc.getString("config.dir");
            String applicationProp;
            String rootPath = null;
            if (productConfigDir == null) {
                //从本地获取配置文件
                 rootPath = FileUtil.getRootPath();
                applicationProp = rootPath + "/maas-web/target/classes/application.properties";
            } else {
                //从服务器的目录获取配置文件
                applicationProp = productConfigDir + "/application.properties";
            }

            log.info("loading  property in directory {}.", applicationProp);
            PropertiesConfiguration conf = new PropertiesConfiguration(
                    applicationProp);

            /*
                递归获得配置目录下的所有文件的路径
             */
            if (productConfigDir != null) {
                File file = new File(productConfigDir);
                listFile(file, conf);
            } else {
                List subModels = conf.getList("maven.submodel");
                for (Object subModel : subModels) {
                    File file = new File(rootPath+"/"+subModel.toString() + "/src/main/resources");
                    listFile(file, conf);
                }
            }

            //加入默认的kafka配置
            conf.addProperty("defaultKafkaConfig", new MaasKafkaConfig());

            cc.addConfiguration(conf);
        } catch (Exception e) {
            log.error("Failed to load configuration files", e);
        }
    }


    public static Configuration getConfiguration() {
        return cc;
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