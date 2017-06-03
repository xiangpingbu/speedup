package com.ecreditpal.maas.common.utils.file;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
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
 *  maas 配置管理类
 *  通过ConfigurationManager,你可以获得当前环境所有的配置信息
 *  具体用法:
 *      String value = ConfigurationManager.getConfiguration().getString("key","defaultValue")
 *      "key"指的是变量名,如果获取不到这个"key"的对应值,那么将会返回"defaultValue"
 *  在第一次访问的时候,这个类会初始化
 *  初始化分为线上和线下两部分,如果指定config.dir参数,即判断是线上环境,那么项目运行所需的参数都在config.dir
 * 目录中,不然视作在本地环境中启动,会默认读取System.getProperty("user.dir")+/maas-web/target/classes
 * 下的文件.
 *  ConfigurationManager读取配置的入口是application.properties文件,将application.properties中的配置信息
 * 以key,value形式读入内存中.然后依赖其中的maven.submodel获得当前项目的子模块,然后通过递归的形式获得这些模块下所有
 * 的文件名和文件路径,并加载到内存中.
 *  因此ConfigurationManager可以帮助获得在config-env.properties中配置的变量,也可以帮助获得项目中所有的资源文件.
 * 项目的变量配置起初是由maven的profile和filter完成的,但是变量配置只能在项目打包时执行,一旦项目打包完成后就无法改变环境,
 * 后面新增manuallyLoad方法替换maven的功能,并可以在项目运行时替换环境变量.
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
                loadLogback(productConfigDir);
            }

            manuallyLoad(productConfigDir, propertiesPath);

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
     * @param configDir      配置文件的路径
     * @param propertiesPath 资源文件的路径
     */
    private static void manuallyLoad(String configDir, String propertiesPath) throws IOException {
        //项目运行时指定,如果为空,默认是local环境
        String env = cc.getString("maas.env");
        if (env == null) env = "local";
        //获得项目
        configDir = configDir == null ?
                FileUtil.getRootPath()+"/config" : configDir;


        Pattern pattern = Pattern.compile("\\$\\{[^}]*\\}");
        Properties pro = new Properties();

        String envFile = configDir + "/config-" + env + ".properties";
        try (FileInputStream in = new FileInputStream(envFile)) {
            pro.load(in);
        }


        File file = new File(propertiesPath);
        File[] files = file.listFiles();
        if (files != null) {
            String bak = propertiesPath + File.separator + "bak";
            File bakFile = new File(bak);
            if (!bakFile.exists()) {
                for (File subFile : files) {
                    if (subFile.isDirectory()) continue;
                    FileUtil.copyFile(subFile, bak);
                }
            }

            files = bakFile.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    if (subFile.isDirectory()) continue;
                    BufferedReader br = new BufferedReader(new FileReader(subFile));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {
                        Matcher m = pattern.matcher(line);
                        while (m.find()) {
                            String hit = m.group();
                            String key = hit.substring(2, hit.length() - 1);
                            String value = pro.getProperty(key);
                            if (value != null) {
                                line = line.replace(hit, value);
                            }
                        }
                        sb.append(line);
                        sb.append(System.getProperty("line.separator"));
                    }

                    br.close();
                    String fileName = subFile.getParentFile().getParent()+File.separator+subFile.getName();
                    BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false));
                    bw.write(sb.toString());
                    bw.flush();
                    bw.close();
                }
            }
        }
}

private static void loadLogback(String configDir) {
    File logbackFile = new File(configDir+"/logback.xml");
    if (logbackFile.exists()) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        try {
            configurator.doConfigure(logbackFile);
        }
        catch (JoranException e) {
            log.error("fail to load logback.xml",e);
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