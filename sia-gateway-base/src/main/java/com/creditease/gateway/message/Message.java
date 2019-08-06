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


package com.creditease.gateway.message;

import java.io.Serializable;
import java.util.Map;

/**
 * Admin统一消息格式
 * 
 * @author peihua
 * 
 * 
 */
public class Message implements Serializable {


	private static final long serialVersionUID = 8222018348042632177L;

	private int code ;

	private Object response = null;
	
	private Map<String,String> request = null;

	public Message()
	{
		code = ResponseCode.SUCCESS_CODE.getCode();
	}	
	
	public Message(Object res)
	{
		code = ResponseCode.SUCCESS_CODE.getCode();
		
		response = res;
	}
	
	public Message(Object res, int code )
	{
		this.code = code;
		
		response = res;
	}
	
	public Message(Map<String,String> req)
	{
		request = req;
	}
	
	public Map<String, String> getRequest() {
		return request;
	}
	
    public Message(String message, int code) {
    	
    	this.response = message;
        this.code = code;
    }
    
    public static Message fail(String message, int code) {

        return new Message(message, code);
    }

    public static  Message buildSuccessResult(Object resultData){
        return new Message(resultData,ResponseCode.SUCCESS_CODE.getCode());
    }

    public static  Message buildSuccessResult(){
        return new Message(null,ResponseCode.SUCCESS_CODE.getCode());
    }

    public static  Message buildInValidParamResult(){
        return new Message("参数不合法!",ResponseCode.PARAM_ERROR_CODE.getCode());
    }

    public static  Message buildExceptionResult(){
        return new Message(null,ResponseCode.SERVER_ERROR_CODE.getCode());
    }
    public static  Message buildExceptionResult(Object resultData){
        return new Message(resultData,ResponseCode.SERVER_ERROR_CODE.getCode());
    }

	public void setRequest(Map<String, String> request) {
		this.request = request;
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object content) {
		this.response = content;
	}
	
	public enum ResponseCode {
	    /** 正确 **/
	    SUCCESS_CODE(200),
	    /** 参数错误 **/
	    PARAM_ERROR_CODE(400),
	    /** 限制调用 **/
	    LIMIT_ERROR_CODE(401),
	    /** token 过期 **/
	    TOKEN_TIMEOUT_CODE(402),
	    /** 禁止访问 **/
	    NO_AUTH_CODE(403),
	    /** 资源没找到 **/
	    NOT_FOUND(404),
	    /** 服务器错误 **/
	    SERVER_ERROR_CODE(500),
	    /** 服务降级中 **/
	    DOWNGRADE(406),
	    /** 路由ID冲突 **/
	    ROUTEID_CONFLICT_CODE(407),
		/** 匹配路径冲突 **/
		PATH_CONFLICT_CODE(408);


	    private int code;

	    public void setCode(int code) {

	        this.code = code;
	    }

	    public int getCode() {

	        return code;
	    }

	    private ResponseCode(int code) {
	        this.code = code;
	    }
	}
}
