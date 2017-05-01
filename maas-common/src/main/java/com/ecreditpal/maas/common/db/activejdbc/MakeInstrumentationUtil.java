package com.ecreditpal.maas.common.db.activejdbc;

import org.javalite.instrumentation.Instrumentation;

/**
 * @author lifeng
 * @version 1.0 on 2017/5/1.
 */
public class MakeInstrumentationUtil {
    public static void make() {
        Instrumentation instrumentation = new Instrumentation();
        instrumentation.setOutputDirectory(ClassLoader.getSystemResource("").getPath());
        instrumentation.instrument();
    }
}
