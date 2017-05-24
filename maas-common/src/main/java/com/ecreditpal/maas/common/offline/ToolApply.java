package com.ecreditpal.maas.common.offline;

import com.ecreditpal.maas.common.excel.ExcelContent;
import com.ecreditpal.maas.common.excel.ExcelReaderUtil;
import com.ecreditpal.maas.common.excel.ExcelRowReader;
import com.google.gson.*;

import java.util.*;

/**
 * @author lifeng
 * @version 1.0 on 2017/5/24.
 */
public class ToolApply {
    public static void main(String[] args) throws Exception {
        ExcelRowReader rowReader = new ExcelRowReader();
        ExcelReaderUtil.readExcel(rowReader, "/Users/lifeng/Desktop/111/model_data.xlsx");
        ExcelContent content = rowReader.getRows();
        LinkedHashMap<String,Integer> head = content.getHead();
        List<List<String>> rows =  content.getContent();
        Map<String,List<ApplyVariable>> variables = new HashMap<>();

        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(json);
        JsonObject jsonObject = element.getAsJsonObject().getAsJsonObject("data");
        for (Map.Entry<String, JsonElement> s : jsonObject.entrySet()) {
           JsonArray binnings =  s.getValue().getAsJsonArray();
           List<ApplyVariable> list = new ArrayList<>();
            for (JsonElement binning : binnings) {
                JsonObject bin = binning.getAsJsonObject();
                ApplyVariable v = new ApplyVariable();

                if ("Numerical".equals(bin.get("type").getAsString())){
                    v.setType(true);
                    v.setMaxBoundary(bin.get("max_boundary").getAsString());
                    v.setMinBoundary(bin.get("min_boundary").getAsString());
                } else{
                    v.setType(false);
                        Set<String> set = new HashSet<>();
                        bin.get(s.getKey()).getAsJsonArray().forEach(a -> set.add(a.getAsString()));
                        v.setCategoricalValue(set);
                }
                v.setWoe(bin.get("woe").getAsString());
                list.add(v);
            }
            variables.put(s.getKey(), list);
            head.put(s.getKey()+"_woe",head.size());
        }

        rows.forEach(row -> applyGetWoeValue(row,variables,head));
        System.out.println();
    }

    private static void applyGetWoeValue(List<String> columns,Map<String,List<ApplyVariable>> keys,Map<String,Integer> head) {
        for (String s : keys.keySet()) {
            String value = columns.get(head.get(s));
            int i= 0;
            for (ApplyVariable applyVariable : keys.get(s)) {
                String woe = applyVariable.getWoe(value);
                if (woe != null){
                    columns.add(woe);
                    break;
                } else{
                    if (i == keys.get(s).size()-1) {
                        columns.add("0.0");
                    }
                }
                i++;
            }
        }
    }


