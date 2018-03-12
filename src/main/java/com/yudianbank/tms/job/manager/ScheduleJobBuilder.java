package com.yudianbank.tms.job.manager;

import com.yudianbank.tms.model.vo.ScheduleJobVO;
import org.quartz.*;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 任务构建工具类
 *
 * @author Song Lea
 */
public class ScheduleJobBuilder {

    // 任务类型:CRON表达式任务;单次任务;周期任务;循环任务
    public enum JobTypeEnum {
        CRON_JOB, SINGLE_JOB, PERIODIC_JOB, LOOP_JOB
    }

    // 任务执行的策略:每秒;每分;每小时;每天;每周;每月
    public enum StrategyEnum {
        SECONDS, MINUTE, HOUR, DAY, WEEK, MONTH
    }

    // 根据任务类型来构建任务
    public static boolean builderScheduleJobByType(Scheduler scheduler, ScheduleJobVO scheduleJobVO,
                                                   Map<String, Object> dataMap) throws SchedulerException {
        // 对传入的ScheduleJobVO参数的属性验证
        Assert.notNull(scheduleJobVO, "构建作业时参数对象(ScheduleJobVO)为空！");
        Assert.notNull(scheduleJobVO.getJobClass(), "构建作业时作业执行类(jobClass)为空！");
        String jobName = scheduleJobVO.getJobName();
        Assert.notNull(jobName, "构建作业时作业名(jobName)为空！");
        String jobGroup = scheduleJobVO.getJobGroup();
        Assert.notNull(jobGroup, "构建作业时作业组(jobGroup)为空！");
        JobTypeEnum jobTypeEnum = scheduleJobVO.getJobTypeEnum();
        Assert.notNull(jobTypeEnum, "构建作业时作业类型枚举(JobTypeEnum)为空！");
        // 先获取是否有这个任务,以便继承已有任务的JobDataMap
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        JobDataMap jobDataMap;
        if (scheduler.checkExists(jobKey)) jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
        else jobDataMap = new JobDataMap();
        // 将方法传入的变量与值也保存到JobDataMap
        if (dataMap != null && dataMap.size() > 0) {
            for (Map.Entry<String, Object> entry : dataMap.entrySet())
                jobDataMap.put(entry.getKey(), entry.getValue());
        }
        // 构建JobDetail
        JobDetail jobDetail = JobBuilder.newJob(scheduleJobVO.getJobClass())
                .withIdentity(jobName, jobGroup).withDescription(scheduleJobVO.getDescription())
                .storeDurably(true).usingJobData(jobDataMap).build();
        // 构建TriggerBuilder
        TriggerBuilder triggerBuilder = null;
        switch (jobTypeEnum) {
            case CRON_JOB:
                triggerBuilder = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder
                        .cronSchedule(scheduleJobVO.getCron())
                        //设置失败指令:当job因为执行时间过长而错过触发器时,所有的misfire不管,执行下一个周期的任务
                        .withMisfireHandlingInstructionDoNothing()).withIdentity(jobName, jobGroup)
                        .forJob(jobDetail);
                break;
            case SINGLE_JOB:
                triggerBuilder = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup)
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(5).withRepeatCount(0)
                                // 设置失败指令:失效之后再恢复并马上执行
                                .withMisfireHandlingInstructionFireNow()).forJob(jobDetail);
                break;
            case PERIODIC_JOB:
                triggerBuilder = buildIntervalTrigger(jobName, jobGroup, scheduleJobVO.getInterval(),
                        scheduleJobVO.getStrategyEnum()).forJob(jobDetail);
                break;
            case LOOP_JOB:
                triggerBuilder = buildLoopTrigger(jobName, jobGroup, scheduleJobVO.getInterval(),
                        scheduleJobVO.getRepeatCount(), scheduleJobVO.getStrategyEnum())
                        .forJob(jobDetail);
                break;
        }
        if (Objects.isNull(triggerBuilder))
            throw new SchedulerException("构建作业时触发器创建者(TriggerBuilder)为空！");
        // 指定作业运行的开始与结束时间,若不指定则默认为startNow且runForever
        Date now = new Date();
        Date startRunTime = scheduleJobVO.getStartRunTime();
        if (startRunTime != null && now.getTime() < startRunTime.getTime())
            triggerBuilder.startAt(startRunTime);
        Date endRunTime = scheduleJobVO.getEndRunTime();
        if (endRunTime != null && endRunTime.getTime() > now.getTime())
            triggerBuilder.endAt(endRunTime);
        Trigger trigger = triggerBuilder.build();
        if (Objects.isNull(trigger))
            throw new SchedulerException("构建作业时触发器(Trigger)为空！");
        // 调度作业,若调度中心已有该作业则更新调度中的作业
        JobDetail detail = scheduler.getJobDetail(new JobKey(jobName, jobGroup));
        Date future;
        if (detail == null || jobTypeEnum == JobTypeEnum.SINGLE_JOB) {
            future = scheduler.scheduleJob(jobDetail, trigger);
        } else {
            scheduler.addJob(jobDetail, true);
            future = scheduler.rescheduleJob(new TriggerKey(jobName, jobGroup), trigger);
        }
        return future != null;
    }

    // 构建周期性执行的作业
    private static TriggerBuilder buildIntervalTrigger(String name, String group, int interval,
                                                       StrategyEnum strategyEnum) {
        CalendarIntervalScheduleBuilder builder = CalendarIntervalScheduleBuilder
                .calendarIntervalSchedule();
        if (strategyEnum == StrategyEnum.SECONDS)
            builder.withIntervalInSeconds(interval);
        else if (strategyEnum == StrategyEnum.MINUTE)
            builder.withIntervalInMinutes(interval);
        else if (strategyEnum == StrategyEnum.HOUR)
            builder.withIntervalInHours(interval);
        else if (strategyEnum == StrategyEnum.DAY)
            builder.withIntervalInDays(interval);
        else if (strategyEnum == StrategyEnum.WEEK)
            builder.withIntervalInWeeks(interval);
        else if (strategyEnum == StrategyEnum.MONTH)
            builder.withIntervalInMonths(interval);
        return TriggerBuilder.newTrigger().withIdentity(name, group)
                .withSchedule(builder.withMisfireHandlingInstructionDoNothing());
    }

    // 构建循环性执行的作业
    private static TriggerBuilder buildLoopTrigger(String name, String group, int interval,
                                                   int repeatCount, StrategyEnum strategyEnum) {
        SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule();
        if (strategyEnum == StrategyEnum.SECONDS)
            builder.withIntervalInSeconds(interval);
        else if (strategyEnum == StrategyEnum.MINUTE)
            builder.withIntervalInMinutes(interval);
        else if (strategyEnum == StrategyEnum.HOUR)
            builder.withIntervalInHours(interval);
        else
            builder.withIntervalInMinutes(interval); // 默认为每分钟来执行
        return TriggerBuilder.newTrigger().withIdentity(name, group).withSchedule(builder
                .withRepeatCount(repeatCount)
                // 设置失败指令:每次失效之后,在下个定义的时间点再执行
                .withMisfireHandlingInstructionNextWithRemainingCount());
    }
}
