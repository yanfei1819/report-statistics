package com.yudianbank.tms.model.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by chengtianren on 2017/8/27.
 *
 * @since 2017-11-13 按支付方式分组来查询
 */
public class PayAmtStatisticsVO implements Serializable {

    private static final long serialVersionUID = -178512538445088973L;

    private String partnerNo; // 物流公司标识
    private BigDecimal amt;   // 运费
    private String payWay;    // 支付方式

    public String getPartnerNo() {
        return partnerNo;
    }

    public void setPartnerNo(String partnerNo) {
        this.partnerNo = partnerNo;
    }

    public BigDecimal getAmt() {
        if (amt == null)
            return BigDecimal.ZERO;
        return amt;
    }

    public void setAmt(BigDecimal amt) {
        this.amt = amt;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    @Override
    public String toString() {
        return "PayAmtStatisticsVO{" +
                "partnerNo='" + partnerNo + '\'' +
                ", amt=" + amt +
                ", payWay='" + payWay + '\'' +
                '}';
    }
}
