package com.ecreditpal.maas.offline.audit.apply;

import com.ecreditpal.maas.common.excel.ExcelContent;
import com.ecreditpal.maas.common.excel.ExcelReaderUtil;
import com.ecreditpal.maas.common.excel.ExcelRowReader;
import com.ecreditpal.maas.offline.MessageBuilder;
import com.google.gson.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author lifeng
 * @version 1.0 on 2017/5/24.
 */
public class ToolApply {
    /**
     * @param filePath     原始文件路径
     * @param columnConfig 变量对应的woe,json形式
     * @return
     * @throws Exception
     */
    public static ExcelContent getApplyed(String filePath, String columnConfig) throws Exception {
        ExcelRowReader rowReader = new ExcelRowReader();
        ExcelReaderUtil.readExcel(rowReader, filePath);
        ExcelContent content = rowReader.getRows();
        LinkedHashMap<String, Integer> head = content.getHead();
        LinkedHashMap<String, Integer> newHead = new LinkedHashMap<>();

        List<List<String>> rows = content.getContent();
        Map<String, List<ApplyVariable>> variables = new HashMap<>();

        JsonParser jsonParser = new JsonParser();
        JsonElement element = jsonParser.parse(columnConfig);
        JsonObject jsonObject = element.getAsJsonObject().getAsJsonObject("data");
        for (Map.Entry<String, JsonElement> s : jsonObject.entrySet()) {
            JsonArray binnings = s.getValue().getAsJsonArray();
            List<ApplyVariable> list = new ArrayList<>();
            for (JsonElement binning : binnings) {
                JsonObject bin = binning.getAsJsonObject();
                ApplyVariable v = new ApplyVariable();

                if ("Numerical".equals(bin.get("type").getAsString())) {
                    v.setType(true);
                    v.setMaxBoundary(bin.get("max_boundary").getAsString());
                    v.setMinBoundary(bin.get("min_boundary").getAsString());
                } else {
                    v.setType(false);
                    Set<String> set = new HashSet<>();
                    bin.get(s.getKey()).getAsJsonArray().forEach(a -> set.add(a.getAsString()));
                    v.setCategoricalValue(set);
                }
                v.setWoe(bin.get("woe").getAsString());
                list.add(v);
            }
            variables.put(s.getKey(), list);
            int headSize = head.size();
            head.put(s.getKey() + "_woe", headSize);
        }

        rows.forEach(row -> applyGetWoeValue(row, newHead, variables, head));
        content.setHead(newHead);
        return content;
    }

    public static void compare(String appliedFile, String originFile, String variableWoe) throws Exception {
        ExcelRowReader readerA = new ExcelRowReader();
        ExcelReaderUtil.readExcel(readerA, appliedFile);
        Map<String, Integer> head = readerA.getRows().getHead();

        ExcelContent excelContent = getApplyed(originFile, variableWoe);
        Map<String, Integer> newHead = excelContent.getHead();
        List<List<String>> newContents = excelContent.getContent();

        for (int i = 0; i < readerA.getRows().getContent().size(); i++) {
            List<String> applyed = readerA.getRows().getContent().get(i);
            List<String> newContent = newContents.get(i);

            for (String s : newHead.keySet()) {
                int i1 = head.get(s);
                int i2 = newHead.get(s);
                String s1 = applyed.get(i1);
                String s2 = newContent.get(i2);
                BigDecimal b1 = new BigDecimal(s1).setScale(4, BigDecimal.ROUND_HALF_UP);
                BigDecimal b2 = new BigDecimal(s2).setScale(4, BigDecimal.ROUND_HALF_UP);
                if (b1.compareTo(b2) != 0) {
                    Integer rowNum = i;
                    Integer columnNum = i1;
                    MessageBuilder.printf("第%s行第%s列的%s不匹配,python结果为:%s,java结果为%s",rowNum,columnNum,s,s1,s2);
                }
            }
        }
    }

    private static void applyGetWoeValue(List<String> columns, Map<String, Integer> newHead, Map<String, List<ApplyVariable>> keys, Map<String, Integer> head) {
        for (String s : keys.keySet()) {
            String value = columns.get(head.get(s));
            int i = 0;
            for (ApplyVariable applyVariable : keys.get(s)) {
                String woe = applyVariable.getWoe(value);
                if (woe != null) {
                    columns.add(woe);
                    break;
                } else {
                    if (i == keys.get(s).size() - 1) {
                        columns.add("0.0");
                    }
                }
                i++;
            }
            newHead.put(s + "_woe", columns.size() - 1);
        }
    }
}
