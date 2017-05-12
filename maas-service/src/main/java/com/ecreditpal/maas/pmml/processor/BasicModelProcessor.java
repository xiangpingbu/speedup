package com.ecreditpal.maas.pmml.processor;

import com.ecreditpal.maas.pmml.container.obj.ModelConfig;
import com.ecreditpal.maas.pmml.container.obj.RawSourceData;
import com.ecreditpal.maas.pmml.util.CommonUtils;
import com.ecreditpal.maas.pmml.container.obj.ColumnConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;


/**
 * Model Basic Processor, it helps to do basic manipulate in model, including load/save configuration, copy
 * configuration file
 */
public class BasicModelProcessor {

    private final static Logger log = LoggerFactory.getLogger(BasicModelProcessor.class);

    protected ModelConfig modelConfig;
    protected List<ColumnConfig> columnConfigList;


    private void validateColumnNameUnique() {
        if (this.columnConfigList == null) {
            return;
        }
        Set<String> names = new HashSet<String>();
        for (ColumnConfig config : this.columnConfigList) {
            if (names.contains(config.getColumnName())) {
                log.warn("Duplicated {} in ColumnConfig.json file, later one will be append index to make it unique.",
                        config.getColumnName());
            }
            names.add(config.getColumnName());
        }
    }

    /**
     * initialize the config file, pathFinder and other input
     *
     *            Shifu running step
     * @throws Exception
     */
    protected void setUp() throws Exception {
        if(hasInitialized()) {
            return;
        }

        // load model configuration and do validation
        loadModelConfig();
        validateModelConfig();

        loadColumnConfig();
        validateColumnNameUnique();
    }

    /**
     * validate the modelconfig if it's well written.
     *
     * @return
     * @throws Exception
     */
    protected void validateModelConfig() throws Exception {
    }

    /**
     * Check the processor is initialized or not
     *
     * @return true - if the process is initialized
     *         false - if not
     */
    private boolean hasInitialized() {
        return (null != this.modelConfig && null != this.columnConfigList);
    }


    protected void clearUp() throws IOException {
        // do nothing now
    }


    /**
     * get the modelConfig instance
     *
     * @return the modelConfig
     */
    public ModelConfig getModelConfig() {
        return modelConfig;
    }

    /**
     * get the columnConfigList instance
     *
     * @return the columnConfigList
     */
    public List<ColumnConfig> getColumnConfigList() {
        return columnConfigList;
    }


    /**
     * load Model Config method
     *
     * @throws IOException
     */
    private void loadModelConfig() throws IOException {
        modelConfig = CommonUtils.loadModelConfig(null);
    }

    /**
     * load Model Config method
     *
     * @throws IOException
     */
    private void loadModelConfig(String pathToModel, RawSourceData.SourceType source) throws IOException {
        modelConfig = CommonUtils.loadModelConfig(pathToModel, source);
    }

    /**
     * load Column Config
     *
     * @throws IOException
     */
    private void loadColumnConfig() throws IOException {
        columnConfigList = CommonUtils.loadColumnConfigList();
    }


}
