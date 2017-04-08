package com.ecreditpal.maas.model.variables;

import com.ecreditpal.maas.common.file.FileUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;


/**
 * @author lifeng
 * @CreateTime 2017/4/8.
 */
public class XMLContentHandler extends DefaultHandler {
    VariableConfiguration v;
    String trmp="";

    private Variable variable;
    public List<Variable> variableList;
    private String node;
    private StringBuilder sb;
    private boolean flag = false;
    @Override
    public void startDocument() throws SAXException {
         v = new VariableConfiguration();
    }


    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        flag = true;
        if ("Variable".equals(name)) {
            variable = new Variable();
        }
        node = name;
        sb = new StringBuilder();

        System.out.println(attributes.getValue("name"));
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(!flag) {
            return;
        }
        sb.append(new String(ch, start, length) );
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        flag = false;
        if( name.equals("Variable")){
            variableList.add(variable);
        }
//        System.out.println(sb);
    }

    public static void readXML(InputStream inStream) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser saxParser = spf.newSAXParser(); // 创建解析器
        // 设置解析器的相关特性，http://xml.org/sax/features/namespaces = true
        // 表示开启命名空间特性
        // saxParser.setProperty("http://xml.org/sax/features/namespaces",true);
        XMLContentHandler handler = new XMLContentHandler();
        saxParser.parse(inStream, handler);
        inStream.close();
    }

    public static void main(String[] args) throws Exception {
        String filePath = FileUtil.getFilePath("model_config/test_variables.xml");
        InputStream inputStream = new FileInputStream(filePath);
        readXML(inputStream);


    }

}
