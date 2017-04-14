package com.ecreditpal.maas.common.offline;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

/**
 * @author lifeng
 * @CreateTime 2017/4/14.
 */
public class XybJsonConvert {
    public static void main(String[] args) throws IOException {
        JsonParser parser = new JsonParser();
        String path = "/Users/lifeng/Desktop/xinyongbao/4.0/\\263\\302\\210\\220\\263\\366_231084197402014011_15045332240.json";
        Reader reader = new FileReader(path);
//        BufferedReader bis = new BufferedReader( reader);
//
//        StringBuilder sb = new StringBuilder();
//        char[] chars = new char[512];
//        int read;
//        while ((read = bis.read(chars)) !=-1) {
//            sb.append(chars,0,read);
//        }

      JsonObject jsonObject =  parser.parse(reader).getAsJsonObject();
        System.out.println(jsonObject.toString());
    }
}
