package com.ecreditpal.maas.model.reload;

import com.ecreditpal.maas.model.model.minganci.MinGanCiFilter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lifeng
 * @version 1.0 on 2017/3/6.
 */
@Slf4j
public class ResReload implements  Subject {

    //loaded model will be put into this map;
    private static Map<String, Register> registers = new ConcurrentHashMap<>();

    static {
        autoLoad();
    }

    public ResReload() {
    }

    private static void autoLoad() {
        MinGanCiFilter minGanCiFilter = new MinGanCiFilter();
        registers.put(minGanCiFilter.toString(),minGanCiFilter);
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

    public void execute() {
        log.info("automatically refresh " + registers.size() + "  model resource which registered in the modelInstances");
        reload();
    }

    public static Map<String, Register> getRegister() {
        return registers;
    }
}
