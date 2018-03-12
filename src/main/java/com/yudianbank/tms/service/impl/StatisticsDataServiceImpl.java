package com.yudianbank.tms.service.impl;

import com.yudianbank.tms.dao.StatisticsDataDao;
import com.yudianbank.tms.model.TmsProfitStatisticsModel;
import com.yudianbank.tms.model.TmsReportStatisticsModel;
import com.yudianbank.tms.model.vo.EchartDataVO;
import com.yudianbank.tms.service.StatisticsDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据逻辑处理的Service层实现
 *
 * @author Song Lea
 */
@Service
public class StatisticsDataServiceImpl implements StatisticsDataService {

    private StatisticsDataDao statisticsDataDao;

    public StatisticsDataServiceImpl() {
    }

    @Autowired
    public StatisticsDataServiceImpl(StatisticsDataDao statisticsDataDao) {
        Assert.notNull(statisticsDataDao, "StatisticsDataServiceImpl.statisticsDataDao must be not null!");
        this.statisticsDataDao = statisticsDataDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmsReportStatisticsModel> statisticsSendDate(String sendDateSql, String calDate, int start,
                                                             int limit, int whichWeek) {
        return statisticsDataDao.statisticsSendDate(sendDateSql, calDate, start, limit, whichWeek);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkHasStatisticsByDate(String sql, String date) {
        return statisticsDataDao.checkHasStatisticsByDate(sql, date);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void batchInsertDataList(List<?> dataList) {
        statisticsDataDao.batchInsertDataList(dataList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmsProfitStatisticsModel> profitStatisticsByDate(String sql, String specifiedDate,
                                                                 int start, int perQueryNumber) {
        return statisticsDataDao.profitStatisticsByDate(sql, specifiedDate, start, perQueryNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> groupDataStatisticsByType(String startDate, int type) {
        List<EchartDataVO> result = statisticsDataDao.groupDataStatisticsByType(startDate, type);
        return result.stream().collect(Collectors.toMap(EchartDataVO::getStatisticsDate,
                EchartDataVO::getStatisticsValue));
    }
}