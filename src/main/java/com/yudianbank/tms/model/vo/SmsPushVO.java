package com.yudianbank.tms.model.vo;

import java.io.Serializable;

/**
 * 保存短信推送对象
 *
 * @author Song Lea
 */
public class SmsPushVO implements Serializable {

    private static final long serialVersionUID = 4197737218598600415L;

    private String mobile;
    private String driverId;
    private String carLength; // 司机对应的车长
    private int hasSendCount;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getCarLength() {
        return carLength;
    }

    public void setCarLength(String carLength) {
        this.carLength = carLength;
    }

    public int getHasSendCount() {
        return hasSendCount;
    }

    public void setHasSendCount(int hasSendCount) {
        this.hasSendCount = hasSendCount;
    }

    @Override
    public String toString() {
        return "SmsPushVO{" +
                "mobile='" + mobile + '\'' +
                ", driverId='" + driverId + '\'' +
                ", carLength='" + carLength + '\'' +
                ", hasSendCount=" + hasSendCount +
                '}';
    }
}
