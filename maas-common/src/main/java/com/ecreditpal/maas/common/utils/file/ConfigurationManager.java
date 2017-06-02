package com.ecreditpal.maas.common.utils.file;

import com.ecreditpal.maas.common.db.activejdbc.MakeInstrumentationUtil;
import com.ecreditpal.maas.common.kafka.MaasKafkaConfig;
import com.ecreditpal.maas.common.utils.ConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        loadConfig();
    }


    public static Configuration getConfiguration() {
        return cc;
    }


    private static void loadConfig() {
        try {
            log.info("loading system properties ...");
            cc.addConfiguration(new SystemConfiguration());
            //判断是否为本地
            String productConfigDir = cc.getString("config.dir");
            String propertiesPath;
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
            String applicationProp = propertiesPath + "/application.properties";
            PropertiesConfiguration conf = new PropertiesConfiguration(applicationProp);
            /*
                递归获得配置目录下的所有文件的路径
             */
            if (productConfigDir != null) {
                File file = new File(productConfigDir);
                listFile(file, conf);
            } else {
                List subModels = conf.getList("maven.submodel");
                for (Object subModel : subModels) {
                    File file = new File(rootPath + "/" + subModel.toString() + "/src/main/resources");
                    listFile(file, conf);

                    file = new File(rootPath + "/" + subModel.toString() + "/src/test/resources");
                    listFile(file, conf);
                }
            }

            String kafkaConfigProp = propertiesPath + "/kafka-config.properties";

            manuallyLoad(propertiesPath,propertiesPath);

            PropertiesConfiguration kafkaConf = demarcateKafkaConf(kafkaConfigProp);

            cc.addConfiguration(conf);
            cc.addConfiguration(kafkaConf);
        } catch (Exception e) {
            log.error("Failed to load configuration files", e);
        }
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
     * 用于在服务器上手动更改jar包的环境
     * 在重启服务时指定环境变量env即可替换指定的环境
     *
     * @param configFilePath 配置文件的路径
     * @param propertiesPath 资源文件的路径
     */
    private static void manuallyLoad(String configFilePath,String propertiesPath) throws IOException {
        String env = cc.getString("maas.env");
        if (env == null) return;

        Pattern pattern = Pattern.compile("\\$\\{[^}]*\\}");
        Properties pro = new Properties();

        String envFile = configFilePath + "/config-" + env + ".properties";
        try (FileInputStream in = new FileInputStream(envFile)) {
            pro.load(in);
        }
        File file = new File(propertiesPath);
        File[] files = file.listFiles();
        if (files != null) {
            for (File subFile : files) {
                BufferedReader br = new BufferedReader(new FileReader(subFile));
                StringBuilder sb = new StringBuilder();
                String line ;
                while ((line = br.readLine()) != null) {
                    Matcher m = pattern.matcher(line);
                    if (m.find()){
                        String key = m.group().substring(2,m.group().length()-1);
                        String value = pro.getProperty(key);
                        if (value != null) {
                            line = line.replace(m.group(), value);
                        }
                    }
                    sb.append(line);
                    sb.append(System.getProperty("line.separator"));
                }
                br.close();

                BufferedWriter bw = new BufferedWriter(new FileWriter(subFile,false));
                bw.write(sb.toString());
                bw.flush();
                bw.close();
            }
        }
    }

    /**
     * 区分kafka的配置
     * 将kafka-config.properties的kafka配置根据类别归类
     *
     * @param filePath 文件路径
     */
    private static PropertiesConfiguration demarcateKafkaConf(String filePath) throws IOException {
        Properties pro = new Properties();
        try (FileInputStream in = new FileInputStream(filePath)) {
            //所有的kafka-config文件,读取所有的变量参数
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