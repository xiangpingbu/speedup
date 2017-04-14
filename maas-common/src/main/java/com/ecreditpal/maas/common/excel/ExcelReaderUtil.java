package com.ecreditpal.maas.common.excel;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/17.
 */

/**
 * 名称: ExcelReaderUtil.java<br>
 * 描述: <br>
 * 类型: JAVA<br>
 * 最近修改时间:2016年7月5日 上午10:10:20<br>
 *
 * @since 2016年7月5日
 * @author “”
 */
public class ExcelReaderUtil {
    // excel2003扩展名
    public static final String EXCEL03_EXTENSION = ".xls";
    // excel2007扩展名
    public static final String EXCEL07_EXTENSION = ".xlsx";

    /**
     * 读取Excel文件，可能是03也可能是07版本
     *
     * @param fileName
     * @throws Exception
     */
    public static void readExcel(IExcelRowReader reader, String fileName) throws Exception {
        // 处理excel2003文件
        if (fileName.endsWith(EXCEL03_EXTENSION)) {
            ExcelXlsReader exceXls = new ExcelXlsReader();
            exceXls.setRowReader(reader);
            exceXls.process(fileName);
            // 处理excel2007文件
        } else if (fileName.endsWith(EXCEL07_EXTENSION)) {
            ExcelXlsxReader exceXlsx = new ExcelXlsxReader();
            exceXlsx.setRowReader(reader);
            exceXlsx.process(fileName);
        } else {
            throw new Exception("文件格式错误，fileName的扩展名只能是xls或xlsx。");
        }
    }

    /**
     * 测试
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ExcelRowReader rowReader = new ExcelRowReader();
        ExcelReaderUtil.readExcel(rowReader, "/Users/lifeng/Desktop/all_data_selected.xlsx");
        ExcelContent content = rowReader.getRows();
    }
}
