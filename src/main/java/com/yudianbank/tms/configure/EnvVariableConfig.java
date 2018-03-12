package com.yudianbank.tms.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 加载系统各个环境下的变量与系统定义的常量
 *
 * @author Song Lea
 */
@Configuration
public class EnvVariableConfig {

    // 短信接口相关参数
    @Value("${com.yudianbank.tms.sms.url}")
    private String smsUrl;

    @Value("${com.yudianbank.tms.TmsSmsSendJob.functionCode}")
    private String smsSendJobCode;

    @Value("${com.yudianbank.tms.TmsSmsSendJob.partnerNo}")
    private String smsSendJobNo;

    @Value("${com.yudianbank.tms.TmsGoodsPushJob.functionCode}")
    private String goodsPushCode;

    @Value("${com.yudianbank.tms.TmsGoodsPushJob.partnerNo}")
    private String goodsPushNo;

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${spring.mail.username}")
    private String sendUser;

    // 登录定时任务管理平台用户名/密码参数
    @Value("${tms.report.platform.username}")
    private String defaultUser;

    @Value("${tms.report.platform.password}")
    private String defaultPassword;

    @Value("${tms.report.platform.cookie-name}")
    private String cookieName;

    // 项目启动时各个作业是否加入到调度
    @Value("${com.yudianbank.tms.job.TmsCarTransportJob.enable}")
    private boolean tmsCarTransportJob;

    @Value("${com.yudianbank.tms.job.TmsSmsContentJob.enable}")
    private boolean tmsSmsContentJob;

    @Value("${com.yudianbank.tms.job.TmsSmsSendJob.enable}")
    private boolean tmsSmsSendJob;

    @Value("${com.yudianbank.tms.job.TmsProfitJob.enable}")
    private boolean tmsProfitJob;

    @Value("${com.yudianbank.tms.job.TmsGoodsPushJob.enable}")
    private boolean tmsGoodsPushJob;

    @Value("${com.yudianbank.tms.job.CopyBakGoodsRelevanceJob}")
    private boolean copyGoodsRelevanceJob;

    @Value("${com.yudianbank.tms.job.email.enable}")
    private boolean jobEmail; // 任务完成后是否邮件通知

    // 个推的参数
    @Value("${com.yudianbank.tms.getui.kache.appId}")
    private String pushAppId;

    @Value("${com.yudianbank.tms.getui.kache.appKey}")
    private String pushAppKey;

    @Value("${com.yudianbank.tms.getui.kache.masterSecret}")
    private String pushMasterSecret;

    @Value("${com.yudianbank.tms.getui.kache.appHost}")
    private String pushHost;

    public String getSmsUrl() {
        return smsUrl;
    }

    public void setSmsUrl(String smsUrl) {
        this.smsUrl = smsUrl;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    public boolean isTmsCarTransportJob() {
        return tmsCarTransportJob;
    }

    public void setTmsCarTransportJob(boolean tmsCarTransportJob) {
        this.tmsCarTransportJob = tmsCarTransportJob;
    }

    public boolean isTmsSmsContentJob() {
        return tmsSmsContentJob;
    }

    public void setTmsSmsContentJob(boolean tmsSmsContentJob) {
        this.tmsSmsContentJob = tmsSmsContentJob;
    }

    public boolean isTmsSmsSendJob() {
        return tmsSmsSendJob;
    }

    public void setTmsSmsSendJob(boolean tmsSmsSendJob) {
        this.tmsSmsSendJob = tmsSmsSendJob;
    }

    public boolean isTmsProfitJob() {
        return tmsProfitJob;
    }

    public void setTmsProfitJob(boolean tmsProfitJob) {
        this.tmsProfitJob = tmsProfitJob;
    }

    public boolean isCopyGoodsRelevanceJob() {
        return copyGoodsRelevanceJob;
    }

    public void setCopyGoodsRelevanceJob(boolean copyGoodsRelevanceJob) {
        this.copyGoodsRelevanceJob = copyGoodsRelevanceJob;
    }

    public String getDefaultUser() {
        return defaultUser;
    }

    public void setDefaultUser(String defaultUser) {
        this.defaultUser = defaultUser;
    }

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public boolean isTmsGoodsPushJob() {
        return tmsGoodsPushJob;
    }

    public void setTmsGoodsPushJob(boolean tmsGoodsPushJob) {
        this.tmsGoodsPushJob = tmsGoodsPushJob;
    }

    public String getPushAppId() {
        return pushAppId;
    }

    public void setPushAppId(String pushAppId) {
        this.pushAppId = pushAppId;
    }

    public String getPushAppKey() {
        return pushAppKey;
    }

    public void setPushAppKey(String pushAppKey) {
        this.pushAppKey = pushAppKey;
    }

    public String getPushMasterSecret() {
        return pushMasterSecret;
    }

    public void setPushMasterSecret(String pushMasterSecret) {
        this.pushMasterSecret = pushMasterSecret;
    }

    public String getPushHost() {
        return pushHost;
    }

    public void setPushHost(String pushHost) {
        this.pushHost = pushHost;
    }

    public String getSmsSendJobCode() {
        return smsSendJobCode;
    }

    public void setSmsSendJobCode(String smsSendJobCode) {
        this.smsSendJobCode = smsSendJobCode;
    }

    public String getSmsSendJobNo() {
        return smsSendJobNo == null ? "" : smsSendJobNo;
    }

    public void setSmsSendJobNo(String smsSendJobNo) {
        this.smsSendJobNo = smsSendJobNo;
    }

    public String getGoodsPushCode() {
        return goodsPushCode;
    }

    public void setGoodsPushCode(String goodsPushCode) {
        this.goodsPushCode = goodsPushCode;
    }

    public String getGoodsPushNo() {
        return goodsPushNo == null ? "" : goodsPushNo;
    }

    public void setGoodsPushNo(String goodsPushNo) {
        this.goodsPushNo = goodsPushNo;
    }

    public boolean isJobEmail() {
        return jobEmail;
    }

    public void setJobEmail(boolean jobEmail) {
        this.jobEmail = jobEmail;
    }
}