package com.yudianbank.tms.servlet;

import com.yudianbank.tms.configure.EnvVariableConfig;
import com.yudianbank.tms.job.*;
import com.yudianbank.tms.job.helper.TmsGoodsPushHelper;
import com.yudianbank.tms.job.listener.CustomJobListener;
import com.yudianbank.tms.job.manager.JobConstant;
import com.yudianbank.tms.job.manager.ScheduleJobBuilder;
import com.yudianbank.tms.model.TmsSourceGoodsPushConfig;
import com.yudianbank.tms.model.vo.ScheduleJobVO;
import com.yudianbank.tms.service.InfoPlatformService;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在ServletContext初始化后启动自定义任务
 *
 * @author Song Lea
 */
@WebListener
public class JobSelfStartListener implements ServletContextListener, JobConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobSelfStartListener.class);
    private static volatile boolean HAS_INITIALIZED = false;

    @Resource
    private Scheduler scheduler;
    @Resource
    private JavaMailSender mailSender;
    @Resource
    private EnvVariableConfig envVariableConfig;
    @Resource
    private SimpMessagingTemplate messagingTemplate;
    @Resource
    private InfoPlatformService infoPlatformService;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        LOGGER.info("系统【{}】启动完成，开始加载任务！", event.getServletContext().getServletContextName());
        if (!HAS_INITIALIZED) {
            // 保证系统启动后各个声明作业只执行一次,防止重复加入
            HAS_INITIALIZED = true;
            try {
                // 添加全局监听器(作业监听,对于异常完成的作业进行邮件通知)
                if (this.scheduler.getListenerManager().getJobListener(CustomJobListener.CUSTOM_JOB_LISTENER_NAME) == null)
                    this.scheduler.getListenerManager().addJobListener(new CustomJobListener(mailSender,
                            messagingTemplate, envVariableConfig));
                // 添加配置文件已经指定为自启动的任务
                if (envVariableConfig.isTmsCarTransportJob()) {
                    Map<String, Object> jobDataMap = new HashMap<>();
                    jobDataMap.put(JOB_TARGET_CLASS, TmsCarTransportJob.class.getName());
                    jobDataMap.put(JOB_EXCEPTION_EMAILS_KEY, TMS_CAR_TRANSPORT_CONTACT_EMAILS);
                    jobDataMap.put(JOB_SUCCESS_MAILS_KEY, DEFAULT_JOB_SUCCESS_EMAILS);
                    ScheduleJobVO scheduleJobVO = new ScheduleJobVO(TmsCarTransportJob.class.getSimpleName(),
                            DEFAULT_GROUP, TMS_CAR_TRANSPORT_DESC, TmsCarTransportJob.class,
                            ScheduleJobBuilder.JobTypeEnum.CRON_JOB, TMS_CAR_TRANSPORT_CRON);
                    boolean isSuccess = ScheduleJobBuilder.builderScheduleJobByType(scheduler, scheduleJobVO, jobDataMap);
                    LOGGER.info("添加发车与运输统计报表作业完成！", isSuccess);
                }
                if (envVariableConfig.isTmsProfitJob()) {
                    Map<String, Object> jobDataMap = new HashMap<>();
                    jobDataMap.put(JOB_TARGET_CLASS, TmsProfitJob.class.getName());
                    jobDataMap.put(JOB_EXCEPTION_EMAILS_KEY, TMS_PROFIT_CONTACT_EMAILS);
                    jobDataMap.put(JOB_SUCCESS_MAILS_KEY, DEFAULT_JOB_SUCCESS_EMAILS);
                    ScheduleJobVO scheduleJobVO = new ScheduleJobVO(TmsProfitJob.class.getSimpleName(),
                            DEFAULT_GROUP, TMS_PROFIT_DESC, TmsProfitJob.class,
                            ScheduleJobBuilder.JobTypeEnum.CRON_JOB, TMS_PROFIT_CRON);
                    boolean isSuccess = ScheduleJobBuilder.builderScheduleJobByType(scheduler, scheduleJobVO, jobDataMap);
                    LOGGER.info("添加利润统计报表作业完成！{}", isSuccess);
                }
                // 短信作业不需要执行完成后通知
                if (envVariableConfig.isTmsSmsContentJob()) {
                    Map<String, Object> jobDataMap = new HashMap<>();
                    jobDataMap.put(JOB_TARGET_CLASS, TmsSmsContentJob.class.getName());
                    jobDataMap.put(JOB_EXCEPTION_EMAILS_KEY, TMS_SMS_CONTENT_CONTACT_EMAILS);
                    ScheduleJobVO scheduleJobVO = new ScheduleJobVO(TmsSmsContentJob.class.getSimpleName(),
                            DEFAULT_GROUP, TMS_SMS_CONTENT_DESC, TmsSmsContentJob.class,
                            ScheduleJobBuilder.JobTypeEnum.CRON_JOB, TMS_SMS_CONTENT_CRON);
                    boolean isSuccess = ScheduleJobBuilder.builderScheduleJobByType(scheduler, scheduleJobVO, jobDataMap);
                    LOGGER.info("添加短信配置内容统计作业完成！{}", isSuccess);
                }
                if (envVariableConfig.isTmsSmsSendJob()) {
                    Map<String, Object> jobDataMap = new HashMap<>();
                    jobDataMap.put(JOB_TARGET_CLASS, TmsSmsSendJob.class.getName());
                    jobDataMap.put(JOB_EXCEPTION_EMAILS_KEY, TMS_SMS_SEND_CONTACT_EMAILS);
                    ScheduleJobVO scheduleJobVO = new ScheduleJobVO(TmsSmsSendJob.class.getSimpleName(),
                            DEFAULT_GROUP, TMS_SMS_SEND_DESC, TmsSmsSendJob.class,
                            ScheduleJobBuilder.JobTypeEnum.CRON_JOB, TMS_SMS_SEND_CRON);
                    boolean isSuccess = ScheduleJobBuilder.builderScheduleJobByType(scheduler, scheduleJobVO, jobDataMap);
                    LOGGER.info("添加短信内容发送作业完成！{}", isSuccess);
                }
                // 备份与删除货源与司机关联数据
                if (envVariableConfig.isCopyGoodsRelevanceJob()) {
                    Map<String, Object> jobDataMap = new HashMap<>();
                    jobDataMap.put(JOB_TARGET_CLASS, BakGoodsRelevanceJob.class.getName());
                    jobDataMap.put(JOB_EXCEPTION_EMAILS_KEY, COPY_BAK_GOODS_RELEVANCE_CONTACT_EMAILS);
                    ScheduleJobVO scheduleJobVO = new ScheduleJobVO(BakGoodsRelevanceJob.class.getSimpleName(),
                            DEFAULT_GROUP, COPY_BAK_GOODS_RELEVANCE_DESC, BakGoodsRelevanceJob.class,
                            ScheduleJobBuilder.JobTypeEnum.CRON_JOB, COPY_BAK_GOODS_RELEVANCE_CRON);
                    boolean isSuccess = ScheduleJobBuilder.builderScheduleJobByType(scheduler, scheduleJobVO, jobDataMap);
                    LOGGER.info("备份与删除货源与司机关联数据作业完成！{}", isSuccess);
                }
                // 信息平台货源推送作业
                if (envVariableConfig.isTmsGoodsPushJob()) {
                    List<TmsSourceGoodsPushConfig> configList = infoPlatformService.listSourceGoodsPushConfig();
                    TmsGoodsPushHelper.buildGoodsPushJob(TMS_GOODS_PUSH_DESC, TmsGoodsPushJob.class,
                            TMS_GOODS_PUSH_CONTACT_EMAILS, DEFAULT_JOB_SUCCESS_EMAILS, scheduler, configList);
                    LOGGER.info("信息平台货源推送作业完成！");
                }
            } catch (Exception e) {
                LOGGER.error("系统启动完成添加自定义作业失败！", e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        LOGGER.warn("ServletContext销毁！");
    }
}
