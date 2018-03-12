package com.yudianbank.tms.job.listener;

import org.quartz.*;

/**
 * 自定义的调度监听器
 *
 * @author Song Lea
 */
public class CustomSchedulerListener implements SchedulerListener {

    @Override
    // Scheduler在有新的 JobDetail 部署时调用此方法
    public void jobScheduled(Trigger trigger) {

    }

    @Override
    // Scheduler 在有新的 JobDetail卸载时调用此方法
    public void jobUnscheduled(TriggerKey triggerKey) {

    }

    @Override
    // 当一个 Trigger 来到了再也不会触发的状态时调用这个方法。
    // 除非这个 Job 已设置成了持久性，否则它就会从 Scheduler 中移除
    public void triggerFinalized(Trigger trigger) {

    }

    @Override
    // Scheduler 调用这个方法是发生在一个 Trigger 或 Trigger 组被暂停时。
    // 假如是 Trigger 组的话，triggerName 参数将为 null
    public void triggerPaused(TriggerKey triggerKey) {

    }

    @Override
    public void triggersPaused(String triggerGroup) {

    }

    @Override
    // Scheduler 调用这个方法是发生成一个 Trigger 或 Trigger 组从暂停中恢复时。
    // 假如是 Trigger 组的话，triggerName 参数将为 null
    public void triggerResumed(TriggerKey triggerKey) {

    }

    @Override
    public void triggersResumed(String triggerGroup) {

    }

    @Override
    //  JobDetail 添加时调用这个方法
    public void jobAdded(JobDetail jobDetail) {

    }

    @Override
    //  JobDetail 删除时调用这个方法
    public void jobDeleted(JobKey jobKey) {

    }

    @Override
    // 当一个或一组 JobDetail 暂停时调用这个方法
    public void jobPaused(JobKey jobKey) {

    }

    @Override
    public void jobsPaused(String jobGroup) {

    }

    @Override
    // 当一个或一组 Job 从暂停上恢复时调用这个方法。
    // 假如是一个 Job 组，jobName 参数将为 null
    public void jobResumed(JobKey jobKey) {

    }

    @Override
    public void jobsResumed(String jobGroup) {

    }

    @Override
    // Scheduler 的正常运行期间产生一个严重错误时调用这个方法
    public void schedulerError(String msg, SchedulerException cause) {

    }

    @Override
    public void schedulerInStandbyMode() {

    }

    @Override
    public void schedulerStarted() {

    }

    @Override
    public void schedulerStarting() {

    }

    @Override
    public void schedulerShutdown() {

    }

    @Override
    public void schedulerShuttingdown() {

    }

    @Override
    public void schedulingDataCleared() {

    }
}