package com.ecreditpal.maas.common.utils.file;

import com.ecreditpal.maas.common.db.activejdbc.MakeInstrumentationUtil;
import com.ecreditpal.maas.common.kafka.MaasKafkaConfig;
import com.ecreditpal.maas.common.utils.ConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            String propertiesPath = null;
            String rootPath = null;
            if (productConfigDir == null) {
                //从本地获取配置文件
                 rootPath = FileUtil.getRootPath();
                propertiesPath = rootPath + "/maas-web/target/classes";
            } else {
                //从服务器的目录获取配置文件
                propertiesPath = productConfigDir;
            }

            log.info("loading  property in directory {}.", propertiesPath);
            String applicationProp = propertiesPath +"/application.properties";
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

            String kafkaConfigProp = propertiesPath +"/kafka-config.properties";
            PropertiesConfiguration kafkaConf = demarcateKafkaConf(kafkaConfigProp);

            cc.addConfiguration(conf);
            cc.addConfiguration(kafkaConf);
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

    /**
     * 区分kafka的配置
     * 将kafka-config.properties的kafka配置根据类别归类
     * @param filePath 文件路径
     */
    private static PropertiesConfiguration demarcateKafkaConf(String filePath) throws IOException {
        Properties pro = new Properties();
        try(FileInputStream in = new FileInputStream(filePath)) {
            pro.load(in);
            Map<String, Properties> map = new HashMap<>();
            for (Object o : pro.keySet()) {
                String[] a = o.toString().split("\\.");
                map.computeIfAbsent(a[0], k -> new Properties());
                map.get(a[0]).setProperty(a[1], pro.get(o.toString()).toString());
            }

            PropertiesConfiguration conf = new PropertiesConfiguration();
            for (String s : map.keySet()) {
                MaasKafkaConfig config = ConvertUtil.convertMap(MaasKafkaConfig.class, map.get(s));
                conf.addProperty(s, config);
            }
            return conf;
        }
    }
}