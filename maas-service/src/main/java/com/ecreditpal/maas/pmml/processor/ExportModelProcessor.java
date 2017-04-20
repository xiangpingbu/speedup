package com.ecreditpal.maas.pmml.processor;


import com.ecreditpal.maas.pmml.builder.PMMLConstructorFactory;
import com.ecreditpal.maas.pmml.builder.PMMLTranslator;
import com.ecreditpal.maas.pmml.builder.PMMLUtils;
import com.ecreditpal.maas.pmml.container.obj.ColumnConfig;
import com.ecreditpal.maas.pmml.container.obj.ModelConfig;
import com.ecreditpal.maas.pmml.container.obj.ModelTrainConf.ALGORITHM;
import com.ecreditpal.maas.pmml.util.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FileUtils;
import org.encog.ml.BasicML;

import org.dmg.pmml.PMML;
import java.io.File;
import java.util.List;

/**
 * ExportModelProcessor class
 *
 * @author xibu
 */
public class ExportModelProcessor extends BasicModelProcessor implements Processor {

    public static final String PMML = "pmml";
    public static final String COLUMN_STATS = "columnstats";

    /**
     * log object
     */
    private final static Logger log = LoggerFactory.getLogger(ExportModelProcessor.class);

    private String type;
    private boolean isConcise;
    private String modelPath="/Users/xpbu/Documents/Work/repo/maas/maas-service/pmmlData/models";

    public ExportModelProcessor(String type, boolean isConcise) {
        this.type = type;
        this.isConcise = isConcise;
    }

    public int run() throws Exception {
        setUp();

        int status = 0;
        File pmmls = new File("/Users/xpbu/Documents/Work/repo/maas/maas-service/pmmlResult/");
        FileUtils.forceMkdir(pmmls);

        if (StringUtils.isBlank(type)) {
            type = PMML;
        }

        if (type.equalsIgnoreCase(PMML)) {
            log.info("Convert models into {} format", type);

            ModelConfig modelConfig = CommonUtils.loadModelConfig();
            List<ColumnConfig> columnConfigList = CommonUtils.loadColumnConfigList();

            List<BasicML> models = CommonUtils.loadBasicModels(modelPath,
                   ALGORITHM.valueOf(modelConfig.getAlgorithm().toUpperCase()));

            PMMLTranslator translator = PMMLConstructorFactory.produce(modelConfig, columnConfigList, this.isConcise);

            for (int index = 0; index < models.size(); index++) {
                log.info("\t start to generate " + "/Users/xpbu/Documents/Work/repo/maas/maas-service/pmmlResult" + File.separator + modelConfig.getModelSetName()
                        + Integer.toString(index) + ".pmml");
                PMML pmml = translator.build(models.get(index));
                PMMLUtils.savePMML(pmml,
                        "/Users/xpbu/Documents/Work/repo/maas/maas-service/pmmlResult" + File.separator + modelConfig.getModelSetName() + Integer.toString(index) + ".pmml");
            }
        } else {
            log.error("Unsupported output format - {}", type);
            status = -1;
        }

        clearUp();

        log.info("Done.");

        return status;
    }

}
