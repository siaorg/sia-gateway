

package com.creditease.gateway.helper;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
/***
 * 
 * @author admin
 *
 */
@Component
public class IpUtils {

    /**
     * 获取真实请求ip，防止反向代理
     * 
     * @param request
     * @return String
     * 
     */
    public static String getIpAddr(HttpServletRequest request) {

        String ip = request.getHeader("x-real-ip");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
            if (ip != null) {
                ip = ip.split(",")[0].trim();
            }
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
    
    public static boolean ipIsValid(String ipSection, String ip) { 
        if (ipSection == null) 
        {
        	throw new NullPointerException("IP段不能为空！"); 
        }
        if (ip == null) 
        {
        	throw new NullPointerException("IP不能为空！"); 
        }
        ipSection = ipSection.trim(); 
        ip = ip.trim(); 
        final String regIp = "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)"; 
        final String regIpb = regIp + "\\-" + regIp; 
        if (!ipSection.matches(regIpb) || !ip.matches(regIp)) 
        {
        	return false; 
        }
        int idx = ipSection.indexOf('-'); 
        String[] sips = ipSection.substring(0, idx).split("\\."); 
        String[] sipe = ipSection.substring(idx + 1).split("\\."); 
        String[] sipt = ip.split("\\."); 
        long ips = 0L, ipe = 0L, ipt = 0L; 
        for (int i = 0; i < 4; ++i) { 
            ips = ips << 8 | Integer.parseInt(sips[i]); 
            ipe = ipe << 8 | Integer.parseInt(sipe[i]); 
            ipt = ipt << 8 | Integer.parseInt(sipt[i]); 
        } 
        if (ips > ipe) { 
            long t = ips; 
            ips = ipe; 
            ipe = t; 
        } 
        return ips <= ipt && ipt <= ipe; 
    }
}