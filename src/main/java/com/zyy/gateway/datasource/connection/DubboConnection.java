package com.zyy.gateway.datasource.connection;

import com.zyy.gateway.datasource.Connection;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;

/**
 * Dubbo RPC Connection
 */
public class DubboConnection implements Connection {

	private final GenericService genericService;

	public DubboConnection(ApplicationConfig applicationConfig, RegistryConfig registryConfig, ReferenceConfig<GenericService> reference) {
		//连接远程服务
		DubboBootstrap bootstrap = DubboBootstrap.getInstance();
		bootstrap.application(applicationConfig)
				.registry(registryConfig)
				.reference(reference)
				.start();
		//获取泛化调用接口
		ReferenceConfigCache cache = ReferenceConfigCache.getCache();
		genericService = cache.get(reference);
	}

	@Override
	public Object execute(String method, String[] parameterTypes, String[] parameterNames, Object[] args) {
		return genericService.$invoke(method, parameterTypes, args);
	}
}
