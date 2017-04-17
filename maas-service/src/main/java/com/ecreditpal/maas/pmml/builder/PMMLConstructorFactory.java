package com.ecreditpal.maas.pmml.builder;

import com.ecreditpal.maas.pmml.builder.creator.AbstractPmmlElementCreator;
import com.ecreditpal.maas.pmml.builder.creator.AbstractSpecifCreator;
import com.ecreditpal.maas.pmml.builder.impl.*;
import com.ecreditpal.maas.pmml.container.obj.ColumnConfig;
import com.ecreditpal.maas.pmml.container.obj.ModelConfig;
import com.ecreditpal.maas.pmml.container.obj.ModelNormalizeConf;
import com.ecreditpal.maas.pmml.container.obj.ModelTrainConf;
import org.dmg.pmml.*;

import java.util.List;


public class PMMLConstructorFactory {

    public static PMMLTranslator produce(ModelConfig modelConfig, List<ColumnConfig> columnConfigList, boolean isConcise) {

        AbstractPmmlElementCreator<Model> modelCreator = null;
        AbstractSpecifCreator specifCreator = null;
        if (ModelTrainConf.ALGORITHM.NN.name().equalsIgnoreCase(modelConfig.getTrain().getAlgorithm())) {
            modelCreator = new NNPmmlModelCreator(modelConfig, columnConfigList, isConcise);
            specifCreator = new NNSpecifCreator();
        } else if (ModelTrainConf.ALGORITHM.LR.name().equalsIgnoreCase(modelConfig.getTrain().getAlgorithm())) {
            modelCreator = new RegressionPmmlModelCreator(modelConfig, columnConfigList, isConcise);
            specifCreator = new RegressionSpecifCreator();
        } else {
            throw new RuntimeException("Model not supported: " + modelConfig.getTrain().getAlgorithm());
        }

        AbstractPmmlElementCreator<DataDictionary> dataDictionaryCreator =
                new DataDictionaryCreator(modelConfig, columnConfigList, isConcise);


        AbstractPmmlElementCreator<MiningSchema> miningSchemaCreator =
                new MiningSchemaCreator(modelConfig, columnConfigList, isConcise);

        AbstractPmmlElementCreator<ModelStats> modelStatsCreator =
                new ModelStatsCreator(modelConfig, columnConfigList, isConcise);

        AbstractPmmlElementCreator<LocalTransformations> localTransformationsCreator = null;
        ModelNormalizeConf.NormType normType = modelConfig.getNormalizeType();
        if ( normType.equals(ModelNormalizeConf.NormType.WOE)
                || normType.equals(ModelNormalizeConf.NormType.WEIGHT_WOE) ) {
            localTransformationsCreator = new WoeLocalTransformCreator(modelConfig, columnConfigList, isConcise);
        } else if ( normType.equals(ModelNormalizeConf.NormType.WOE_ZSCORE) ) {
            localTransformationsCreator
                    = new WoeZscoreLocalTransformCreator(modelConfig, columnConfigList, isConcise, false);
        } else {
            localTransformationsCreator = new ZscoreLocalTransformCreator(modelConfig, columnConfigList, isConcise);
        }

        return new PMMLTranslator(modelCreator,
                dataDictionaryCreator,
                miningSchemaCreator,
                modelStatsCreator,
                localTransformationsCreator,
                specifCreator);
    }
}
