package com.ecreditpal.maas.common.schedule.drive;

import com.ecreditpal.maas.common.schedule.impl.ResReload;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author lifeng
 * @version 1.0 on 2017/2/23.
 */
public class ModelReloadSchedule {

    public void init(){
        Scheduler scheduler;
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            JobDetail jobDetail = newJob(null).
                    withIdentity("model_resource_reload","group_1").
                    build();
            Trigger trigger=
                    newTrigger()
                            .withIdentity("model_resource_reload","group_1")
                            .withSchedule(cronSchedule("0/30 * * * * ?")
                            )
                            .build();
            scheduler.start();
            scheduler.scheduleJob(jobDetail,trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
