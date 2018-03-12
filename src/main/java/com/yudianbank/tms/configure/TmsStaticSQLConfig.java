package com.yudianbank.tms.configure;

import com.yudianbank.tms.util.ProjectUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 取配置文件中的统计SQL
 *
 * @author Song Lea
 */
@Component
@PropertySource(value = "classpath:config/tms-report-statistics-sql.properties", encoding = ProjectUtil.DEFAULT_CHARSET)
@ConfigurationProperties(prefix = "tms.report.statistics")
public class TmsStaticSQLConfig {

    // 发车与运输报表的查询SQL
    private String sendDate;

    // 利润报表统计的查询SQL
    private String profitQuery;

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getProfitQuery() {
        return profitQuery;
    }

    public void setProfitQuery(String profitQuery) {
        this.profitQuery = profitQuery;
    }

}