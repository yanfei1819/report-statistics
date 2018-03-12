package com.yudianbank.tms.model.vo;

import com.yudianbank.tms.job.manager.ScheduleJobBuilder;
import org.quartz.Job;

import java.io.Serializable;
import java.util.Date;

/**
 * 保存Quartz中的作业属性对象
 *
 * @author Song Lea
 */
public class ScheduleJobVO implements Serializable {

    private static final long serialVersionUID = 1293289626705955217L;

    private String jobName; // 作业名
    private String jobGroup;  // 作业组
    private String cron; // 作业的CRON表达式
    private String description; // 作业描述
    private Class<? extends Job> jobClass; // 作业执行的类
    private Date startRunTime; // 作业执行的开始时间
    private Date endRunTime; // 作业执行的结束时间
    private ScheduleJobBuilder.JobTypeEnum jobTypeEnum; // 作业类型
    private ScheduleJobBuilder.StrategyEnum strategyEnum; // 作业周期执行策略
    private int interval; // 作业周期执行时的间隔
    private int repeatCount = -1; // 作业周期执行时的重复次数;负数代表不限次数

    // cron作业
    public ScheduleJobVO(String jobName, String jobGroup, String description, Class<? extends Job> jobClass,
                         ScheduleJobBuilder.JobTypeEnum jobTypeEnum, String cron) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.description = description;
        this.jobClass = jobClass;
        this.jobTypeEnum = jobTypeEnum;
        this.cron = cron;
    }

    // 单次作业
    public ScheduleJobVO(String jobName, String jobGroup, String description, Class<? extends Job> jobClass,
                         ScheduleJobBuilder.JobTypeEnum jobTypeEnum) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.description = description;
        this.jobClass = jobClass;
        this.jobTypeEnum = jobTypeEnum;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Class<? extends Job> getJobClass() {
        return jobClass;
    }

    public void setJobClass(Class<? extends Job> jobClass) {
        this.jobClass = jobClass;
    }

    public Date getStartRunTime() {
        return startRunTime;
    }

    public void setStartRunTime(Date startRunTime) {
        this.startRunTime = startRunTime;
    }

    public Date getEndRunTime() {
        return endRunTime;
    }

    public void setEndRunTime(Date endRunTime) {
        this.endRunTime = endRunTime;
    }

    public ScheduleJobBuilder.JobTypeEnum getJobTypeEnum() {
        return jobTypeEnum;
    }

    public void setJobTypeEnum(ScheduleJobBuilder.JobTypeEnum jobTypeEnum) {
        this.jobTypeEnum = jobTypeEnum;
    }

    public ScheduleJobBuilder.StrategyEnum getStrategyEnum() {
        return strategyEnum;
    }

    public void setStrategyEnum(ScheduleJobBuilder.StrategyEnum strategyEnum) {
        this.strategyEnum = strategyEnum;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    @Override
    public String toString() {
        return "ScheduleJobVO{" +
                "jobName='" + jobName + '\'' +
                ", jobGroup='" + jobGroup + '\'' +
                ", cron='" + cron + '\'' +
                ", description='" + description + '\'' +
                ", jobClass=" + jobClass +
                ", startRunTime=" + startRunTime +
                ", endRunTime=" + endRunTime +
                ", jobTypeEnum=" + jobTypeEnum +
                ", strategyEnum=" + strategyEnum +
                ", interval=" + interval +
                ", repeatCount=" + repeatCount +
                '}';
    }
}
