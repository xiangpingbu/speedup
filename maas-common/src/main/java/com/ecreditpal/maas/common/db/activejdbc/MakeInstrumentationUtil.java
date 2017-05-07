package com.ecreditpal.maas.common.db.activejdbc;

import org.javalite.instrumentation.Instrumentation;

/**
 * @author lifeng
 * @version 1.0 on 2017/5/1.
 */
public class MakeInstrumentationUtil {
    public static void make(String rootpath) {
        Instrumentation instrumentation = new Instrumentation();
        String path =rootpath+"/maas-model/target/classes/";
        instrumentation.setOutputDirectory(path);
        instrumentation.instrument();
    }
}
