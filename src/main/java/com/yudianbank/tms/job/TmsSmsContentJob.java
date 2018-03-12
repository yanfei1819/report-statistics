package com.yudianbank.tms.job;

import com.yudianbank.tms.service.SmsContentStatisticsService;
import com.yudianbank.tms.configure.ServletContextConfig;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.TimeUnit;

/**
 * Created by chengtianren on 2017/8/27.
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class TmsSmsContentJob extends BaseJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(TmsSmsContentJob.class);

    @Override
    @SuppressWarnings("unchecked")
    protected void jobExecuteImpl(JobExecutionContext context) throws JobExecutionException {
        long start = System.currentTimeMillis();
        LOGGER.info("TMS短信内容统计作业开始执行！");
        SMS_CONTENT_JOB_MAP.clear();  // 每次执行时清空已存在的统计数据
        SmsContentStatisticsService statisticsService = ServletContextConfig
                .getBean(SmsContentStatisticsService.class);
        // 付款信息与发车等信息统计入到缓存map中
        SMS_CONTENT_JOB_MAP.putAll(statisticsService.getPayAmtMap());
        SMS_CONTENT_JOB_MAP.putAll(statisticsService.getSendCarsAmountMap());
        // 待发送的短信信息放入到redis中去(备用以免程序重启丢失数据)
        RedisTemplate redisTemplate = ServletContextConfig.getBean("redisTemplate");
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.opsForValue().set(SMS_CONTENT_JOB_REDIS_KEY, SMS_CONTENT_JOB_MAP, 1, TimeUnit.DAYS);
        String jobKey = context.getJobDetail().getKey().toString();
        long end = System.currentTimeMillis();
        LOGGER.info("TMS短信内容统计作业【{}】执行完成，共用时：{}毫秒！", jobKey, (end - start));
    }
}