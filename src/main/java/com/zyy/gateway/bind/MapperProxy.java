package com.zyy.gateway.bind;


import com.zyy.gateway.session.GatewaySession;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.dubbo.rpc.service.GenericService;

import java.lang.reflect.Method;

/**
 * 映射代理调用：=完成代理部分，并调用映射器方法完成逻辑处理
 */
public class MapperProxy implements MethodInterceptor {

	private GatewaySession gatewaySession;

	private final String uri;

	public MapperProxy(GatewaySession gatewaySession, String uri) {
		this.gatewaySession = gatewaySession;
		this.uri = uri;
	}

	@Override
	public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		MapperMethod linkMethod = new MapperMethod(uri, method, gatewaySession.getConfiguration());
		return linkMethod.execute(gatewaySession, args);
	}
}
