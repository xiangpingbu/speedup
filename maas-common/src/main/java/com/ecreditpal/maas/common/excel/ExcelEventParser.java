package com.ecreditpal.maas.common.excel;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/17.
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class ExcelEventParser {

    private String filename;
    public SheetContentsHandler handler;

    public ExcelEventParser(String filename){
        this.filename = filename;
    }

    public ExcelEventParser setHandler(SheetContentsHandler handler) {
        this.handler = handler;
        return this;
    }

    public void parse(){
        OPCPackage pkg = null;
        InputStream sheetInputStream = null;

        try {
            pkg = OPCPackage.open(filename, PackageAccess.READ);
            XSSFReader xssfReader = new XSSFReader(pkg);

            StylesTable styles = xssfReader.getStylesTable();
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            sheetInputStream = xssfReader.getSheetsData().next();

            processSheet(styles, strings, sheetInputStream);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }finally {
            if(sheetInputStream != null){
                try {
                    sheetInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
            if(pkg != null){
                try {
                    pkg.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }

    private void processSheet(StylesTable styles, ReadOnlySharedStringsTable strings, InputStream sheetInputStream) throws SAXException, ParserConfigurationException, IOException{
        XMLReader sheetParser = SAXHelper.newXMLReader();

        if(handler != null){
            sheetParser.setContentHandler(new XSSFSheetXMLHandler(styles, strings, handler, false));
        }else{
            sheetParser.setContentHandler(new XSSFSheetXMLHandler(styles, strings, new SimpleSheetContentsHandler(), false));
        }

        sheetParser.parse(new InputSource(sheetInputStream));
    }

    public static class SimpleSheetContentsHandler implements SheetContentsHandler{
        protected List<String> row = new LinkedList<>();

        @Override
        public void startRow(int rowNum) {
            row.clear();
        }

        @Override
        public void endRow(int rowNum) {
            System.err.println(rowNum + " : " + row);
        }

        @Override
        public void cell(String cellReference, String formattedValue, XSSFComment comment) {
            row.add(formattedValue);
        }

        @Override
        public void headerFooter(String text, boolean isHeader, String tagName) {

        }
    }

    public static void main(String[] args) throws Throwable{
        long start = System.currentTimeMillis();

        final List<List<String>> table = new ArrayList<>();
        new ExcelEventParser("/Users/lifeng/Desktop/all_data_selected.xlsx").setHandler(new SimpleSheetContentsHandler(){

            private List<String> fields;

            @Override
            public void endRow(int rowNum) {
                if(rowNum == 0){
                    // 第一行中文描述忽略
                }else if(rowNum == 1){
                    // 第二行字段名
                    fields = row;
                }else {
                    // 数据
                    table.add(row);
                }
            }
        }).parse();

        long end = System.currentTimeMillis();

        System.err.println(table.size());
        System.err.println(end - start);
    }
}
