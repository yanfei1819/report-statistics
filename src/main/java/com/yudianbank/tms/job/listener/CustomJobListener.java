package com.yudianbank.tms.job.listener;

import com.yudianbank.tms.configure.EnvVariableConfig;
import com.yudianbank.tms.configure.WebSocketConfig;
import com.yudianbank.tms.job.manager.JobConstant;
import com.yudianbank.tms.util.ProjectUtil;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;

/**
 * 作业执行的监听类--用于作业邮件通知与界面弹窗通知
 *
 * @author Song Lee
 */
public class CustomJobListener implements JobListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomJobListener.class);
    public static final String CUSTOM_JOB_LISTENER_NAME = "CustomJobListener";

    private JavaMailSender mailSender;
    private SimpMessagingTemplate simpMessagingTemplate;
    private EnvVariableConfig envVariableConfig;

    // 这个类每次使用new关键字创建的,故不能使用spring的注入机制来注入成员变量
    public CustomJobListener(JavaMailSender mailSender, SimpMessagingTemplate simpMessagingTemplate,
                             EnvVariableConfig envVariableConfig) {
        this.mailSender = mailSender;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.envVariableConfig = envVariableConfig;
    }

    @Override
    // 作业监听器--作业执行完成后
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        StringWriter sw = null;
        try {
            JobDetail jobDetail = context.getJobDetail();
            String jobKey = jobDetail.getKey().toString();
            String calDate = jobDetail.getJobDataMap().getString(JobConstant.JOB_CALCULATE_DATE);
            if (envVariableConfig.isJobEmail()) {
                if (jobException != null) {
                    LOGGER.error("作业监听器--作业【" + jobKey + "】执行出现异常！", jobException);
                    String contactEmails = jobDetail.getJobDataMap().getString(JobConstant.JOB_EXCEPTION_EMAILS_KEY);
                    if (StringUtils.hasText(contactEmails)) {
                        String[] emails = contactEmails.trim().split(ProjectUtil.SEMICOLON_SEPARATOR);
                        // 处理并返回异常的所有堆栈内容,否则不全
                        String subject = buildMailSubject(jobKey, calDate, true);
                        sw = new StringWriter();
                        jobException.printStackTrace(new PrintWriter(sw, true));
                        sw.flush();
                        String text = subject + "\n" + sw.toString();
                        mailNotice(emails, subject, text, jobKey);
                    }
                } else {
                    // 看是否配置了正常执行完成后是否发送邮件通知
                    String noticeMails = jobDetail.getJobDataMap().getString(JobConstant.JOB_SUCCESS_MAILS_KEY);
                    if (StringUtils.hasText(noticeMails)) {
                        String[] emails = noticeMails.trim().split(ProjectUtil.SEMICOLON_SEPARATOR);
                        String subject = buildMailSubject(jobKey, calDate, false);
                        String result = jobDetail.getJobDataMap().getString(JobConstant.JOB_SUCCESS_NOTICE_CONTENT);
                        String text = subject + "\n" + result;
                        mailNotice(emails, subject, text, jobKey);
                    }
                }
            } else {
                LOGGER.warn("作业监听器--未开启作业完成后邮件通知，" +
                        "可以将配置文件中【com.yudianbank.tms.job.email.enable】设置为true启用邮件通知！");
            }
            LOGGER.info("作业监听器--作业【{}】执行完成，WebSocket推送界面通知！", jobKey);
            pageNotice(jobKey, jobException != null);
        } finally {
            IOUtils.closeQuietly(sw);
        }
    }

    // 作业执行完成后邮件通知的实现
    private void mailNotice(String[] emails, String subject, String text, String jobKey) {
        ProjectUtil.EXECUTOR_SERVICE.execute(() -> {
            Thread.currentThread().setUncaughtExceptionHandler((t, e) -> LOGGER.error("异步发送邮件出现异常！", e));
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(envVariableConfig.getSendUser());
            message.setTo(emails);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            LOGGER.info("作业监听器--作业【{}】异步发送邮件成功，收件人：{}", jobKey, Arrays.toString(emails));
        });
    }

    // 构建邮件主题
    private String buildMailSubject(String jobKey, String calDate, boolean isException) {
        StringBuilder subject = new StringBuilder();
        subject.append("<").append(envVariableConfig.getProfile()).append(">作业【").append(jobKey)
                .append("】处理的日期或提前的天数【").append(calDate).append("】");
        if (isException)
            subject.append("执行异常！");
        else
            subject.append("执行完成(无异常)！");
        return subject.toString();
    }

    // 界面上发送通知作业执行完成
    private void pageNotice(String jobKey, boolean exception) {
        if (exception)
            simpMessagingTemplate.convertAndSend(WebSocketConfig.TOPIC_REQUIRE, "作业【" + jobKey
                    + "】<br>执行异常，发送通知邮件！【" + ProjectUtil.defaultDateFormat(new Date()) + "】");
        else
            simpMessagingTemplate.convertAndSend(WebSocketConfig.TOPIC_REQUIRE, "作业【" + jobKey
                    + "】<br>执行完成！【" + ProjectUtil.defaultDateFormat(new Date()) + "】");
    }

    @Override
    // 作业监听器--作业将被执行
    public void jobToBeExecuted(JobExecutionContext context) {
    }

    @Override
    // 作业监听器--作业执行被拒绝
    public void jobExecutionVetoed(JobExecutionContext context) {
    }

    @Override
    public String getName() {
        return CUSTOM_JOB_LISTENER_NAME;
    }
}