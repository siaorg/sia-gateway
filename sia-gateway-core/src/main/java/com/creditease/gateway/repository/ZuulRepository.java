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


package com.creditease.gateway.repository;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.creditease.gateway.constant.GatewayConstant;
import com.creditease.gateway.domain.CompInfo;
import com.creditease.gateway.excpetion.GatewayException;
import com.creditease.gateway.excpetion.GatewayException.ExceptionType;
import com.creditease.gateway.helper.StringHelper;
import com.creditease.gateway.hystrix.FallbackEvent;

/**
 * 用于zuul的状态及组件管理
 * 
 * @author peihua
 **/

@Repository
public class ZuulRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZuulRepository.class);

    private static final String INSERTZUULINFO = "INSERT INTO gateway_info (zuulInstanceId, zuulHotsName, zuulGroupName, zuulDesc, zuulRouteEnable, zuulLastStartTime,zuulStatus) values(?,?,?,?,?, ?,?)";

    private static final String UPDATEZUULINFO = "update gateway_info set zuulLastStartTime = ? ,zuulStatus=? , zuulGroupName = ?,zuulDesc = ? where zuulInstanceId =?";

    private static final String INSERTFILTERCOMP = "INSERT INTO gateway_component (compName, compFilterName, compType, compOrder, compUpdateTime, status,compdesc,zuulGroupName) values(?,?,?,?,?,?,?,?)";

    private static final String INSERTFALLBACK = "INSERT INTO gateway_fallback (zuulGroupName, zuulInstance, fallbackType, fallbackMsg, stackTrace, startTime) values(?,?,?,?,?,?)";

    private static final String QUERYCOMP = "select * from gateway_component where compFilterName='%s' and zuulGroupName = '%s' ";

    private static final String QUERYDESC = "select zuulDesc from gateway_info where  zuulGroupName = '%s' ";

    @Value("${eureka.instance.instance-id}")
    private String instance;

    @Value("${spring.application.name}")
    private String groupName;

    @Value("${zuul.desc}")
    private String zuulDesc;

    @Autowired
    @Qualifier(GatewayConstant.JDBCTEMPLATENAME)
    protected JdbcTemplate gatewayJdbcTemplate;

    private static final int MAXSIZE = 8000;

    /**
     * 插入ZUUL网关信息
     * 
     * @throws Exception
     * 
     */
    public boolean initZuulInfo() throws Exception {

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        Boolean enabled = true;

        try {

            String hostName = InetAddress.getLocalHost().getHostName();

            LOGGER.info(">insert instance" + instance + " host Name: " + hostName);

            // 描述从已有网关组中获得
            zuulDesc = getZuulDescByGroupName();

            gatewayJdbcTemplate.update(INSERTZUULINFO, instance, hostName, groupName, zuulDesc, enabled, currentTime,
                    GatewayConstant.ZuulState.RUNNING.toString());

            LOGGER.info(">insert zuul success!");

            return true;
        }
        catch (DuplicateKeyException e) {
            LOGGER.info("> DuplicateKeyException ... ");

            throw e;
        }
        catch (Exception e) {
            new GatewayException(ExceptionType.CoreException, e);
            throw e;
        }
    }

    /**
     * 更新ZUUL网关信息
     * 
     */
    public boolean updateZuulInfo(GatewayConstant.ZuulState status, String zuulGourpName) {

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        try {

            // 描述从已有网关组中获得
            zuulDesc = getZuulDescByGroupName();

            LOGGER.info("> update instance:" + instance);

            gatewayJdbcTemplate.update(UPDATEZUULINFO, currentTime, status.toString(), zuulGourpName, zuulDesc,
                    instance);

            LOGGER.info("> update zuul success, status is: " + status.toString());

            return true;

        }
        catch (Exception e) {

            LOGGER.error(e.getMessage());
            new GatewayException(ExceptionType.CoreException, e);
            return false;
        }

    }

    /**
     * 插入组件信息
     * 
     */
    public boolean insertFilterComp(CompInfo info) {

        try {

            LOGGER.info("> insert instance:" + instance + " 组件:{}" , info.getCompFilterName());

            gatewayJdbcTemplate.update(INSERTFILTERCOMP, info.getCompName(), info.getCompFilterName(),
                    info.getCompType(), info.getCompOrder(), info.getCompUpdateTime(), info.getStatus(),
                    info.getCompdesc(), groupName);

            LOGGER.info(">插入組件成功！  insertFilterComp success， 組件名：{}" , info.getCompName());

            return true;

        }
        catch (Exception e) {
            LOGGER.error(">插入組件失敗！  insertFilterComp faile 組件名：{}", info.getCompName());
            new GatewayException(ExceptionType.CoreException, e);
            LOGGER.error(e.getMessage());

            return false;
        }
    }

    /**
     * 查询组件信息
     * 
     */
    public CompInfo queryComp(String compFilterName) {

        try {

            String querySQL = StringHelper.format(QUERYCOMP, compFilterName, groupName);

            LOGGER.info("> CompDBRepository SQL:" + querySQL);

            RowMapper<CompInfo> rm = BeanPropertyRowMapper.newInstance(CompInfo.class);

            CompInfo info = gatewayJdbcTemplate.queryForObject(querySQL, rm);

            return info;

        }
        catch (EmptyResultDataAccessException e) {
            return null;

        }
        catch (Exception e) {
            LOGGER.error("> Exception:" + e.getCause());
            new GatewayException(ExceptionType.CoreException, e);
        }
        return null;
    }

    private String getZuulDescByGroupName() {

        String zuulDescription = null;

        String querydesc = StringHelper.format(QUERYDESC, groupName);

        List<String> results = gatewayJdbcTemplate.queryForList(querydesc, String.class);

        if (results == null || results.isEmpty()) {
            LOGGER.info(">zuulGroup results is empty...");

        }
        else {
            for (String name : results) {
                if (zuulDesc.equals(name)) {
                    continue;
                }
                else {
                    zuulDesc = name;

                    break;
                }
            }
        }

        zuulDescription = zuulDesc;

        return zuulDescription;
    }

    /**
     * 插入Fallback
     * 
     */
    public boolean insertFallback(FallbackEvent event) {

        try {

            String stacktrace = event.getStackTrace();

            int size = stacktrace.length();

            if (size > MAXSIZE) {
                stacktrace = stacktrace.substring(0, MAXSIZE);
            }
            String fallBackMsg = event.getFallbackMsg();
            fallBackMsg = null == fallBackMsg ? "" : fallBackMsg;

            gatewayJdbcTemplate.update(INSERTFALLBACK, event.getZuulGroupName(), event.getInstanceId(),
                    event.getFallbackType(), fallBackMsg, stacktrace, event.getStartTime());

            LOGGER.info("插入异常成功！  insertFilterComp success， 組件名：{}", event.getZuulGroupName());

            return true;

        }
        catch (Exception e) {
            e.printStackTrace();

            new GatewayException(ExceptionType.CoreException, e);
            return false;
        }
    }

}
