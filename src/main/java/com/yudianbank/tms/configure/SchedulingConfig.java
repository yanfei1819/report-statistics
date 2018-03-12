package com.yudianbank.tms.configure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * 定时任务配置
 *
 * @author Song Lea
 */
@Configuration
public class SchedulingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulingConfig.class);

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContextKey");
        schedulerFactoryBean.setConfigLocation(new ClassPathResource("quartz.properties"));
        LOGGER.info("初始化SchedulerFactoryBean成功！");
        return schedulerFactoryBean;
    }
}