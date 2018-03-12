package com.yudianbank.tms.dao.impl;

import com.yudianbank.tms.dao.StatisticsDataDao;
import com.yudianbank.tms.job.helper.TmsProfitJobHelper;
import com.yudianbank.tms.model.TmsProfitStatisticsModel;
import com.yudianbank.tms.model.TmsReportStatisticsModel;
import com.yudianbank.tms.model.vo.EchartDataVO;
import com.yudianbank.tms.util.ProjectUtil;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 报表数据处理Dao层实现
 *
 * @author Song Lea
 */
@Repository
public class StatisticsDataDaoImpl implements StatisticsDataDao {

    private JdbcTemplate jdbcTemplate;
    private SessionFactory sessionFactory;

    public StatisticsDataDaoImpl() {
    }

    @Autowired
    public StatisticsDataDaoImpl(JdbcTemplate jdbcTemplate, EntityManagerFactory entityManagerFactory) {
        Assert.notNull(jdbcTemplate, "StatisticsDataDaoImpl.jdbcTemplate must be not null!");
        Assert.notNull(entityManagerFactory, "StatisticsDataDaoImpl.entityManagerFactory must be not null!");
        this.jdbcTemplate = jdbcTemplate;
        if (entityManagerFactory.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("entityManagerFactory is not a hibernate factory!");
        }
        this.sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
    }

    private Session getCurrentSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @Override
    public void batchInsertDataList(List<?> dataList) {
        ProjectUtil.batchInsertImpl(dataList, getCurrentSession());
    }

