package com.yudianbank.tms.service.impl;

import com.yudianbank.tms.dao.InfoPlatformDao;
import com.yudianbank.tms.job.manager.JobConstant;
import com.yudianbank.tms.model.TmsSourceGoods;
import com.yudianbank.tms.model.TmsSourceGoodsConfig;
import com.yudianbank.tms.model.TmsSourceGoodsPushConfig;
import com.yudianbank.tms.model.vo.AppPushVO;
import com.yudianbank.tms.model.vo.GoodsDriverRelevanceVO;
import com.yudianbank.tms.model.vo.SmsPushVO;
import com.yudianbank.tms.service.InfoPlatformService;
import com.yudianbank.tms.util.ProjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 信息平台需求的Service接口实现
 *
 * @author Song Lea
 */
@Service
public class InfoPlatformServiceImpl implements InfoPlatformService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfoPlatformServiceImpl.class);

    private InfoPlatformDao infoPlatformDao;

    public InfoPlatformServiceImpl() {
    }

    @Autowired
    public InfoPlatformServiceImpl(InfoPlatformDao infoPlatformDao) {
        Assert.notNull(infoPlatformDao, "InfoPlatformServiceImpl.infoPlatformDao must be not null!");
        this.infoPlatformDao = infoPlatformDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmsSourceGoodsPushConfig> listSourceGoodsPushConfig() {
        return infoPlatformDao.listSourceGoodsPushConfig();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmsSourceGoodsConfig> listValidSourceGoodsConfig(int start, int limit) {
        return infoPlatformDao.listValidSourceGoodsConfig(start, limit);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void batchUpdateSourceGoodsByIds(List<Integer> ids) {
        infoPlatformDao.batchUpdateSourceGoodsByIds(ids);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void batchInsertSourceGoods(List<TmsSourceGoods> sourceGoodsList) {
        infoPlatformDao.batchInsertSourceGoods(sourceGoodsList);
    }

    @Override
    @Transactional(readOnly = true)
    public TmsSourceGoodsConfig getTmsSourceGoodsConfigById(int id) {
        return infoPlatformDao.getTmsSourceGoodsConfigById(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int getDriverRelevanceListAndBatchInsert(String date, Integer sourceGoodsConfigId) {
        List<GoodsDriverRelevanceVO> list = infoPlatformDao
                .getGoodsDriverRelevanceListAfterDate(date, sourceGoodsConfigId);
        if (!CollectionUtils.isEmpty(list)) {
            LOGGER.info("关联查询后需要入货源【配置ID:{}】与司机关联表的数据量(真实)：{}",
                    sourceGoodsConfigId, list.size());
            // 真实数据的被联系次数入0
            infoPlatformDao.batchInsertGoodsDriverRelevance(list, 0);
            return list.size();
        }
        return 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<GoodsDriverRelevanceVO> randomDriverRelevanceListAndBatchInsert(int earlyDays) {
        String date = ProjectUtil.getSpecifiedDateStr(new Date(), earlyDays, ProjectUtil.DAY_DATE_FORMAT);
        List<Integer> list = infoPlatformDao.getSourceGoodsIdByDate(date); // 只增加处理那天的货源
        // 没有查询到有效的货源,此时不生成随机的货源与司机关联数据
        if (CollectionUtils.isEmpty(list))
            return new ArrayList<>();
        List<GoodsDriverRelevanceVO> result = new ArrayList<>();
        for (Integer id : list) {
            int length = new Random().nextInt(4); // 0-3的随机数
            for (int j = 0; j < length; j++) {
                GoodsDriverRelevanceVO vo = new GoodsDriverRelevanceVO();
                vo.setId(id);
                vo.setLoginId(JobConstant.RANDOM_LOGIN_IDS[j]);
                result.add(vo);
                LOGGER.debug("生成随机的货源与司机关联表数据：{}", vo);
            }
        }
        if (!CollectionUtils.isEmpty(result))
            infoPlatformDao.batchInsertGoodsDriverRelevance(result, 1);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmsSourceGoods> listSortTmsSourceGoodsAfterDate(String date, String ids) {
        return infoPlatformDao.listSortTmsSourceGoodsAfterDate(date, ids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SmsPushVO> listDriverMobile(int id) {
        String today = ProjectUtil.dateFormatByPattern(new Date(), ProjectUtil.DAY_DATE_FORMAT)
                + ProjectUtil.DAY_DATE_SUFFIX;
        return infoPlatformDao.listDriverMobile(id, today);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppPushVO> getGoodsDriverRelevanceAfterDateById(String date, int id) {
        List<AppPushVO> result = new ArrayList<>();
        // 先查询城市间的匹配的货源(区域一定是匹配的)
        List<AppPushVO> matchCityList = infoPlatformDao.getCityMatchRelevanceById(date, id);
        if (!CollectionUtils.isEmpty(matchCityList))
            result.addAll(matchCityList);
        // 查询区域匹配但城市不匹配的货源
        List<AppPushVO> matchRegionList = infoPlatformDao.getRegionMatchRelevanceById(date, id);
        if (!CollectionUtils.isEmpty(matchRegionList))
            result.addAll(matchRegionList);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int updateCountReceiveSms(int sourceGoodsId, String driverId) {
        return infoPlatformDao.updateCountReceiveSms(sourceGoodsId, driverId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listOverCalledCountGoodsIds(int everydayCallCount, int totalCallCount) {
        return infoPlatformDao.listOverCalledCountGoodsIds(everydayCallCount, totalCallCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TmsSourceGoods> getOverUpdateTimeGoodsListById(int configId, Date updateTime, String applyDate) {
        if (Objects.isNull(updateTime)) return null;
        String update = ProjectUtil.dateFormatByPattern(updateTime, ProjectUtil.DEFAULT_DATE_FORMAT);
        return infoPlatformDao.getOverUpdateTimeGoodsListById(configId, update, applyDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getSourceGoodsConfigIdsAfterDate(String date) {
        return infoPlatformDao.getSourceGoodsConfigIdsAfterDate(date);
    }
}