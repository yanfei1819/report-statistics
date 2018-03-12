package com.yudianbank.tms.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 报表管理汇总表(车辆与运输)
 *
 * @author Song Lea
 */
@Entity
@Table(name = "YD_TMS_REPORT_STATISTICS")
public class TmsReportStatisticsModel implements Serializable {

    private static final long serialVersionUID = -3002806248191367307L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, length = 11)
    private long id; // 主键

    @Column(name = "send_date", columnDefinition = "date")
    private Date sendDate; // 发车日期

    @Column(name = "project_id", length = 11)
    private Long projectId; // 项目ID(可能为空)

    @Column(name = "send_city", length = 20)
    private String sendCity; // 出发城市

    @Column(name = "arrive_city", length = 20)
    private String arriveCity; // 到达城市

    @Column(name = "car_type", length = 1)
    private Integer carType; // 车辆类型

    @Column(name = "supplier_id", length = 11)
    private Long supplierId; // 供应商ID

    @Column(name = "total_income", columnDefinition = "decimal(18,2)")
    private BigDecimal totalIncome; // 总收入

    @Column(name = "total_transport_cash", columnDefinition = "decimal(18,2)")
    private BigDecimal totalTransportCash; // 总运费

    @Column(name = "total_car_number", length = 11)
    private int totalCarNumber; // 车辆总数

    @Column(name = "length_42_income", columnDefinition = "decimal(18,2)")
    private BigDecimal length42Income;

    @Column(name = "length_42_transport_cash", columnDefinition = "decimal(18,2)")
    private BigDecimal length42TransportCash;

    @Column(name = "length_42_car_number", length = 11)
    private int length42CarNumber;

    @Column(name = "length_68_income", columnDefinition = "decimal(18,2)")
    private BigDecimal length68Income;

    @Column(name = "length_68_transport_cash", columnDefinition = "decimal(18,2)")
    private BigDecimal length68TransportCash;

    @Column(name = "length_68_car_number", length = 11)
    private int length68CarNumber;

    @Column(name = "length_76_income", columnDefinition = "decimal(18,2)")
    private BigDecimal length76Income;

    @Column(name = "length_76_transport_cash", columnDefinition = "decimal(18,2)")
    private BigDecimal length76TransportCash;

    @Column(name = "length_76_car_number", length = 11)
    private int length76CarNumber;

    @Column(name = "length_96_income", columnDefinition = "decimal(18,2)")
    private BigDecimal length96Income;

    @Column(name = "length_96_transport_cash", columnDefinition = "decimal(18,2)")
    private BigDecimal length96TransportCash;

    @Column(name = "length_96_car_number", length = 11)
    private int length96CarNumber;

    @Column(name = "length_125_income", columnDefinition = "decimal(18,2)")
    private BigDecimal length125Income;

    @Column(name = "length_125_transport_cash", columnDefinition = "decimal(18,2)")
    private BigDecimal length125TransportCash;

    @Column(name = "length_125_car_number", length = 11)
    private int length125CarNumber;

    @Column(name = "length_130_income", columnDefinition = "decimal(18,2)")
    private BigDecimal length130Income;

    @Column(name = "length_130_transport_cash", columnDefinition = "decimal(18,2)")
    private BigDecimal length130TransportCash;

    @Column(name = "length_130_car_number", length = 11)
    private int length130CarNumber;

    @Column(name = "length_150_income", columnDefinition = "decimal(18,2)")
    private BigDecimal length150Income;

    @Column(name = "length_150_transport_cash", columnDefinition = "decimal(18,2)")
    private BigDecimal length150TransportCash;

    @Column(name = "length_150_car_number", length = 11)
    private int length150CarNumber;

    @Column(name = "length_160_income", columnDefinition = "decimal(18,2)")
    private BigDecimal length160Income;

    @Column(name = "length_160_transport_cash", columnDefinition = "decimal(18,2)")
    private BigDecimal length160TransportCash;

    @Column(name = "length_160_car_number", length = 11)
    private int length160CarNumber;

    @Column(name = "length_175_income", columnDefinition = "decimal(18,2)")
    private BigDecimal length175Income;

    @Column(name = "length_175_transport_cash", columnDefinition = "decimal(18,2)")
    private BigDecimal length175TransportCash;

    @Column(name = "length_175_car_number", length = 11)
    private int length175CarNumber;

    @Column(name = "length_210_income", columnDefinition = "decimal(18,2)")
    private BigDecimal length210Income;

    @Column(name = "length_210_transport_cash", columnDefinition = "decimal(18,2)")
    private BigDecimal length210TransportCash;

    @Column(name = "length_210_car_number", length = 11)
    private int length210CarNumber;

    @Column(name = "length_other_income", columnDefinition = "decimal(18,2)")
    private BigDecimal lengthOtherIncome;

    @Column(name = "length_other_transport_cash", columnDefinition = "decimal(18,2)")
    private BigDecimal lengthOtherTransportCash;

    @Column(name = "length_other_car_number", length = 11)
    private int lengthOtherCarNumber;

    @Column(name = "average_wait_time", columnDefinition = "decimal(18,2)")
    private BigDecimal averageWaitTime; // 平均等待时长

    @Column(name = "average_run_time", columnDefinition = "decimal(18,2)")
    private BigDecimal averageRunTime; // 平均运行时长

    @Column(name = "average_receipt_time", columnDefinition = "decimal(18,2)")
    private BigDecimal averageReceiptTime; // 平均回单时长

    @Column(name = "punctuality_cars", length = 11)
    private Integer punctualityCars; // 准点到达车辆数

    @Column(name = "late_warning_cars", length = 11)
    private Integer lateWarningCars; // 晚到预警车辆数

    @Column(name = "positioning_times", length = 11)
    private Integer positioningTimes; // 应定位次数

    @Column(name = "success_positioning_times", length = 11)
    private Integer successPositioningTimes; // 成功定位次数

    @Column(name = "line_aging", columnDefinition = "decimal(18,2)")
    private BigDecimal lineAging; // 线路时效

    @Column(name = "partner_no")
    private String partnerNo; // partner number

    @Column(name = "which_week", length = 2)
    private int whichWeek; // 发车日期位于哪周

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome == null ? BigDecimal.ZERO : totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalTransportCash() {
        return totalTransportCash == null ? BigDecimal.ZERO : totalTransportCash;
    }

    public void setTotalTransportCash(BigDecimal totalTransportCash) {
        this.totalTransportCash = totalTransportCash;
    }

    public int getTotalCarNumber() {
        return totalCarNumber;
    }

    public void setTotalCarNumber(int totalCarNumber) {
        this.totalCarNumber = totalCarNumber;
    }

    public BigDecimal getLength42Income() {
        return length42Income == null ? BigDecimal.ZERO : length42Income;
    }

    public void setLength42Income(BigDecimal length42Income) {
        this.length42Income = length42Income;
    }

    public BigDecimal getLength42TransportCash() {
        return length42TransportCash == null ? BigDecimal.ZERO : length42TransportCash;
    }

    public void setLength42TransportCash(BigDecimal length42TransportCash) {
        this.length42TransportCash = length42TransportCash;
    }

    public int getLength42CarNumber() {
        return length42CarNumber;
    }

    public void setLength42CarNumber(int length42CarNumber) {
        this.length42CarNumber = length42CarNumber;
    }

    public BigDecimal getLength68Income() {
        return length68Income == null ? BigDecimal.ZERO : length68Income;
    }

    public void setLength68Income(BigDecimal length68Income) {
        this.length68Income = length68Income;
    }

    public BigDecimal getLength68TransportCash() {
        return length68TransportCash == null ? BigDecimal.ZERO : length68TransportCash;
    }

    public void setLength68TransportCash(BigDecimal length68TransportCash) {
        this.length68TransportCash = length68TransportCash;
    }

    public int getLength68CarNumber() {
        return length68CarNumber;
    }

    public void setLength68CarNumber(int length68CarNumber) {
        this.length68CarNumber = length68CarNumber;
    }

    public BigDecimal getLength76Income() {
        return length76Income == null ? BigDecimal.ZERO : length76Income;
    }

    public void setLength76Income(BigDecimal length76Income) {
        this.length76Income = length76Income;
    }

    public BigDecimal getLength76TransportCash() {
        return length76TransportCash == null ? BigDecimal.ZERO : length76TransportCash;
    }

    public void setLength76TransportCash(BigDecimal length76TransportCash) {
        this.length76TransportCash = length76TransportCash;
    }

    public int getLength76CarNumber() {
        return length76CarNumber;
    }

    public void setLength76CarNumber(int length76CarNumber) {
        this.length76CarNumber = length76CarNumber;
    }

    public BigDecimal getLength96Income() {
        return length96Income == null ? BigDecimal.ZERO : length96Income;
    }

    public void setLength96Income(BigDecimal length96Income) {
        this.length96Income = length96Income;
    }

    public BigDecimal getLength96TransportCash() {
        return length96TransportCash == null ? BigDecimal.ZERO : length96TransportCash;
    }

    public void setLength96TransportCash(BigDecimal length96TransportCash) {
        this.length96TransportCash = length96TransportCash;
    }

    public int getLength96CarNumber() {
        return length96CarNumber;
    }

    public void setLength96CarNumber(int length96CarNumber) {
        this.length96CarNumber = length96CarNumber;
    }

    public BigDecimal getLength125Income() {
        return length125Income == null ? BigDecimal.ZERO : length125Income;
    }

    public void setLength125Income(BigDecimal length125Income) {
        this.length125Income = length125Income;
    }

    public BigDecimal getLength125TransportCash() {
        return length125TransportCash == null ? BigDecimal.ZERO : length125TransportCash;
    }

    public void setLength125TransportCash(BigDecimal length125TransportCash) {
        this.length125TransportCash = length125TransportCash;
    }

    public int getLength125CarNumber() {
        return length125CarNumber;
    }

    public void setLength125CarNumber(int length125CarNumber) {
        this.length125CarNumber = length125CarNumber;
    }

    public BigDecimal getLength130Income() {
        return length130Income == null ? BigDecimal.ZERO : length130Income;
    }

    public void setLength130Income(BigDecimal length130Income) {
        this.length130Income = length130Income;
    }

    public BigDecimal getLength130TransportCash() {
        return length130TransportCash == null ? BigDecimal.ZERO : length130TransportCash;
    }

    public void setLength130TransportCash(BigDecimal length130TransportCash) {
        this.length130TransportCash = length130TransportCash;
    }

    public int getLength130CarNumber() {
        return length130CarNumber;
    }

    public void setLength130CarNumber(int length130CarNumber) {
        this.length130CarNumber = length130CarNumber;
    }

    public BigDecimal getLength150Income() {
        return length150Income == null ? BigDecimal.ZERO : length150Income;
    }

    public void setLength150Income(BigDecimal length150Income) {
        this.length150Income = length150Income;
    }

    public BigDecimal getLength150TransportCash() {
        return length150TransportCash == null ? BigDecimal.ZERO : length150TransportCash;
    }

    public void setLength150TransportCash(BigDecimal length150TransportCash) {
        this.length150TransportCash = length150TransportCash;
    }

    public int getLength150CarNumber() {
        return length150CarNumber;
    }

    public void setLength150CarNumber(int length150CarNumber) {
        this.length150CarNumber = length150CarNumber;
    }

    public BigDecimal getLength160Income() {
        return length160Income == null ? BigDecimal.ZERO : length160Income;
    }

    public void setLength160Income(BigDecimal length160Income) {
        this.length160Income = length160Income;
    }

    public BigDecimal getLength160TransportCash() {
        return length160TransportCash == null ? BigDecimal.ZERO : length160TransportCash;
    }

    public void setLength160TransportCash(BigDecimal length160TransportCash) {
        this.length160TransportCash = length160TransportCash;
    }

    public int getLength160CarNumber() {
        return length160CarNumber;
    }

    public void setLength160CarNumber(int length160CarNumber) {
        this.length160CarNumber = length160CarNumber;
    }

    public BigDecimal getLength175Income() {
        return length175Income == null ? BigDecimal.ZERO : length175Income;
    }

    public void setLength175Income(BigDecimal length175Income) {
        this.length175Income = length175Income;
    }

    public BigDecimal getLength175TransportCash() {
        return length175TransportCash == null ? BigDecimal.ZERO : length175TransportCash;
    }

    public void setLength175TransportCash(BigDecimal length175TransportCash) {
        this.length175TransportCash = length175TransportCash;
    }

    public int getLength175CarNumber() {
        return length175CarNumber;
    }

    public void setLength175CarNumber(int length175CarNumber) {
        this.length175CarNumber = length175CarNumber;
    }

    public BigDecimal getLength210Income() {
        return length210Income == null ? BigDecimal.ZERO : length210Income;
    }

    public void setLength210Income(BigDecimal length210Income) {
        this.length210Income = length210Income;
    }

    public BigDecimal getLength210TransportCash() {
        return length210TransportCash == null ? BigDecimal.ZERO : length210TransportCash;
    }

    public void setLength210TransportCash(BigDecimal length210TransportCash) {
        this.length210TransportCash = length210TransportCash;
    }

    public int getLength210CarNumber() {
        return length210CarNumber;
    }

    public void setLength210CarNumber(int length210CarNumber) {
        this.length210CarNumber = length210CarNumber;
    }

    public BigDecimal getLengthOtherIncome() {
        return lengthOtherIncome == null ? BigDecimal.ZERO : lengthOtherIncome;
    }

    public void setLengthOtherIncome(BigDecimal lengthOtherIncome) {
        this.lengthOtherIncome = lengthOtherIncome;
    }

    public BigDecimal getLengthOtherTransportCash() {
        return lengthOtherTransportCash == null ? BigDecimal.ZERO : lengthOtherTransportCash;
    }

    public void setLengthOtherTransportCash(BigDecimal lengthOtherTransportCash) {
        this.lengthOtherTransportCash = lengthOtherTransportCash;
    }

    public int getLengthOtherCarNumber() {
        return lengthOtherCarNumber;
    }

    public void setLengthOtherCarNumber(int lengthOtherCarNumber) {
        this.lengthOtherCarNumber = lengthOtherCarNumber;
    }

    public BigDecimal getAverageWaitTime() {
        return averageWaitTime;
    }

    public void setAverageWaitTime(BigDecimal averageWaitTime) {
        this.averageWaitTime = averageWaitTime;
    }

    public BigDecimal getAverageRunTime() {
        return averageRunTime;
    }

    public void setAverageRunTime(BigDecimal averageRunTime) {
        this.averageRunTime = averageRunTime;
    }

    public BigDecimal getAverageReceiptTime() {
        return averageReceiptTime;
    }

    public void setAverageReceiptTime(BigDecimal averageReceiptTime) {
        this.averageReceiptTime = averageReceiptTime;
    }

    public Integer getPunctualityCars() {
        return punctualityCars;
    }

    public void setPunctualityCars(Integer punctualityCars) {
        this.punctualityCars = punctualityCars;
    }

    public Integer getLateWarningCars() {
        return lateWarningCars;
    }

    public void setLateWarningCars(Integer lateWarningCars) {
        this.lateWarningCars = lateWarningCars;
    }

    public Integer getPositioningTimes() {
        return positioningTimes;
    }

    public void setPositioningTimes(Integer positioningTimes) {
        this.positioningTimes = positioningTimes;
    }

    public Integer getSuccessPositioningTimes() {
        return successPositioningTimes;
    }

    public void setSuccessPositioningTimes(Integer successPositioningTimes) {
        this.successPositioningTimes = successPositioningTimes;
    }

    public BigDecimal getLineAging() {
        return lineAging;
    }

    public void setLineAging(BigDecimal lineAging) {
        this.lineAging = lineAging;
    }

    public String getPartnerNo() {
        return partnerNo;
    }

    public void setPartnerNo(String partnerNo) {
        this.partnerNo = partnerNo;
    }

    public int getWhichWeek() {
        return whichWeek;
    }

    public void setWhichWeek(int whichWeek) {
        this.whichWeek = whichWeek;
    }
}
