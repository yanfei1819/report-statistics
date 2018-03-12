package com.yudianbank.tms.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

// 货源司机关联表
@Entity
@Table(name = "YD_TMS_SOURCEGOODS_DRIVER_RELEVANCE")
public class TmsSourceGoodsDriverRelevance implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int sourceGoodsId;//货源ID

    private String driverId;//司机ID

    private int countCalled;//联系次数，0次表示没有联系过'

    private Date calledTime;//最后一次联系货源时间

    private int countReceiveSms;//接收货源短信推送次数（手动推送不包含在内），0次表示没有推送过

    private Date receiveSmsTime;//司机最后一次接收短信时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSourceGoodsId() {
        return sourceGoodsId;
    }

    public void setSourceGoodsId(int sourceGoodsId) {
        this.sourceGoodsId = sourceGoodsId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public int getCountCalled() {
        return countCalled;
    }

    public void setCountCalled(int countCalled) {
        this.countCalled = countCalled;
    }

    public Date getCalledTime() {
        return calledTime;
    }

    public void setCalledTime(Date calledTime) {
        this.calledTime = calledTime;
    }

    public int getCountReceiveSms() {
        return countReceiveSms;
    }

    public void setCountReceiveSms(int countReceiveSms) {
        this.countReceiveSms = countReceiveSms;
    }

    public Date getReceiveSmsTime() {
        return receiveSmsTime;
    }

    public void setReceiveSmsTime(Date receiveSmsTime) {
        this.receiveSmsTime = receiveSmsTime;
    }
}
