package com.yudianbank.tms.job.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 作业常量
 *
 * @author Song Lea
 */
public interface JobConstant {

    // 短信作业
    String TMS_SMS_SEND_CONTACT_EMAILS = "lisong@keking.cn";
    String TMS_SMS_SEND_CRON = "0 0/5 8-23 * * ?";
    String TMS_SMS_SEND_DESC = "短信配置内容发送作业";
    String TMS_SMS_CONTENT_CONTACT_EMAILS = "lisong@keking.cn";
    String TMS_SMS_CONTENT_CRON = "0 0 3 * * ?";
    String TMS_SMS_CONTENT_DESC = "短信配置内容数据统计作业";
    String YESTERDAY_PAY_AMT_KEY = "yesterdayPayAmtKey";
    String MONTH_PAY_AMT_KEY = "monthPayAmtKey";
    String YESTERDAY_CAR_INFO_KEY = "yesterdayCarInfoKey";
    String MONTH_CAR_INFO_KEY = "monthCarInfoKey";
    Map<String, Object> SMS_CONTENT_JOB_MAP = new ConcurrentHashMap<>();
    String SMS_CONTENT_JOB_REDIS_KEY = "sms:content:statistics:map:key";

    // 复制备份货源与司机关联数据作业
    String COPY_BAK_GOODS_RELEVANCE_CONTACT_EMAILS = "lisong@keking.cn";
    String COPY_BAK_GOODS_RELEVANCE_CRON = "0 0 2 * * ?";
    String COPY_BAK_GOODS_RELEVANCE_DESC = "备份与删除货源与司机关联数据作业";

    // 短信作业中短信内容计算时的支付方式
    String PAR_WAY_KEKING_TO_DRIVER = "1"; // 白条付司机
    String PAR_WAY_WALLET_TO_DRIVER = "2"; // 余额付司机
    String PAR_WAY_KEKING_TO_COMPANY = "3"; // 贷款付商户
    String PAR_WAY_OFFLINE = "4"; // 线下支付

    // 利润报表作业
    String TMS_PROFIT_CONTACT_EMAILS = "lisong@keking.cn";
    String TMS_PROFIT_CRON = "0 30 1 * * ?";
    String TMS_PROFIT_DESC = "利润报表统计作业";

    // 货源推送作业
    String TMS_GOODS_PUSH_CONTACT_EMAILS = "lisong@keking.cn";
    String TMS_GOODS_PUSH_DESC = "信息平台货源计算与推送作业";
    String TMS_GOODS_JOB_NAME = "TmsGoodsPushJob";
    String TMS_GOODS_JOB_GROUP = "TmsGoodsPushGroup";
    String[] RANDOM_LOGIN_IDS = new String[]{"APP110", "APP119", "APP120", "APP911"};

    // 货源推送中限制每个司机接收货源短信的次数不能超过三次
    int RECEIVE_SMS_COUNT = 3;
    String REDIS_PREFIX_HAS_PUSH_APP = "Statistics-HasPushAppKey_";
    String REDIS_PREFIX_HAS_SEND_SMS = "Statistics-HasSendSmsKey_";

    // 发车与运输报表作业
    String TMS_CAR_TRANSPORT_CONTACT_EMAILS = "lisong@keking.cn";
    String TMS_CAR_TRANSPORT_CRON = "0 30 0 * * ?";
    String TMS_CAR_TRANSPORT_DESC = "发车与运输报表统计作业";

    // 默认的作业处理常量
    String DEFAULT_GROUP = "DEFAULT_GROUP";
    String JOB_TARGET_CLASS = "targetClass";
    String JOB_CALCULATE_DATE = "calculateDate";
    String JOB_RUN_ONCE = "runOnce";
    String JOB_EXCEPTION_EMAILS_KEY = "exceptionNoticeEmails";
    String JOB_SUCCESS_MAILS_KEY = "successNoticeMails";
    String JOB_SUCCESS_NOTICE_CONTENT = "successNoticeContent";
    String JOB_GOODS_PUSH_CONFIG = "goodsPushConfig";

    // 任务执行成功后默认的通知邮件
    String DEFAULT_JOB_SUCCESS_EMAILS = "lisong@keking.cn;tangyanhu@keking.cn;" +
            "liuxinyu@keking.cn;chengtianren@keking.cn;yaolufei@keking.cn";

    // 作业执行成功时的返回
    String JOB_CALL_SUCCESS = "SUCCESS";
}
