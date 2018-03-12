package com.yudianbank.tms.model.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by chengtianren on 2017/8/27.
 */
public class SmsCarsAmountStatisticsVO implements Serializable {

    private static final long serialVersionUID = -6175506488108970359L;

    private String partnerNo;
    private BigInteger totalCarNumber; // 车数量
    private BigInteger carType;
    private BigDecimal totalTransportCash;
    private BigDecimal totalIncome;

    public String getPartnerNo() {
        return partnerNo;
    }

    public void setPartnerNo(String partnerNo) {
        this.partnerNo = partnerNo;
    }

    public BigDecimal getTotalTransportCash() {
        if (totalTransportCash == null)
            return BigDecimal.ZERO;
        return totalTransportCash;
    }

    public void setTotalTransportCash(BigDecimal totalTransportCash) {
        this.totalTransportCash = totalTransportCash;
    }

    public BigInteger getCarType() {
        if (carType == null)
            return BigInteger.ZERO;
        return carType;
    }

    public void setCarType(BigInteger carType) {
        this.carType = carType;
    }

    public BigInteger getTotalCarNumber() {
        if (totalCarNumber == null)
            return BigInteger.ZERO;
        return totalCarNumber;
    }

    public void setTotalCarNumber(BigInteger totalCarNumber) {
        this.totalCarNumber = totalCarNumber;
    }

    public BigDecimal getTotalIncome() {
        if (totalIncome == null)
            return BigDecimal.ZERO;
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    @Override
    public String toString() {
        return "SmsCarsAmountStatisticsVO{" +
                "partnerNo='" + partnerNo + '\'' +
                ", totalTransportCash=" + totalTransportCash +
                ", totalCarNumber=" + totalCarNumber +
                ", totalIncome=" + totalIncome +
                '}';
    }
}
