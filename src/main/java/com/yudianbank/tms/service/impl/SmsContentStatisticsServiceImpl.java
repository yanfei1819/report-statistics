package com.yudianbank.tms.service.impl;

import com.yudianbank.tms.dao.SmsContentStatisticsDao;
import com.yudianbank.tms.job.manager.JobConstant;
import com.yudianbank.tms.model.TmsSmsSettingModel;
import com.yudianbank.tms.model.vo.PayAmtStatisticsVO;
import com.yudianbank.tms.model.vo.SmsCarsAmountStatisticsVO;
import com.yudianbank.tms.service.SmsContentStatisticsService;
import com.yudianbank.tms.util.ProjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chengtianren on 2017/8/27.
 *
 * @since 2017-11-13 修改 by Song Lea
 */
@Service
public class SmsContentStatisticsServiceImpl implements SmsContentStatisticsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsContentStatisticsServiceImpl.class);

    private SmsContentStatisticsDao smsContentStatisticsDao;

    public SmsContentStatisticsServiceImpl() {
    }

    @Autowired
    public SmsContentStatisticsServiceImpl(SmsContentStatisticsDao smsContentStatisticsDao) {
        Assert.notNull(smsContentStatisticsDao, "SmsContentStatisticsServiceImpl.smsContentStatisticsDao must be not null");
        this.smsContentStatisticsDao = smsContentStatisticsDao;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<SmsCarsAmountStatisticsVO>> getSendCarsAmountMap() {
        Date date = new Date();
        // 日统计
        String dayStart = ProjectUtil.getSpecifiedDateStr(date, -1, ProjectUtil.DAY_DATE_FORMAT);
        String end = ProjectUtil.dateFormatByPattern(date, ProjectUtil.DAY_DATE_FORMAT);
        List<SmsCarsAmountStatisticsVO> yesterdayCarInfoList = smsContentStatisticsDao.listSendCarsAmount(dayStart, end);
        // 月统计
        String monthStart = ProjectUtil.dateFormatByPattern(ProjectUtil  // 统计的是昨天所在月的第一天
                .getMonthFirstDayByDate(ProjectUtil.getYesterday()), ProjectUtil.DAY_DATE_FORMAT);
        List<SmsCarsAmountStatisticsVO> monthCarInfoList = smsContentStatisticsDao.listSendCarsAmount(monthStart, end);
        Map<String, List<SmsCarsAmountStatisticsVO>> map = new HashMap<>();
        map.put(JobConstant.YESTERDAY_CAR_INFO_KEY, yesterdayCarInfoList);
        map.put(JobConstant.MONTH_CAR_INFO_KEY, monthCarInfoList);
        LOGGER.info("TMS短信内容统计作业_车辆信息昨日统计的数据量：{}", yesterdayCarInfoList.size());
        LOGGER.info("TMS短信内容统计作业_车辆信息月统计的数据量：{}", monthCarInfoList.size());
        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<PayAmtStatisticsVO>> getPayAmtMap() {
        // 天的统计:这里统计的是支付时间,而不是用车时间,必须加上时分秒
        Date date = new Date();
        String dayStart = ProjectUtil.getSpecifiedDateStr(date, -1, ProjectUtil.DAY_DATE_FORMAT)
                + ProjectUtil.DAY_DATE_SUFFIX;
        String end = ProjectUtil.dateFormatByPattern(date, ProjectUtil.DAY_DATE_FORMAT)
                + ProjectUtil.DAY_DATE_SUFFIX;
        List<PayAmtStatisticsVO> yesterdayPayAmtList = smsContentStatisticsDao.listPayAmt(dayStart, end);
        // 统计月的数据
        String monthStart = ProjectUtil.dateFormatByPattern(ProjectUtil
                        .getMonthFirstDayByDate(ProjectUtil.getYesterday()),
                ProjectUtil.DAY_DATE_FORMAT) + ProjectUtil.DAY_DATE_SUFFIX; // 统计的是昨天所在月的第一天
        List<PayAmtStatisticsVO> monthPayAmtList = smsContentStatisticsDao.listPayAmt(monthStart, end);
        Map<String, List<PayAmtStatisticsVO>> map = new HashMap<>();
        map.put(JobConstant.YESTERDAY_PAY_AMT_KEY, yesterdayPayAmtList);
        map.put(JobConstant.MONTH_PAY_AMT_KEY, monthPayAmtList);
        LOGGER.info("TMS短信内容统计作业_支付信息昨日统计的数据量：{}", yesterdayPayAmtList.size());
        LOGGER.info("TMS短信内容统计作业_支付信息月统计的数据量：{}", monthPayAmtList.size());
        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmsSmsSettingModel> listSendSmsSetting() {
        return smsContentStatisticsDao.listSendSmsSetting();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int updateTmsSmsSetting(int smsSettingId) {
        return smsContentStatisticsDao.updateTmsSmsSetting(smsSettingId);
    }
}
