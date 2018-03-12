package com.yudianbank.tms.service;

import com.yudianbank.tms.model.TmsSourceGoods;
import com.yudianbank.tms.model.TmsSourceGoodsConfig;
import com.yudianbank.tms.model.TmsSourceGoodsPushConfig;
import com.yudianbank.tms.model.vo.AppPushVO;
import com.yudianbank.tms.model.vo.GoodsDriverRelevanceVO;
import com.yudianbank.tms.model.vo.SmsPushVO;

import java.util.Date;
import java.util.List;

/**
 * 信息平台需求的Service接口
 *
 * @author Song Lea
 */
public interface InfoPlatformService {

    // 加载货源推送定时任务表信息
    List<TmsSourceGoodsPushConfig> listSourceGoodsPushConfig();

    // 加载货源配置表信息(分页取,防止内存溢出)
    List<TmsSourceGoodsConfig> listValidSourceGoodsConfig(int start, int limit);

    // 更新货源表数据
    void batchUpdateSourceGoodsByIds(List<Integer> ids);

    // 批量新增货源表数据
    void batchInsertSourceGoods(List<TmsSourceGoods> sourceGoodsList);

    // 通过Id加载货源配置表数据
    TmsSourceGoodsConfig getTmsSourceGoodsConfigById(int id);

    // 查询货源与司机的对应关系(在货源与司机关系表中没有的)并入库
    int getDriverRelevanceListAndBatchInsert(String date, Integer sourceGoodsConfigId);

    // 随机生成货源与司机关联表记录(只生成发车时间是那天的)
    List<GoodsDriverRelevanceVO> randomDriverRelevanceListAndBatchInsert(int earlyDays);

    // 通过货源配置信息的ID获取发车日期在当前时间之后的货源信息(要通过司机的数据来进行倒序排序)
    List<TmsSourceGoods> listSortTmsSourceGoodsAfterDate(String date, String ids);

    //获取发送的司机手机信息
    List<SmsPushVO> listDriverMobile(int id);

    // 取指定时间后的货源与司机关联数据(包括clientId,loginId,sendCity与arriveCity这四个字段来推送消息)
    List<AppPushVO> getGoodsDriverRelevanceAfterDateById(String date, int id);

    // 根据货源id与司机id来更新货源与司机关系表中的发送短信次数
    int updateCountReceiveSms(int sourceGoodsId, String driverId);

    // 查询出达到总次数与当天联系次数的货源ids列表
    List<String> listOverCalledCountGoodsIds(int everydayCallCount, int totalCallCount);

    // 查询出货源表中超过货源配置表更新时间的货源
    List<TmsSourceGoods> getOverUpdateTimeGoodsListById(int configId, Date updateTime, String applyDate);

    //  加载指定时间后的所有货源ids列表
    List<Integer> getSourceGoodsConfigIdsAfterDate(String date);
}