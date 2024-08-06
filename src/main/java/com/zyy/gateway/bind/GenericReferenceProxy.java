package com.zyy.gateway.bind;


import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.dubbo.rpc.service.GenericService;

import java.lang.reflect.Method;

/**
 * 泛化调用静态代理
 */
public class GenericReferenceProxy implements MethodInterceptor {

	/**
	 * 泛化调用服务
	 */
	private final GenericService genericService;

	/**
	 * 泛化调用方法
	 */
	private final String methodName;

	public GenericReferenceProxy(GenericService genericService, String methodName) {
		this.genericService = genericService;
		this.methodName = methodName;
	}

	@Override
	public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

		Class<?>[] parameterTypes = method.getParameterTypes();//参数类型列表
		String[] parameters = new String[parameterTypes.length];//

		for (int i = 0; i < parameterTypes.length; i++) {
			parameters[i] = parameterTypes[i].getName();
		}

		return genericService.$invoke(methodName, parameters, args);//方法名 + 参数类型限定名数组 + 参数
	}
}
