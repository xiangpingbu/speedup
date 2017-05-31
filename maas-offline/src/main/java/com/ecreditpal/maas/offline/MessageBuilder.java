package com.ecreditpal.maas.offline;

/**
 * @author lifeng
 * @version 1.0 on 2017/5/26.
 */
public class MessageBuilder {
    private static ThreadLocal<StringBuilder> context = new ThreadLocal<StringBuilder>() {
        protected StringBuilder initialValue() {
            return new StringBuilder();
        }
    };

    public static void println(String s) {
        context.get().append(s).append("\n");
    }

    public static void printf(String s, Object... args) {
        context.get().append(String.format(s, args)).append("\n");
    }

    public static String build() {
        return context.get().toString();
    }

}
