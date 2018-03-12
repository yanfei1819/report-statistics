package com.yudianbank.tms.job;

import com.yudianbank.tms.job.thread.GoodsHandlerThread;
import com.yudianbank.tms.model.TmsSourceGoodsConfig;
import com.yudianbank.tms.model.TmsSourceGoodsPushConfig;
import com.yudianbank.tms.model.vo.ResponseData;
import com.yudianbank.tms.service.InfoPlatformService;
import com.yudianbank.tms.util.ProjectUtil;
import com.yudianbank.tms.configure.ServletContextConfig;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 货源推送定时任务
 *
 * @author Song Lea
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class TmsGoodsPushJob extends BaseJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(TmsGoodsPushJob.class);

    private static final Object JOB_LOCK = new Object(); // 全局锁对象
    public static volatile boolean IS_RUNNING = false;

    @Override
    protected void jobExecuteImpl(JobExecutionContext context) throws JobExecutionException {
        try {
            // 全局对象锁解决同一时刻多个作业并发执行数据入重问题
            synchronized (JOB_LOCK) {
                // 解决后台定时任务与界面点击重复执行入重问题
                IS_RUNNING = true;
                String jobKey = context.getJobDetail().getKey().toString();
                TmsSourceGoodsPushConfig pushConfig = null;
                int earlyDays;
                Object runOnce = context.getJobDetail().getJobDataMap().get(JOB_RUN_ONCE);
                if (runOnce != null && runOnce instanceof Boolean && (Boolean) runOnce) {
                    String group = context.getJobDetail().getKey().getGroup();
                    String[] splitGroup = group.split(ProjectUtil.UNDERLINE_SEPARATOR);
                    if (splitGroup.length == 2)
                        earlyDays = Integer.parseInt(splitGroup[1]);
                    else
                        throw new JobExecutionException("TMS信息平台货源推送作业【" + jobKey
                                + "】界面点击单次执行时【" + group + "】格式不正确！");
                } else {
                    pushConfig = (TmsSourceGoodsPushConfig) context.getJobDetail()
                            .getJobDataMap().get(JOB_GOODS_PUSH_CONFIG);
                    if (pushConfig == null)
                        throw new JobExecutionException("TMS信息平台货源推送作业【" + jobKey
                                + "】执行时JobDataMap中的TmsSourceGoodsPushConfig为空！");
                    earlyDays = pushConfig.getEarlyDays();
                }
                String result = tmsGoodsPushJobImpl(jobKey, pushConfig, earlyDays);  // 作业的实现逻辑
                // 完成后通知的邮件内容
                context.getJobDetail().getJobDataMap().put(JOB_SUCCESS_NOTICE_CONTENT, result);
                context.getJobDetail().getJobDataMap().put(JOB_CALCULATE_DATE, earlyDays + "");
            }
        } finally {
            IS_RUNNING = false; // 任务执行完成后必须再设置为false
        }
    }

    // 任务实现逻辑
    private static String tmsGoodsPushJobImpl(String jobKey, TmsSourceGoodsPushConfig pushConfig, int earlyDays)
            throws JobExecutionException {
        long startTime = System.currentTimeMillis();
        LOGGER.info("TMS信息平台货源推送作业【{}】开始执行，提前天数：{}", jobKey, earlyDays);
        StringBuilder result = new StringBuilder();
        try {
            int start = 0;
            while (true) {
                List<TmsSourceGoodsConfig> queryList = ServletContextConfig.getBean(InfoPlatformService.class)
                        .listValidSourceGoodsConfig(start, ProjectUtil.PER_QUERY_NUMBER);
                if (queryList != null && queryList.size() > 0) {
                    result.append(new GoodsHandlerThread(pushConfig, earlyDays, queryList)
                            .goodsHandlerThreadImpl()).append("\n");
                } else break;
                start += ProjectUtil.PER_QUERY_NUMBER;
            }
        } catch (Exception e) {
            LOGGER.error("TMS信息平台货源推送作业【" + jobKey + "】提前天数【" + earlyDays + "】数据异常！", e);
            throw new JobExecutionException(e);
        }
        long end = System.currentTimeMillis();
        LOGGER.info("TMS信息平台货源推送作业【{}】提前天数【{}】执行完成，共用时：{}毫秒！",
                jobKey, earlyDays, (end - startTime));
        return result.length() == 0 ? ResponseData.NO_SOURCE_GOODS_CONFIG : result.toString();
    }
}