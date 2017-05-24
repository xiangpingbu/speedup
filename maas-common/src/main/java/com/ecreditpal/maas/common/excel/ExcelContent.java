package com.ecreditpal.maas.common.excel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/17.
 */
public class ExcelContent {
    private LinkedHashMap<String, Integer> head;
    private List<List<String>> content;

    public LinkedHashMap<String, Integer> getHead() {
        return head;
    }

    public void setHead(LinkedHashMap<String, Integer> head) {
        this.head = head;
    }

    public List<List<String>> getContent() {
        return content;
    }

    public void setContent(List<List<String>> content) {
        this.content = content;
    }
}
