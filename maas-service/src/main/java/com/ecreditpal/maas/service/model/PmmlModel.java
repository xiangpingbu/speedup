package com.ecreditpal.maas.service.model;

import com.ecreditpal.maas.common.utils.PMMLUtils;
import com.ecreditpal.maas.service.model.variables.Variable;
import lombok.extern.slf4j.Slf4j;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.*;

import java.util.*;

/**
 * @author lifeng
 * @version 1.0 on 2017/6/1.
 */
@Slf4j
public abstract class PmmlModel{
    public  PMML pmml;
    public  Evaluator evaluator;

    /**
     * load pmml file and generate evaluator
     */
    public  void pmmlFileLoad(String pmmlPath) {
        try {
            pmml = PMMLUtils.loadPMML(pmmlPath);
            Model m = pmml.getModels().get(0);
            evaluator = ModelEvaluatorFactory.getInstance().getModelManager(pmml, m);
        } catch (Exception e) {
            log.error("load pmml file error !", e);
        }
    }

    public ArrayList<Double> getScores(Map<String,Variable> variableMap, String resultFieldName) {
        List<Map<FieldName, FieldValue>> input = prepareModelInput(evaluator, variableMap);
        ArrayList<Double> scores = new ArrayList<>();

        for (Map<FieldName, FieldValue> maps : input) {
            Map<FieldName, Double> regressionTerm = (Map<FieldName, Double>) evaluator.evaluate(maps);
            scores.add(regressionTerm.get(new FieldName(resultFieldName)));
        }

        return scores;
    }

    /**
     * pmml model used only
     * run after all the variable calculations are done
     * prepare model variables for model execution
     * @param evaluator
     * @param variableMap
     * @return variable map <name, variable value>
     */
    public List<Map<FieldName, FieldValue>> prepareModelInput(Evaluator evaluator, Map<String, Variable> variableMap) {
        List<FieldName> groupFields = evaluator.getGroupFields();
        List<Map<FieldName, Object>> stringRows = new ArrayList<Map<FieldName, Object>>();
        Map<FieldName, Object> stringRow = new LinkedHashMap<FieldName, Object>();

        for (Map.Entry<String, Variable> entry : variableMap.entrySet()) {
            FieldName name = FieldName.create(entry.getKey());
            String value = entry.getValue().getValue().toString();
            if (("").equals(value) || ("NA").equals(value) || ("N/A").equals(value)) {
                value = null;
            }
            stringRow.put(name, value);
        }

        stringRows.add(stringRow);
        if (groupFields.size() == 1) {
            FieldName groupField = groupFields.get(0);

            stringRows = EvaluatorUtil.groupRows(groupField, stringRows);
        } else if (groupFields.size() > 1) {
            throw new EvaluationException();
        }

        List<Map<FieldName, FieldValue>> fieldValueRows = new ArrayList<Map<FieldName, FieldValue>>();

        for (Map<FieldName, Object> sr : stringRows) {
            Map<FieldName, FieldValue> fieldValueRow = new LinkedHashMap<FieldName, FieldValue>();

            Collection<Map.Entry<FieldName, Object>> entries = sr.entrySet();
            for (Map.Entry<FieldName, Object> entry : entries) {
                FieldName name = entry.getKey();
                // Pre Data process: for numeric variable convert non-double
                // value to null.
                if (evaluator.getDataField(name).getDataType() == DataType.DOUBLE) {
                    try {
                        Double.parseDouble((String) entry.getValue());
                    } catch (Exception e) {
                        entry.setValue(null);
                    }
                }
                FieldValue value = EvaluatorUtil.prepare(evaluator, name, entry.getValue());
                fieldValueRow.put(name, value);
            }

            fieldValueRows.add(fieldValueRow);
        }

        return fieldValueRows;
    }
}
