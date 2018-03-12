package com.yudianbank.tms.service;

import com.yudianbank.tms.model.TmsSourceGoodsPushConfig;
import com.yudianbank.tms.model.vo.CronJobInfoVO;
import org.quartz.Job;
import org.quartz.SchedulerException;

import java.util.List;
import java.util.Map;

/**
 * 任务管理Service层
 *
 * @author Song Lea
 */
public interface SchedulerManagerService {

    // 移除作业
    String removeJob(String jobName, String jobGroup) throws SchedulerException;

    // 执行/暂停作业
    String runOrPauseJob(String jobName, String jobGroup) throws SchedulerException;

    // 获取所有的作业列表
    List<CronJobInfoVO> getJobDetailsList() throws SchedulerException;

    // 验证作业名是否重复
    boolean testJobNameRepeat(String jobName);

    // 操作作业(新增或修改)
    void addOrUpdateJob(String jobName, String jobGroup, String cron, String jobDescription, Class<? extends Job> executeClass,
                        String contactEmails, String successEmails) throws Exception;

    // 立即执行一次作业
    String runJobOnce(String jobName, String jobGroup, String jobDescription, Class<? extends Job> executeClass,
                      String contactEmails, String successEmails, String statisticsDate) throws SchedulerException;

    // 发车运输与利润报表的数据趋势
    Map<String, Object> getChartData();

    // 重新构建信息平台的货源推送作业
    String rebuildGoodsPushJob(List<TmsSourceGoodsPushConfig> pushConfigList) throws Exception;

    // 生成货源表与货源与司机关联表
    String buildGoodsAndDriverRelevance(Integer goodsConfigId, Integer whichDay);
}