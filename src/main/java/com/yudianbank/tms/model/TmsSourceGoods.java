package com.yudianbank.tms.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

// 货源表
@Entity
@Table(name = "YD_TMS_SOURCEGOODS")
public class TmsSourceGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String partnerNo;//合作方编号

    private String partnerName;//合作方名称

    private String yardmanMobileNum;//调度员手机号

    private String sendRegion;//出发地区

    private String arriveRegion;//到达地区

    private String sendProvince;//出发省份

    private String arriveProvince;//到达省份

    private String sendCity;//出发城市

    private String arriveCity;//到达城市

    private String sendDistrict;//发车区县

    private String arriveDistrict;//到达区县

    private String cargoName;//货物名称

    private Double cargoWeight;//货物重量

    private String carLength;//车长

    private String carModel;//车型，车型，多个用逗号分隔

    private BigDecimal billFee;//运费

    private Integer handleMode;//装卸方式:1,一装一卸、2,一装两卸、3,两装一卸、4,两装两卸、5,两装多卸

    private String payWay;//支付方式:1,预付现金、2,押回单、3,三段付，多个用逗号隔开

    private String content;//备注：50字符

    private Date applyDate;//用车日期

    private String applyTimeStart;//用车时间范围（开始时间）

    private String applyTimeEnd;//用车时间范围（结束时间）

    private Date updateTime;//更新时间

    private int sourceGoodsConfigId;//货源配置表ID

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPartnerNo() {
        return partnerNo;
    }

    public void setPartnerNo(String partnerNo) {
        this.partnerNo = partnerNo;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getYardmanMobileNum() {
        return yardmanMobileNum;
    }

    public void setYardmanMobileNum(String yardmanMobileNum) {
        this.yardmanMobileNum = yardmanMobileNum;
    }

    public String getSendRegion() {
        return sendRegion;
    }

    public void setSendRegion(String sendRegion) {
        this.sendRegion = sendRegion;
    }

    public String getArriveRegion() {
        return arriveRegion;
    }

    public void setArriveRegion(String arriveRegion) {
        this.arriveRegion = arriveRegion;
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

    public String getCargoName() {
        return cargoName;
    }

    public void setCargoName(String cargoName) {
        this.cargoName = cargoName;
    }

    public Double getCargoWeight() {
        return cargoWeight;
    }

    public void setCargoWeight(Double cargoWeight) {
        this.cargoWeight = cargoWeight;
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

    public BigDecimal getBillFee() {
        return billFee;
    }

    public void setBillFee(BigDecimal billFee) {
        this.billFee = billFee;
    }

    public Integer getHandleMode() {
        return handleMode;
    }

    public void setHandleMode(Integer handleMode) {
        this.handleMode = handleMode;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public String getApplyTimeStart() {
        return applyTimeStart;
    }

    public void setApplyTimeStart(String applyTimeStart) {
        this.applyTimeStart = applyTimeStart;
    }

    public String getApplyTimeEnd() {
        return applyTimeEnd;
    }

    public void setApplyTimeEnd(String applyTimeEnd) {
        this.applyTimeEnd = applyTimeEnd;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getSourceGoodsConfigId() {
        return sourceGoodsConfigId;
    }

    public void setSourceGoodsConfigId(int sourceGoodsConfigId) {
        this.sourceGoodsConfigId = sourceGoodsConfigId;
    }
}
