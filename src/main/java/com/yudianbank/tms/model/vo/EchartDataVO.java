package com.yudianbank.tms.model.vo;

import java.io.Serializable;

/**
 * 构造echart的数据查询结果
 *
 * @author Song Lea
 */
public class EchartDataVO implements Serializable {

    private static final long serialVersionUID = 1559309069867139034L;

    private String statisticsDate; // 日期
    private String statisticsValue;  // 值

    public String getStatisticsDate() {
        return statisticsDate;
    }

    public void setStatisticsDate(String statisticsDate) {
        this.statisticsDate = statisticsDate;
    }

    public String getStatisticsValue() {
        return statisticsValue;
    }

    public void setStatisticsValue(String statisticsValue) {
        this.statisticsValue = statisticsValue;
    }

    @Override
    public String toString() {
        return "EchartDataVO{" +
                "statisticsDate='" + statisticsDate + '\'' +
                ", statisticsValue='" + statisticsValue + '\'' +
                '}';
    }
}
