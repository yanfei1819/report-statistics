package com.yudianbank.tms.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 利润报表
 *
 * @author Song Lea
 */
@Entity
@Table(name = "YD_TMS_PROFIT_STATISTICS")
public class TmsProfitStatisticsModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, length = 11)
    private int id; // 主键

    @Column(name = "tmsBillCode", length = 128)
    private String tmsBillCode; // 运单号

    @Column(name = "upperBillCode", length = 128)
    private String upperBillCode; // 上游单号

    @Column(name = "sendCity", length = 64)
    private String sendCity = ""; // 出发城市

    @Column(name = "arriveCity", length = 64)
    private String arriveCity = ""; // 到达城市

    @Column(name = "sendDate", length = 16)
    private String sendDate; // 发车日期

    @Column(name = "carType", length = 1)
    private Integer carType; // 用车性质

    @Column(name = "projectId", length = 11)
    private Integer projectId; // 项目ID

    @Column(name = "carLength", length = 16)
    private String carLength; // 车长

    @Column(name = "carModel", length = 16)
    private String carModel; // 车型

    @Column(name = "partnerNo")
    private String partnerNo; // partnerNo

    @Column(name = "income", columnDefinition = "decimal(18,3)")
    private BigDecimal income; // 发车收入

    @Column(name = "totalFreight", columnDefinition = "decimal(18,3)")
    private BigDecimal totalFreight; // 发车收入

    @Column(name = "kilometers", columnDefinition = "decimal(18,3)")
    private BigDecimal kilometers; // 公里数(KM)

    @Column(name = "oilCost", columnDefinition = "decimal(18,3)")
    private BigDecimal oilCost; // 百公里油耗(元)

    @Column(name = "roadCost", columnDefinition = "decimal(18,3)")
    private BigDecimal roadCost; // 公里路桥费(元)

    @Column(name = "repairCost", columnDefinition = "decimal(18,3)")
    private BigDecimal repairCost; // 公里维修费(元)

    @Column(name = "depreciationCost", columnDefinition = "decimal(18,3)")
    private BigDecimal depreciationCost; // 折旧费(元)

    @Column(name = "insurance", columnDefinition = "decimal(18,3)")
    private BigDecimal insurance; // 保险年检费(元)

    @Column(name = "personCost", columnDefinition = "decimal(18,3)")
    private BigDecimal personCost; // 人工费用(元)

    @Column(name = "taxRate", columnDefinition = "decimal(18,3)")
    private BigDecimal taxRate; // 发票税率(%)

    @Column(name = "otherCost", columnDefinition = "decimal(18,3)")
    private BigDecimal otherCost; // 其他成本(元)

    @Column(name = "infoCost", columnDefinition = "decimal(18,3)")
    private BigDecimal infoCost; // 信息费(元)

    @Column(name = "taxCost", columnDefinition = "decimal(18,3)")
    private BigDecimal taxCost; // 开票成本(=发车收入*发票税额)

    @Column(name = "totalCost", columnDefinition = "decimal(18,3)")
    private BigDecimal totalCost; // 成本合计

    @Column(name = "profit", columnDefinition = "decimal(18,3)")
    private BigDecimal profit; // 毛利

    @Column(name = "statisticsDate", length = 16)
    private String statisticsDate; // 统计日期 yyyy-MM-dd格式

    // 5#新需求,加上出发到达省与三级区域,途经地(默认值为空串)
    @Column(name = "sendProvince", length = 64)
    private String sendProvince = "";

    @Column(name = "arriveProvince", length = 64)
    private String arriveProvince = "";

    @Column(name = "sendDistrict", length = 64)
    private String sendDistrict = "";

    @Column(name = "arriveDistrict", length = 64)
    private String arriveDistrict = "";

    @Column(name = "stationAProvince", length = 64)
    private String stationAProvince = "";

    @Column(name = "stationACity", length = 64)
    private String stationACity = "";

    @Column(name = "stationADistrict", length = 64)
    private String stationADistrict = "";

    @Column(name = "stationBProvince", length = 64)
    private String stationBProvince = "";

    @Column(name = "stationBCity", length = 64)
    private String stationBCity = "";

    @Column(name = "stationBDistrict", length = 64)
    private String stationBDistrict = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTmsBillCode() {
        return tmsBillCode;
    }

    public void setTmsBillCode(String tmsBillCode) {
        this.tmsBillCode = tmsBillCode;
    }

    public String getUpperBillCode() {
        return upperBillCode;
    }

    public void setUpperBillCode(String upperBillCode) {
        this.upperBillCode = upperBillCode;
    }

    public String getSendCity() {
        return sendCity;
    }

    public void setSendCity(String sendCity) {
        this.sendCity = sendCity;
    }

    public String getArriveCity() {
        return arriveCity;
    }

    public void setArriveCity(String arriveCity) {
        this.arriveCity = arriveCity;
    }

    public Integer getCarType() {
        return carType;
    }

    public void setCarType(Integer carType) {
        this.carType = carType;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getCarLength() {
        return carLength;
    }

    public void setCarLength(String carLength) {
        this.carLength = carLength;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getPartnerNo() {
        return partnerNo;
    }

    public void setPartnerNo(String partnerNo) {
        this.partnerNo = partnerNo;
    }

    public BigDecimal getKilometers() {
        return kilometers;
    }

    public void setKilometers(BigDecimal kilometers) {
        this.kilometers = kilometers;
    }

    public BigDecimal getOilCost() {
        return oilCost;
    }

    public void setOilCost(BigDecimal oilCost) {
        this.oilCost = oilCost;
    }

    public BigDecimal getRoadCost() {
        return roadCost;
    }

    public void setRoadCost(BigDecimal roadCost) {
        this.roadCost = roadCost;
    }

    public BigDecimal getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(BigDecimal repairCost) {
        this.repairCost = repairCost;
    }

    public BigDecimal getDepreciationCost() {
        return depreciationCost;
    }

    public void setDepreciationCost(BigDecimal depreciationCost) {
        this.depreciationCost = depreciationCost;
    }

    public BigDecimal getInsurance() {
        return insurance;
    }

    public void setInsurance(BigDecimal insurance) {
        this.insurance = insurance;
    }

    public BigDecimal getPersonCost() {
        return personCost;
    }

    public void setPersonCost(BigDecimal personCost) {
        this.personCost = personCost;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getOtherCost() {
        return otherCost;
    }

    public void setOtherCost(BigDecimal otherCost) {
        this.otherCost = otherCost;
    }

    public BigDecimal getInfoCost() {
        return infoCost;
    }

    public void setInfoCost(BigDecimal infoCost) {
        this.infoCost = infoCost;
    }

    public BigDecimal getTaxCost() {
        return taxCost;
    }

    public void setTaxCost(BigDecimal taxCost) {
        this.taxCost = taxCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getTotalFreight() {
        return totalFreight;
    }

    public void setTotalFreight(BigDecimal totalFreight) {
        this.totalFreight = totalFreight;
    }

    public String getStatisticsDate() {
        return statisticsDate;
    }

    public void setStatisticsDate(String statisticsDate) {
        this.statisticsDate = statisticsDate;
    }

    public String getSendProvince() {
        return sendProvince;
    }

    public void setSendProvince(String sendProvince) {
        this.sendProvince = sendProvince;
    }

    public String getArriveProvince() {
        return arriveProvince;
    }

    public void setArriveProvince(String arriveProvince) {
        this.arriveProvince = arriveProvince;
    }

    public String getSendDistrict() {
        return sendDistrict;
    }

    public void setSendDistrict(String sendDistrict) {
        this.sendDistrict = sendDistrict;
    }

    public String getArriveDistrict() {
        return arriveDistrict;
    }

    public void setArriveDistrict(String arriveDistrict) {
        this.arriveDistrict = arriveDistrict;
    }

    public String getStationAProvince() {
        return stationAProvince;
    }

    public void setStationAProvince(String stationAProvince) {
        this.stationAProvince = stationAProvince;
    }

    public String getStationACity() {
        return stationACity;
    }

    public void setStationACity(String stationACity) {
        this.stationACity = stationACity;
    }

    public String getStationADistrict() {
        return stationADistrict;
    }

    public void setStationADistrict(String stationADistrict) {
        this.stationADistrict = stationADistrict;
    }

    public String getStationBProvince() {
        return stationBProvince;
    }

    public void setStationBProvince(String stationBProvince) {
        this.stationBProvince = stationBProvince;
    }

    public String getStationBCity() {
        return stationBCity;
    }

    public void setStationBCity(String stationBCity) {
        this.stationBCity = stationBCity;
    }

    public String getStationBDistrict() {
        return stationBDistrict;
    }

    public void setStationBDistrict(String stationBDistrict) {
        this.stationBDistrict = stationBDistrict;
    }
}