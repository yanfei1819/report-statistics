package com.yudianbank.tms.job;

import com.yudianbank.tms.job.manager.JobConstant;
import org.quartz.*;

/**
 * 任务基类
 *
 * @author Song Lea
 */
public abstract class BaseJob implements Job, JobConstant {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        jobExecuteImpl(context);
    }

    // 具体的作业实现逻辑,子类去实现
    protected abstract void jobExecuteImpl(JobExecutionContext context) throws JobExecutionException;
}