    public static String json = "{\n" +
            "    \"data\": {\n" +
            "        \"手机入网时间\": [{\n" +
            "            \"手机入网时间\": [\"5年以上\", \"nan\"],\n" +
            "            \"woe\": \"-0.2903\",\n" +
            "            \"binNum\": \"0\",\n" +
            "            \"type\": \"Categorical\"\n" +
            "        }, {\n" +
            "            \"手机入网时间\": [\"3--5年\"],\n" +
            "            \"woe\": \"0.0267\",\n" +
            "            \"binNum\": \"1\",\n" +
            "            \"type\": \"Categorical\"\n" +
            "        }, {\n" +
            "            \"手机入网时间\": [\"1--3年\"],\n" +
            "            \"woe\": \"0.1479\",\n" +
            "            \"binNum\": \"2\",\n" +
            "            \"type\": \"Categorical\"\n" +
            "        }, {\n" +
            "            \"手机入网时间\": [\"6-12个月\"],\n" +
            "            \"woe\": \"0.2521\",\n" +
            "            \"binNum\": \"3\",\n" +
            "            \"type\": \"Categorical\"\n" +
            "        }],\n" +
            "        \"年龄\": [{\n" +
            "            \"max\": \"22.00000\",\n" +
            "            \"min\": \"18.00000\",\n" +
            "            \"min_boundary\": \"0\",\n" +
            "            \"max_boundary\": \"23.00000\",\n" +
            "            \"woe\": \"-0.1085\",\n" +
            "            \"binNum\": \"1\",\n" +
            "            \"type\": \"Numerical\"\n" +
            "        }, {\n" +
            "            \"max\": \"35.00000\",\n" +
            "            \"min\": \"23.00000\",\n" +
            "            \"min_boundary\": \"23.00000\",\n" +
            "            \"max_boundary\": \"36.00000\",\n" +
            "            \"woe\": \"0.0075\",\n" +
            "            \"binNum\": \"2\",\n" +
            "            \"type\": \"Numerical\"\n" +
            "        }, {\n" +
            "            \"max\": \"42.00000\",\n" +
            "            \"min\": \"36.00000\",\n" +
            "            \"min_boundary\": \"36.00000\",\n" +
            "            \"max_boundary\": \"43.00000\",\n" +
            "            \"woe\": \"0.0528\",\n" +
            "            \"binNum\": \"3\",\n" +
            "            \"type\": \"Numerical\"\n" +
            "        }, {\n" +
            "            \"max\": \"54.00000\",\n" +
            "            \"min\": \"43.00000\",\n" +
            "            \"min_boundary\": \"43.00000\",\n" +
            "            \"max_boundary\": \"inf\",\n" +
            "            \"woe\": \"-0.1373\",\n" +
            "            \"binNum\": \"4\",\n" +
            "            \"type\": \"Numerical\"\n" +
            "        }],\n" +
            "        \"性别\": [{\n" +
            "            \"性别\": [\"女\"],\n" +
            "            \"woe\": \"-0.3294\",\n" +
            "            \"binNum\": \"0\",\n" +
            "            \"type\": \"Categorical\"\n" +
            "        }, {\n" +
            "            \"性别\": [\"男\"],\n" +
            "            \"woe\": \"0.1026\",\n" +
            "            \"binNum\": \"1\",\n" +
            "            \"type\": \"Categorical\"\n" +
            "        }],\n" +
            "        \"工作年限\": [{\n" +
            "            \"max\": \"1.00000\",\n" +
            "            \"min\": \"1.00000\",\n" +
            "            \"min_boundary\": \"0\",\n" +
            "            \"max_boundary\": \"2.00000\",\n" +
            "            \"woe\": \"-0.1525\",\n" +
            "            \"binNum\": \"1\",\n" +
            "            \"type\": \"Numerical\"\n" +
            "        }, {\n" +
            "            \"max\": \"2.00000\",\n" +
            "            \"min\": \"2.00000\",\n" +
            "            \"min_boundary\": \"2.00000\",\n" +
            "            \"max_boundary\": \"3.00000\",\n" +
            "            \"woe\": \"-0.1979\",\n" +
            "            \"binNum\": \"2\",\n" +
            "            \"type\": \"Numerical\"\n" +
            "        }, {\n" +
            "            \"max\": \"8.00000\",\n" +
            "            \"min\": \"3.00000\",\n" +
            "            \"min_boundary\": \"3.00000\",\n" +
            "            \"max_boundary\": \"9.00000\",\n" +
            "            \"woe\": \"-0.0889\",\n" +
            "            \"binNum\": \"3\",\n" +
            "            \"type\": \"Numerical\"\n" +
            "        }, {\n" +
            "            \"max\": \"13.00000\",\n" +
            "            \"min\": \"9.00000\",\n" +
            "            \"min_boundary\": \"9.00000\",\n" +
            "            \"max_boundary\": \"14.00000\",\n" +
            "            \"woe\": \"-0.1887\",\n" +
            "            \"binNum\": \"4\",\n" +
            "            \"type\": \"Numerical\"\n" +
            "        }, {\n" +
            "            \"max\": \"15.00000\",\n" +
            "            \"min\": \"14.00000\",\n" +
            "            \"min_boundary\": \"14.00000\",\n" +
            "            \"max_boundary\": \"inf\",\n" +
            "            \"woe\": \"0.4333\",\n" +
            "            \"binNum\": \"5\",\n" +
            "            \"type\": \"Numerical\"\n" +
            "        }],\n" +
            "        \"公司性质\": [{\n" +
            "            \"公司性质\": [\"国有股份\", \"机关及事业单位\", \"社会团体\"],\n" +
            "            \"woe\": \"-0.4807\",\n" +
            "            \"binNum\": \"0\",\n" +
            "            \"type\": \"Categorical\"\n" +
            "        }, {\n" +
            "            \"公司性质\": [\"个体\"],\n" +
            "            \"woe\": \"-0.1612\",\n" +
            "            \"binNum\": \"1\",\n" +
            "            \"type\": \"Categorical\"\n" +
            "        }, {\n" +
            "            \"公司性质\": [\"nan\", \"民营\", \"外资\"],\n" +
            "            \"woe\": \"-0.067\",\n" +
            "            \"binNum\": \"2\",\n" +
            "            \"type\": \"Categorical\"\n" +
            "        }, {\n" +
            "            \"公司性质\": [\"私营\", \"合资\"],\n" +
            "            \"woe\": \"0.6192\",\n" +
            "            \"binNum\": \"3\",\n" +
            "            \"type\": \"Categorical\"\n" +
            "        }]\n" +
            "    },\n" +
            "    \"target\": \"bad_4w\",\n" +
            "    \"modelName\": \"model_data\",\n" +
            "    \"branch\": \"master\"\n" +
            "}";
}
