package com.yudianbank.tms.dao;

import com.yudianbank.tms.model.TmsProfitStatisticsModel;
import com.yudianbank.tms.model.TmsReportStatisticsModel;
import com.yudianbank.tms.model.vo.EchartDataVO;

import java.util.List;

/**
 * 报表数据处理Dao层接口
 *
 * @author Song Lea
 */
public interface StatisticsDataDao {

    // 统计发车日期的数据
    List<TmsReportStatisticsModel> statisticsSendDate(String sendDateSql, String calDate, int start,
                                                      int limit, int whichWeek);
    // 插入统计数据
    void batchInsertDataList(List<?> dataList);

    // 判断之前是否统计过
    boolean checkHasStatisticsByDate(String sql, String date);

    // 统计利润报表的查询
    List<TmsProfitStatisticsModel> profitStatisticsByDate(String sql, String specifiedDate,
                                                          int start, int perQueryNumber);
    // 通过类型来加载统计数据
    List<EchartDataVO> groupDataStatisticsByType(String startDate, int type);
}
