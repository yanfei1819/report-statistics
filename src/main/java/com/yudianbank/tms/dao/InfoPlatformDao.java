package com.yudianbank.tms.dao;

import com.yudianbank.tms.model.TmsSourceGoods;
import com.yudianbank.tms.model.TmsSourceGoodsConfig;
import com.yudianbank.tms.model.TmsSourceGoodsPushConfig;
import com.yudianbank.tms.model.vo.AppPushVO;
import com.yudianbank.tms.model.vo.GoodsDriverRelevanceVO;
import com.yudianbank.tms.model.vo.SmsPushVO;

import java.util.List;

/**
 * 信息平台需求的数据库操作Dao接口
 *
 * @author Song Lea
 */
public interface InfoPlatformDao {

    // 加载货源推送定时任务表信息
    List<TmsSourceGoodsPushConfig> listSourceGoodsPushConfig();

    // 加载货源配置表信息
    List<TmsSourceGoodsConfig> listValidSourceGoodsConfig(int start, int limit);

    // 批量更新货源表数据
    void batchUpdateSourceGoodsByIds(List<Integer> ids);

    // 批量新增货源表数据
    void batchInsertSourceGoods(List<TmsSourceGoods> sourceGoodsList);

    // 通过Id加载货源配置表数据
    TmsSourceGoodsConfig getTmsSourceGoodsConfigById(int id);

    // 查询货源与司机的对应关系(在货源与司机关系表中没有的)/ sourceGoodsConfigId不为空时为界面点击推送时
    List<GoodsDriverRelevanceVO> getGoodsDriverRelevanceListAfterDate(String date, Integer sourceGoodsConfigId);

    // 批量入货源与司机关联表数据
    void batchInsertGoodsDriverRelevance(List<GoodsDriverRelevanceVO> relationList, int countCalled);

    // 按司机数量倒序来取货源表数据(加上未通过联系次数校验的货源id列表的条件)
    List<TmsSourceGoods> listSortTmsSourceGoodsAfterDate(String date, String ids);

    // 加载司机的手机号列表
    List<SmsPushVO> listDriverMobile(int id, String date);

    // 取指定时间的货源与司机关联表数据(包括clientId)(加上未通过联系次数校验的货源id列表) -- 城市匹配
    List<AppPushVO> getCityMatchRelevanceById(String date, int id);

    // 取指定时间的货源与司机关联表数据(包括clientId)(加上未通过联系次数校验的货源id列表) -- 区域匹配
    List<AppPushVO> getRegionMatchRelevanceById(String date, int id);

    // 根据货源id与司机id来更新货源与司机关系表中的发送短信次数
    int updateCountReceiveSms(int sourceGoodsId, String driverId);

    // 查询出达到总次数与当天联系次数的货源ids列表
    List<String> listOverCalledCountGoodsIds(int everydayCallCount, int totalCallCount);

    // 查询出货源表中超过货源配置表更新时间的货源(updateTime非空且为yyyy-MM-dd HH:mm:ss格式)
    List<TmsSourceGoods> getOverUpdateTimeGoodsListById(int configId, String updateTime, String applyDate);

    // 加载指定时间的所有货源id列表
    List<Integer> getSourceGoodsIdByDate(String date);

    // 加载指定时间后的所有货源ids列表
    List<Integer> getSourceGoodsConfigIdsAfterDate(String date);

    // 备份指定天数前的货源与司机数据
    int bakGoodsRelevanceBeforeDate(String bakDate);

    // 删除指定天数前的货源与司机数据
    int delGoodsRelevanceBeforeDate(String bakDate);
}