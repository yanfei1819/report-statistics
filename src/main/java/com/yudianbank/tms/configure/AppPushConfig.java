package com.yudianbank.tms.configure;

import com.alibaba.fastjson.JSONObject;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.ITemplate;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * APP消息推送类
 */
@Component
public class AppPushConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppPushConfig.class);
    public static final String DEFAULT_APP_PUSH_TITLE = "货源消息";
    public static final String DEFAULT_APP_PUSH_CONTENT = "您有%s到%s的货源消息，速来联系货主>>";

    private EnvVariableConfig envVariableConfig;

    public AppPushConfig() {
    }

    @Autowired
    public AppPushConfig(EnvVariableConfig envVariableConfig) {
        Assert.notNull(envVariableConfig, "AppPushConfig.envVariableConfig must be not null!");
        this.envVariableConfig = envVariableConfig;
    }

    // 推送信息的实现
    public boolean appPushMessageByType(String clientId, String loginId, String title, String text) {
        IGtPush push = new IGtPush(envVariableConfig.getPushHost(), envVariableConfig.getPushAppKey(),
                envVariableConfig.getPushMasterSecret());
        // 通过传入的type值判断是通知消息还是透传消息
        String content = messageToJson(title, text, loginId);
        ITemplate template = transmissionTemplate(text, content);
        SingleMessage message = new SingleMessage();
        message.setOffline(true);
        // 离线有效时间,单位为毫秒,可选
        message.setOfflineExpireTime(24 * 3600 * 1000);
        message.setData(template);
        // 可选:1为wifi,0为不限制网络环境,根据手机处于的网络情况,决定是否下发
        message.setPushNetWorkType(0);
        Target target = new Target();
        target.setAppId(envVariableConfig.getPushAppId());
        target.setClientId(clientId);
        // target.setAlias("alias"); // 用户别名推送,cid和用户别名只能2者选其一
        IPushResult ret;
        try {
            ret = push.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            LOGGER.error("个推服务器响应异常！", e);
            ret = push.pushMessageToSingle(message, target, e.getRequestId());
        } finally {
            try {
                push.close();
            } catch (IOException e) {
                LOGGER.error("IGtPush close error!", e);
            }
        }
        if (ret != null) {
            LOGGER.info("透传数据【{}】,个推推送后响应数据:{}", content, ret.getResponse());
            if ("ok".equals(ret.getResponse().get("result"))) return true;
        }
        return false;
    }

    // 透传消息模板
    private TransmissionTemplate transmissionTemplate(String text, String content) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(envVariableConfig.getPushAppId());
        template.setAppkey(envVariableConfig.getPushAppKey());
        template.setTransmissionContent(content); // 透传内容
        template.setTransmissionType(2); // 收到消息是否立即启动应用:1为立即启动,2则广播等待客户端自启动
        APNPayload payload = new APNPayload();
        payload.addCustomMsg("content", content);
        payload.setContentAvailable(1); // 推送直接带有透传数据
        payload.setSound("default");
        payload.setCategory("$由客户端定义"); // 在客户端通知栏触发特定的action和button显示
        payload.setAlertMsg(new APNPayload.SimpleAlertMsg(text)); // 走苹果的APN通道
        template.setAPNInfo(payload);
        return template;
    }

    // android的透传信息设置
    private String messageToJson(String title, String message, String loginId) {
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> content = new HashMap<>();
        content.put("title", title);
        content.put("message", message);
        jsonObject.put("content", content); // 消息内容
        jsonObject.put("id", 0); // 消息id
        jsonObject.put("userId", loginId); // 用户id
        jsonObject.put("type", "H"); // 消息类型
        jsonObject.put("style", 0); // 样式编号
        return jsonObject.toJSONString();
    }
}