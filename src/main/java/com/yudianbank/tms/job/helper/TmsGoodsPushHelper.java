package com.yudianbank.tms.job.helper;

import com.google.common.collect.Lists;
import com.yudianbank.tms.configure.AppPushConfig;
import com.yudianbank.tms.configure.EnvVariableConfig;
import com.yudianbank.tms.job.manager.JobConstant;
import com.yudianbank.tms.job.manager.ScheduleJobBuilder;
import com.yudianbank.tms.model.TmsSourceGoods;
import com.yudianbank.tms.model.TmsSourceGoodsConfig;
import com.yudianbank.tms.model.TmsSourceGoodsPushConfig;
import com.yudianbank.tms.model.vo.AppPushVO;
import com.yudianbank.tms.model.vo.GoodsDriverRelevanceVO;
import com.yudianbank.tms.model.vo.ScheduleJobVO;
import com.yudianbank.tms.model.vo.SmsPushVO;
import com.yudianbank.tms.service.InfoPlatformService;
import com.yudianbank.tms.util.ProjectUtil;
import com.yudianbank.tms.util.SendSmsUtil;
import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 信息平台货源推送作业帮助类
 *
 * @author Song Lea
 */
public final class TmsGoodsPushHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(TmsGoodsPushHelper.class);

    private TmsGoodsPushHelper() {
    }

    // 构建信息平台推送作业
    public static void buildGoodsPushJob(String description, Class<? extends Job> executeClass,
                                         String exceptionNoticeEmails, String successNoticeMails,
                                         Scheduler scheduler, List<TmsSourceGoodsPushConfig> pushConfigList)
            throws SchedulerException {
        // 定时任务配置表数据为空直接返回不构建任务
        if (CollectionUtils.isEmpty(pushConfigList))
            return;
        for (TmsSourceGoodsPushConfig pushConfig : pushConfigList) {
            if (Objects.isNull(pushConfig))
                continue;
            // 获取到界面配置的每个推送时间信息
            if (StringUtils.hasText(pushConfig.getPushTimes())) {
                String[] perTimes = pushConfig.getPushTimes().split(ProjectUtil.COMMA_SEPARATOR);
                for (String perTime : perTimes) {
                    String[] splitTime = perTime.split(ProjectUtil.COLON_SEPARATOR);
                    // 对每一个时间分解成时+分的情况以便构建cron表达式
                    if (splitTime.length == 2) {
                        // 处理生成唯一的JobName与JobGroup,否则添加到quartz中时报错
                        String jobName = JobConstant.TMS_GOODS_JOB_NAME
                                + ProjectUtil.UNDERLINE_SEPARATOR + perTime;
                        String jobGroup = JobConstant.TMS_GOODS_JOB_GROUP +
                                ProjectUtil.UNDERLINE_SEPARATOR + pushConfig.getEarlyDays();
                        // JobDataMap中需要保存的变量
                        Map<String, Object> map = new HashMap<>();
                        map.put(JobConstant.JOB_TARGET_CLASS, executeClass.getName());
                        map.put(JobConstant.JOB_EXCEPTION_EMAILS_KEY, exceptionNoticeEmails);
                        map.put(JobConstant.JOB_SUCCESS_MAILS_KEY, successNoticeMails);
                        map.put(JobConstant.JOB_GOODS_PUSH_CONFIG, pushConfig);
                        // 构建触发的CRON表达式
                        int hour = Integer.parseInt(splitTime[0]);
                        int minute = Integer.parseInt(splitTime[1]);
                        DateBuilder.validateHour(hour);
                        DateBuilder.validateMinute(minute);
                        ScheduleJobVO scheduleJobVO = new ScheduleJobVO(jobName, jobGroup, description, executeClass,
                                ScheduleJobBuilder.JobTypeEnum.CRON_JOB, String.format("0 %d %d ? * *", minute, hour));
                        // 构建CRON执行作业
                        boolean isSuccess = ScheduleJobBuilder.builderScheduleJobByType(scheduler, scheduleJobVO, map);
                        LOGGER.info("信息平台推送作业【{};{}】构建结果：{}", jobName, jobGroup, isSuccess);
                    } else {
                        LOGGER.warn("信息平台推送作业任务调度时间【{}】格式【HH:mm】不正确，忽略该时间！", perTime);
                    }
                }
            } else {
                LOGGER.warn("信息平台推送作业任务未调度时间【pushTimes】为空，忽略该配置！");
            }
        }
    }

    // 生成或更新货源表数据
    public static String buildSourceGoods(List<TmsSourceGoodsConfig> goodsConfigList, int earlyDays,
                                          InfoPlatformService infoPlatformService) {
        // 保存需要批量更新或新增的货源表数据
        List<Integer> updateList = new ArrayList<>();
        List<TmsSourceGoods> addList = new ArrayList<>();
        // 界面上配置的每周用车时间(每周一,每周二等等...)将生成未来几天的用车时间列表
        List<String> applyDays = ProjectUtil.getDaysListInSpecified(earlyDays); // Not Null
        // 对每一个货源配置表处理生成货源表数据
        for (TmsSourceGoodsConfig config : goodsConfigList) {
            for (int i = 0, len = applyDays.size(); i < len; i++) {
                // 判断用车时间是周几
                String dayOfWeek = ProjectUtil.dayOfWeek(new Date(), i, true) + "";
                // 判断要生成的货源表用车时间是否在配置的每周用车时间内
                if (Objects.nonNull(config.getApplyWeek()) && config.getApplyWeek().contains(dayOfWeek)) {
                    String applyDate = applyDays.get(i);
                    // 查询指定发车时间货源表中超过货源配置表更新时间的货源
                    List<TmsSourceGoods> sourceGoodsList = infoPlatformService
                            .getOverUpdateTimeGoodsListById(config.getId(), config.getUpdateTime(), applyDate);
                    if (CollectionUtils.isEmpty(sourceGoodsList)) {
                        // 货源配置表中存在,但货源表没有记录的要放到'新增'的列表中
                        addList.add(handlerNewGoods(config, applyDate));
                    } else {
                        // 循环遍历已存在的货源表数据,只要发车日期相同则放到需要'更新'的列表
                        for (TmsSourceGoods goods : sourceGoodsList) {
                            if (applyDate.equals(ProjectUtil.dateFormatByPattern(goods.getApplyDate(),
                                    ProjectUtil.DAY_DATE_FORMAT)))
                                updateList.add(goods.getId());
                        }
                    }
                } else {
                    LOGGER.warn("TMS信息平台作业货源配置【ID：{}】中第【{}】天后的日期周几【{}】不在用车时间内【{}】" +
                                    "或未配置每周用车时间，不生成货源信息！", config.getId(), i,
                            dayOfWeek, config.getApplyWeek());
                }
            }
        }
        // 批量更新与新增货源表数据
        infoPlatformService.batchUpdateSourceGoodsByIds(updateList);
        infoPlatformService.batchInsertSourceGoods(addList);
        String result = "货源配置表有效的记录数：" + goodsConfigList.size() + "\n处理后更新货源表数据量："
                + updateList.size() + "；更新的主键ID列表：" + updateList + "\n处理后新增货源表数据量："
                + addList.size() + "\n\n";
        LOGGER.info(result);
        return result;
    }

    // 生成货源与司机关联关系表数据(若sourceGoodsConfigId不为空则只生成对应货源的,yesterday表示只处理今天之后的数据)
    public static String buildGoodsDriverRelevance(InfoPlatformService infoPlatformService, String yesterday,
                                                   Integer goodsConfigId, int earlyDays) {
        final StringBuilder result = new StringBuilder();
        if (Objects.isNull(goodsConfigId)) {
            // 先查询要生成货源关系的所有货源列表,再进行多线程遍历处理
            List<Integer> goodsList = infoPlatformService.getSourceGoodsConfigIdsAfterDate(yesterday);
            goodsList.stream().parallel().forEach(c -> {
                int length = infoPlatformService.getDriverRelevanceListAndBatchInsert(yesterday, c);
                result.append("后台定时任务生成货源【配置ID:").append(c)
                        .append("】与司机关联数据量(真实)：").append(length).append("\n");
            });
            // 生成随机的货源与司机关联关系表(每次入库0-3条数据)且只在后台定时调用时生成
            List<GoodsDriverRelevanceVO> list = infoPlatformService.randomDriverRelevanceListAndBatchInsert(earlyDays);
            result.append("后台定时任务生成货源与司机关联数据量(随机)：").append(list.size()).append("\n");
        } else {
            // 界面点击推送消息或短信时
            int count = infoPlatformService.getDriverRelevanceListAndBatchInsert(yesterday, goodsConfigId);
            result.append("界面推送点击时生成货源与司机关联数据量(真实)：").append(count).append("\n");
        }
        LOGGER.info(result.toString());
        return result.toString();
    }

    // 使用异步线程推送手机APP消息(10分钟内只能推送一条信息)
    public static void pushAppNoticeImpl(String ids, String yesterday, InfoPlatformService infoPlatformService,
                                         AppPushConfig appPushConfig, StringRedisTemplate redisTemplate) {
        List<TmsSourceGoods> tmsSourceGoodsList = infoPlatformService.listSortTmsSourceGoodsAfterDate(yesterday, ids);
        if (CollectionUtils.isEmpty(tmsSourceGoodsList)) {
            LOGGER.warn("未获取到【{}】货源列表，不进行货源APP推送！", yesterday);
            return;
        }
        for (TmsSourceGoods goods : tmsSourceGoodsList) {
            // 使用线程去推送APP消息
            ProjectUtil.EXECUTOR_SERVICE.execute(() -> {
                Thread.currentThread().setUncaughtExceptionHandler((t, e) -> LOGGER.error("异步推送手机APP消息出现异常！", e));
                List<AppPushVO> clients = infoPlatformService.getGoodsDriverRelevanceAfterDateById(yesterday, goods.getId());
                for (AppPushVO vo : clients) {
                    String hasKey = JobConstant.REDIS_PREFIX_HAS_PUSH_APP + vo.getClientId();
                    // 在GetAndSet之前必须判断是否已有,否则设置的过期时间会被覆盖掉
                    if (redisTemplate.opsForValue().get(hasKey) == null
                            && redisTemplate.opsForValue().getAndSet(hasKey, vo.getLoginId()) == null) {
                        // 不关心是否推送成功,10分钟内总不重复推送
                        redisTemplate.expire(hasKey, 10, TimeUnit.MINUTES);
                        // 生成个推消息内容并调用个推
                        String content = String.format(AppPushConfig.DEFAULT_APP_PUSH_CONTENT,
                                vo.getSendCity(), vo.getArriveCity());
                        boolean pushResult = appPushConfig.appPushMessageByType(vo.getClientId(), vo.getLoginId(),
                                AppPushConfig.DEFAULT_APP_PUSH_TITLE, content);
                        LOGGER.info("Redis【key:{}】中不存在clientId【{}】，后台自动推送APP消息结果：{}", hasKey,
                                vo.getClientId(), pushResult);
                    } else {
                        LOGGER.info("Redis【key:{}】中已存在clientId【{}】，在10分钟内不重复推送！",
                                hasKey, vo.getClientId());
                    }
                }
                LOGGER.info("异步推送货源【ID:{}】手机APP消息完成！", goods.getId());
            });
        }
    }

    // 推送短信的实现逻辑(同一货源对于同一手机号码一天之内只能收到一条短信)
    public static void pushSmsImpl(String ids, String yesterday, InfoPlatformService infoPlatformService,
                                   EnvVariableConfig envVariableConfig, StringRedisTemplate redisTemplate) {
        List<TmsSourceGoods> tmsSourceGoodsList = infoPlatformService.listSortTmsSourceGoodsAfterDate(yesterday, ids);
        if (CollectionUtils.isEmpty(tmsSourceGoodsList)) {
            LOGGER.warn("未获取到【{}】货源列表，不进行货源短信发送！", yesterday);
            return;
        }
        for (TmsSourceGoods goods : tmsSourceGoodsList) {
            List<SmsPushVO> mobileList = infoPlatformService.listDriverMobile(goods.getId());
            String configCarLength = goods.getCarLength();  // 货源配置表中的车长
            for (SmsPushVO vo : mobileList) {
                // 发送短信的条件:每个司机未收到超过3次且(货源中未配置车长或配置的车长包含此认证司机的车长)且同一货源只发送一次
                String totalPerCallKey = JobConstant.REDIS_PREFIX_HAS_SEND_SMS
                        + goods.getSourceGoodsConfigId() + ProjectUtil.UNDERLINE_SEPARATOR + vo.getMobile();
                if (vo.getHasSendCount() < JobConstant.RECEIVE_SMS_COUNT  // 总次数要小于3
                        && redisTemplate.opsForValue().get(totalPerCallKey) == null // redis中该货源未标识已发送短信
                        && (StringUtils.isEmpty(configCarLength)  // 车长为空或车长符合司机的车长
                        || (vo.getCarLength() != null && configCarLength.contains(vo.getCarLength())))) {
                    Map<String, String> sendMap = new HashMap<>();
                    sendMap.put("mobile", vo.getMobile());
                    sendMap.put("shipperName", goods.getPartnerName());
                    sendMap.put("sendCity", goods.getSendCity());
                    sendMap.put("arriveCity", goods.getArriveCity());
                    SendSmsUtil.sendSmsImpl(envVariableConfig.getGoodsPushCode(), envVariableConfig.getGoodsPushNo(),
                            Lists.newArrayList(sendMap).toArray(), envVariableConfig.getSmsUrl(), () -> {
                                // 将货源与司机关系表中的发送短信次数
                                int update = infoPlatformService.updateCountReceiveSms(goods.getId(), vo.getDriverId());
                                LOGGER.info("短信成功后，货源与司机关系表【goodsConfigId:{}；" +
                                        "driverId:{}】中的发送短信次数增加1：{}", goods.getId(), vo.getDriverId(), update);
                                // 同一货源同一手机号码加入到redis中,同一天内不重复发送
                                redisTemplate.opsForValue().set(totalPerCallKey, "1",
                                        ProjectUtil.getTodayLastMilliseconds(), TimeUnit.MILLISECONDS);
                            });
                } else {
                    LOGGER.info("司机【{}】短信通知超过次数【已发送:{}；限制:{}】或车长不匹配【货源表车长:{}；" +
                                    "该司机车长:{}】或该货源【{}】在redis存在【{}】，不发短信！",
                            vo.getDriverId(), vo.getHasSendCount(), JobConstant.RECEIVE_SMS_COUNT, configCarLength,
                            vo.getCarLength(), goods.getId(), totalPerCallKey);
                }
            }
        }
        LOGGER.info("同步推送短信完成！");
    }

    // 根据货源配置表来生成货源表对象
    private static TmsSourceGoods handlerNewGoods(TmsSourceGoodsConfig goodsConfig, String applyDate) {
        TmsSourceGoods newGoods = new TmsSourceGoods();
        newGoods.setPartnerNo(goodsConfig.getPartnerNo());
        newGoods.setPartnerName(goodsConfig.getPartnerName());
        newGoods.setYardmanMobileNum(goodsConfig.getYardmanMobileNum()); // 调度员手机号
        newGoods.setSendRegion(goodsConfig.getSendRegion());
        newGoods.setArriveRegion(goodsConfig.getArriveRegion());
        newGoods.setSendProvince(goodsConfig.getSendProvince());
        newGoods.setArriveProvince(goodsConfig.getArriveProvince());
        newGoods.setSendCity(goodsConfig.getSendCity());
        newGoods.setArriveCity(goodsConfig.getArriveCity());
        newGoods.setSendDistrict(goodsConfig.getSendDistrict());
        newGoods.setArriveDistrict(goodsConfig.getArriveDistrict());
        newGoods.setCargoName(goodsConfig.getCargoName());
        newGoods.setCargoWeight(goodsConfig.getCargoWeight());
        newGoods.setCarLength(goodsConfig.getCarLength());
        newGoods.setCarModel(goodsConfig.getCarModel());
        newGoods.setBillFee(goodsConfig.getBillFee());
        newGoods.setHandleMode(goodsConfig.getHandleMode());
        newGoods.setPayWay(goodsConfig.getPayWay());
        newGoods.setContent(goodsConfig.getContent());
        newGoods.setApplyDate(ProjectUtil.safeGetDateByStr(applyDate, ProjectUtil.DAY_DATE_FORMAT));
        newGoods.setApplyTimeStart(goodsConfig.getApplyTimeStart());
        newGoods.setApplyTimeEnd(goodsConfig.getApplyTimeEnd());
        newGoods.setUpdateTime(new Date());
        newGoods.setSourceGoodsConfigId(goodsConfig.getId());
        return newGoods;
    }
}