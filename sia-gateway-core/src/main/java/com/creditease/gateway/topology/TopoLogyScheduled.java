package com.creditease.gateway.topology;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.creditease.gateway.database.BaseRepository;
import com.creditease.gateway.discovery.DiscoveryService;
import com.creditease.gateway.domain.EurekaInfo;
import com.creditease.gateway.domain.topo.Link;
import com.creditease.gateway.domain.topo.RouteTopo;

/**
 * 拓扑数据定时清理任务
 * 
 * 
 * @author peihua
 * 
 */

@Component
public class TopoLogyScheduled {

    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Value("${eureka.client.serviceUrl.defaultZone}")
    private String defaultEurekaUrl;

    @Value("${spring.application.name}")
    private String groupName;

    @Autowired
    private TopologyManager tmg;

    @Autowired
    private BaseRepository baseRepository;

    @Autowired
    private DiscoveryService discovery;

    private long oneDayBefore;

    private static final Logger LOGGER = LoggerFactory.getLogger(TopoLogyScheduled.class);

    @Scheduled(cron = "0 0/60 * * * *")
    public void clearTopoTask() {

        LOGGER.info(">开始拓扑清理 ");

        try {

            // 得到一天前时间
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -1);
            Date start = c.getTime();
            oneDayBefore = start.getTime();

            Map<String, RouteTopo> topo = tmg.getRouteTopoMap();

            Map<String, List<String>> listMap = new HashMap<String, List<String>>();

            /** step1: 查找所有网关组下对应服务列表 **/

            List<String> listArray = new LinkedList<String>();

            EurekaInfo einfo = baseRepository.getEurekaInfo(groupName);

            if (null == einfo) {
                listArray = discovery.getServiceListByEurekaUrl(defaultEurekaUrl);
            }
            else {
                listArray = discovery.getServiceListByEurekaUrl(einfo.getEurekaUrls());
            }

            /** step2: 根据网关组名称查找对应Link下的Dest是否存在，不存在&超过1天 删除 */

            for (Object value : topo.values().toArray()) {
                RouteTopo topoValue = (RouteTopo) value;

                Set<String> upsteams = topoValue.getUpstreamNodes();

                Set<Link> links = topoValue.getLink();

                Iterator<Link> iterator = links.iterator();

                while (iterator.hasNext()) {

                    Link lk = iterator.next();
                    long lastTime = lk.getLastRequestTime();

                    if (oneDayBefore > lastTime) {
                        String dest = lk.getDest();

                        if (upsteams.contains(dest)) {
                            if (!listArray.contains(dest) && !isUrl(dest)) {

                                LOGGER.info(">TopoLogyClearTask remove dest:" + dest);

                                iterator.remove();
                                upsteams.remove(dest);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(">TopoLogyScheduledTask Exception:{}", e);
        }
    }

    public boolean isUrl(String dest) {

        if (dest.contains("http") || dest.contains("https")) {
            return true;
        }

        return false;
    }
}
