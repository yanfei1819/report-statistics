package com.yudianbank.tms.job.helper;

import com.yudianbank.tms.configure.TmsStaticSQLConfig;
import com.yudianbank.tms.job.thread.CarTransportStatisticsThread;
import com.yudianbank.tms.model.TmsReportStatisticsModel;
import com.yudianbank.tms.model.vo.ResponseData;
import com.yudianbank.tms.service.StatisticsDataService;
import com.yudianbank.tms.util.ProjectUtil;
import com.yudianbank.tms.configure.ServletContextConfig;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * 发车与运输报表共通的地方提取出来
 *
 * @author Song Lea
 */
public final class TmsCarTransportJobHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(TmsCarTransportJobHelper.class);

    private TmsCarTransportJobHelper() {
    }

    // 发车与运输报表作业执行的实现方法
    public static String tmsCarTransportJobImpl(String jobKey, String specifiedDate) throws JobExecutionException {
        long start = System.currentTimeMillis();
        int whichWeek;
        try {
            whichWeek = ProjectUtil.dateToWeekOfYear(specifiedDate, 0, ProjectUtil.DAY_DATE_FORMAT);
        } catch (ParseException e) {
            LOGGER.error("TMS发车与运输报表作业【" + jobKey + "】计算周数时传入的统计日期【" + specifiedDate
                    + "】格式错误！", e);
            throw new JobExecutionException(e);
        }
        LOGGER.info("TMS发车与运输报表统计日期【{}】周数【{}】任务开始执行！", specifiedDate, whichWeek);
        boolean hasCal = ServletContextConfig.getBean(StatisticsDataService.class)
                .checkHasStatisticsByDate(ProjectUtil.CHECK_CAR_TRANSPORT_REPORT, specifiedDate);
        if (hasCal) {
            LOGGER.warn("TMS发车与运输报表作业【{}】统计的日期【{}】已经处理过，直接跳过！", jobKey, specifiedDate);
            return ResponseData.HAS_HANDLER;
        }
        int count;
        try {
            count = statisticsSendDate(specifiedDate, whichWeek);
            LOGGER.info("TMS发车与运输报表作业【{}】处理日期【{}】的数据完成，数量：{}", jobKey, specifiedDate, count);
        } catch (Exception e) {
            LOGGER.error("TMS发车与运输报表作业【" + jobKey + "】处理日期【" + specifiedDate + "】的数据异常！", e);
            throw new JobExecutionException(e);
        }
        long end = System.currentTimeMillis();
        LOGGER.info("TMS发车与运输报表作业【{}】处理日期【{}】执行完成，共用时：{}毫秒！", jobKey,
                specifiedDate, (end - start));
        return ResponseData.INSERT_DATA_COUNT + count;
    }

    // 发车时间必须先处理并进行入库
    private static int statisticsSendDate(String specifiedDate, int whichWeek) throws Exception {
        List<Future<String>> futureList = new ArrayList<>();
        int start = 0, result = 0;
        String sql = ServletContextConfig.getBean(TmsStaticSQLConfig.class).getSendDate();
        while (true) {
            List<TmsReportStatisticsModel> queryList = ServletContextConfig.getBean(StatisticsDataService.class)
                    .statisticsSendDate(sql, specifiedDate, start, ProjectUtil.PER_QUERY_NUMBER, whichWeek);
            if (queryList != null && queryList.size() > 0) {
                result += queryList.size();
                futureList.add(ProjectUtil.EXECUTOR_SERVICE.submit(new CarTransportStatisticsThread(queryList)));
            } else break;
            start += ProjectUtil.PER_QUERY_NUMBER;
        }
        for (Future future : futureList)
            future.get();
        return result;
    }
}
