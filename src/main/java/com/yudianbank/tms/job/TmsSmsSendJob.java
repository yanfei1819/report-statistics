package com.yudianbank.tms.job;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.yudianbank.tms.configure.EnvVariableConfig;
import com.yudianbank.tms.configure.ServletContextConfig;
import com.yudianbank.tms.model.TmsSmsSettingModel;
import com.yudianbank.tms.model.vo.PayAmtStatisticsVO;
import com.yudianbank.tms.model.vo.SmsCarsAmountStatisticsVO;
import com.yudianbank.tms.model.vo.TmsSmsReceiverVO;
import com.yudianbank.tms.service.SmsContentStatisticsService;
import com.yudianbank.tms.util.*;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by chengtianren on 2017/8/27.
 *
 * @since 2017-11-13 修改 by Song Lea
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class TmsSmsSendJob extends BaseJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(TmsSmsSendJob.class);

    @Override
    @SuppressWarnings("unchecked")
    protected void jobExecuteImpl(JobExecutionContext context) throws JobExecutionException {
        LOGGER.info("TMS短信发送作业开始执行！");
        SmsContentStatisticsService service = ServletContextConfig.getBean(SmsContentStatisticsService.class);
        EnvVariableConfig envVariableConfig = ServletContextConfig.getBean(EnvVariableConfig.class);
        if (SMS_CONTENT_JOB_MAP.isEmpty()) {
            RedisTemplate redisTemplate = ServletContextConfig.getBean("redisTemplate");
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            Map<String, Object> map = (Map<String, Object>)
                    redisTemplate.opsForValue().get(SMS_CONTENT_JOB_REDIS_KEY);
            if (CollectionUtils.isEmpty(map)) {
                LOGGER.info("TmsSmsSendJob:没有数据，不发送短信！");
                return;
            }
            SMS_CONTENT_JOB_MAP.putAll(map);
        }
        List<TmsSmsSettingModel> list = service.listSendSmsSetting(); // 查询出所有要发送的短信模板信息
        if (CollectionUtils.isEmpty(list)) {
            LOGGER.warn("未查询到可用的短信模板配置信息，不处理发送短信！");
            return;
        }
        for (TmsSmsSettingModel model : list) {
            // 处理生成短信内容
            String consistSmsContent = consistSmsContent(model.getPartnerName(),
                    model.getPartnerNo(), model.getSendContent(), model.getSmsSettingId());
            if (StringUtils.hasText(consistSmsContent))
                sendSmsImpl(model.getReceiver(), consistSmsContent, model.getSmsSettingId(),
                        envVariableConfig, service);  // 发送短信(成功后回调更新配置表的时间)
        }
        LOGGER.info("TMS短信发送作业执行结束！");
    }

    // 调用接口去发送短信
    private static void sendSmsImpl(String receiver, String consistSmsContent, int smsSettingId,
                                    EnvVariableConfig envVariableConfig, SmsContentStatisticsService service) {
        JSONArray json = JSONArray.parseArray(receiver);
        List<TmsSmsReceiverVO> receiverList = json.toJavaList(TmsSmsReceiverVO.class);
        for (TmsSmsReceiverVO vo : receiverList) {
            Map<String, String> map = new HashMap<>();
            map.put("mobile", vo.getMobile());
            map.put("statisticsText", consistSmsContent);
            LOGGER.info("TMS短信发送作业__发送短信手机号:{}，内容:{}", vo.getMobile(), consistSmsContent);
            ProjectUtil.EXECUTOR_SERVICE.execute(() -> {
                Thread.currentThread().setUncaughtExceptionHandler((t, e)
                        -> LOGGER.error("发车统计发送短信线程执行出现异常！", e));
                SendSmsUtil.sendSmsImpl(envVariableConfig.getSmsSendJobCode(), envVariableConfig.getSmsSendJobNo(),
                        Lists.newArrayList(map).toArray(), envVariableConfig.getSmsUrl(), () -> {
                            int result = service.updateTmsSmsSetting(smsSettingId);
                            LOGGER.info("发送短信【{}】后更新短信配置表【{}】结果:{}",
                                    consistSmsContent, smsSettingId, result > 0);
                        });
            });
        }
    }

    @SuppressWarnings("unchecked")
    private static String consistSmsContent(String partnerName, String partnerNo,
                                            String sendContent, int smsSettingId) {
        if (StringUtils.isEmpty(sendContent)) {
            LOGGER.warn("短信模板【{}】未配置发送内容，不处理短信！", smsSettingId);
            return null;
        }
        // 短信最前面的提示信息,物流公司+时间信息
        StringBuilder statisticsText = new StringBuilder("");
        String statisticsDate = ProjectUtil.getSpecifiedDateStr(new Date(), -1,
                ProjectUtil.DATE_FORMAT_YYYY_MM_DD_CN); // 统计日期
        statisticsText.append(partnerName).append(statisticsDate).append("，");
        // 天的短信内容
        List<SmsCarsAmountStatisticsVO> yesterdayCarInfoList = (List<SmsCarsAmountStatisticsVO>)
                SMS_CONTENT_JOB_MAP.get(YESTERDAY_CAR_INFO_KEY);
        if (!CollectionUtils.isEmpty(yesterdayCarInfoList))
            statisticsText.append("当日").append(handlerSmsContent(yesterdayCarInfoList,
                    sendContent, partnerNo, "day"));
        // 月的短信内容
        List<SmsCarsAmountStatisticsVO> monthCarInfoList = (List<SmsCarsAmountStatisticsVO>)
                SMS_CONTENT_JOB_MAP.get(MONTH_CAR_INFO_KEY);
        if (!CollectionUtils.isEmpty(monthCarInfoList))
            statisticsText.append("当月累计").append(handlerSmsContent(monthCarInfoList,
                    sendContent, partnerNo, "month"));
        replaceLastSign(statisticsText); // 替换最后一个逗号为句号
        return statisticsText.toString();
    }

    // 处理短信内容
    @SuppressWarnings("unchecked")
    private static String handlerSmsContent(List<SmsCarsAmountStatisticsVO> carInfoList, String sendContent,
                                            String partnerNo, String type) {
        StringBuilder result = new StringBuilder("");
        int totalCars = -1;
        boolean hasTotal = sendContent.contains(ProjectUtil.SmsContentEnum.TOTAL.toString());
        if (hasTotal) {
            // 发车总台数
            totalCars = carInfoList.stream()
                    .filter(t -> Objects.equals(partnerNo, t.getPartnerNo())
                            && Objects.nonNull(t.getTotalCarNumber()))
                    .mapToInt(t -> t.getTotalCarNumber().intValue()).sum();
            // 发车总收入
            if (totalCars != 0) {
                result.append("共发车").append(totalCars).append("台，");
                BigDecimal totalIncome = carInfoList.stream()
                        .filter(t -> Objects.equals(partnerNo, t.getPartnerNo()))
                        .map(SmsCarsAmountStatisticsVO::getTotalIncome)
                        .reduce(BigDecimal.ZERO, BigDecimal::add); // 总收入
                if (Objects.nonNull(totalIncome) && !BigDecimal.ZERO.equals(totalIncome))
                    // 保留2位小数,并防止出现科学计数法
                    result.append("总收入").append(totalIncome.setScale(2,
                            BigDecimal.ROUND_HALF_UP).toPlainString()).append("元，");
            }
        }
        boolean hasCompany = sendContent.contains(ProjectUtil.SmsContentEnum.COMPANY.toString());
        if (totalCars != 0 && hasCompany) {
            int companyCars = carInfoList.stream()
                    .filter(t -> Objects.equals(partnerNo, t.getPartnerNo())
                            && BigInteger.ONE.equals(t.getCarType())
                            && Objects.nonNull(t.getTotalCarNumber()))
                    .mapToInt(t -> t.getTotalCarNumber().intValue()).sum();
            if (companyCars != 0 || totalCars == -1)
                result.append("自有车发车").append(companyCars).append("台，");
        }
        boolean hasApp = sendContent.contains(ProjectUtil.SmsContentEnum.APP.toString());
        if (totalCars != 0 && hasApp) {
            int appCars = carInfoList.stream()
                    .filter(t -> Objects.equals(partnerNo, t.getPartnerNo())
                            && BigInteger.valueOf(2).equals(t.getCarType())
                            && Objects.nonNull(t.getTotalCarNumber()))
                    .mapToInt(t -> t.getTotalCarNumber().intValue()).sum();
            if (appCars != 0 || totalCars == -1)
                result.append("外请车发车").append(appCars).append("台，");
            // 外请车总运费
            if (appCars != 0) {
                BigDecimal appTransportCash = carInfoList.stream()
                        .filter(t -> Objects.equals(partnerNo, t.getPartnerNo())
                                && BigInteger.valueOf(2).equals(t.getCarType()))
                        .map(SmsCarsAmountStatisticsVO::getTotalTransportCash)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (Objects.nonNull(appTransportCash)
                        && !BigDecimal.ZERO.equals(appTransportCash))
                    result.append("外请车运费").append(appTransportCash.setScale(2,
                            BigDecimal.ROUND_HALF_UP).toPlainString()).append("元；");
            }
        }
        if (!hasTotal && !hasApp && !hasCompany)
            result.append("未统计；");  // 用户短信配置中未勾选所有的用车配置
        if (result.length() == 0)
            result.append("无发车；");
        if (sendContent.contains(ProjectUtil.SmsContentEnum.PAY_AMT.toString())) {
            // 付款信息(按支付日期来统计的故不用判断车辆的总台数)
            String key = "day".equals(type) ? YESTERDAY_PAY_AMT_KEY : MONTH_PAY_AMT_KEY;
            List<PayAmtStatisticsVO> payAmtList = (List<PayAmtStatisticsVO>) SMS_CONTENT_JOB_MAP.get(key);
            if (!CollectionUtils.isEmpty(payAmtList)) {
                StringBuilder cache = new StringBuilder();
                for (PayAmtStatisticsVO payAmt : payAmtList) {
                    // partnerNo相同,费用不为空则不为0,支付方式不为空时需要加到短信内容中
                    BigDecimal amt = payAmt.getAmt();
                    if (Objects.equals(partnerNo, payAmt.getPartnerNo()) && Objects.nonNull(amt)
                            && !BigDecimal.ZERO.equals(amt) && Objects.nonNull(payAmt.getPayWay())) {
                        switch (payAmt.getPayWay()) {
                            case PAR_WAY_KEKING_TO_COMPANY:
                                cache.append("贷款付商户").append(amt.setScale(2,
                                        BigDecimal.ROUND_HALF_UP).toPlainString()).append("元，");
                                break;
                            case PAR_WAY_KEKING_TO_DRIVER:
                                cache.append("白条付司机").append(amt.setScale(2,
                                        BigDecimal.ROUND_HALF_UP).toPlainString()).append("元，");
                                break;
                            case PAR_WAY_WALLET_TO_DRIVER:
                                cache.append("余额付司机").append(amt.setScale(2,
                                        BigDecimal.ROUND_HALF_UP).toPlainString()).append("元，");
                                break;
                            case PAR_WAY_OFFLINE:
                                cache.append("线下支付").append(amt.setScale(2,
                                        BigDecimal.ROUND_HALF_UP).toPlainString()).append("元，");
                                break;
                            default:
                                break;
                        }
                    }
                }
                if (cache.length() > 0)
                    result.append("day".equals(type) ? "当日" : "当月累计").append(cache);
            }
        }
        return result.toString();
    }

    // 替换最后一个逗号
    private static void replaceLastSign(StringBuilder builder) {
        int length = builder.length();
        if (length > 0 && builder.toString().endsWith("，")) {
            builder.deleteCharAt(length - 1);
            builder.append("。");
        }
    }
}