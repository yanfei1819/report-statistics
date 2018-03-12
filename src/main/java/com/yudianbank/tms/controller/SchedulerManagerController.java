package com.yudianbank.tms.controller;

import com.yudianbank.tms.configure.WebSocketConfig;
import com.yudianbank.tms.job.TmsGoodsPushJob;
import com.yudianbank.tms.job.helper.TmsCarTransportJobHelper;
import com.yudianbank.tms.job.helper.TmsProfitJobHelper;
import com.yudianbank.tms.model.TmsSourceGoodsPushConfig;
import com.yudianbank.tms.model.vo.CronJobInfoVO;
import com.yudianbank.tms.model.vo.ResponseData;
import com.yudianbank.tms.service.InfoPlatformService;
import com.yudianbank.tms.service.SchedulerManagerService;
import com.yudianbank.tms.util.ProjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.util.*;

/**
 * 界面请求Controller层,对Schedule任务进行界面管理
 *
 * @author Song Lea
 */
@Controller
@RequestMapping("/api/tms/schedule/handler")
@Api(value = "SchedulerManagerController", description = "作业管理界面API")
public class SchedulerManagerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerManagerController.class);
    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private SchedulerManagerService schedulerManagerService;
    private SimpMessagingTemplate simpMessagingTemplate;
    private InfoPlatformService infoPlatformService;

    public SchedulerManagerController() {
    }

    @Autowired
    public SchedulerManagerController(SchedulerManagerService schedulerManagerService, SimpMessagingTemplate simpMessagingTemplate,
                                      InfoPlatformService infoPlatformService) {
        Assert.notNull(schedulerManagerService, "StatisticsDataController.schedulerManagerService must be not null!");
        Assert.notNull(simpMessagingTemplate, "StatisticsDataController.simpMessagingTemplate must be not null!");
        Assert.notNull(infoPlatformService, "StatisticsDataController.infoPlatformService must be not null!");
        this.schedulerManagerService = schedulerManagerService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.infoPlatformService = infoPlatformService;
    }

    // 渲染界面
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @ApiOperation(value = "作业管理界面", hidden = true)
    public String index() {
        return "schedule";
    }

    // 移除作业
    @ResponseBody
    @RequestMapping(value = "/removeJob", method = RequestMethod.POST)
    @ApiOperation(value = "移除作业", notes = "根据jobName与jobGroup来指定删除作业", response = String.class, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "作业名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "jobGroup", value = "作业组", required = true, dataType = "String", paramType = "query")
    })
    public String removeJob(String jobName, String jobGroup) {
        try {
            return schedulerManagerService.removeJob(jobName, jobGroup);
        } catch (Exception e) {
            LOGGER.error("作业删除失败！", e);
        }
        return ResponseData.FAILURE;
    }

    // 执行或暂停作业(根据作业的实时状态判断)
    @ResponseBody
    @RequestMapping(value = "/operateJob", method = RequestMethod.POST)
    @ApiOperation(value = "执行或暂停作业", notes = "根据jobName与jobGroup来指定执行或暂停作业", response = String.class, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "作业名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "jobGroup", value = "作业组", required = true, dataType = "String", paramType = "query")
    })
    public String runJob(String jobName, String jobGroup) {
        try {
            return schedulerManagerService.runOrPauseJob(jobName, jobGroup);
        } catch (Exception e) {
            LOGGER.error("作业执行/暂停失败！", e);
        }
        return ResponseData.FAILURE;
    }

    // 立即执行一次作业
    @ResponseBody
    @RequestMapping(value = "/runJobOnce", method = RequestMethod.POST)
    @ApiOperation(value = "单次执行作业", notes = "根据配置条件立即执行一次指定的作业", response = String.class, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "作业名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "jobGroup", value = "作业组", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "targetClass", value = "作业类", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "jobDescription", value = "作业描述", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "contactEmails", value = "异常联系邮箱(多个时请用逗号分隔)", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "successEmails", value = "执行后通知邮箱(多个时请用逗号分隔)", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "statisticsDate", value = "统计日期", required = true, dataType = "String", paramType = "query")
    })
    @SuppressWarnings("unchecked")
    public String runJobOnce(String jobName, String jobGroup, String targetClass, String jobDescription,
                             String contactEmails, String successEmails, String statisticsDate) {
        if (targetClass == null || "".equals(targetClass.trim()))
            return ResponseData.EMPTY_TARGET_CLASS;
        Class<? extends Job> executeClass;
        try {
            Class<?> target = Class.forName(targetClass);
            boolean father = Job.class.isAssignableFrom(target);
            if (!father)
                return ResponseData.ILLEGAL_TARGET_CLASS;
            executeClass = (Class<? extends Job>) target;
        } catch (ClassNotFoundException e) {
            LOGGER.error("立即执行一次作业时目标类【{}】不存在！", targetClass);
            return ResponseData.NO_TARGET_CLASS;
        }
        if (statisticsDate == null || "".equals(statisticsDate.trim()))
            return ResponseData.NULL_CALCULATE_DATE;
        else
            try {
                Date date = ProjectUtil.getDateByStr(statisticsDate, ProjectUtil.DAY_DATE_FORMAT);
                if (date.getTime() > new Date().getTime())
                    return ResponseData.LARGE_CALCULATE_DATE;
            } catch (ParseException e) {
                LOGGER.error("立即执行一次作业时统计日期【{}】格式不正确！", statisticsDate);
                return ResponseData.ILLEGAL_CALCULATE_DATE;
            }
        // 作业名需要唯一,用当前的时间戳
        String name = jobName + ProjectUtil.UNDERLINE_SEPARATOR + System.currentTimeMillis();
        try {
            return schedulerManagerService.runJobOnce(name, jobGroup, jobDescription, executeClass,
                    contactEmails, successEmails, statisticsDate);
        } catch (Exception e) {
            LOGGER.error("立即执行一次作业失败！", e);
        }
        return ResponseData.FAILURE;
    }

    // 获取所有作业
    @ResponseBody
    @RequestMapping(value = "/getJobs", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有作业", notes = "获取所有作业列表", responseContainer = "List",
            response = CronJobInfoVO.class, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CronJobInfoVO> getJobDetailsList() {
        try {
            return schedulerManagerService.getJobDetailsList();
        } catch (Exception e) {
            LOGGER.error("获取所有作业列表失败！", e);
        }
        return new ArrayList<>();
    }

    // 添加作业
    @ResponseBody
    @RequestMapping(value = "/addJob", method = RequestMethod.POST)
    @ApiOperation(value = "添加作业", notes = "添加作业", response = String.class, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "作业名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "jobGroup", value = "作业组", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "cron", value = "CRON表达式", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "targetClass", value = "作业类", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "jobDescription", value = "作业描述", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "contactEmails", value = "异常联系邮箱(多个时请用逗号分隔)", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "successEmails", value = "执行后通知邮箱(多个时请用逗号分隔)", dataType = "String", paramType = "query")
    })
    @SuppressWarnings("unchecked")
    public String addJob(String jobName, String jobGroup, String cron, String jobDescription, String targetClass,
                         String contactEmails, String successEmails) {
        if (jobName == null || "".equals(jobName.trim()))
            return ResponseData.NO_JOB_NAME;
        if (cron == null || "".equals(cron.trim()))
            return ResponseData.NO_CRON;
        if (!CronExpression.isValidExpression(cron))
            return ResponseData.ILLEGAL_CRON;
        if (schedulerManagerService.testJobNameRepeat(jobName))
            return ResponseData.REPEAT_JOB_NAME;
        if (targetClass == null || "".equals(targetClass.trim()))
            return ResponseData.EMPTY_TARGET_CLASS;
        Class<? extends Job> executeClass;
        try {
            Class<?> target = Class.forName(targetClass);
            boolean father = Job.class.isAssignableFrom(target);
            if (!father)
                return ResponseData.ILLEGAL_TARGET_CLASS;
            executeClass = (Class<? extends Job>) target;
        } catch (ClassNotFoundException e) {
            LOGGER.error("添加作业时目标类【{}】不存在！", targetClass);
            return ResponseData.NO_TARGET_CLASS;
        }
        try {
            schedulerManagerService.addOrUpdateJob(jobName, jobGroup, cron, jobDescription,
                    executeClass, contactEmails, successEmails);
        } catch (Exception e) {
            LOGGER.error("添加作业失败！", e);
            return ResponseData.FAILURE;
        }
        return ResponseData.SUCCESS;
    }

    // 修改作业
    @ResponseBody
    @RequestMapping(value = "/editJob", method = RequestMethod.POST)
    @ApiOperation(value = "修改作业", notes = "修改作业", response = String.class, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "作业名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "jobGroup", value = "作业组", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "cron", value = "CRON表达式", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "targetClass", value = "作业类", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "jobDescription", value = "作业描述", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "contactEmails", value = "异常联系邮箱(多个时请用逗号分隔)", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "successEmails", value = "执行后通知邮箱(多个时请用逗号分隔)", dataType = "String", paramType = "query")
    })
    @SuppressWarnings("unchecked")
    public String editJob(String jobName, String jobGroup, String cron, String jobDescription, String targetClass,
                          String contactEmails, String successEmails) {
        if (!CronExpression.isValidExpression(cron))
            return ResponseData.ILLEGAL_CRON;
        Class<? extends Job> executeClass;
        try {
            Class<?> target = Class.forName(targetClass);
            boolean father = Job.class.isAssignableFrom(target);
            if (!father)
                return ResponseData.ILLEGAL_TARGET_CLASS;
            executeClass = (Class<? extends Job>) target;
        } catch (ClassNotFoundException e) {
            LOGGER.error("修改作业时目标类【{}】不存在！", targetClass);
            return ResponseData.NO_TARGET_CLASS;
        }
        try {
            schedulerManagerService.addOrUpdateJob(jobName, jobGroup, cron, jobDescription,
                    executeClass, contactEmails, successEmails);
        } catch (Exception e) {
            LOGGER.error("修改作业失败！", e);
            return ResponseData.FAILURE;
        }
        return ResponseData.SUCCESS;
    }

    // 发车与运输报表利润报表的时间段作业:type=1发车与运输报表;2:利润报表(与quartz调度无关,直接执行实现逻辑)
    @ResponseBody
    @RequestMapping(value = "/runZone", method = RequestMethod.GET)
    @ApiOperation(value = "[统计报表]批量执行", notes = "发车与运输报表利润报表的时间段作业", response = String.class, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startDate", value = "开始日期", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "结束日期", dataType = "String", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "类型(1:发车与运输报表;2:利润报表)", required = true, dataType = "int",
                    allowableValues = "1,2", paramType = "query")
    })
    public String runZoneJob(String startDate, String endDate, int type) {
        if (startDate == null || "".equals(startDate.trim()))
            return ResponseData.NULL_START_DATE;
        if (endDate == null || "".equals(endDate.trim()))
            return ResponseData.NULL_END_DATE;
        Date start, end;
        try {
            start = ProjectUtil.getDateByStr(startDate, ProjectUtil.DAY_DATE_FORMAT);
            end = ProjectUtil.getDateByStr(endDate, ProjectUtil.DAY_DATE_FORMAT);
            if (end.getTime() < start.getTime())
                return ResponseData.ILLEGAL_START_END_DATE;
        } catch (ParseException e) {
            LOGGER.error("报表的时间段统计时日期【{}-{}】格式不正确！", startDate, endDate);
            return ResponseData.ILLEGAL_CALCULATE_DATE;
        }
        List<String> calDateList = ProjectUtil.getBetweenDateStr(start, end, ProjectUtil.DAY_DATE_FORMAT);
        final String typeName = type == 1 ? "发车与传输报表" : type == 2 ? "利润报表" : "";
        ProjectUtil.EXECUTOR_SERVICE.submit(() -> {
            int success = 0;
            for (String calDate : calDateList) {
                LOGGER.info("统计日期【{}】的{}开始！", calDate, typeName);
                String jobKey = calDate + typeName;
                try {
                    if (type == 1)
                        TmsCarTransportJobHelper.tmsCarTransportJobImpl(jobKey, calDate);
                    else if (type == 2)
                        TmsProfitJobHelper.tmsProfitJobImpl(jobKey, calDate);
                    success++;
                } catch (JobExecutionException e) {
                    LOGGER.error("作业【" + jobKey + "】统计实现异常！", e);
                }
                LOGGER.info("统计日期【{}】{}结束！", calDate, typeName);
            }
            simpMessagingTemplate.convertAndSend(WebSocketConfig.TOPIC_REQUIRE,
                    "时间段：【" + startDate + "至" + endDate + "】作业执行结束！<br>"
                            + "共" + calDateList.size() + "个作业，执行无异常：" + success + "个。");
        });
        return ResponseData.TRYING_TO_DEAL_WITH;
    }

    // 滚动加载日志文件(末尾的2000行)
    @ResponseBody
    @RequestMapping(value = "/scrollLogInfo", method = RequestMethod.GET)
    @ApiOperation(value = "滚动加载日志文件(末尾的2000行)", hidden = true)
    public Map<String, Object> scrollLogInfo() {
        Map<String, Object> result = new HashMap<>();
        RandomAccessFile randomFile = null;
        String logFile = "";
        try {
            String date = ProjectUtil.dateFormatByPattern(new Date(), ProjectUtil.DAY_DATE_FORMAT);
            result.put(ProjectUtil.LOG_DATE, date);
            // 取日志文件名,固定文件名格式
            logFile = ProjectUtil.LOG_DIR_PREFIX + "report-" + date + ".log";
            randomFile = new RandomAccessFile(logFile, "r");
            // 获取文件指定倒数行的内容
            String fileContent = ProjectUtil.getFileReverseByLine(randomFile, ProjectUtil.LOG_DEFAULT_LINE);
            if (Objects.isNull(fileContent)) {
                result.put(ProjectUtil.LOG_CONTENT, "日志文件：" + logFile + " 文件内容为空！");
                return result;
            }
            result.put(ProjectUtil.LOG_CONTENT, fileContent);
        } catch (FileNotFoundException e) {
            result.put(ProjectUtil.LOG_CONTENT, "日志文件：" + logFile + " 不存在！");
        } catch (IOException e) {
            result.put(ProjectUtil.LOG_CONTENT, "日志文件：" + logFile + " 读取失败！");
        } finally {
            IOUtils.closeQuietly(randomFile);
        }
        return result;
    }

    // 下载日志文件
    @ResponseBody
    @RequestMapping(value = "/downloadLogFile", method = RequestMethod.GET)
    @ApiOperation(value = "下载日志文件", hidden = true)
    public void downloadLogFile(HttpServletResponse response, String fileDate) {
        OutputStream outputStream = null;
        FileInputStream fileInputStream = null;
        String logFile = "";
        try {
            logFile = ProjectUtil.LOG_DIR_PREFIX + "report-" + fileDate + ".log";  // 取日志文件名,固定文件名格式
            response.setHeader("content-disposition", "attachment;filename=report-" + fileDate + ".log");
            outputStream = response.getOutputStream();
            fileInputStream = new FileInputStream(new File(logFile));
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int len;
            while ((len = fileInputStream.read(buffer)) > 0)
                outputStream.write(buffer, 0, len);
        } catch (FileNotFoundException e) {
            LOGGER.error("日志文件【{}】不存在！{}", logFile, e.getMessage());
        } catch (IOException e) {
            LOGGER.error("日志文件【{}】下载异常！{}", logFile, e.getMessage());
        } finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    // 发车运输与利润报表的数据趋势
    @ResponseBody
    @RequestMapping(value = "/getChartData", method = RequestMethod.GET)
    @ApiOperation(value = "发车运输与利润报表数据统计趋势", notes = "发车运输与利润报表数据统计趋势",
            response = Map.class, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getChartData() {
        return schedulerManagerService.getChartData();
    }

    // 重新构建信息平台的作业
    @ResponseBody
    @RequestMapping(value = "/rebuildGoodsPushJob", method = RequestMethod.GET)
    @ApiOperation(value = "重新构建信息平台货源推送作业", notes = "重新构建信息平台货源推送作业", response = String.class,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String rebuildGoodsPushJob() {
        try {
            List<TmsSourceGoodsPushConfig> pushConfigList = infoPlatformService.listSourceGoodsPushConfig();
            if (pushConfigList == null) return ResponseData.NO_PUSH_CONFIG;
            return schedulerManagerService.rebuildGoodsPushJob(pushConfigList);
        } catch (Exception e) {
            LOGGER.error("重新构建信息平台货源推送作业失败！", e);
            return ResponseData.FAILURE;
        }
    }

    // 生成货源表与货源与司机关联表,供app后管平台调用
    @ResponseBody
    @RequestMapping(value = "/buildGoodsAndDriverRelevance", method = RequestMethod.GET)
    @ApiOperation(value = "生成货源表与货源与司机关联表", notes = "生成货源表与货源与司机关联表", response = String.class,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsConfigId", value = "货源配置表ID", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "whichDay", value = "计算哪天数据(0:今天;1:明天;2:后天;3:大后天)", required = true,
                    dataType = "int", paramType = "query", allowableValues = "0,1,2,3")
    })
    public String buildGoodsAndDriverRelevance(Integer goodsConfigId, Integer whichDay) {
        try {
            if (TmsGoodsPushJob.IS_RUNNING)
                return ResponseData.PUSH_CONFIG_IS_RUNNING;
            return schedulerManagerService.buildGoodsAndDriverRelevance(goodsConfigId, whichDay);
        } catch (Exception e) {
            LOGGER.error("货源配置表【ID：" + goodsConfigId + "】生成货源表与货源与司机关联表失败！", e);
            return ResponseData.FAILURE;
        }
    }
}