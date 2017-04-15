package com.ecreditpal.maas.model.variables;

import com.ecreditpal.maas.common.utils.file.FileUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * @author lifeng
 * @CreateTime 2017/4/8.
 */
public class VariableContentHandler extends DefaultHandler {
    private VariableConfiguration v;
    private Variable variable;
    public List<Variable> variableList;
    private StringBuilder sb;
    private static Map<String, Object> globalBeanMap = Maps.newHashMap();

    private Map<String, Field> fieldMap;


    @Override
    public void startDocument() throws SAXException {
        v = new VariableConfiguration();
    }


    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        sb = new StringBuilder();
        if ("bean".equals(name)) {
            try {
                Object bean = Class.forName(attributes.getValue("class")).newInstance();
                globalBeanMap.put(attributes.getValue("id"), bean);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else if ("Variables".equals(name)) {
            variableList = Lists.newArrayList();
            v.setVariables(variableList);
        } else if ("Variable".equals(name)) {
            String className = attributes.getValue(1); //Variable的子类的全限定名
            try {
                variable = (Variable) Class.forName(className).newInstance();
                Field[] superFields = variable.getClass().getSuperclass().getDeclaredFields();
                Field[] fields = variable.getClass().getDeclaredFields();
                fieldMap = Maps.newHashMap();
                for (Field superField : superFields) {
                    fieldMap.put(superField.getName(), superField);
                }
                for (Field field : fields) {
                    fieldMap.put(field.getName(), field);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            variable.setName(attributes.getValue("name")); //指定的variable的名称
            variableList.add(variable);
        } else if ("property".equals(name)) {
            String fieldName = attributes.getValue("name"); //variable的值域
            String value = attributes.getValue("value");  //值域的值

            /*通过反射的方式注入对应的值*/
            Field field = fieldMap.get(fieldName);
            if (field != null) {
                String firstLetter = field.getName().substring(0, 1)
                        .toUpperCase();
                String setter = "set" + firstLetter
                        + field.getName().substring(1);
                try {
                    Method method = variable.getClass().getMethod(setter, field.getType());
                    if (value != null) {
                        method.invoke(variable, value);
                    }
                    else {
                        String ref = attributes.getValue("ref");
                        Object obj = globalBeanMap.get(ref);
                        method.invoke(variable, obj);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
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
