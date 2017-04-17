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

import com.ecreditpal.maas.pmml.util.CommonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * ModelConfig class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelConfig {

    private ModelBasicConf basic = new ModelBasicConf();

    private ModelSourceDataConf dataSet = new ModelSourceDataConf();

    private ModelStatsConf stats = new ModelStatsConf();

    private ModelVarSelectConf varSelect = new ModelVarSelectConf();

    private ModelNormalizeConf normalize = new ModelNormalizeConf();

    private ModelTrainConf train = new ModelTrainConf();


    public ModelBasicConf getBasic() {
        return basic;
    }

    public void setBasic(ModelBasicConf basic) {
        this.basic = basic;
    }

    public ModelSourceDataConf getDataSet() {
        return dataSet;
    }

    public void setDataSet(ModelSourceDataConf dataSet) {
        this.dataSet = dataSet;
    }

    public ModelStatsConf getStats() {
        return stats;
    }

    public void setStats(ModelStatsConf stats) {
        this.stats = stats;
    }

    public ModelVarSelectConf getVarSelect() {
        return varSelect;
    }

    public void setVarSelect(ModelVarSelectConf varSelect) {
        this.varSelect = varSelect;
    }

    public ModelNormalizeConf getNormalize() {
        return normalize;
    }

    public void setNormalize(ModelNormalizeConf normalize) {
        this.normalize = normalize;
    }

    public ModelTrainConf getTrain() {
        return train;
    }

    public void setTrain(ModelTrainConf train) {
        this.train = train;
    }


    @JsonIgnore
    public String getDataSetDelimiter() {
        return dataSet.getDataDelimiter();
    }

    @JsonIgnore
    public int getBaggingNum() {
        return train.getBaggingNum();
    }

    @JsonIgnore
    public Double getNormalizeStdDevCutOff() {
        return normalize.getStdDevCutOff();
    }

    @JsonIgnore
    public boolean isBinningSampleNegOnly() {
        return stats.getSampleNegOnly();
    }

    @JsonIgnore
    public Double getBinningSampleRate() {
        return stats.getSampleRate();
    }

    @JsonIgnore
    public List<String> getPosTags() {
        return dataSet.getPosTags();
    }

    @JsonIgnore
    public List<String> getMissingOrInvalidValues() {
        return dataSet.getMissingOrInvalidValues();
    }

    @JsonIgnore
    public boolean isBinaryClassification() {
        return (CollectionUtils.isNotEmpty(dataSet.getPosTags()) && CollectionUtils.isNotEmpty(dataSet.getNegTags()));
    }

    @JsonIgnore
    public boolean isMultiClassification() {
        return (CollectionUtils.isNotEmpty(dataSet.getPosTags()) && CollectionUtils.isEmpty(dataSet.getNegTags()))
                || (CollectionUtils.isEmpty(dataSet.getPosTags()) && CollectionUtils.isNotEmpty(dataSet.getNegTags()));
    }

    /**
     * Flattened tags for multiple classification. '1', '2|3' will be flattened to '1', '2', '3'. While '2' and '3' are
     * combined to one class.
     */
    @JsonIgnore
    public List<String> getFlattenTags() {
        return getFlattenTags(dataSet.getPosTags(), dataSet.getNegTags());
    }

    @JsonIgnore
    public List<String> getFlattenTags(List<String> tags1, List<String> tags2) {
        List<String> tags = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(tags1)) {
            for (String tag : tags1) {
                if (tag.contains("|")) {
                    for (String inTag : tag.split("\\|")) {
                        // FIXME, if blank or not
                        if (StringUtils.isNotBlank(inTag)) {
                            tags.add(inTag);
                        }
                    }
                } else {
                    tags.add(tag);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(tags2)) {
            for (String tag : tags2) {
                if (tag.contains("|")) {
                    for (String inTag : tag.split("\\|")) {
                        if (StringUtils.isNotBlank(inTag)) {
                            tags.add(inTag);
                        }
                    }
                } else {
                    tags.add(tag);
                }
            }
        }
        return tags;
    }

    @JsonIgnore
    public List<String> getTags(List<String> tags1, List<String> tags2) {
        List<String> tags = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(tags1)) {
            for (String tag : tags1) {
                tags.add(tag);
            }
        }
        if (CollectionUtils.isNotEmpty(tags2)) {
            for (String tag : tags2) {
                tags.add(tag);
            }
        }
        return tags;
    }

    @JsonIgnore
    public List<String> getTags() {
        return getTags(dataSet.getPosTags(), dataSet.getNegTags());
    }

    @JsonIgnore
    public List<Set<String>> getSetTags(List<String> tags1, List<String> tags2) {
        List<String> tags = getTags(tags1, tags2);
        List<Set<String>> result = new ArrayList<Set<String>>();
        for (String tag : tags) {
            Set<String> set = new HashSet<String>(16);
            if (tag.contains("|")) {
                for (String inTag : tag.split("\\|")) {
                    if (StringUtils.isNotBlank(inTag)) {
                        set.add(inTag);
                    }
                }
            } else {
                set.add(tag);
            }
            result.add(set);
        }
        return result;
    }


    @JsonIgnore
    public List<String> getNegTags() {
        return dataSet.getNegTags();
    }


    @JsonIgnore
    public ModelNormalizeConf.NormType getNormalizeType() {
        return normalize.getNormType();
    }


    @JsonIgnore
    public String getAlgorithm() {
        return train.getAlgorithm();
    }

    @JsonIgnore
    public String getModelSetName() {
        return basic.getName();
    }


    @JsonIgnore
    public String getDataSetRawPath() {
        return dataSet.getDataPath();
    }

    @JsonIgnore
    public boolean isVariableStoreEnabled() {
        // default - disable
        return false;
    }

    @JsonIgnore
    public String getTargetColumnName() {
        return dataSet.getTargetColumnName();
    }




    @JsonIgnore
    public String getHeaderDelimiter() {
        return dataSet.getHeaderDelimiter();
    }

    @JsonIgnore
    public ModelStatsConf.BinningAlgorithm getBinningAlgorithm() {
        return this.stats.getBinningAlgorithm();
    }

    @JsonIgnore
    public void setBinningAlgorithm(ModelStatsConf.BinningAlgorithm binningAlgorithm) {
        this.stats.setBinningAlgorithm(binningAlgorithm);
    }

    @JsonIgnore
    public String getPsiColumnName() {
        return this.stats.getPsiColumnName();
    }

    @JsonIgnore
    public void setPsiColumnName(String columnName) {
        this.stats.setPsiColumnName(columnName);
    }



    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ModelConfig)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        ModelConfig mc = (ModelConfig) obj;
        return mc.getBasic().equals(basic);
    }

    /**
     * Auto generated by eclipse
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((basic == null) ? 0 : basic.hashCode());
        return result;
    }

}
