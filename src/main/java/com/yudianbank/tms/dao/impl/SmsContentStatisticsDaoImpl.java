package com.yudianbank.tms.dao.impl;

import com.yudianbank.tms.dao.SmsContentStatisticsDao;
import com.yudianbank.tms.model.TmsSmsSettingModel;
import com.yudianbank.tms.model.vo.PayAmtStatisticsVO;
import com.yudianbank.tms.model.vo.SmsCarsAmountStatisticsVO;
import com.yudianbank.tms.util.ProjectUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.persistence.EntityManagerFactory;
import java.util.Date;
import java.util.List;

/**
 * Created by chengtianren on 2017/8/27.
 *
 * @since 2017-11-13 修改 by Song Lea
 */
@Repository
public class SmsContentStatisticsDaoImpl implements SmsContentStatisticsDao {


    private JdbcTemplate jdbcTemplate;
    private SessionFactory sessionFactory;

    public SmsContentStatisticsDaoImpl() {
    }

    @Autowired
    public SmsContentStatisticsDaoImpl(JdbcTemplate jdbcTemplate, EntityManagerFactory entityManagerFactory) {
        Assert.notNull(jdbcTemplate, "InfoPlatformDaoImpl.jdbcTemplate must be not null!");
        Assert.notNull(entityManagerFactory, "InfoPlatformDaoImpl.entityManagerFactory must be not null!");
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
    @SuppressWarnings("unchecked")
    public List<SmsCarsAmountStatisticsVO> listSendCarsAmount(String startDate, String endDate) {
        String sql = "SELECT t.partnerNo,t.carType,COUNT(t.id) totalCarNumber,SUM(t.feeAmt) totalTransportCash," +
                "SUM(t.income) totalIncome FROM YD_APP_TRANSPORTCASH t where t.delStatus = 0 AND (t.transStatus = 'E' " +
                "OR t.transStatus = 'A' OR t.transStatus = 'C') AND t.billStatus != 'Q' AND t.applyDate >= :startDate " +
                "AND t.applyDate < :endDate GROUP BY t.partnerNo,t.carType";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.addScalar("partnerNo", StringType.INSTANCE)
                .addScalar("totalCarNumber", BigIntegerType.INSTANCE)
                .addScalar("carType", BigIntegerType.INSTANCE)
                .addScalar("totalTransportCash", BigDecimalType.INSTANCE)
                .addScalar("totalIncome", BigDecimalType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(SmsCarsAmountStatisticsVO.class));
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PayAmtStatisticsVO> listPayAmt(String startDate, String endDate) {
        String sql = "SELECT sum(x.amt) amt,x.partnerNo,x.payWay FROM ( " +
                "SELECT sum(d.actualCash) amt, partnerNo,cashPayWay payWay FROM YD_TMS_PAY_DETAIL d " +
                "INNER JOIN YD_APP_TRANSPORTCASH t ON d.wayBillId = t.id WHERE d.cashPayTime >= :startDate " +
                "AND d.cashPayTime <= :endDate AND d.cashPayStatus = 'SUCCESS' AND t.delStatus = 0 AND t.billStatus <> 'Q' " +
                "AND (t.transStatus = 'E' OR t.transStatus = 'A' OR t.transStatus = 'C') GROUP BY partnerNo,cashPayWay " +
                "UNION SELECT sum(d.actualOilFee) amt,partnerNo,oilFeePayWay payWay FROM YD_TMS_PAY_DETAIL d " +
                "INNER JOIN YD_APP_TRANSPORTCASH t ON d.wayBillId = t.id WHERE d.oilFeePayTime >= :startDate " +
                "AND d.oilFeePayTime <= :endDate AND d.oilFeePayStatus = 'SUCCESS' AND t.delStatus = 0 AND t.billStatus <> 'Q' " +
                "AND (t.transStatus = 'E' OR t.transStatus = 'A' OR t.transStatus = 'C') GROUP BY partnerNo,oilFeePayWay " +
                "UNION SELECT sum(d.actualDestAmt) amt,partnerNo,destAmtPayWay payWay FROM YD_TMS_PAY_DETAIL d " +
                "INNER JOIN YD_APP_TRANSPORTCASH t ON d.wayBillId = t.id WHERE d.destAmtPayTime >= :startDate " +
                "AND d.destAmtPayTime <= :endDate AND d.destAmtPayStatus = 'SUCCESS' AND t.delStatus = 0 AND t.billStatus <> 'Q' " +
                "AND (t.transStatus = 'E' OR t.transStatus = 'A' OR t.transStatus = 'C') GROUP BY partnerNo,destAmtPayWay " +
                "UNION SELECT sum(d.actualRetAmt) amt,partnerNo, retAmtPayWay FROM YD_TMS_PAY_DETAIL d " +
                "INNER JOIN YD_APP_TRANSPORTCASH t ON d.wayBillId = t.id WHERE d.retAmtPayTime >= :startDate " +
                "AND d.retAmtPayTime <= :endDate AND d.retAmtPayStatus = 'SUCCESS' AND t.delStatus = 0 AND t.billStatus <> 'Q' " +
                "AND (t.transStatus = 'E' OR t.transStatus = 'A' OR t.transStatus = 'C') GROUP BY partnerNo, retAmtPayWay " +
                ") x GROUP BY x.partnerNo,x.payWay ";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.addScalar("amt", BigDecimalType.INSTANCE).addScalar("partnerNo")
                .addScalar("payWay", StringType.INSTANCE);
        query.setResultTransformer(Transformers.aliasToBean(PayAmtStatisticsVO.class));
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TmsSmsSettingModel> listSendSmsSetting() {
        String sql = "SELECT * FROM YD_TMS_SMS_SETTING WHERE receiver is not null AND sendContent is not null AND " +
                "(lastSendDate != :currentDate OR lastSendDate IS NULL) AND (createDate != :currentDate " +
                "OR createDate IS NULL) AND sendTime <= :currentTime";
        Date date = new Date();
        String currentDate = ProjectUtil.dateFormatByPattern(date, ProjectUtil.DAY_DATE_FORMAT);
        String currentTime = ProjectUtil.dateFormatByPattern(date, ProjectUtil.DATE_FORMAT_HH_MM_SS);
        Query query = getCurrentSession().createSQLQuery(sql)
                .setResultTransformer(Transformers.aliasToBean(TmsSmsSettingModel.class));
        query.setParameter("currentDate", currentDate);
        query.setParameter("currentTime", currentTime);
        return query.list();
    }

    @Override
    public int updateTmsSmsSetting(int smsSettingId) {
        return jdbcTemplate.update("UPDATE YD_TMS_SMS_SETTING SET lastSendDate = now() WHERE smsSettingId = ?",
                smsSettingId);
    }
}