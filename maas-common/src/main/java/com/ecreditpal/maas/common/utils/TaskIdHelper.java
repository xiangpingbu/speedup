package com.ecreditpal.maas.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @author lifeng
 * @CreateTime 2017/4/12.
 */
public class TaskIdHelper {
    public static final Logger logger = LoggerFactory.getLogger(TaskIdHelper.class);

    public static String generateTaskId() {
        String taskId = UUID.randomUUID().toString().replace("-", "");
        logger.debug("generated taskId {}", taskId);
        return taskId;
    }
}
