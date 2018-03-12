package com.yudianbank.tms.service;

import com.yudianbank.tms.model.TmsSmsSettingModel;
import com.yudianbank.tms.model.vo.PayAmtStatisticsVO;
import com.yudianbank.tms.model.vo.SmsCarsAmountStatisticsVO;

import java.util.List;
import java.util.Map;

/**
 * Created by chengtianren on 2017/8/27.
 *
 * @since 2017-11-13 修改 by Song Lea
 */
public interface SmsContentStatisticsService {

    // 发车信息统计
    Map<String, List<SmsCarsAmountStatisticsVO>> getSendCarsAmountMap();

    // 各类型付款信息统计
    Map<String, List<PayAmtStatisticsVO>> getPayAmtMap();

    // 加载短信发送配置信息
    List<TmsSmsSettingModel> listSendSmsSetting();

    // 更新短信配置中最后发送时间
    int updateTmsSmsSetting(int smsSettingId);
}
