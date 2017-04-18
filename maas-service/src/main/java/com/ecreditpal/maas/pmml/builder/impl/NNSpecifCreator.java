package com.ecreditpal.maas.pmml.builder.impl;


import com.ecreditpal.maas.pmml.builder.PMMLEncogNeuralNetworkModel;
import com.ecreditpal.maas.pmml.builder.creator.AbstractSpecifCreator;
import org.dmg.pmml.*;
import org.encog.ml.BasicML;
import org.encog.neural.networks.BasicNetwork;

/**
 * Created by zhanhu on 3/29/16.
 */
public class NNSpecifCreator extends AbstractSpecifCreator {

    @Override
    public boolean build(BasicML basicML, Model model) {
        NeuralNetwork nnPmmlModel = (NeuralNetwork) model;
        new PMMLEncogNeuralNetworkModel().adaptMLModelToPMML((BasicNetwork) basicML, nnPmmlModel);
        nnPmmlModel.withOutput(createNormalizedOutput());
        return true;
    }

}
