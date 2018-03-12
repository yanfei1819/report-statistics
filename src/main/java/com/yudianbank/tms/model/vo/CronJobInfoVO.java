package com.yudianbank.tms.model.vo;

import java.io.Serializable;

/**
 * 定时任务的基本信息
 *
 * @author Song Lea
 */
public class CronJobInfoVO implements Serializable {

    private static final long serialVersionUID = -1328004991006258040L;

    private String jobName; // 作业名
    private String jobGroup;  // 作业组
    private String jobStatus; // 作业状态
    private String cron; // 作业的CRON表达式
    private String previousFireTime; // 最后一次执行时间
    private String nextFireTime; // 下次执行时间
    private String jobDescription; // 作业描述
    private String jobType; // 作业类型
    private String targetClass; // 作业执行的类全路径
    private String contactEmails; // 作业异常时需要联系的邮箱列表(以分号分隔)
    private String successEmails; // 作业执行完成后需要通知的邮箱列表
    private String calculateDate; // 统计日期

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

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getPreviousFireTime() {
        return previousFireTime;
    }

    public void setPreviousFireTime(String previousFireTime) {
        this.previousFireTime = previousFireTime;
    }

    public String getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(String nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }

    public String getContactEmails() {
        return contactEmails;
    }

    public void setContactEmails(String contactEmails) {
        this.contactEmails = contactEmails;
    }

    public String getSuccessEmails() {
        return successEmails;
    }

    public void setSuccessEmails(String successEmails) {
        this.successEmails = successEmails;
    }

    public String getCalculateDate() {
        return calculateDate;
    }

    public void setCalculateDate(String calculateDate) {
        this.calculateDate = calculateDate;
    }

    @Override
    public String toString() {
        return "CronJobInfoVO{" +
                "jobName='" + jobName + '\'' +
                ", jobGroup='" + jobGroup + '\'' +
                ", jobStatus='" + jobStatus + '\'' +
                ", cron='" + cron + '\'' +
                ", previousFireTime='" + previousFireTime + '\'' +
                ", nextFireTime='" + nextFireTime + '\'' +
                ", jobDescription='" + jobDescription + '\'' +
                ", jobType='" + jobType + '\'' +
                ", targetClass='" + targetClass + '\'' +
                ", contactEmails='" + contactEmails + '\'' +
                ", successEmails='" + successEmails + '\'' +
                ", calculateDate='" + calculateDate + '\'' +
                '}';
    }
}