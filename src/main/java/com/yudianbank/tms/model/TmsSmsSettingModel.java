package com.yudianbank.tms.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by chengtianren on 2017/8/24.
 */
@Entity
@Table(name = "YD_TMS_SMS_SETTING")
public class TmsSmsSettingModel implements Serializable {
    
    private static final long serialVersionUID = 8637632454500759243L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int smsSettingId;
    private String partnerNo;
    private String receiver;  //收信人
    private Date sendTime;  //短信配置的发送时间
    private String sendContent;  //短信发送内容
    private Date lastSendDate;   //上次发送日期
    private Date createDate; //保存日期
    private String partnerName; //合作方名称

    public int getSmsSettingId() {
        return smsSettingId;
    }

    public void setSmsSettingId(int smsSettingId) {
        this.smsSettingId = smsSettingId;
    }

    public String getPartnerNo() {
        return partnerNo;
    }

    public void setPartnerNo(String partnerNo) {
        this.partnerNo = partnerNo;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getSendContent() {
        return sendContent;
    }

    public void setSendContent(String sendContent) {
        this.sendContent = sendContent;
    }

    public Date getLastSendDate() {
        return lastSendDate;
    }

    public void setLastSendDate(Date lastSendDate) {
        this.lastSendDate = lastSendDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }
}

