package com.zyy.gateway.session;

import com.zyy.gateway.bind.IGenericReference;

/**
 * 网关会话接口：用户处理网关 HTTP 请求
 */
public interface GatewaySession {

	Object get(String uri, Object parameter);

	IGenericReference getMapper(String uri);

	Configuration getConfiguration();
}
