package com.ecreditpal.maas.common.utils;

import java.util.List;
import java.util.Map;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/17.
 */
public class ExcelContent {
    private Map<String, Integer> head;
    private List<List<String>> content;

    public Map<String, Integer> getHead() {
        return head;
    }

    public void setHead(Map<String, Integer> head) {
        this.head = head;
    }

    public List<List<String>> getContent() {
        return content;
    }

    public void setContent(List<List<String>> content) {
        this.content = content;
    }
}
