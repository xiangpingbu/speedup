package com.ecreditpal.maas.pmml.builder.impl;

import com.ecreditpal.maas.pmml.core.LR;
import com.ecreditpal.maas.pmml.builder.PMMLLRModelBuilder;
import com.ecreditpal.maas.pmml.builder.creator.AbstractSpecifCreator;
import org.dmg.pmml.Model;
import org.dmg.pmml.RegressionModel;
import org.encog.ml.BasicML;

public class RegressionSpecifCreator extends AbstractSpecifCreator {

    @Override
    public boolean build(BasicML basicML, Model model) {
        RegressionModel regression = (RegressionModel)model;
        new PMMLLRModelBuilder().adaptMLModelToPMML((LR)basicML, regression);
        regression.withOutput(createNormalizedOutput());
        return true;
    }

}
