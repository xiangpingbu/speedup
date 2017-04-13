package com.ecreditpal.maas.model.variables;

import com.ecreditpal.maas.common.utils.file.FileUtil;
import com.google.common.collect.Lists;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;


/**
 * @author lifeng
 * @CreateTime 2017/4/8.
 */
public class VariableContentHandler extends DefaultHandler {
    private VariableConfiguration v;
    private Variable variable;
    public List<Variable> variableList;
    private StringBuilder sb;



    @Override
    public void startDocument() throws SAXException {
        v = new VariableConfiguration();
    }


    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        sb = new StringBuilder();

        if ("Variables".equals(name)) {
            variableList = Lists.newArrayList();
            v.setVariables(variableList);
        } else if ("Variable".equals(name)) {
            String className = attributes.getValue(1);
            try {
                variable = (Variable) Class.forName(className).newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            variable.setName(attributes.getValue(0));
            variableList.add(variable);
        } else if ("property".equals(name)) {
            String nameType = attributes.getValue(0);
            String value = attributes.getValue(1);
            Field[] allFields = Variable.class.getDeclaredFields();
            for (Field field : allFields) {
                if (field.getName().equals(nameType)) {
                    String firstLetter = field.getName().substring(0, 1)
                            .toUpperCase();
                    String setter = "set" + firstLetter
                            + field.getName().substring(1);
                    try {
                        Method method = Variable.class.getMethod(setter,field.getType());
                        method.invoke(variable, value);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        String s = new String(ch, start, length).trim();
        if (s.length() == 0) {
            return;
        }
        if (s.contains("\n")) {
            sb.append(s.replace("\n", "").replace(" ", ""));
        }
    }
    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        if ("Model".equals(name)) {
            v.setModel(sb.toString());
        }
    }
    public void endDocument()
            throws SAXException {
        // no op
    }

    public static VariableConfiguration readXML(InputStream inStream) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser saxParser = spf.newSAXParser(); // 创建解析器
        // 设置解析器的相关特性，http://xml.org/sax/features/namespaces = true
        // 表示开启命名空间特性
        // saxParser.setProperty("http://xml.org/sax/features/namespaces",true);
        VariableContentHandler handler = new VariableContentHandler();
        saxParser.parse(inStream, handler);
        inStream.close();

        return handler.getV();
    }


    public static VariableConfiguration readXML(String path) throws Exception {
        return readXML(new FileInputStream(path));
    }

    public VariableConfiguration getV() {
        return v;
    }



}
