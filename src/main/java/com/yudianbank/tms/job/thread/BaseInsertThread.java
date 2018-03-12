package com.yudianbank.tms.job.thread;

import com.yudianbank.tms.job.manager.JobConstant;
import com.yudianbank.tms.service.StatisticsDataService;
import com.yudianbank.tms.util.ProjectUtil;
import com.yudianbank.tms.configure.ServletContextConfig;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 处理并插入数据的逻辑
 *
 * @param <T> 类型
 * @author Song Lea
 */
public class BaseInsertThread<T> implements Callable<String> {

    private List<T> dataList;
    private StatisticsDataService statisticsDataService;

    BaseInsertThread(List<T> dataList) {
        this.dataList = dataList;
        this.statisticsDataService = ServletContextConfig.getBean(StatisticsDataService.class);
    }

    @Override
    public String call() throws Exception {
        List<T> processList = processDataList(dataList);
        statisticsDataService.batchInsertDataList(processList); // 插入到数据库中
        return JobConstant.JOB_CALL_SUCCESS;
    }

    // 处理dataList并返回需要使用的(钩子方法)
    protected List<T> processDataList(List<T> dataList) {
        return dataList;
    }
}