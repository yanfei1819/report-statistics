package com.yudianbank.tms.model.vo;

import java.io.Serializable;

/**
 * 保存APP消息推送对象
 *
 * @author Song Lea
 */
public class AppPushVO implements Serializable {

    private static final long serialVersionUID = 1033626942376981927L;

    private String clientId;
    private String loginId;
    private String sendCity;
    private String arriveCity;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getSendCity() {
        return sendCity;
    }

    public void setSendCity(String sendCity) {
        this.sendCity = sendCity;
    }

    public String getArriveCity() {
        return arriveCity;
    }

    public void setArriveCity(String arriveCity) {
        this.arriveCity = arriveCity;
    }

    @Override
    public String toString() {
        return "AppPushVO{" +
                "clientId='" + clientId + '\'' +
                ", loginId='" + loginId + '\'' +
                ", sendCity='" + sendCity + '\'' +
                ", arriveCity='" + arriveCity + '\'' +
                '}';
    }
}
