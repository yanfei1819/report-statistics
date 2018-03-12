package com.yudianbank.tms.job.thread;

import com.yudianbank.tms.model.TmsReportStatisticsModel;

import java.util.List;

/**
 * 发车日期为指定天数的数据的处理线程
 *
 * @author Song Lea
 */
public class CarTransportStatisticsThread extends BaseInsertThread<TmsReportStatisticsModel> {

    public CarTransportStatisticsThread(List<TmsReportStatisticsModel> dataList) {
        super(dataList);
    }

    @Override
    protected List<TmsReportStatisticsModel> processDataList(List<TmsReportStatisticsModel> dataList) {
        // 无需处理直接入库
        return super.processDataList(dataList);
    }
}