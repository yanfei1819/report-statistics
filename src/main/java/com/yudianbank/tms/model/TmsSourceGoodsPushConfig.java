package com.yudianbank.tms.model;

import javax.persistence.*;
import java.io.Serializable;

// 定时配置表
@Entity
@Table(name = "YD_TMS_SOURCEGOODS_PUSH_CONFIG")
public class TmsSourceGoodsPushConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int earlyDays; // 提前几天推送

    private String pushTimes; // 更新时间集合，如：8:00,12:00,16:00

    private int sendSmsStatus; // 是否发送货源短信，1：发送、0：不发送

    private int everydayCallAccount; // 同一货源当天最多联系次数

    private int totalCallAccount; // 同一货源总共最多联系次数

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEarlyDays() {
        return earlyDays;
    }

    public void setEarlyDays(int earlyDays) {
        this.earlyDays = earlyDays;
    }

    public String getPushTimes() {
        return pushTimes;
    }

    public void setPushTimes(String pushTimes) {
        this.pushTimes = pushTimes;
    }

    public int getSendSmsStatus() {
        return sendSmsStatus;
    }

    public void setSendSmsStatus(int sendSmsStatus) {
        this.sendSmsStatus = sendSmsStatus;
    }

    public int getEverydayCallAccount() {
        return everydayCallAccount;
    }

    public void setEverydayCallAccount(int everydayCallAccount) {
        this.everydayCallAccount = everydayCallAccount;
    }

    public int getTotalCallAccount() {
        return totalCallAccount;
    }

    public void setTotalCallAccount(int totalCallAccount) {
        this.totalCallAccount = totalCallAccount;
    }
}
