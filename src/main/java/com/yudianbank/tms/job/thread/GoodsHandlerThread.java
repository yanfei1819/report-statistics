package com.yudianbank.tms.job.thread;

import com.yudianbank.tms.configure.AppPushConfig;
import com.yudianbank.tms.configure.EnvVariableConfig;
import com.yudianbank.tms.job.helper.TmsGoodsPushHelper;
import com.yudianbank.tms.model.TmsSourceGoodsConfig;
import com.yudianbank.tms.model.TmsSourceGoodsPushConfig;
import com.yudianbank.tms.service.InfoPlatformService;
import com.yudianbank.tms.util.ProjectUtil;
import com.yudianbank.tms.configure.ServletContextConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 异步处理信息平台货源推送(因为加了全局锁,此处不需要使用线程处理)
 *
 * @author Song Lea
 */
public class GoodsHandlerThread {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsHandlerThread.class);
    private static final String NO_SEND_SMS_LOG = "定时推送任务表【YD_TMS_SOURCEGOODS_PUSH_CONFIG】" +
            "字段【sendSmsStatus】设置为0(不发送短信)【1发送,0不发送】！";

    private int earlyDays;
    private List<TmsSourceGoodsConfig> goodsConfigList;
    private InfoPlatformService infoPlatformService;
    private TmsSourceGoodsPushConfig pushConfig;
    private AppPushConfig appPushConfig;
    private EnvVariableConfig envVariableConfig;
    private StringRedisTemplate redisTemplate;

    public GoodsHandlerThread(TmsSourceGoodsPushConfig pushConfig, int earlyDays,
                              List<TmsSourceGoodsConfig> goodsConfigList) {
        this.pushConfig = pushConfig;
        this.earlyDays = earlyDays;
        this.goodsConfigList = goodsConfigList;
        this.infoPlatformService = ServletContextConfig.getBean(InfoPlatformService.class);
        this.appPushConfig = ServletContextConfig.getBean(AppPushConfig.class);
        this.envVariableConfig = ServletContextConfig.getBean(EnvVariableConfig.class);
        this.redisTemplate = ServletContextConfig.getStringRedisTemplate();
    }

    // 实现逻辑
    public String goodsHandlerThreadImpl() {
        // 生成或更新货源表的数据
        String result = TmsGoodsPushHelper.buildSourceGoods(goodsConfigList, earlyDays, infoPlatformService);
        // 生成货源与司机关联表的数据
        String yesterday = ProjectUtil.getSpecifiedDateStr(new Date(), -1, ProjectUtil.DAY_DATE_FORMAT);
        result += TmsGoodsPushHelper.buildGoodsDriverRelevance(infoPlatformService,
                yesterday, null, earlyDays);
        if (pushConfig != null) {
            // 同一货源当天最多联系次数与同一货源总共最多联系次数
            int everydayCallAccount = pushConfig.getEverydayCallAccount();
            int totalCallAccount = pushConfig.getTotalCallAccount();
            // 未通过次数校验后的货源id列表(也就是达到联系次数上限的货源ids)
            List<String> idsList = infoPlatformService.listOverCalledCountGoodsIds(everydayCallAccount, totalCallAccount);
            String ids = CollectionUtils.isEmpty(idsList) ? null
                    : idsList.stream().collect(Collectors.joining(ProjectUtil.COMMA_SEPARATOR));
            LOGGER.info("货源推送作业__已达到最大联系次数的货源Ids列表：{}", ids);
            // 自动推送APP消息(redis的get/set方法的原子性故使用异步线程去进行推送部分)
            TmsGoodsPushHelper.pushAppNoticeImpl(ids, yesterday, infoPlatformService, appPushConfig, redisTemplate);
            // 自动推送短信
            if (pushConfig.getSendSmsStatus() == 1) {
                TmsGoodsPushHelper.pushSmsImpl(ids, yesterday, infoPlatformService, envVariableConfig, redisTemplate);
            } else {
                // 定时配置表全局设置为不发送短信
                result += NO_SEND_SMS_LOG;
                LOGGER.warn(NO_SEND_SMS_LOG);
            }
        }
        return result;
    }
}