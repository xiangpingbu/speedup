package com.ecreditpal.maas.pmml.processor;

/**
 * interface for ModelProcessor
 */
public interface Processor {

    /**
     * The run function for each processor
     *
     * @return 0 - if the processor run successfully
     * other - if there is any wrong when the processor executes
     * @throws Exception Exception when processor executes
     */
    public int run() throws Exception;
}
