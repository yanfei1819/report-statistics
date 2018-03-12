package com.yudianbank.tms.job.helper;

import com.yudianbank.tms.configure.TmsStaticSQLConfig;
import com.yudianbank.tms.job.thread.ProfitStatisticsThread;
import com.yudianbank.tms.model.TmsProfitStatisticsModel;
import com.yudianbank.tms.model.vo.ResponseData;
import com.yudianbank.tms.service.StatisticsDataService;
import com.yudianbank.tms.util.ProjectUtil;
import com.yudianbank.tms.configure.ServletContextConfig;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * 利润统计报表共通的地方提取出来
 *
 * @author Song Lea
 */
public final class TmsProfitJobHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(TmsProfitJobHelper.class);

    // 只处理这两个状态的运单
    public static final String NODE_STATUS = "结束运单";
    public static final String BILL_STATUS = "已完成";
    // 保留的小数位数
    public static final int DECIMAL_DIGITS = 3;

    // 利润报表作业执行的实现方法
    public static String tmsProfitJobImpl(String jobKey, String specifiedDate) throws JobExecutionException {
        long start = System.currentTimeMillis();
        LOGGER.info("TMS利润统计报表统计日期【{}】任务开始执行！", specifiedDate);
        boolean hasCal = ServletContextConfig.getBean(StatisticsDataService.class).
                checkHasStatisticsByDate(ProjectUtil.CHECK_PROFIT_REPORT, specifiedDate);
        if (hasCal) {
            LOGGER.warn("TMS利润统计报表作业【{}】统计日期【{}】已经处理过，直接跳过！", jobKey, specifiedDate);
            return ResponseData.HAS_HANDLER;
        }
        int count;
        try {
            count = TmsProfitJobHelper.statisticsByDate(specifiedDate);
            LOGGER.info("TMS利润统计报表作业【{}】处理日期【{}】的数据完成，数量：{}", jobKey, specifiedDate, count);
        } catch (Exception e) {
            LOGGER.error("TMS利润统计报表作业【" + jobKey + "】处理日期【" + specifiedDate + "】数据异常！", e);
            throw new JobExecutionException(e);
        }
        long end = System.currentTimeMillis();
        LOGGER.info("TMS利润统计报表作业【{}】处理日期【{}】执行完成，共用时：{}毫秒！", jobKey,
                specifiedDate, (end - start));
        return ResponseData.INSERT_DATA_COUNT + count;
    }

    // 发车时间必须先处理并进行入库
    private static int statisticsByDate(String specifiedDate) throws Exception {
        List<Future<String>> futureList = new ArrayList<>();
        int start = 0, result = 0;
        String sql = ServletContextConfig.getBean(TmsStaticSQLConfig.class).getProfitQuery();
        while (true) {
            List<TmsProfitStatisticsModel> queryList = ServletContextConfig.getBean(StatisticsDataService.class)
                    .profitStatisticsByDate(sql, specifiedDate, start, ProjectUtil.PER_QUERY_NUMBER);
            if (queryList != null && queryList.size() > 0) {
                result += queryList.size();
                futureList.add(ProjectUtil.EXECUTOR_SERVICE.submit(new ProfitStatisticsThread(queryList)));
            } else break;
            start += ProjectUtil.PER_QUERY_NUMBER;
        }
        for (Future future : futureList) // 等待线程执行完成
            future.get();
        return result;
    }
}