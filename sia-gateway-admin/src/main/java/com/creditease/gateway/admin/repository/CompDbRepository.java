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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.creditease.gateway.admin.repository.base.BaseAdminRepository;
import com.creditease.gateway.domain.CompInfo;
import com.creditease.gateway.domain.RouteRibbonHolder;
import com.creditease.gateway.helper.StringHelper;

/**
 *
 * 路由组件查詢功能
 *
 * @author peihua
 */

@Repository
public class CompDbRepository extends BaseAdminRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompDbRepository.class);

    private static final String QUERYCOMPLIST = "select * from gateway_component where zuulGroupName = 'ALL' or zuulGroupName='%s' ";

    private static final String QUERYBIZCOMPLIST = "select * from gateway_component where compdesc != '公共组件' ";

    private static final String QUERYCOMPDETAIL = "select * from gateway_component where compFilterName='%s' ";

    private static final String UPDATECOMPROUTE = "UPDATE gateway_component SET routeidList='%s' where compFilterName='%s'";

    private static final String QUERYGATEWAY_RIBBON_MAP = "select * from  gateway_ribbon_map where serviceid='%s' and routeid = '%s' ";

    private static final String QUERYGATEWAY_RIBBON_MAP_BY_ROUTEID = "select * from  gateway_ribbon_map where  routeid = '%s' ";

    private static final String INSERT_RIBBON_MAP = "INSERT INTO gateway_ribbon_map (serviceid,routeid, currentVersion, allVersions, updateTime,strategy,context) values(?,?,?,?,?,?,?)";

    private static final String UPDATE_RIBBON_MAP = "UPDATE gateway_ribbon_map SET currentVersion='%s' ,serviceid = '%s' where routeid='%s' ";

    private static final String UPDATE_CANARY_RIBBON_MAP = "UPDATE gateway_ribbon_map SET context='%s' ,serviceid = '%s',strategy = '%s' where routeid='%s' ";

    private static final String QUERYROUTERATELIMIT = "select ratelimit from gateway_route_ratelimit where routeid='%s'";

    private static final String QUERYROUTERATECOUNT = "select count(*) from gateway_route_ratelimit where routeid='%s'";

    private static final String INSERTROUTELIMIT = "INSERT INTO gateway_route_ratelimit (routeid, ratelimit) values(?,?)";

    private static final String UPDATEROUTELIMIT = "UPDATE gateway_route_ratelimit SET ratelimit='%s' where routeid='%s'";

    private static final String DELETECOMPONENT = "DELETE FROM gateway_component where compFilterName='%s'";

    private static final String UPDATE_COMPDESC = "UPDATE gateway_component SET compdesc ='%s' WHERE compFilterName='%s'";

    /**
     * 查询组件列表
     *
     * @return
     */
    public List<CompInfo> queryCompList() throws Exception {

        String order = " order by id";

        String querySQL = StringHelper.format(QUERYCOMPLIST, super.getGroupName()) + order;

        LOGGER.info("CompDBRepository SQL:" + querySQL);

        List<CompInfo> results = adminJdbcTemplate.query(querySQL,

                new BeanPropertyRowMapper<>(CompInfo.class));

        LOGGER.info("queryCompList success!");

        return results;

    }

    /**
     * 查询第三方组件列表
     *
     * @return
     */
    public List<CompInfo> queryBizCompList() throws Exception {

        String order = " order by zuulGroupName";

        String querySQL = QUERYBIZCOMPLIST + order;

        LOGGER.info("queryBizCompList SQL:" + querySQL);

        List<CompInfo> results = adminJdbcTemplate.query(querySQL,

                new BeanPropertyRowMapper<>(CompInfo.class));

        LOGGER.info("queryBizCompList success!");

        return results;

    }

    /**
     * 获取组件详情
     *
     * @return
     */
    public CompInfo queryCompDetail(String compFilterName) throws Exception {

        try {

            String querySQL = StringHelper.format(QUERYCOMPDETAIL, compFilterName);

            LOGGER.info("CompDBRepository SQL:" + querySQL);

            RowMapper<CompInfo> rm = BeanPropertyRowMapper.newInstance(CompInfo.class);

            CompInfo info = adminJdbcTemplate.queryForObject(querySQL, rm);

            if (info == null) {
                LOGGER.warn("info is null!");

                return info;
            }

            String routeidArray = info.getRouteidList();

            if (StringHelper.isEmpty(routeidArray)) {
                return info;
            }
            String[] routids = routeidArray.split(";");

            List<String> l = Arrays.asList(routids);

            info.setContext(l);

            return info;

        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
        catch (Exception e) {
            LOGGER.error(">>> Exception occur", e);
            throw e;
        }
    }

    /**
     * 组件绑定
     *
     */
    public boolean bindRoute(String routeIds, String compFilterName) throws Exception {

        try {

            String formatupdate = StringHelper.format(UPDATECOMPROUTE, routeIds, compFilterName);

            adminJdbcTemplate.update(formatupdate);

            LOGGER.info("DBRepository updateRoute SQL: " + formatupdate);

        }
        catch (Exception e) {

            LOGGER.error(">>> Exception:" + e.getCause());

            return false;
        }
        return true;

    }

    /**
     *
     * 查询路由版本
     */
    public RouteRibbonHolder queryrouteVerion(String serviceId, String routeId) throws Exception {

        try {

            String querySQL = StringHelper.format(QUERYGATEWAY_RIBBON_MAP, serviceId, routeId);

            LOGGER.info("queryrouteVerion SQL:" + querySQL);

            RowMapper<RouteRibbonHolder> rm = BeanPropertyRowMapper.newInstance(RouteRibbonHolder.class);

            RouteRibbonHolder holder = adminJdbcTemplate.queryForObject(querySQL, rm);

            return holder;

        }
        catch (EmptyResultDataAccessException e) {
            return null;

        }
        catch (Exception e) {

            LOGGER.error(">>> Exception:" + e.getCause());

            return null;
        }

    }

    /**
     *
     * 查询此路由是否与服务进行绑定
     */
    public RouteRibbonHolder queryrouteVerionByRouteId(String routeId) throws Exception {

        try {

            String querySQL = StringHelper.format(QUERYGATEWAY_RIBBON_MAP_BY_ROUTEID, routeId);

            LOGGER.info("queryrouteVerionByRouteId SQL:" + querySQL);

            RowMapper<RouteRibbonHolder> rm = BeanPropertyRowMapper.newInstance(RouteRibbonHolder.class);

            RouteRibbonHolder holder = adminJdbcTemplate.queryForObject(querySQL, rm);

            return holder;

        }
        catch (EmptyResultDataAccessException e) {

            return null;

        }
        catch (Exception e) {

            LOGGER.error(">>>查询此路由是否与服务进行绑定操作异常， Exception:" + e.getCause(), e);

            throw e;
        }

    }

    /**
     *
     * 蓝绿部署插入路由版本
     *
     */

    public boolean addRouteRibbonHolder(RouteRibbonHolder holder) throws Exception {

        LOGGER.info(">>>> addRouteRibbonHolder insert obj..");

        int rst = 0;

        try {
            Date date = new Date(System.currentTimeMillis());
            Timestamp currentTime = new Timestamp(date.getTime());

            String strategy = holder.getStrategy();
            String currentVersion = holder.getCurrentVersion();

            if (StringHelper.isEmpty(currentVersion)) {
                currentVersion = "";
            }

            rst = adminJdbcTemplate.update(INSERT_RIBBON_MAP, holder.getServiceid(), holder.getRouteid(),
                    currentVersion, holder.getAllVersions(), currentTime, strategy, holder.getContext());

            LOGGER.info(">>>>> DBRepository insert RouteRibbonHolder result: " + rst);

        }
        catch (DataAccessException e) {

            LOGGER.error(">>> DataAccessException:" + e.getCause());
            return false;

        }
        catch (Exception e) {

            LOGGER.error(">>> Exception:" + e.getCause());
            return false;
        }
        return true;

    }

    /**
     *
     * 更新蓝绿路由版本
     *
     */
    public boolean udpateRouteRibbon(String serviceid, String routeId, String version) throws Exception {

        try {

            String formatupdate = StringHelper.format(UPDATE_RIBBON_MAP, version, serviceid, routeId);

            adminJdbcTemplate.update(formatupdate);

            LOGGER.info("udpateRouteRibbon updateRoute SQL: " + formatupdate);

        }
        catch (Exception e) {

            LOGGER.error(">>> Exception:" + e.getCause());

            return false;
        }
        return true;

    }

    /**
     *
     * 更新金丝雀配置
     *
     */
    public boolean udpateCanaryRibbon(String serviceid, String routeId, String context, String stretgy)
            throws Exception {

        try {

            String formatupdate = StringHelper.format(UPDATE_CANARY_RIBBON_MAP, context, serviceid, stretgy, routeId);

            adminJdbcTemplate.update(formatupdate);

            LOGGER.info("udpateCanaryRibbon updateRoute SQL: " + formatupdate);

        }
        catch (Exception e) {

            LOGGER.error(">>> Exception:" + e.getCause());

            return false;
        }
        return true;

    }

    /****
     *
     *
     * 限流
     *
     */
    public String queryroutelimit(String routeIds) throws Exception {

        try {

            String querySQL = StringHelper.format(QUERYROUTERATELIMIT, routeIds);

            LOGGER.info("queryroutelimit SQL:" + querySQL);

            // RowMapper<String> rm =
            // BeanPropertyRowMapper.newInstance(String.class);

            String limit = adminJdbcTemplate.queryForObject(querySQL, String.class);

            return limit;

        }
        catch (EmptyResultDataAccessException e) {
            LOGGER.error(e.getMessage());
            return null;

        }
        catch (Exception e) {

            LOGGER.error(">>> Exception:" + e.getCause());

            return null;
        }

    }

    /****
     *
     *
     * 查询限流记录是否存在
     *
     */
    public boolean isExist(String routeIds) throws Exception {

        boolean flag = true;
        try {

            String querySQL = StringHelper.format(QUERYROUTERATECOUNT, routeIds);

            LOGGER.info("queryroutelimit SQL:" + querySQL);

            int count = adminJdbcTemplate.queryForObject(querySQL, Integer.class);
            if (count == 0) {
                flag = false;
            }
            return flag;
        }
        catch (Exception e) {
            LOGGER.error("查询限流记录是否存在", e);
            throw e;
        }
    }

    /**
     * 限流
     */

    public boolean addRouteRatelimit(String routeid, String ratelimit) throws Exception {

        LOGGER.info(">>>> addRouteRatelimit insert obj..");

        int rst = 0;

        try {

            rst = adminJdbcTemplate.update(INSERTROUTELIMIT, routeid, ratelimit);

            LOGGER.info(">>>>>addRouteRatelimit result: " + rst);

        }
        catch (DataAccessException e) {

            LOGGER.error(">>> DataAccessException:" + e.getCause());

            throw e;

        }
        catch (Exception e) {

            LOGGER.error(">>> Exception:" + e.getCause());

            if (e instanceof DuplicateKeyException) {
                throw e;
            }

            return false;
        }
        return true;

    }

    public boolean updateRouteLimit(String routeId, String limit) throws Exception {

        try {

            String formatupdate = StringHelper.format(UPDATEROUTELIMIT, limit, routeId);

            adminJdbcTemplate.update(formatupdate);

            LOGGER.info("udpateRouteLimit updateRoute SQL: " + formatupdate);

        }
        catch (Exception e) {

            LOGGER.error(">>> Exception:" + e.getCause());

            throw e;
        }
        return true;

    }

    /**
     * 删除Componet
     *
     * @return
     */
    public boolean deleteComponent(String compfilterName) throws Exception {

        try {

            String deleteSQL = StringHelper.format(DELETECOMPONENT, compfilterName);

            LOGGER.info("DBRepository delete comp obj deleteSQL: " + deleteSQL);

            int rst = adminJdbcTemplate.update(deleteSQL);

            LOGGER.info("DBRepository delete comp obj result: " + rst);

            return true;

        }
        catch (DataAccessException e) {

            LOGGER.error(">>> Exception:" + e.getCause());

            throw e;
        }
    }

    /**
     *
     * 更新COMP 描述
     *
     */
    public boolean udpateCompDesc(Map<String, Map<String, Object>> map) {

        try {

            for (String compfilterName : map.keySet()) {
                String desc = (String) map.get(compfilterName).get("compdesc");

                String formatupdate = StringHelper.format(UPDATE_COMPDESC, desc, compfilterName);

                LOGGER.info("udpateCompDesc updateRoute SQL: {}" + formatupdate);

                adminJdbcTemplate.update(formatupdate);

            }

        }
        catch (Exception e) {

            e.printStackTrace();
            LOGGER.error(">>> Exception:" + e);

            return false;
        }
        return true;

    }
}
