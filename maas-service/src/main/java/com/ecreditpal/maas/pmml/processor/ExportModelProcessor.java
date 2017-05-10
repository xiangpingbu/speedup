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
    private final static Logger log = LoggerFactory.getLogger(ExportModelProcessor.class);


    public static final String PMML_STR = "pmml";
    public static final String COLUMN_STATS = "columnstats";
    private String type;
    private boolean isConcise;
    private String columnConfigStr;
    private String modelConfigPath;
    private String params;
    private PMML pmml;

    public ExportModelProcessor(String type, boolean isConcise,String columnConfigStr,String modelConfigPath,String params) {
        this.type = type;
        this.isConcise = isConcise;
        this.columnConfigStr = columnConfigStr;
        this.params = params;
        this.modelConfigPath = modelConfigPath;
    }

    public int run() throws Exception {

        int status = 0;
        if (StringUtils.isBlank(type)) {
            type = PMML_STR;
        }

        if (type.equalsIgnoreCase(PMML_STR)) {
            log.info("Convert models into {} format", type);

            ModelConfig modelConfig = CommonUtils.loadModelConfig(modelConfigPath);
            List<ColumnConfig> columnConfigList = CommonUtils.loadColumnConfigList(columnConfigStr);

            List<BasicML> models = CommonUtils.loadSpecificBasicModels(params,
                   ALGORITHM.valueOf(modelConfig.getAlgorithm().toUpperCase()));

            PMMLTranslator translator = PMMLConstructorFactory.produce(modelConfig, columnConfigList, this.isConcise);

            for (int index = 0; index < models.size(); index++) {
                log.info("start to generate {}.pmml",modelConfig.getModelSetName()+Integer.toString(index));
                pmml = translator.build(models.get(index));
            }
        } else {
            log.error("Unsupported output format - {}", type);
            status = -1;
        }

        clearUp();

        log.info("Done.");

        return status;
    }

    public PMML getPmml() {
        return pmml;
    }
}
