package com.yudianbank.tms.service;

import com.yudianbank.tms.model.TmsProfitStatisticsModel;
import com.yudianbank.tms.model.TmsReportStatisticsModel;

import java.util.List;
import java.util.Map;

/**
 * 数据逻辑处理的Service层
 *
 * @author Song Lea
 */
public interface StatisticsDataService {

    // 统计发车日期的数据--插入
    List<TmsReportStatisticsModel> statisticsSendDate(String sendDateSql, String calDate,
                                                      int start, int limit, int whichWeek);

    // 判断数据是否统计过
    boolean checkHasStatisticsByDate(String sql, String date);

    // 插入统计数据
    void batchInsertDataList(List<?> dataList);

    // 统计利润报表数据
    List<TmsProfitStatisticsModel> profitStatisticsByDate(String sql, String specifiedDate,
                                                          int start, int perQueryNumber);

    // 通过类型来加载统计数据
    Map<String, String> groupDataStatisticsByType(String startDate, int type);
}
