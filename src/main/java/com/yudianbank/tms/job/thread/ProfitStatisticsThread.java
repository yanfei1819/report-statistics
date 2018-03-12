package com.yudianbank.tms.job.thread;

import com.yudianbank.tms.job.helper.TmsProfitJobHelper;
import com.yudianbank.tms.model.TmsProfitStatisticsModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 利润统计报表的计算与保存
 *
 * @author Song Lea
 */
public class ProfitStatisticsThread extends BaseInsertThread<TmsProfitStatisticsModel> {

    public ProfitStatisticsThread(List<TmsProfitStatisticsModel> dataList) {
        super(dataList);
    }

    @Override
    protected List<TmsProfitStatisticsModel> processDataList(List<TmsProfitStatisticsModel> dataList) {
        return dataList.stream().parallel().peek(model -> {
            BigDecimal taxCost = calculateTaxCost(model.getIncome(), model.getTaxRate());
            model.setTaxCost(taxCost);
            BigDecimal totalCost = calculateTotalCost(model, taxCost);
            model.setTotalCost(totalCost);
            model.setProfit(calculateProfit(model.getIncome(), totalCost));
        }).collect(Collectors.toList());
    }

    // 计算开票成本 = 发车收入*发票税额
    private static BigDecimal calculateTaxCost(BigDecimal income, BigDecimal taxRate) {
        if (income == null || taxRate == null) return null;
        return income.multiply(taxRate).divide(BigDecimal.valueOf(100), TmsProfitJobHelper.DECIMAL_DIGITS,
                BigDecimal.ROUND_HALF_UP); // 四舍五入
    }

    // 计算成本合计
    private static BigDecimal calculateTotalCost(TmsProfitStatisticsModel model, BigDecimal taxCost) {
        Integer carType = model.getCarType();
        if (carType == null) return null; // 用车性质为空时
        BigDecimal totalCost = null;
        BigDecimal cache = BigDecimal.ZERO;
        // 共有的开票成本+其他成本
        if (taxCost != null)
            cache = cache.add(taxCost);
        if (model.getOtherCost() != null)
            cache = cache.add(model.getOtherCost());
        if (carType == 1) {
            // 公司车:百公里油耗+公里路桥费+公里维修费+公里折旧费+公里保险年检费+人工费用+开票成本+其他成本
            BigDecimal kilometers = model.getKilometers();
            if (kilometers != null) {
                if (model.getOilCost() != null)
                    cache = cache.add(kilometers.divide(BigDecimal.valueOf(100), TmsProfitJobHelper.DECIMAL_DIGITS,
                            BigDecimal.ROUND_HALF_UP).multiply(model.getOilCost()));
                if (model.getRoadCost() != null)
                    cache = cache.add(kilometers.multiply(model.getRoadCost()));
                if (model.getRepairCost() != null)
                    cache = cache.add(kilometers.multiply(model.getRepairCost()));
                if (model.getDepreciationCost() != null)
                    cache = cache.add(kilometers.multiply(model.getDepreciationCost()));
                if (model.getInsurance() != null)
                    cache = cache.add(kilometers.multiply(model.getInsurance()));
            }
            if (model.getPersonCost() != null)
                cache = cache.add(model.getPersonCost());
        } else if (carType == 2) {
            // 外请车:总运费+信息费+开票成本+其他成本
            if (model.getTotalFreight() != null)
                cache = cache.add(model.getTotalFreight());
            if (model.getInfoCost() != null)
                cache = cache.add(model.getInfoCost());
        }
        if (cache.compareTo(BigDecimal.ZERO) != 0)
            totalCost = cache.setScale(TmsProfitJobHelper.DECIMAL_DIGITS, BigDecimal.ROUND_HALF_UP);
        return totalCost;
    }

    // 计算毛利 = 发车收入-成本合计
    private static BigDecimal calculateProfit(BigDecimal income, BigDecimal totalCost) {
        if (income == null) return totalCost.multiply(BigDecimal.valueOf(-1));
        if (totalCost == null) return income;
        return income.subtract(totalCost).setScale(TmsProfitJobHelper.DECIMAL_DIGITS, BigDecimal.ROUND_HALF_UP);
    }
}