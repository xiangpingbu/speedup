package com.ecreditpal.maas.pmml.builder.impl;


import com.ecreditpal.maas.pmml.container.obj.ColumnConfig;
import com.ecreditpal.maas.pmml.container.obj.ModelConfig;
import org.dmg.pmml.Model;
import org.dmg.pmml.RegressionModel;

import java.util.List;

/**
 * Created by zhanhu on 3/29/16.
 */
public class RegressionPmmlModelCreator extends NNPmmlModelCreator {

    public RegressionPmmlModelCreator(ModelConfig modelConfig, List<ColumnConfig> columnConfigList) {
        super(modelConfig, columnConfigList);
    }

    public RegressionPmmlModelCreator(ModelConfig modelConfig, List<ColumnConfig> columnConfigList, boolean isConcise) {
        super(modelConfig, columnConfigList, isConcise);
    }

    @Override
    public Model build() {
        Model model = new RegressionModel();
        model.setTargets(createTargets(modelConfig));
        return model;
    }

}
