package com.yudianbank.tms.dao;

import com.yudianbank.tms.model.TmsSmsSettingModel;
import com.yudianbank.tms.model.vo.PayAmtStatisticsVO;
import com.yudianbank.tms.model.vo.SmsCarsAmountStatisticsVO;

import java.util.List;

/**
 * Created by chengtianren on 2017/8/27.
 */
public interface SmsContentStatisticsDao {

    // 按用车性质来查询
    List<SmsCarsAmountStatisticsVO> listSendCarsAmount(String startDate, String endDate);

    // 统计APP放款金额数据(按支付方式分组)
    List<PayAmtStatisticsVO> listPayAmt(String startDate, String endDate);

    // 加载短信配置信息
    List<TmsSmsSettingModel> listSendSmsSetting();

    // 更新配置表时间
    int updateTmsSmsSetting(int smsSettingId);
}