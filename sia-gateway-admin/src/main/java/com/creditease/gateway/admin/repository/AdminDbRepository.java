/*-
 * <<
 * sag
 * ==
 * Copyright (C) 2019 sia
 * ==
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * >>
 */


package com.creditease.gateway.admin.repository;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.creditease.gateway.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.creditease.gateway.admin.domain.AdminInfo;
import com.creditease.gateway.admin.domain.AuditQuery;
import com.creditease.gateway.admin.domain.FallbackQuery;
import com.creditease.gateway.admin.domain.StatisticQuery;
import com.creditease.gateway.admin.domain.common.PaginateList;
import com.creditease.gateway.admin.event.GatewayAuditEvent;
import com.creditease.gateway.admin.repository.base.BaseAdminRepository;
import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.constant.GatewayConstant.ZuulState;
import com.creditease.gateway.helper.StringHelper;

/**
 * 
 * 首頁管理功能
 * 
 * @author peihua
 */

@Repository
public class AdminDbRepository extends BaseAdminRepository {

    @Value("${eureka.instance.instance-id}")
    private String instance;

    @Value("${spring.application.name}")
    private String zuuladminName;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminDbRepository.class);

    private static final String INSERTZUULADMININFO = "INSERT INTO gateway_admin (adminInstanceId, adminHotsName, zuulLastStartTime, adminStatus) values(?,?,?,?)";

    private static final String UPDATEZUULADMININFO = "update gateway_admin set zuulLastStartTime = ? ,adminStatus=? where adminInstanceId =?";

    private static final String QUERYROUTECOUNT = "select count(1) from gateway_route where zuulGroupName ='%s'";

    private static final String QUERYZUULLIST = "select * from gateway_info ";

    private static final String QUERYZUULADMINLIST = "select * from gateway_admin ";

    private static final String QUERYZUULDETAIL = "select * from gateway_info where zuulInstanceId='%s' ";

    private static final String QUERYALARMLIST = "select * from gateway_alarm where zuulGroupName = '%s' ";

    private static final String QUERYALARMCOUNT = "select count(1)  from gateway_alarm where  zuulGroupName = '%s'";

    private static final String QUERYSUMCOUTER = "SELECT sum(counterValue) FROM gateway_counter  where counterKey like %s and zuulGroupName = '%s' ";

    private static final String QUERYCOUNTERPAIR = "SELECT datetime as counterkey, sum(counterValue) as countervalue FROM gateway_counter where counterKey like %s and zuulGroupName = '%s' ";

    private static final String QUERYCOUNTERGROUPBY = " group by datetime order by datetime desc limit 30";

    private static final String DELETEZUUL = "DELETE FROM gateway_info where zuulInstanceId='%s'";

    private static final String DELETEZUULADMIN = "DELETE FROM gateway_admin where adminInstanceId='%s'";

    private static final String QUERYROUTECOUNTERPAIR = "SELECT datetime as counterkey, sum(sumCount) as countervalue FROM gateway_route_counter where routeid ='%s' and datetime>'%s' and  datetime<'%s' group by datetime order by datetime ";

    private static final String QUERYROUTEFAILCOUNTERPAIR = "SELECT datetime as counterkey, sum(failCount) as countervalue FROM gateway_route_counter where routeid ='%s' and datetime>'%s' and  datetime<'%s' group by datetime order by datetime ";

    private static final String QUERYZUULGROUPNAME = "SELECT zuulGroupName FROM gateway_route WHERE id = '%s'";

    private static final String SELECTZUULINSTANCE = "SELECT zuulInstanceId FROM gateway_info where zuulGroupName ='%s' ";

    private static final String INSERTALARMSETTING = "INSERT INTO gateway_setting (zuulGroupName, alarmEmailAddr) values(?,?)";

    private static final String UPDATEALARMSETTING = "update gateway_setting set alarmEmailAddr=? where zuulGroupName =?";

    private static final String QUERYALARMSETTING = "select alarmEmailAddr from gateway_setting  where zuulGroupName ='%s'";

    private static final String QUERYALLZUULIST = "SELECT * FROM gateway_info;";

    private static final String UPDATEZUULDESCBYGROUP = "update gateway_info set zuulDesc = '%s' where zuulGroupName = '%s' ";

    private static final String SELECTDISTINCTZUULGROUP = "select DISTINCT zuulGroupName from gateway_info";

    private static final String SELECTFALLBACK = "select * FROM gateway_fallback where zuulGroupName = '%s' ;";

    private static final String INSERTAUDITINFO = "INSERT INTO gateway_audit (username, method, zuulGroupName, url, ip, start_time, time_loss, status, params ) values(?,?,?,?,?,?,?,?,?)";

    private static final String GATEWAYAUDITFILEDS = " id, username userName, method , zuulGroupName , url, ip, start_time startTime, time_loss  timeLoss, status , params  ";

    private static final String QUERYAUDITINFOS = " select " + GATEWAYAUDITFILEDS + " from gateway_audit where 1 = 1 ";

    private static final String QUERYAUDITCOUNT = " select count(*) from gateway_audit  where 1 = 1";

    private static final String QUERYINSTANCESQL = "select distinct zuulInstance from gateway_counter where zuulGroupName = '%s' and datetime >= '%s' ";

    private static final String INSTANCECONDITION = " and zuulInstance = '%s' and datetime >= '%s' ";

    private static final String QUERYFALLBACKCOUNT = " select count(*) from gateway_fallback  where 1 = 1";

    private static final String QUERYFALLBACKINFOS = " select * FROM gateway_fallback where 1 = 1 ";

    /**
     * 获取本网关注册Route数
     * 
     * @return
     */

    public Integer queryRouteCount() throws Exception {

        String querySQL = StringHelper.format(QUERYROUTECOUNT, super.getGroupName());

        LOGGER.info("queryRouteCount querySQL :" + querySQL);

        Integer count = adminJdbcTemplate.queryForObject(querySQL, Integer.class);

        LOGGER.info("query route count success! the cout is :" + count);

        return count;
    }

    public String getSelectedZuulInstance() {

        String querySQL = StringHelper.format(SELECTZUULINSTANCE, super.getGroupName());

        LOGGER.info("> getSelectedZuulInstance SQL :" + querySQL);

        List<String> zuulGroupName = adminJdbcTemplate.queryForList(querySQL, String.class);

        StringBuffer buf = new StringBuffer();

        for (String ins : zuulGroupName) {
            buf.append("'");
            buf.append(ins);
            buf.append("'");
            buf.append(",");
        }

        String rst = buf.toString();

        rst = rst.substring(0, rst.lastIndexOf(","));

        return rst;
    }

    public String appendInSql() {

        StringBuffer buf = new StringBuffer();
        buf.append(" and zuulInstance in");
        buf.append("(");
        buf.append(getSelectedZuulInstance());
        buf.append(")");

        String rst = buf.toString();

        LOGGER.info("> appendInSql SQL :" + rst);

        return rst;
    }

    public String appendInSqlwithoutIn() {

        StringBuffer buf = new StringBuffer();
        buf.append(" zuulInstance in");
        buf.append("(");
        buf.append(getSelectedZuulInstance());
        buf.append(")");

        String rst = buf.toString();

        LOGGER.info("> appendInSql SQL :" + rst);

        return rst;
    }

    /**
     * 获取全部管理节点信息
     * 
     * @return
     */
    public List<AdminInfo> queryAdminList() throws Exception {

        List<AdminInfo> results = adminJdbcTemplate.query(QUERYZUULADMINLIST,

                new BeanPropertyRowMapper<>(AdminInfo.class));
        LOGGER.info("get zuul success!");

        return results;

    }

    /**
     * 获取指定group的zuul节点
     * 
     * @return
     */
    public List<ZuulInfo> queryZuulList() throws Exception {

        String querySQL = getSqlStrByGroupName(QUERYZUULLIST);

        List<ZuulInfo> results = adminJdbcTemplate.query(querySQL,

                new BeanPropertyRowMapper<>(ZuulInfo.class));

        LOGGER.info("get zuul success!");

        return results;
    }

    /**
     * 获取All zuul节点
     *
     * @return
     */
    public List<ZuulInfo> queryAllZuulList() {

        List<ZuulInfo> results = adminJdbcTemplate.query(QUERYALLZUULIST, new BeanPropertyRowMapper<>(ZuulInfo.class));

        return results;
    }

    /**
     * 获取All zuul Group
     *
     * @return
     */
    public List<String> queryZuulGroupList() {

        List<String> results = adminJdbcTemplate.queryForList(SELECTDISTINCTZUULGROUP, String.class);

        LOGGER.info("get zuul group list success!");

        return results;
    }

    /**
     * 更新预警邮箱地址
     * 
     * @return
     */
    public boolean updateEmailSetting(String zuulGroupname, String emailSetting) throws Exception {

        try {
            adminJdbcTemplate.update(UPDATEALARMSETTING, emailSetting, zuulGroupname);

            LOGGER.info("saveEmailSetting success!");

        }
        catch (Exception e) {

            return false;
        }

        return true;
    }

    /**
     * 获取zuul节点信息
     * 
     * @return
     */
    public ZuulInfo queryZuulDetail(String insid) throws Exception {

        try {

            String querySQL = StringHelper.format(QUERYZUULDETAIL, insid);

            LOGGER.info("queryZuulDetail SQL:" + querySQL);

            RowMapper<ZuulInfo> rm = BeanPropertyRowMapper.newInstance(ZuulInfo.class);

            ZuulInfo info = adminJdbcTemplate.queryForObject(querySQL, rm);

            return info;

        }
        catch (DataAccessException e) {

            LOGGER.error(">>> Exception:" + e.getCause());
        }
        return null;
    }

    /**
     * 删除zuul节点信息
     * 
     * @return
     */
    public int deleteZuul(String instanceID) throws Exception {

        int rst = 0;

        try {

            String deleteSQL = StringHelper.format(DELETEZUUL, instanceID);

            LOGGER.info("DBRepository delete ZUUL obj deleteSQL: " + deleteSQL);

            rst = adminJdbcTemplate.update(deleteSQL);

            LOGGER.info("DBRepository delete ZUUL obj result: " + rst);

        }
        catch (DataAccessException e) {

            throw e;
        }
        return rst;
    }

    /**
     * 获取Alarm 信息
     * 
     * @return
     */
    public List<AlarmInfo> queryAlarmList() throws Exception {

        String sql;
        List<AlarmInfo> results;
        try {
            sql = StringHelper.format(QUERYALARMLIST, super.getGroupName()) + " order by alarmCreateTime desc";
            LOGGER.info(">queryAlarmList execute queryAlarmList sql:[{}]", sql);

            results = adminJdbcTemplate.query(sql,

                    new BeanPropertyRowMapper<>(AlarmInfo.class));

            LOGGER.info("get admin success!");

        }
        catch (Exception e) {
            LOGGER.error(">queryAlarmList异常!", e);
            throw e;
        }
        return results;
    }

    /**
     * 获取Alarm Count
     * 
     * @return
     */
    public Integer queryAlarmCount() throws Exception {

        String sql = StringHelper.format(QUERYALARMCOUNT, super.getGroupName());

        Integer results = adminJdbcTemplate.queryForObject(sql, Integer.class);

        LOGGER.info("get admin success!");

        return results;
    }

    /**
     * 
     * 統計：調用
     */

    public int getSumCounter() throws Exception {

        String formatquery = StringHelper.format(QUERYSUMCOUTER, StringHelper.appendLike(GatewayConstant.APISUMCOUNT),
                super.getGroupName());

        Integer results = adminJdbcTemplate.queryForObject(formatquery, Integer.class);

        if (results == null) {
            LOGGER.warn("getSumCounter is null!");
            return 0;
        }

        LOGGER.info("getSumCounter success!");

        return results.intValue();
    }

    public List<String> getInstanceIdsByGroup(String startTime) throws Exception {

        try {
            String queryInstanceSql = StringHelper.format(QUERYINSTANCESQL, super.getGroupName(), startTime);
            return adminJdbcTemplate.queryForList(queryInstanceSql, String.class);
        }
        catch (DataAccessException e) {
            return null;
        }
        catch (Exception e) {
            LOGGER.error("Failed to get the instance information in the gateway group....", e);
            throw e;
        }
    }

    public List<CounterPair> getCounterPairs(String instanceId, String startTime) throws Exception {

        try {
            String zuulGroupName = super.getGroupName();

            String formatquery = StringHelper.format(QUERYCOUNTERPAIR,
                    StringHelper.appendLike(GatewayConstant.APISUMCOUNT), zuulGroupName);

            String supplementSql = GatewayConstant.TOTAL.equals(instanceId) ? ""
                    : StringHelper.format(INSTANCECONDITION, instanceId, startTime);

            formatquery = formatquery + supplementSql + QUERYCOUNTERGROUPBY;
            LOGGER.info("> getApiAccessCount formatquery: " + formatquery);

            return adminJdbcTemplate.query(formatquery, new BeanPropertyRowMapper<>(CounterPair.class));
        }
        catch (DataAccessException e) {
            return null;
        }
        catch (Exception e) {
            LOGGER.error("Failed to get call trend information....", e);
            throw e;
        }
    }

    @SuppressWarnings("rawtypes")
    public List<CounterPair> getHealthy(String instanceId, String startTime) throws Exception {

        String sumquery = StringHelper.format(QUERYCOUNTERPAIR, StringHelper.appendLike(GatewayConstant.APISUMCOUNT),
                super.getGroupName());

        String supplementSql = GatewayConstant.TOTAL.equals(instanceId) ? ""
                : StringHelper.format(INSTANCECONDITION, instanceId, startTime);

        sumquery = sumquery + supplementSql + QUERYCOUNTERGROUPBY;

        LOGGER.info("> getHealthy, sumquery: " + sumquery);

        List<CounterPair> resultsok = adminJdbcTemplate.query(sumquery, new BeanPropertyRowMapper<>(CounterPair.class));

        String failquery = StringHelper.format(QUERYCOUNTERPAIR, StringHelper.appendLike(GatewayConstant.APIFAILCOUNT),
                super.getGroupName());

        failquery = failquery + supplementSql + QUERYCOUNTERGROUPBY;

        LOGGER.info("> getHealthy, failquery: " + failquery);

        List<CounterPair> resultsfail = adminJdbcTemplate.query(failquery,
                new BeanPropertyRowMapper<>(CounterPair.class));

        Map<String, String> rstFail = getFaildMap(resultsfail);

        for (CounterPair countersum : resultsok) {

            String datetime = countersum.getCounterkey();

            int sum = Integer.parseInt(countersum.getCountervalue());

            int failedCount = 0;

            String fvalue = rstFail.get(datetime);

            if (fvalue != null) {
                failedCount = Integer.parseInt(rstFail.get(datetime));
            }
            if (sum != 0) {
                DecimalFormat df = new DecimalFormat("0.00");

                String successRate = df.format((float) (sum - failedCount) / (float) sum);

                countersum.setCountervalue(successRate);
            }
            else {

                countersum.setCountervalue("1.00");
            }
        }

        return resultsok;
    }

    public Map<String, String> getFaildMap(List<CounterPair> resultsfail) throws Exception {

        return resultsfail.stream().collect(Collectors.toMap(CounterPair::getCounterkey, CounterPair::getCountervalue));
    }

    public int deleteZuulAdmin(String adminInstanceid) throws Exception {

        int rst = 0;

        try {

            String deleteSQL = StringHelper.format(DELETEZUULADMIN, adminInstanceid);

            LOGGER.info("DBRepository delete Admin obj deleteSQL: " + deleteSQL);

            rst = adminJdbcTemplate.update(deleteSQL);

            LOGGER.info("DBRepository delete Admin obj result: " + rst);

        }
        catch (DataAccessException e) {

            LOGGER.error("> DataAccessException:" + e.getCause());
        }
        return rst;
    }

    public boolean registeradmin() throws Exception {

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        try {

            LOGGER.info(">insert Admin instance" + instance);

            adminJdbcTemplate.update(INSERTZUULADMININFO, instance, zuuladminName, currentTime,
                    GatewayConstant.ZuulState.RUNNING.toString());

            LOGGER.info(">insert zuul success!");

            return true;

        }
        catch (Exception e) {

            throw e;
        }

    }

    public boolean updateAdmin(ZuulState status) throws Exception {

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        try {

            LOGGER.info(">update instance:" + instance);

            adminJdbcTemplate.update(UPDATEZUULADMININFO, currentTime, status.toString(), instance);

            LOGGER.info(">update zuuladmin success, status is: " + status.toString());

            return true;

        }
        catch (Exception e) {

            LOGGER.error(e.getMessage());

            return false;
        }
    }

    public List<CounterPair> getRouteAccessCount(StatisticQuery request) throws Exception {

        String formatquery = StringHelper.format(QUERYROUTECOUNTERPAIR, request.getRouteid(), request.getStarttime(),
                request.getEndtime());

        LOGGER.info("> get getRouteAccessCount formatquery: " + formatquery);

        List<CounterPair> results = adminJdbcTemplate.query(formatquery,
                new BeanPropertyRowMapper<>(CounterPair.class));

        return results;
    }

    public List<CounterPair> getRouteHealthCount(StatisticQuery request) throws Exception {

        List<CounterPair> sumList = getRouteAccessCount(request);

        String formatquery = StringHelper.format(QUERYROUTEFAILCOUNTERPAIR, request.getRouteid(),
                request.getStarttime(), request.getEndtime());

        LOGGER.info("> get FailCountPaire formatquery: " + formatquery);

        List<CounterPair> failPair = adminJdbcTemplate.query(formatquery,
                new BeanPropertyRowMapper<>(CounterPair.class));

        for (CounterPair counter : sumList) {

            String datetimeKey = counter.getCounterkey();

            String sumcountvalue = counter.getCountervalue();

            for (CounterPair failcount : failPair) {

                String faildatetimeKey = failcount.getCounterkey();

                if (datetimeKey.equals(faildatetimeKey)) {

                    String failcountvalue = failcount.getCountervalue();

                    int fail = Integer.parseInt(failcountvalue);

                    int sum = Integer.parseInt(sumcountvalue);

                    if (sum != 0) {

                        DecimalFormat df = new DecimalFormat("0.00");

                        String successRate = df.format((float) (sum - fail) / (float) sum);

                        counter.setCountervalue(successRate);

                    }
                    else {

                        counter.setCountervalue("1.00");
                    }

                }
            }
        }

        return sumList;
    }

    public String queryZuulGroupName(String routeid) {

        String querySQL = StringHelper.format(QUERYZUULGROUPNAME, routeid);

        List<String> zuulGroupName = adminJdbcTemplate.queryForList(querySQL, String.class);

        if (zuulGroupName != null && zuulGroupName.size() > 0) {

            return (String) zuulGroupName.get(0);

        }

        return null;

    }

    /**
     * 保存预警邮箱地址
     * 
     * @return
     */
    public boolean saveEmailSetting(String zuulGroupname, String emailSetting) throws Exception {

        try {
            adminJdbcTemplate.update(INSERTALARMSETTING, zuulGroupname, emailSetting);

            LOGGER.info("saveEmailSetting success!");

        }
        catch (Exception e) {

            LOGGER.error(">saveEmailSetting:" + e.getCause());

            throw e;
        }

        return true;
    }

    /**
     * 获取EmailSetting
     * 
     */
    public String getAlarmEmailSetting(String groupName) {

        String setting = null;
        try {
            String querySQL = StringHelper.format(QUERYALARMSETTING, groupName);

            LOGGER.info("saveEmailSetting querySQL::" + querySQL);

            setting = adminJdbcTemplate.queryForObject(querySQL, String.class);
        }
        catch (DataAccessException e) {
            return null;
        }
        catch (Exception e) {
            LOGGER.error("exception occur..", e);
        }

        return setting;
    }

    public List<Fallback> getFallbackList(String groupName) {

        try {
            String querySQL = StringHelper.format(SELECTFALLBACK, groupName);

            List<Fallback> resultsok = adminJdbcTemplate.query(querySQL, new BeanPropertyRowMapper<>(Fallback.class));

            LOGGER.info("getFallbackList querySQL::" + querySQL);
            return resultsok;

        }
        catch (DataAccessException e) {
            return null;
        }
        catch (Exception e) {
            LOGGER.error("exception occur..", e);
        }
        return null;

    }

    /**
     * 按条件搜索熔斷信息
     */
    public PaginateList queryFallbackList(FallbackQuery fallbackQuery) throws Exception {

        try {
            String conditionSQL = getFallbackConditionSQL(fallbackQuery);

            String countSQL = QUERYFALLBACKCOUNT + conditionSQL;
            LOGGER.info(">queryFallbackList countSQL ={}", countSQL);

            /**
             * get total construct PaginateList
             */
            PaginateList paginateList = getPaginateList(fallbackQuery, countSQL);
            if (0 == fallbackQuery.getTotal()) {
                return paginateList;
            }

            String querySQL = QUERYFALLBACKINFOS + conditionSQL + getTailSql(fallbackQuery);
            LOGGER.info(">queryFallbackList querySQL ={}", querySQL);
            List<Fallback> gatewayFallbackObjs = adminJdbcTemplate.query(querySQL,
                    new BeanPropertyRowMapper<>(Fallback.class));
            paginateList.setDataList(gatewayFallbackObjs);

            return paginateList;
        }
        catch (Exception e) {
            LOGGER.error(">>>> query fallback info occur exception...", e);
            throw e;
        }
    }

    public int updateZuulDescByGroupName(String zuulGroupName, String zuulGroupDesc) throws Exception {

        try {
            String updateSql = StringHelper.format(UPDATEZUULDESCBYGROUP, zuulGroupDesc, zuulGroupName);
            LOGGER.info("updateSql: [{}]", updateSql);
            return adminJdbcTemplate.update(updateSql);
        }
        catch (Exception e) {
            LOGGER.error("修改数据库描述操作异常，请检查！", e);
            throw e;
        }
    }

    public int recordAuditInfo(GatewayAuditEvent gatewayAuditObj) throws Exception {

        try {
            LOGGER.debug("+++++++ gatewayAuditObj: [{}]", JSON.toJSONString(gatewayAuditObj));
            return adminJdbcTemplate.update(INSERTAUDITINFO, gatewayAuditObj.getUserName(), gatewayAuditObj.getMethod(),
                    gatewayAuditObj.getZuulGroupName(), gatewayAuditObj.getUrl(), gatewayAuditObj.getIp(),
                    gatewayAuditObj.getStartTime(), gatewayAuditObj.getTimeLoss(), gatewayAuditObj.getStatus(),
                    gatewayAuditObj.getParams());
        }
        catch (Exception e) {
            LOGGER.error("An abnormality occurred in the audit information...gatewayAuditObj=[{}]", e, JSON.toJSONString(gatewayAuditObj));
            throw e;
        }
    }

    /**
     * 按条件搜索操作审计信息
     */
    public PaginateList queryAuditInfos(AuditQuery auditQuery) throws Exception {

        try {
            String conditionSQL = getConditionSQL(auditQuery);

            String countSQL = QUERYAUDITCOUNT + conditionSQL;
            LOGGER.info(">countSQL ={}", countSQL);

            /**
             * get total construct PaginateList
             */
            PaginateList paginateList = getPaginateList(auditQuery, countSQL);
            if (0 == auditQuery.getTotal()) {
                return paginateList;
            }

            String querySQL = QUERYAUDITINFOS + conditionSQL + getTailSql(auditQuery);
            LOGGER.info("> querySQL ={}", querySQL);
            List<GatewayAuditObj> gatewayAuditObjs = adminJdbcTemplate.query(querySQL,
                    new BeanPropertyRowMapper<>(GatewayAuditObj.class));
            paginateList.setDataList(gatewayAuditObjs);

            return paginateList;
        }
        catch (Exception e) {
            LOGGER.error(">query audit info occur exception...", e);
            throw e;
        }
    }

    private String getConditionSQL(AuditQuery auditQuery) {

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        StringBuffer stringBuffer = new StringBuffer();
        String userName = auditQuery.getUserName();
        String zuulGroupName = authCheckor.getZuulGroupName();
        Date startTime = auditQuery.getStartTime(), endTime = auditQuery.getEndTime();
        if (!StringUtils.isEmpty(userName)) {
            stringBuffer.append(" and username = '" + userName + "' ");
        }
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            stringBuffer.append(" and start_time between '" + f.format(startTime.getTime()) + "' and '"
                    + f.format(endTime.getTime()) + "' ");
        }
        if (!StringUtils.isEmpty(zuulGroupName)) {
            stringBuffer.append(" and zuulGroupName = '" + zuulGroupName + "' ");
        }
        return stringBuffer.toString();
    }

    private String getFallbackConditionSQL(FallbackQuery fallbackQuery) {

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        StringBuffer stringBuffer = new StringBuffer();
        String keywords = fallbackQuery.getSearchKeyword();
        String zuulGroupName = authCheckor.getZuulGroupName();
        Date startTime = fallbackQuery.getStartTime();

        if (!StringUtils.isEmpty(zuulGroupName)) {
            stringBuffer.append(" and zuulGroupName = '" + zuulGroupName + "' ");
        }
        if (!StringUtils.isEmpty(keywords)) {
            stringBuffer.append(" and fallbackMsg like " + StringHelper.appendLike(keywords));
        }

        if (!StringUtils.isEmpty(startTime)) {
            stringBuffer.append(" and startTime >= '" + f.format(startTime.getTime()) + "' ");
        }

        return stringBuffer.toString();
    }
}
