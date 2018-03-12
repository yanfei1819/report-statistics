package com.yudianbank.tms.job;

import com.yudianbank.tms.configure.ServletContextConfig;
import com.yudianbank.tms.service.BakGoodsRelevanceService;
import com.yudianbank.tms.util.ProjectUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 复制及备份货源与司机关联表的数据作业
 *
 * @author Song Lea
 */
public class BakGoodsRelevanceJob extends BaseJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(BakGoodsRelevanceJob.class);

    @Override
    protected void jobExecuteImpl(JobExecutionContext context) throws JobExecutionException {
        String jobKey = context.getJobDetail().getKey().toString();
        LOGGER.info("复制及备份货源与司机关联表的数据作业【{}】开始执行！", jobKey);
        String calDate;
        Object runOnce = context.getJobDetail().getJobDataMap().get(JOB_RUN_ONCE);
        // 判断是否界面传入的处理日期
        if (runOnce != null && runOnce instanceof Boolean && (boolean) runOnce) {
            calDate = context.getJobDetail().getJobDataMap().getString(JOB_CALCULATE_DATE);
        } else {
            // 要保留3天前的数据
            calDate = ProjectUtil.getSpecifiedDateStr(new Date(), -3, ProjectUtil.DAY_DATE_FORMAT);
            context.getJobDetail().getJobDataMap().put(JOB_CALCULATE_DATE, calDate);
        }
        BakGoodsRelevanceService service = ServletContextConfig.getBean(BakGoodsRelevanceService.class);
        // 备份及删除货源与司机关联表数据
        String result = service.bakGoodsRelevanceOps(jobKey, calDate);
        LOGGER.info("复制及备份货源与司机关联表的数据作业【{}】执行结果：{}", jobKey, result);
        context.getJobDetail().getJobDataMap().put(JOB_SUCCESS_NOTICE_CONTENT, result);
        context.getJobDetail().getJobDataMap().put(JOB_CALCULATE_DATE, calDate);
        LOGGER.info("复制及备份货源与司机关联表的数据作业【{}】执行完成！", jobKey);
    }
}
