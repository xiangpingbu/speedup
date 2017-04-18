/**
 * Copyright [2012-2014] PayPal Software Foundation
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ecreditpal.maas.pmml.container.obj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * ModelTrainConf class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelTrainConf {

    public static enum ALGORITHM {
        NN, LR, SVM, DT, RF, GBT
    }

    private Integer baggingNum = Integer.valueOf(5);
    // this is set default as true as bagging often with replacement sampleing.
    private Boolean baggingWithReplacement = Boolean.TRUE;
    private Double baggingSampleRate = Double.valueOf(1.0);
    private Double validSetRate = Double.valueOf(0.2);
    private Double convergenceThreshold = Double.valueOf(0.0);
    private Integer numTrainEpochs = Integer.valueOf(100);
    private Integer epochsPerIteration = Integer.valueOf(1);

    private Boolean trainOnDisk = Boolean.FALSE;
    private Boolean fixInitInput = Boolean.FALSE;

    private Boolean isContinuous = Boolean.FALSE;

    private Boolean isCrossOver = Boolean.FALSE;

    private Integer workerThreadCount = 4;

    private Double upSampleWeight = Double.valueOf(1d);

    private String algorithm = "NN";

    private Map<String, Object> params;

    private Map<String, String> customPaths;

    public ModelTrainConf() {
        customPaths = new HashMap<String, String>(1);

        /**
         * Since most user won't use this function,
         * hidden the custom paths for creating new model.
         */
        /*
         * customPaths.put(Constants.KEY_PRE_TRAIN_STATS_PATH, null);
         * customPaths.put(Constants.KEY_SELECTED_RAW_DATA_PATH, null);
         * customPaths.put(Constants.KEY_NORMALIZED_DATA_PATH, null);
         * customPaths.put(Constants.KEY_TRAIN_SCORES_PATH, null);
         * customPaths.put(Constants.KEY_BIN_AVG_SCORE_PATH, null);
         */
    }

    public Integer getBaggingNum() {
        return baggingNum;
    }

    public void setBaggingNum(Integer baggingNum) {
        this.baggingNum = baggingNum;
    }

    public Boolean getBaggingWithReplacement() {
        return baggingWithReplacement;
    }

    public void setBaggingWithReplacement(Boolean baggingWithReplacement) {
        this.baggingWithReplacement = baggingWithReplacement;
    }

    public Double getBaggingSampleRate() {
        return baggingSampleRate;
    }

    public void setBaggingSampleRate(Double baggingSampleRate) {
        this.baggingSampleRate = baggingSampleRate;
    }

    public Double getValidSetRate() {
        return validSetRate;
    }

    public void setValidSetRate(Double validSetRate) {
        this.validSetRate = validSetRate;
    }

    public Boolean getTrainOnDisk() {
        return trainOnDisk;
    }

    public void setTrainOnDisk(Boolean trainOnDisk) {
        this.trainOnDisk = trainOnDisk;
    }

    @JsonIgnore
    public Boolean getFixInitInput() {
        return fixInitInput;
    }

    public void setFixInitInput(Boolean fixInitInput) {
        this.fixInitInput = fixInitInput;
    }

    public Integer getNumTrainEpochs() {
        return numTrainEpochs;
    }

    public void setNumTrainEpochs(Integer numTrainEpochs) {
        this.numTrainEpochs = numTrainEpochs;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, String> getCustomPaths() {
        return customPaths;
    }

    public void setCustomPaths(Map<String, String> customPaths) {
        this.customPaths = customPaths;
    }

    /**
     * @return the epochsPerIteration
     */
    public Integer getEpochsPerIteration() {
        return epochsPerIteration;
    }

    /**
     * @param epochsPerIteration
     *            the epochsPerIteration to set
     */
    public void setEpochsPerIteration(Integer epochsPerIteration) {
        this.epochsPerIteration = epochsPerIteration;
    }

    /**
     * As threshold is an optional setting, Use @{@link JsonIgnore} to ignore threshold when initially write
     * out to ModelConfig.json.
     *
     * @return Convergence threshold.
     */
    @JsonIgnore
    public Double getConvergenceThreshold() {
        return convergenceThreshold;
    }

    @JsonProperty
    public void setConvergenceThreshold(Double convergenceThreshold) {
        this.convergenceThreshold = convergenceThreshold;
    }

    @JsonIgnore
    public Boolean getIsCrossOver() {
        return isCrossOver;
    }

    /**
     * @param isCrossOver
     *            the isCrossOver to set
     */
    @JsonProperty
    public void setIsCrossOver(Boolean isCrossOver) {
        this.isCrossOver = isCrossOver;
    }

    /**
     * @return the isContinuous
     */
    public Boolean getIsContinuous() {
        return isContinuous;
    }

    /**
     * @param isContinuous
     *            the isContinuous to set
     */
    public void setIsContinuous(Boolean isContinuous) {
        this.isContinuous = isContinuous;
    }

    /**
     * @return the workerThreadCount
     */
    public Integer getWorkerThreadCount() {
        return workerThreadCount;
    }

    /**
     * @param workerThreadCount
     *            the workerThreadCount to set
     */
    public void setWorkerThreadCount(Integer workerThreadCount) {
        this.workerThreadCount = workerThreadCount;
    }

    /**
     * @return the upSampleWeight
     */
    @JsonIgnore
    public Double getUpSampleWeight() {
        return upSampleWeight;
    }

    /**
     * @param upSampleWeight
     *            the upSampleWeight to set
     */
    public void setUpSampleWeight(Double upSampleWeight) {
        this.upSampleWeight = upSampleWeight;
    }

}