    @Override
    public boolean checkHasStatisticsByDate(String sql, String date) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, date);
        return list != null && list.size() > 0;
    }

    @Override
    public List<TmsReportStatisticsModel> statisticsSendDate(String sendDateSql, String calDate, int start,
                                                             int limit, int whichWeek) {
        return jdbcTemplate.query(sendDateSql, new Object[]{calDate, start, limit}, (rs, i) -> {
            TmsReportStatisticsModel record = new TmsReportStatisticsModel();
            record.setSendDate(ProjectUtil.safeGetDateByStr(rs.getString(1), ProjectUtil.DAY_DATE_FORMAT));
            record.setProjectId(ProjectUtil.object2Long(rs.getObject(2)));
            record.setSendCity(rs.getString(3));
            record.setArriveCity(rs.getString(4));
            record.setCarType(ProjectUtil.object2Integer(rs.getObject(5)));
            record.setPartnerNo(rs.getString(6));
            record.setTotalTransportCash(rs.getBigDecimal(7));
            record.setTotalIncome(rs.getBigDecimal(8));
            record.setTotalCarNumber(rs.getInt(9));
            record.setLength42CarNumber(rs.getInt(10));
            record.setLength68CarNumber(rs.getInt(11));
            record.setLength76CarNumber(rs.getInt(12));
            record.setLength96CarNumber(rs.getInt(13));
            record.setLength125CarNumber(rs.getInt(14));
            record.setLength130CarNumber(rs.getInt(15));
            record.setLength150CarNumber(rs.getInt(16));
            record.setLength160CarNumber(rs.getInt(17));
            record.setLength175CarNumber(rs.getInt(18));
            record.setLength210CarNumber(rs.getInt(19));
            record.setLengthOtherCarNumber(rs.getInt(20));
            record.setLength42Income(rs.getBigDecimal(21));
            record.setLength68Income(rs.getBigDecimal(22));
            record.setLength76Income(rs.getBigDecimal(23));
            record.setLength96Income(rs.getBigDecimal(24));
            record.setLength125Income(rs.getBigDecimal(25));
            record.setLength130Income(rs.getBigDecimal(26));
            record.setLength150Income(rs.getBigDecimal(27));
            record.setLength160Income(rs.getBigDecimal(28));
            record.setLength175Income(rs.getBigDecimal(29));
            record.setLength210Income(rs.getBigDecimal(30));
            record.setLengthOtherIncome(rs.getBigDecimal(31));
            record.setLength42TransportCash(rs.getBigDecimal(32));
            record.setLength68TransportCash(rs.getBigDecimal(33));
            record.setLength76TransportCash(rs.getBigDecimal(34));
            record.setLength96TransportCash(rs.getBigDecimal(35));
            record.setLength125TransportCash(rs.getBigDecimal(36));
            record.setLength130TransportCash(rs.getBigDecimal(37));
            record.setLength150TransportCash(rs.getBigDecimal(38));
            record.setLength160TransportCash(rs.getBigDecimal(39));
            record.setLength175TransportCash(rs.getBigDecimal(40));
            record.setLength210TransportCash(rs.getBigDecimal(41));
            record.setLengthOtherTransportCash(rs.getBigDecimal(42));
            record.setLateWarningCars(ProjectUtil.object2Integer(rs.getObject(43)));
            record.setWhichWeek(whichWeek);
            return record;
        });
    }

    @Override
    public List<TmsProfitStatisticsModel> profitStatisticsByDate(String sql, String specifiedDate, int start, int limit) {
        return jdbcTemplate.query(sql, new Object[]{TmsProfitJobHelper.NODE_STATUS, TmsProfitJobHelper.BILL_STATUS,
                specifiedDate, start, limit}, (rs, i) -> {
            TmsProfitStatisticsModel record = new TmsProfitStatisticsModel();
            record.setTmsBillCode(rs.getString(1));
            record.setUpperBillCode(rs.getString(2));
            record.setSendCity(rs.getString(3));
            record.setArriveCity(rs.getString(4));
            record.setCarType(ProjectUtil.object2Integer(rs.getObject(5)));
            record.setProjectId(ProjectUtil.object2Integer(rs.getObject(6)));
            record.setSendDate(rs.getString(7));
            record.setIncome(rs.getBigDecimal(8));
            record.setTotalFreight(rs.getBigDecimal(9));
            record.setCarLength(rs.getString(10));
            record.setCarModel(rs.getString(11));
            record.setPartnerNo(rs.getString(12));
            record.setKilometers(rs.getBigDecimal(13));
            record.setOilCost(rs.getBigDecimal(14));
            record.setRoadCost(rs.getBigDecimal(15));
            record.setRepairCost(rs.getBigDecimal(16));
            record.setDepreciationCost(rs.getBigDecimal(17));
            record.setInsurance(rs.getBigDecimal(18));
            record.setPersonCost(rs.getBigDecimal(19));
            record.setTaxRate(rs.getBigDecimal(20));
            record.setOtherCost(rs.getBigDecimal(21));
            record.setInfoCost(rs.getBigDecimal(22));
            // 加上省、三级区域与途经地
            record.setSendProvince(rs.getString(23));
            record.setSendDistrict(rs.getString(24));
            record.setArriveProvince(rs.getString(25));
            record.setArriveDistrict(rs.getString(26));
            record.setStationAProvince(rs.getString(27));
            record.setStationACity(rs.getString(28));
            record.setStationADistrict(rs.getString(29));
            record.setStationBProvince(rs.getString(30));
            record.setStationBCity(rs.getString(31));
            record.setStationBDistrict(rs.getString(32));
            record.setStatisticsDate(specifiedDate);
            return record;
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<EchartDataVO> groupDataStatisticsByType(String startDate, int type) {
        String sql = null;
        if (type == 1)
            sql = "SELECT DATE_FORMAT(r.send_date,'%Y-%m-%d') statisticsDate,CONCAT(COUNT(r.id),'') statisticsValue "
                    + "FROM YD_TMS_REPORT_STATISTICS r WHERE r.send_date > ? GROUP BY r.send_date";
        else if (type == 2)
            sql = "SELECT r.statisticsDate,CONCAT(COUNT(r.id),'') statisticsValue FROM YD_TMS_PROFIT_STATISTICS r WHERE "
                    + "r.statisticsDate > ? GROUP BY r.statisticsDate";
        if (sql == null) return new ArrayList<>();
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter(0, startDate);
        query.addScalar("statisticsDate", StringType.INSTANCE)
                .addScalar("statisticsValue", StringType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(EchartDataVO.class));
        return query.list();
    }
}