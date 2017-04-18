package com.ecreditpal.maas.pmml.builder.impl;


import com.ecreditpal.maas.pmml.builder.creator.AbstractPmmlElementCreator;
import com.ecreditpal.maas.pmml.container.obj.ColumnConfig;
import com.ecreditpal.maas.pmml.container.obj.ModelConfig;
import org.apache.commons.lang.StringUtils;
import org.dmg.pmml.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanhu on 3/29/16.
 */
public class ModelStatsCreator extends AbstractPmmlElementCreator<ModelStats> {

    private static final double EPS = 1e-10;

    public ModelStatsCreator(ModelConfig modelConfig, List<ColumnConfig> columnConfigList) {
        super(modelConfig, columnConfigList);
    }

    public ModelStatsCreator(ModelConfig modelConfig, List<ColumnConfig> columnConfigList, boolean isConcise) {
        super(modelConfig, columnConfigList, isConcise);
    }

    @Override
    public ModelStats build() {
        ModelStats modelStats = new ModelStats();

        for(ColumnConfig columnConfig: columnConfigList) {
            if(columnConfig.isFinalSelect()) {
                UnivariateStats univariateStats = new UnivariateStats();
                univariateStats.setField(FieldName.create(columnConfig.getColumnName()));

                if(columnConfig.isCategorical()) {
                    DiscrStats discrStats = new DiscrStats();

                    Array countArray = createCountArray(columnConfig);
                    discrStats.withArrays(countArray);

                    if ( !isConcise ) {
                        List<Extension> extensions = createExtensions(columnConfig);
                        discrStats.withExtensions(extensions);
                    }

                    univariateStats.setDiscrStats(discrStats);
                } else { // numerical column
                    univariateStats.setNumericInfo(createNumericInfo(columnConfig));

                    if ( !isConcise ) {
                        univariateStats.setContStats(createConStats(columnConfig));
                    }
                }

                modelStats.withUnivariateStats(univariateStats);
            }
        }

        return modelStats;
    }

    /**
     * Create @Array for numerical variable
     *
     * @param columnConfig
     *            - ColumnConfig for numerical variable
     * @return Array for numerical variable ( positive count + negative count )
     */
    private Array createCountArray(ColumnConfig columnConfig) {
        Array countAllArray = new Array();
        List<Integer> s = columnConfig.getBinCountPos();
        List<Integer> binCountAll = new ArrayList<Integer>(columnConfig.getBinCountPos().size());
        for(int i = 0; i < binCountAll.size(); i++) {
            binCountAll.add(columnConfig.getBinCountPos().get(i) + columnConfig.getBinCountNeg().get(i));
        }

        countAllArray.setType(Array.Type.INT);
        countAllArray.setN(binCountAll.size());
        countAllArray.setValue(StringUtils.join(binCountAll, ' '));

        return countAllArray;
    }

    /**
     * Create common extension list from ColumnConfig
     *
     * @param columnConfig
     *            - ColumnConfig to create extension
     * @return extension list
     */
    private List<Extension> createExtensions(ColumnConfig columnConfig) {
        Map<String, String> extensionMap = new HashMap<String, String>();

        extensionMap.put("BinCountPos", columnConfig.getBinCountPos().toString());
        extensionMap.put("BinCountNeg", columnConfig.getBinCountNeg().toString());
        extensionMap.put("BinWeightedCountPos", columnConfig.getBinWeightedPos().toString());
        extensionMap.put("BinWeightedCountNeg", columnConfig.getBinWeightedNeg().toString());
        extensionMap.put("BinPosRate", columnConfig.getBinPosRate().toString());

        return createExtensions(extensionMap);
    }

    /**
     * Create extension list from HashMap
     *
     * @param extensionMap
     *            the <String,String> map to create extension list
     * @return extension list
     */
    private List<Extension> createExtensions(Map<String, String> extensionMap) {
        List<Extension> extensions = new ArrayList<Extension>();

        for(Map.Entry<String, String> entry: extensionMap.entrySet()) {
            String key = entry.getKey();
            Extension extension = new Extension();
            extension.setName(key);
            extension.setValue(entry.getValue());
            extensions.add(extension);
        }

        return extensions;
    }

    /**
     * Create @NumericInfo for numerical variable
     *
     * @param columnConfig
     *            - ColumnConfig for numerical variable
     * @return NumericInfo for variable
     */
    private NumericInfo createNumericInfo(ColumnConfig columnConfig) {
        NumericInfo numericInfo = new NumericInfo();

        numericInfo.setMaximum(columnConfig.getColumnStats().getMax());
        numericInfo.setMinimum(columnConfig.getColumnStats().getMin());
        numericInfo.setMean(columnConfig.getMean());
        numericInfo.setMedian(columnConfig.getMedian());
        numericInfo.setStandardDeviation(columnConfig.getStdDev());

        return numericInfo;
    }

    /**
     * Create @ConStats for numerical variable
     *
     * @param columnConfig
     *            - ColumnConfig to generate ConStats
     * @return ConStats for variable
     */
    private ContStats createConStats(ColumnConfig columnConfig) {
        ContStats conStats = new ContStats();

        List<Interval> intervals = new ArrayList<Interval>();
        for(int i = 0; i < columnConfig.getBinBoundary().size(); i++) {
            Interval interval = new Interval();
            interval.setClosure(Interval.Closure.OPEN_CLOSED);
            interval.setLeftMargin(columnConfig.getBinBoundary().get(i));

            if(i == columnConfig.getBinBoundary().size() - 1) {
                interval.setRightMargin(Double.POSITIVE_INFINITY);
            } else {
                interval.setRightMargin(columnConfig.getBinBoundary().get(i + 1));
            }

            intervals.add(interval);
        }
        conStats.withIntervals(intervals);

        Map<String, String> extensionMap = new HashMap<String, String>();

        extensionMap.put("BinCountPos", columnConfig.getBinCountPos().toString());
        extensionMap.put("BinCountNeg", columnConfig.getBinCountNeg().toString());
        extensionMap.put("BinWeightedCountPos", columnConfig.getBinWeightedPos().toString());
        extensionMap.put("BinWeightedCountNeg", columnConfig.getBinWeightedNeg().toString());
        extensionMap.put("BinPosRate", columnConfig.getBinPosRate().toString());
        extensionMap.put("BinWOE", calculateWoe(columnConfig.getBinCountPos(), columnConfig.getBinCountNeg())
                .toString());
        extensionMap.put("KS", Double.toString(columnConfig.getKs()));
        extensionMap.put("IV", Double.toString(columnConfig.getIv()));
        conStats.withExtensions(createExtensions(extensionMap));

        return conStats;
    }

    /**
     * Generate Woe data from positive and negative counts
     *
     * @param binCountPos
     *            - positive count list
     * @param binCountNeg
     *            - negative count list
     * @return Woe value list
     */
    private List<Double> calculateWoe(List<Integer> binCountPos, List<Integer> binCountNeg) {
        List<Double> woe = new ArrayList<Double>();

        double sumPos = 0.0;
        double sumNeg = 0.0;

        for(int i = 0; i < binCountPos.size(); i++) {
            sumPos += binCountPos.get(i);
            sumNeg += binCountNeg.get(i);
        }

        for(int i = 0; i < binCountPos.size(); i++) {
            woe.add(Math.log((binCountPos.get(i) / sumPos + EPS) / (binCountNeg.get(i) / sumNeg + EPS)));
        }

        return woe;
    }
}
