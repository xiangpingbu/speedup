package com.ecreditpal.maas.pmml.builder.impl;

import com.ecreditpal.maas.pmml.builder.creator.AbstractPmmlElementCreator;
import com.ecreditpal.maas.pmml.container.obj.ColumnConfig;
import com.ecreditpal.maas.pmml.container.obj.ModelConfig;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.FieldName;

import java.util.ArrayList;
import java.util.List;



public class DataDictionaryCreator extends AbstractPmmlElementCreator<DataDictionary> {

    public DataDictionaryCreator(ModelConfig modelConfig, List<ColumnConfig> columnConfigList) {
        super(modelConfig, columnConfigList);
    }

    public DataDictionaryCreator(ModelConfig modelConfig, List<ColumnConfig> columnConfigList, boolean isConcise) {
        super(modelConfig, columnConfigList, isConcise);
    }

    @Override
    public DataDictionary build() {
        DataDictionary dict = new DataDictionary();
        List<DataField> fields = new ArrayList<DataField>();

        for(ColumnConfig columnConfig: columnConfigList) {
            if ( isConcise ) {
                if ( columnConfig.isFinalSelect() || columnConfig.isTarget() ) {
                    fields.add(convertColumnToDataField(columnConfig));
                } // else ignore
            } else {
                fields.add(convertColumnToDataField(columnConfig));
            }
        }

        dict.withDataFields(fields);
        dict.withNumberOfFields(fields.size());
        return dict;
    }

    private DataField convertColumnToDataField(ColumnConfig columnConfig) {
        DataField field = new DataField();
        field.setName(FieldName.create(columnConfig.getColumnName()));
        field.setOptype(getOptype(columnConfig));
        field.setDataType(getDataType(field.getOptype()));
        return field;
    }

}
