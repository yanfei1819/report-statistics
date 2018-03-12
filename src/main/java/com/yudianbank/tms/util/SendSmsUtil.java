package com.yudianbank.tms.util;

import com.alibaba.fastjson.JSON;
import com.yudianbank.tms.job.callback.ThreadCallback;
import com.yudianbank.tms.model.vo.ResponseContentVO;
import com.yudianbank.tms.model.vo.SendSmsVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 发送短信的工具类,一般在线程中调用
 *
 * @author Song Lea
 */
public final class SendSmsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendSmsUtil.class);

    private SendSmsUtil() {
    }

    // 发送短信的实现方法,提取出来方便不是线程调用的情况
    public static void sendSmsImpl(String functionCode, String partnerNo, Object[] variable,
                                   String smsUrl, ThreadCallback threadCallback) {
        SendSmsVO sendSms = new SendSmsVO();
        sendSms.setFunctionCode(functionCode);
        sendSms.setPartnerNo(partnerNo);
        sendSms.setVariable(variable);
        String jsonText = JSON.toJSONString(sendSms);
        String result = HttpClientUtil.doPost(smsUrl, jsonText);
        LOGGER.info("短信平台URL:{}；报文:{}；结果:{}", smsUrl, jsonText, result);
        if (threadCallback != null) {
            try {
                ResponseContentVO vo = JSON.parseObject(result, ResponseContentVO.class);
                if (Objects.nonNull(vo) && vo.getCode() == 0)
                    threadCallback.handler(); // 短信发送成功处理回调方法
            } catch (Exception e) {
                LOGGER.error("解析发送短信平台返回结果异常，无法判断是否发送成功！", e);
            }
        }
    }
}