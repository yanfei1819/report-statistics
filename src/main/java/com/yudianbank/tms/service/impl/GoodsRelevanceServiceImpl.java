package com.yudianbank.tms.service.impl;

import com.yudianbank.tms.dao.InfoPlatformDao;
import com.yudianbank.tms.service.BakGoodsRelevanceService;
import com.yudianbank.tms.util.ProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * 复制及备份货源与司机关联表的数据作业Service实现
 *
 * @author Song Lea
 */
@Service
public class GoodsRelevanceServiceImpl implements BakGoodsRelevanceService {

    private InfoPlatformDao infoPlatformDao;

    public GoodsRelevanceServiceImpl() {
    }

    @Autowired
    public GoodsRelevanceServiceImpl(InfoPlatformDao infoPlatformDao) {
        Assert.notNull(infoPlatformDao, "InfoPlatformServiceImpl.infoPlatformDao must be not null!");
        this.infoPlatformDao = infoPlatformDao;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String bakGoodsRelevanceOps(String jobKey, String bakDate) {
        StringBuilder result = new StringBuilder("作业【").append(jobKey).append("】开始执行备份与删除【")
                .append(bakDate).append("】之前的数据！\n开始执行时间：")
                .append(ProjectUtil.dateFormatByPattern(new Date(), ProjectUtil.DEFAULT_DATE_FORMAT)).append("\n");
        int insert = infoPlatformDao.bakGoodsRelevanceBeforeDate(bakDate);
        result.append("插入YD_TMS_SOURCEGOODS_DRIVER_RELEVANCE_copy备份表数据量：").append(insert).append("\n");
        int delete = infoPlatformDao.delGoodsRelevanceBeforeDate(bakDate);
        result.append("删除YD_TMS_SOURCEGOODS_DRIVER_RELEVANCE表数据量：").append(delete).append("\n");
        result.append("执行完成时间：").append(ProjectUtil
                .dateFormatByPattern(new Date(), ProjectUtil.DEFAULT_DATE_FORMAT));
        return result.toString();
    }
}
