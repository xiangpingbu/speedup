package com.ecreditpal.maas.common.schedule.impl;

import com.ecreditpal.maas.common.schedule.Register;
import com.ecreditpal.maas.common.schedule.Subject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/6.
 */
public class ResReload implements Job, Subject {
    private final static Logger log = LoggerFactory.getLogger(ResReload.class);

    //loaded model will be put into this map;
    private static Map<String, Register> registers = new ConcurrentHashMap<>();

    static {
        autoLoad();
    }

    public ResReload() {
    }

    private static void autoLoad() {
//        MinGanCiFilter minGanCiFilter = new MinGanCiFilter();
//        registers.put(minGanCiFilter.toString(),minGanCiFilter);
    }

    @Override
    public void register(Register register) {
        registers.put(register.toString(),register);
    }

    @Override
    public void reload() {
        for (Register register : registers.values()) {
            register.work();
        }
    }
    /**
     * automatically load model resource
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.info("automatically refresh " + registers.size() + "  model resource which registered in the modelInstances");
        reload();
    }

    public static Map<String, Register> getRegister() {
        return registers;
    }
}
