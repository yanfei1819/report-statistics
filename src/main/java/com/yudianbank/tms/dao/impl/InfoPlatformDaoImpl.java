package com.yudianbank.tms.dao.impl;

import com.yudianbank.tms.dao.InfoPlatformDao;
import com.yudianbank.tms.model.TmsSourceGoods;
import com.yudianbank.tms.model.TmsSourceGoodsConfig;
import com.yudianbank.tms.model.TmsSourceGoodsPushConfig;
import com.yudianbank.tms.model.vo.AppPushVO;
import com.yudianbank.tms.model.vo.GoodsDriverRelevanceVO;
import com.yudianbank.tms.model.vo.SmsPushVO;
import com.yudianbank.tms.util.ProjectUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManagerFactory;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 信息平台需求的数据库操作Dao接口实现
 *
 * @author Song Lea
 */
@Repository
public class InfoPlatformDaoImpl implements InfoPlatformDao {

    private JdbcTemplate jdbcTemplate;
    private SessionFactory sessionFactory;

    public InfoPlatformDaoImpl() {
    }

    @Autowired
    public InfoPlatformDaoImpl(JdbcTemplate jdbcTemplate, EntityManagerFactory entityManagerFactory) {
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
    public List<TmsSourceGoodsPushConfig> listSourceGoodsPushConfig() {
        String hql = "from TmsSourceGoodsPushConfig";
        return getCurrentSession().createQuery(hql).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TmsSourceGoodsConfig> listValidSourceGoodsConfig(int start, int limit) {
        Date now = new Date();
        String hourMinute = ProjectUtil.dateFormatByPattern(now, ProjectUtil.TIME_DATE_FORMAT); // 每天用车时间范围内
        String today = ProjectUtil.dateFormatByPattern(now, ProjectUtil.DAY_DATE_FORMAT); // 有效期
        String sql = "SELECT * FROM YD_TMS_SOURCEGOODS_CONFIG WHERE enabledStatus = 1 AND (effectiveStatus = 1 "
                + "OR (effectiveStatus =0 AND validityStart <= :today AND validityEnd >= :today)) "
                + "AND applyTimeStart <= :hourMinute AND applyTimeEnd >= :hourMinute";
        Query query = getCurrentSession().createSQLQuery(sql)
                .setResultTransformer(Transformers.aliasToBean(TmsSourceGoodsConfig.class));
        query.setParameter("today", today);
        query.setParameter("hourMinute", hourMinute);
        query.setFirstResult(start);
        query.setMaxResults(limit);
        return query.list();
    }

    @Override
    public void batchUpdateSourceGoodsByIds(List<Integer> ids) {
        String sql = "UPDATE YD_TMS_SOURCEGOODS SET updateTime = NOW() WHERE id = ?";
        this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, ids.get(i));
            }

            @Override
            public int getBatchSize() {
                return ids.size();
            }
        });
    }

    @Override
    public void batchInsertSourceGoods(List<TmsSourceGoods> sourceGoodsList) {
        ProjectUtil.batchInsertImpl(sourceGoodsList, getCurrentSession());
    }

    @Override
    @SuppressWarnings("unchecked")
    public TmsSourceGoodsConfig getTmsSourceGoodsConfigById(int id) {
        String hql = "from TmsSourceGoodsConfig where id = :id "; // and enabledStatus = 1
        Query query = getCurrentSession().createQuery(hql);
        query.setParameter("id", id);
        List<TmsSourceGoodsConfig> list = query.list();
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<GoodsDriverRelevanceVO> getGoodsDriverRelevanceListAfterDate(String date, Integer sourceGoodsConfigId) {
        String sql = "SELECT DISTINCT g.id, m.loginId FROM YD_TMS_SOURCEGOODS g, YD_APP_MY_LINE m,YD_APP_USER r " +
                "WHERE g.sendRegion = m.sendRegion AND g.arriveRegion = m.arriveRegion AND m.loginId = r.loginId " +
                "AND r.isCertifacate = 'Y' AND r.isCarCertificate = 'Y' " + // 已认证的
                "AND NOT EXISTS ( select 1 from YD_TMS_DRIVERS s where s.mobile = r.mobile) " + // 外请车的
                "AND m.loginId IS NOT NULL AND NOT EXISTS (SELECT 1 FROM YD_TMS_SOURCEGOODS_DRIVER_RELEVANCE e " +
                "WHERE e.sourceGoodsId = g.id AND e.driverId = m.loginId) AND g.applyDate > :date ";
        if (Objects.nonNull(sourceGoodsConfigId))
            // 后管界面调用时只生成对应货源的
            sql += "AND sourceGoodsConfigId = " + sourceGoodsConfigId;
        Query query = getCurrentSession().createSQLQuery(sql).setParameter("date", date)
                .setResultTransformer(Transformers.aliasToBean(GoodsDriverRelevanceVO.class));
        return query.list();
    }

    @Override
    public void batchInsertGoodsDriverRelevance(List<GoodsDriverRelevanceVO> relationList, final int countCalled) {
        String sql = "INSERT INTO YD_TMS_SOURCEGOODS_DRIVER_RELEVANCE(sourceGoodsId, driverId, countCalled, " +
                "calledTime, countReceiveSms) VALUES (?, ?, ?, ?, 0)";
        Timestamp calledTime = countCalled > 0 ? new Timestamp(new Date().getTime()) : null;
        this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, relationList.get(i).getId());
                ps.setString(2, relationList.get(i).getLoginId());
                ps.setInt(3, countCalled);
                // 当时时间戳
                ps.setTimestamp(4, calledTime);
            }

            @Override
            public int getBatchSize() {
                return relationList.size();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TmsSourceGoods> listSortTmsSourceGoodsAfterDate(String date, String ids) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT d.* FROM YD_TMS_SOURCEGOODS d INNER JOIN (SELECT e.sourceGoodsId,COUNT(e.driverId) " +
                "driverCount FROM YD_TMS_SOURCEGOODS_DRIVER_RELEVANCE e GROUP BY e.sourceGoodsId " +
                "ORDER BY driverCount DESC, sourceGoodsId) b WHERE d.id = b.sourceGoodsId AND d.applyDate > :date ");
        if (!StringUtils.isEmpty(ids))
            sql.append("AND b.sourceGoodsId NOT IN (").append(ids).append(") ");
        sql.append("ORDER BY b.driverCount DESC,b.sourceGoodsId ");
        Query query = getCurrentSession().createSQLQuery(sql.toString()).setParameter("date", date)
                .setResultTransformer(Transformers.aliasToBean(TmsSourceGoods.class));
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SmsPushVO> listDriverMobile(int id, String date) {
        String sql = "SELECT DISTINCT u.mobile,r.driverId,c.carLength,(SELECT ifnull(SUM(countReceiveSms),0) " +
                "FROM YD_TMS_SOURCEGOODS_DRIVER_RELEVANCE WHERE driverId = r.driverId AND receiveSmsTime >= :date) " +
                "hasSendCount FROM (SELECT driverId FROM YD_TMS_SOURCEGOODS_DRIVER_RELEVANCE WHERE sourceGoodsId = :id) r " +
                "INNER JOIN YD_APP_USER u ON u.loginId = r.driverId LEFT JOIN YD_APP_CARS c ON c.loginId = r.driverId " +
                "WHERE u.mobile IS NOT NULL ";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.addScalar("mobile").addScalar("driverId").addScalar("carLength")
                .addScalar("hasSendCount", IntegerType.INSTANCE);
        query.setParameter("id", id)
                .setParameter("date", date)
                .setResultTransformer(Transformers.aliasToBean(SmsPushVO.class));
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AppPushVO> getCityMatchRelevanceById(String date, int id) {
        String sql = "SELECT DISTINCT r.clientId,r.loginId,g.sendCity,g.arriveCity FROM YD_APP_USER r,YD_TMS_SOURCEGOODS g," +
                "YD_APP_MY_LINE m,YD_TMS_SOURCEGOODS_DRIVER_RELEVANCE d WHERE r.loginId = d.driverId AND g.id = d.sourceGoodsId " +
                "AND r.loginId = m.loginId AND g.sendCity = m.sendCity AND g.arriveCity = m.arriveCity " +
                "AND r.clientId IS NOT NULL AND g.applyDate > :applyDate AND g.id = :id";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.addScalar("clientId").addScalar("loginId").addScalar("sendCity").addScalar("arriveCity");
        query.setParameter("applyDate", date).setParameter("id", id)
                .setResultTransformer(Transformers.aliasToBean(AppPushVO.class));
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AppPushVO> getRegionMatchRelevanceById(String date, int id) {
        String sql = "SELECT DISTINCT r.clientId,r.loginId,g.sendCity,g.arriveCity FROM YD_APP_USER r,YD_TMS_SOURCEGOODS g," +
                "YD_APP_MY_LINE m,YD_TMS_SOURCEGOODS_DRIVER_RELEVANCE d WHERE r.loginId = d.driverId AND g.id = d.sourceGoodsId " +
                "AND r.loginId = m.loginId AND g.sendRegion = m.sendRegion AND g.arriveRegion = m.arriveRegion " +
                "AND g.sendCity <> m.sendCity AND g.arriveCity <> m.arriveCity " + // 区域匹配但城市不匹配
                "AND r.clientId IS NOT NULL AND g.applyDate > :applyDate AND g.id = :id ";
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        query.addScalar("clientId").addScalar("loginId").addScalar("sendCity").addScalar("arriveCity");
        query.setParameter("applyDate", date).setParameter("id", id)
                .setResultTransformer(Transformers.aliasToBean(AppPushVO.class));
        return query.list();
    }

    @Override
    public int updateCountReceiveSms(int sourceGoodsId, String driverId) {
        String sql = "UPDATE YD_TMS_SOURCEGOODS_DRIVER_RELEVANCE e SET e.countReceiveSms = e.countReceiveSms + 1," +
                "e.receiveSmsTime = NOW() WHERE e.sourceGoodsId = ? AND e.driverId = ? ";
        return jdbcTemplate.update(sql, sourceGoodsId, driverId);
    }

    @Override
    public List<String> listOverCalledCountGoodsIds(int everydayCallCount, int totalCallCount) {
        Date now = new Date();
        String start = ProjectUtil.dateFormatByPattern(now, ProjectUtil.DAY_DATE_FORMAT) + " 00:00:00";
        String end = ProjectUtil.getSpecifiedDateStr(now, 1, ProjectUtil.DAY_DATE_FORMAT) + " 00:00:00";
        String sql = "SELECT CONCAT(sourceGoodsId,'') FROM YD_TMS_SOURCEGOODS_DRIVER_RELEVANCE WHERE countCalled > 0 " +
                "GROUP BY sourceGoodsId HAVING count(countCalled) >= ? UNION SELECT CONCAT(sourceGoodsId,'') " +
                "FROM YD_TMS_SOURCEGOODS_DRIVER_RELEVANCE WHERE countCalled > 0 AND calledTime < ? AND calledTime >= ? " +
                "GROUP BY sourceGoodsId HAVING count(countCalled) >= ?";
        return jdbcTemplate.queryForList(sql, new Object[]{totalCallCount, end, start, everydayCallCount}, String.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TmsSourceGoods> getOverUpdateTimeGoodsListById(int configId, String updateTime, String applyDate) {
        String sql = "SELECT * FROM YD_TMS_SOURCEGOODS s WHERE s.sourceGoodsConfigId = :sourceGoodsConfigId " +
                "AND s.updateTime > :updateTime AND s.applyDate = :applyDate";
        Query query = getCurrentSession().createSQLQuery(sql)
                .setParameter("sourceGoodsConfigId", configId)
                .setParameter("updateTime", updateTime)
                .setParameter("applyDate", applyDate)
                .setResultTransformer(Transformers.aliasToBean(TmsSourceGoods.class));
        return query.list();
    }

    @Override
    public List<Integer> getSourceGoodsIdByDate(String date) {
        String sql = "SELECT id FROM YD_TMS_SOURCEGOODS WHERE applyDate = ? ";
        return jdbcTemplate.queryForList(sql, new Object[]{date}, Integer.class);
    }

    @Override
    public List<Integer> getSourceGoodsConfigIdsAfterDate(String date) {
        String sql = "SELECT DISTINCT sourceGoodsConfigId FROM YD_TMS_SOURCEGOODS WHERE applyDate > ? ";
        return jdbcTemplate.queryForList(sql, new Object[]{date}, Integer.class);
    }

    @Override
    public int bakGoodsRelevanceBeforeDate(String bakDate) {
        String sql = "INSERT INTO YD_TMS_SOURCEGOODS_DRIVER_RELEVANCE_copy(sourceGoodsId,driverId,countCalled," +
                "countReceiveSms,calledTime,receiveSmsTime) SELECT e.sourceGoodsId,e.driverId,e.countCalled," +
                "e.countReceiveSms,e.calledTime,e.receiveSmsTime FROM YD_TMS_SOURCEGOODS_DRIVER_RELEVANCE e " +
                "INNER JOIN YD_TMS_SOURCEGOODS s ON e.sourceGoodsId = s.id AND s.applyDate <= ?";
        return jdbcTemplate.update(sql, bakDate);
    }

    @Override
    public int delGoodsRelevanceBeforeDate(String bakDate) {
        String sql = "DELETE e FROM YD_TMS_SOURCEGOODS_DRIVER_RELEVANCE e INNER JOIN YD_TMS_SOURCEGOODS s " +
                "ON e.sourceGoodsId = s.id AND s.applyDate <= ?";
        return jdbcTemplate.update(sql, bakDate);
    }
}