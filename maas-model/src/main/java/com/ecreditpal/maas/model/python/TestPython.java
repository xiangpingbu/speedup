package com.ecreditpal.maas.model.python;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author lifeng
 * @version 1.0 on 2017/2/16.
 */
public class TestPython {
    public static void main(String[] args) throws IOException, InterruptedException {
        Process proc = Runtime.getRuntime().exec("python  /Users/lifeng/Work/Python/test/demo.py");
//        InputStream inputStream = proc.getInputStream();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//        String line;
//        while ((line = reader.readLine()) != null) {
//            System.out.println(line);
//        }
        proc.waitFor();
        System.out.println(proc.exitValue());

    }
}
