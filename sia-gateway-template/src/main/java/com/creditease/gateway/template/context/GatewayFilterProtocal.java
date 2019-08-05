package com.creditease.gateway.template.context;

/**
 * 
 * 组件类型
 * 
 * @author peihua
 * 
 */

public class GatewayFilterProtocal {
	 
    public static enum GWFilterType {
    	
    	/**
    	 * ERROR_TYPE: 错误Filter类型
    	 * POST_TYPE： 后置Filter类型
    	 * PRE_TYPE： 前置Filter类型
    	 * ROUTE_TYPE：路由Filter类型
    	 * 
    	 * */
    	ERROR_TYPE("error"), POST_TYPE("post"), PRE_TYPE("pre"), ROUTE_TYPE("route");

        private String type;

        GWFilterType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {

            return type;
        }
    }

 
}
