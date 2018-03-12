package com.yudianbank.tms.job;

import com.yudianbank.tms.job.helper.TmsCarTransportJobHelper;
import com.yudianbank.tms.util.ProjectUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import java.util.Date;

/**
 * TMS发车报表统计任务(运输报表暂不做)
 *
 * @author Song Lea
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class TmsCarTransportJob extends BaseJob {

    @Override
    protected void jobExecuteImpl(JobExecutionContext context) throws JobExecutionException {
        String calDate;
        Object runOnce = context.getJobDetail().getJobDataMap().get(JOB_RUN_ONCE);
        if (runOnce != null && runOnce instanceof Boolean && (boolean) runOnce) {
            calDate = context.getJobDetail().getJobDataMap().getString(JOB_CALCULATE_DATE);
        } else {
            calDate = ProjectUtil.getSpecifiedDateStr(new Date(), -1, ProjectUtil.DAY_DATE_FORMAT);
            context.getJobDetail().getJobDataMap().put(JOB_CALCULATE_DATE, calDate);
        }
        String jobKey = context.getJobDetail().getKey().toString();
        String result = TmsCarTransportJobHelper.tmsCarTransportJobImpl(jobKey, calDate);
        context.getJobDetail().getJobDataMap().put(JOB_SUCCESS_NOTICE_CONTENT, result);
    }
}