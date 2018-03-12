package com.yudianbank.tms.service.impl;

import com.google.common.collect.Lists;
import com.yudianbank.tms.configure.EnvVariableConfig;
import com.yudianbank.tms.job.TmsGoodsPushJob;
import com.yudianbank.tms.job.helper.TmsGoodsPushHelper;
import com.yudianbank.tms.job.listener.CustomJobListener;
import com.yudianbank.tms.job.manager.JobConstant;
import com.yudianbank.tms.job.manager.ScheduleJobBuilder;
import com.yudianbank.tms.model.TmsSourceGoodsConfig;
import com.yudianbank.tms.model.TmsSourceGoodsPushConfig;
import com.yudianbank.tms.model.vo.CronJobInfoVO;
import com.yudianbank.tms.model.vo.ResponseData;
import com.yudianbank.tms.model.vo.ScheduleJobVO;
import com.yudianbank.tms.service.InfoPlatformService;
import com.yudianbank.tms.service.SchedulerManagerService;
import com.yudianbank.tms.service.StatisticsDataService;
import com.yudianbank.tms.util.ProjectUtil;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.utils.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务管理Service层实现
 *
 * @author Song Lea
 */
@Service
public class SchedulerManagerServiceImpl implements SchedulerManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerManagerServiceImpl.class);
    private static final String X_AXIS_DATA = "xAxisData";
    private static final String SEND_CAR_DATA = "sendCarData";
    private static final String PROFIT_DATA = "profitData";

    private Scheduler scheduler;
    private JavaMailSender mailSender;
    private SimpMessagingTemplate messagingTemplate;
    private StatisticsDataService statisticsDataService;
    private EnvVariableConfig envVariableConfig;
    private InfoPlatformService infoPlatformService;

    public SchedulerManagerServiceImpl() {
    }

    @Autowired
    public SchedulerManagerServiceImpl(Scheduler scheduler, JavaMailSender mailSender, EnvVariableConfig envVariableConfig,
                                       SimpMessagingTemplate messagingTemplate, StatisticsDataService statisticsDataService,
                                       InfoPlatformService infoPlatformService) {
        Assert.notNull(scheduler, "SchedulerManagerServiceImpl.scheduler must be not null!");
        Assert.notNull(envVariableConfig, "SchedulerManagerServiceImpl.envVariableConfig must be not null!");
        Assert.notNull(mailSender, "SchedulerManagerServiceImpl.mailSender must be not null!");
        Assert.notNull(messagingTemplate, "SchedulerManagerServiceImpl.messagingTemplate must be not null!");
        Assert.notNull(statisticsDataService, "SchedulerManagerServiceImpl.statisticsDataService must be not null!");
        Assert.notNull(infoPlatformService, "SchedulerManagerServiceImpl.infoPlatformService must be not null!");
        this.scheduler = scheduler;
        this.mailSender = mailSender;
        this.envVariableConfig = envVariableConfig;
        this.messagingTemplate = messagingTemplate;
        this.statisticsDataService = statisticsDataService;
        this.infoPlatformService = infoPlatformService;

    }

    @Override
    public String removeJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        if (!this.scheduler.checkExists(jobKey)) {
            LOGGER.info("待删除的作业【{}】不存在！", jobKey);
            return ResponseData.NO_JOB_EXISTS;
        }
        boolean result = this.scheduler.deleteJob(jobKey);
        LOGGER.info("删除作业【{}】的状态:{}", jobKey, result);
        return result ? ResponseData.SUCCESS : ResponseData.FAILURE;
    }

    @Override
    public String runOrPauseJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        if (!this.scheduler.checkExists(jobKey)) {
            LOGGER.info("待暂停的作业【{}】不存在！", jobKey);
            return ResponseData.NO_JOB_EXISTS;
        }
        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
        Trigger schedulerTrigger = null;
        for (Trigger trigger : triggers) {
            if ("PAUSED".equals(scheduler.getTriggerState(trigger.getKey()).name())) {
                LOGGER.info("触发器的当前状态为PAUSED,表示要执行这个作业【{}】", jobKey);
                schedulerTrigger = trigger;
                break;
            }
        }
        if (schedulerTrigger != null) {
            JobDetail detail = this.scheduler.getJobDetail(jobKey);
            this.scheduler.addJob(detail, true);
            this.scheduler.rescheduleJob(new TriggerKey(jobName, jobGroup), schedulerTrigger);
        } else {
            this.scheduler.pauseJob(jobKey);
        }
        LOGGER.info("根据任务的状态执行/暂停作业【{}】", jobKey);
        return ResponseData.SUCCESS;
    }

    @Override
    public List<CronJobInfoVO> getJobDetailsList() throws SchedulerException {
        List<CronJobInfoVO> jobs = new ArrayList<>();
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
        for (JobKey jobKey : jobKeys) {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            for (Trigger trigger : triggers) {
                CronJobInfoVO job = new CronJobInfoVO();
                job.setJobName(jobKey.getName());
                job.setJobGroup(jobKey.getGroup());
                job.setJobStatus(scheduler.getTriggerState(trigger.getKey()).name());
                if (trigger instanceof CronTrigger) job.setCron(((CronTrigger) trigger).getCronExpression());
                job.setPreviousFireTime(ProjectUtil.defaultDateFormat(trigger.getPreviousFireTime()));
                job.setNextFireTime(ProjectUtil.defaultDateFormat(trigger.getNextFireTime()));
                job.setJobDescription(jobDetail.getDescription());
                JobDataMap jobDataMap = jobDetail.getJobDataMap();
                String targetClass = jobDataMap.getString(JobConstant.JOB_TARGET_CLASS);
                job.setTargetClass(targetClass);
                job.setJobType(TmsGoodsPushJob.class.getName().equals(targetClass)
                        ? "TmsGoodsPushJob" : "StatefulJob");
                job.setContactEmails(jobDataMap.getString(JobConstant.JOB_EXCEPTION_EMAILS_KEY));
                job.setSuccessEmails(jobDataMap.getString(JobConstant.JOB_SUCCESS_MAILS_KEY));
                job.setCalculateDate(jobDataMap.getString(JobConstant.JOB_CALCULATE_DATE));
                jobs.add(job);
                LOGGER.debug("作业【{}】的信息:{}", jobKey, job);
            }
        }
        return jobs;
    }

    @Override
    public boolean testJobNameRepeat(final String jobName) {
        try {
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
            List<String> jobNames = jobKeys.stream().map(Key::getName).collect(Collectors.toList());
            return jobNames.contains(jobName);
        } catch (SchedulerException e) {
            LOGGER.error("获取JobKey列表失败！", e);
        }
        return false;
    }

    @Override
    public void addOrUpdateJob(String jobName, String jobGroup, String cron, String jobDescription,
                               Class<? extends Job> executeClass, String contactEmails, String successEmails)
            throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put(JobConstant.JOB_TARGET_CLASS, executeClass.getName());
        map.put(JobConstant.JOB_EXCEPTION_EMAILS_KEY, contactEmails);
        map.put(JobConstant.JOB_SUCCESS_MAILS_KEY, successEmails);
        String group = StringUtils.isEmpty(jobGroup) ? JobConstant.DEFAULT_GROUP : jobGroup; // 默认的作业组
        // 界面调度时是使用的CRON表达式来构建作业的
        ScheduleJobVO scheduleJobVO = new ScheduleJobVO(jobName, group, jobDescription,
                executeClass, ScheduleJobBuilder.JobTypeEnum.CRON_JOB, cron);
        // 添加监听器,只添加一次
        if (this.scheduler.getListenerManager().getJobListener(CustomJobListener.CUSTOM_JOB_LISTENER_NAME) == null)
            this.scheduler.getListenerManager().addJobListener(new CustomJobListener(mailSender,
                    messagingTemplate, envVariableConfig));
        ScheduleJobBuilder.builderScheduleJobByType(this.scheduler, scheduleJobVO, map);
    }

    @Override
    public String runJobOnce(String jobName, String jobGroup, String jobDescription, Class<? extends Job> executeClass,
                             String contactEmails, String successEmails, String statisticsDate) throws SchedulerException {
        Map<String, Object> map = new HashMap<>();
        map.put(JobConstant.JOB_TARGET_CLASS, executeClass.getName());
        map.put(JobConstant.JOB_EXCEPTION_EMAILS_KEY, contactEmails);
        map.put(JobConstant.JOB_SUCCESS_MAILS_KEY, successEmails);
        map.put(JobConstant.JOB_CALCULATE_DATE, statisticsDate);
        map.put(JobConstant.JOB_RUN_ONCE, true);
        String group = StringUtils.isEmpty(jobGroup) ? JobConstant.DEFAULT_GROUP : jobGroup;
        // 构建单次执行作业
        ScheduleJobVO scheduleJobVO = new ScheduleJobVO(jobName, group, jobDescription,
                executeClass, ScheduleJobBuilder.JobTypeEnum.SINGLE_JOB);
        // 添加监听器,只添加一次
        if (this.scheduler.getListenerManager().getJobListener(CustomJobListener.CUSTOM_JOB_LISTENER_NAME) == null)
            this.scheduler.getListenerManager().addJobListener(new CustomJobListener(mailSender,
                    messagingTemplate, envVariableConfig));
        boolean isSuccess = ScheduleJobBuilder.builderScheduleJobByType(this.scheduler, scheduleJobVO, map);
        return isSuccess ? ResponseData.SUCCESS : ResponseData.FAILURE;
    }

    @Override
    public Map<String, Object> getChartData() {
        String startDate = ProjectUtil.getSpecifiedDateStr(new Date(), -31, ProjectUtil.DAY_DATE_FORMAT);
        List<String> xAxis = ProjectUtil.getMonthDaysList();
        List<String> sendCarData = new ArrayList<>();
        List<String> profitData = new ArrayList<>();
        Map<String, String> sendCarDataMap = statisticsDataService.groupDataStatisticsByType(startDate, 1);
        Map<String, String> profitDataMap = statisticsDataService.groupDataStatisticsByType(startDate, 2);
        for (String x : xAxis) {
            sendCarData.add(StringUtils.isEmpty(sendCarDataMap.get(x)) ? "0" : sendCarDataMap.get(x));
            profitData.add(StringUtils.isEmpty(profitDataMap.get(x)) ? "0" : profitDataMap.get(x));
        }
        Map<String, Object> result = new HashMap<>();
        result.put(X_AXIS_DATA, xAxis);
        result.put(SEND_CAR_DATA, sendCarData);
        result.put(PROFIT_DATA, profitData);
        return result;
    }

    @Override
    public String rebuildGoodsPushJob(List<TmsSourceGoodsPushConfig> pushConfigList) throws Exception {
        // 先删除调度中心有的任务
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupStartsWith(JobConstant.TMS_GOODS_JOB_GROUP));
        for (JobKey jobKey : jobKeys) {
            if (this.scheduler.checkExists(jobKey)) {
                boolean result = this.scheduler.deleteJob(jobKey);
                LOGGER.info("重建推送货源配置作业时删除已有的作业【{}】结果：{}！", jobKey, result);
            }
        }
        // 再重新构建新的作业
        if (this.scheduler.getListenerManager().getJobListener(CustomJobListener.CUSTOM_JOB_LISTENER_NAME) == null)
            this.scheduler.getListenerManager().addJobListener(new CustomJobListener(mailSender,
                    messagingTemplate, envVariableConfig));
        TmsGoodsPushHelper.buildGoodsPushJob(JobConstant.TMS_GOODS_PUSH_DESC, TmsGoodsPushJob.class,
                JobConstant.TMS_GOODS_PUSH_CONTACT_EMAILS, JobConstant.DEFAULT_JOB_SUCCESS_EMAILS,
                scheduler, pushConfigList);
        LOGGER.info("重建货源推送配置作业成功！");
        return ResponseData.SUCCESS;
    }

    @Override
    public String buildGoodsAndDriverRelevance(Integer goodsConfigId, Integer whichDay) {
        if (whichDay == null || (whichDay != 0 && whichDay != 1 && whichDay != 2 && whichDay != 3))
            return ResponseData.PUSH_CONFIG_NO_VALIDITY_DAY;
        if (goodsConfigId == null)
            return ResponseData.PUSH_CONFIG_NULL_CONFIG_ID;
        // 修改#5需求,不管模板是否启用,均生成货源与司机关系数据
        TmsSourceGoodsConfig config = infoPlatformService.getTmsSourceGoodsConfigById(goodsConfigId);
        // 判断该配置是否已经失效
        if (config == null) return ResponseData.PUSH_CONFIG_HAS_DELETE;
        // 要判断每周用车时间
        Date now = new Date();
        String dayOfWeek = ProjectUtil.dayOfWeek(now, whichDay, true) + "";
        if (!config.getApplyWeek().contains(dayOfWeek))
            return ResponseData.PUSH_CONFIG_NO_APPLY_WEEK;
        // 判断是否在有效期内
        if (config.getEffectiveStatus() != 1) {
            Date validityStart = config.getValidityStart();
            Date validityEnd = config.getValidityEnd();
            if (validityEnd == null || validityStart == null)
                return ResponseData.PUSH_CONFIG_NULL_VALIDITY_DATE;
            // 取指定天数的那天的日期
            Date specifiedDate = ProjectUtil.getDayNoHourMinSecond(ProjectUtil
                    .getSpecifiedDate(now, whichDay));
            if (validityStart.after(specifiedDate) || validityEnd.before(specifiedDate))
                return ResponseData.PUSH_CONFIG_NOT_IN_VALIDITY_DATE;
        }
        // 界面上手动推送时生成指定天数的货源信息
        TmsGoodsPushHelper.buildSourceGoods(Lists.newArrayList(config),
                whichDay, infoPlatformService);
        // 生成货源与司机关联数据
        String yesterday = ProjectUtil.getSpecifiedDateStr(new Date(), -1, ProjectUtil.DAY_DATE_FORMAT);
        TmsGoodsPushHelper.buildGoodsDriverRelevance(infoPlatformService, yesterday, goodsConfigId, whichDay);
        return JobConstant.JOB_CALL_SUCCESS;
    }
}