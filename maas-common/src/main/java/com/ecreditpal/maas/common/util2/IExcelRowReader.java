package com.ecreditpal.maas.common.util2;

import java.util.List;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/17.
 */
public interface IExcelRowReader {
    /**
     * 业务逻辑实现方法
     *
     * @param sheetIndex
     * @param curRow
     * @param rowlist
     */
    void getRows(int sheetIndex, int curRow, List<String> rowlist);
}
