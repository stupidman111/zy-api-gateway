package com.zyy.gateway.session;

import com.zyy.gateway.bind.IGenericReference;

import java.util.Map;

/**
 * 网关会话接口：用户处理网关 HTTP 请求
 */
public interface GatewaySession {

	Object get(String methodName, Map<String, Object> params);

	Object post(String methodName, Map<String, Object> params);

	IGenericReference getMapper();

	Configuration getConfiguration();
}
