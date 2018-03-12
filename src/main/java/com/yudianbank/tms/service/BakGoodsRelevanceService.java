package com.yudianbank.tms.service;

/**
 * 复制及备份货源与司机关联表的数据作业Service
 *
 * @author Song Lea
 */
public interface BakGoodsRelevanceService {

    // 备份及删除数据
    String bakGoodsRelevanceOps(String jobKey, String bakDate);
}
