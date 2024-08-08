package com.zyy.gateway.bind;

import com.zyy.gateway.mapping.HttpStatement;
import com.zyy.gateway.session.Configuration;
import com.zyy.gateway.session.GatewaySession;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.HashMap;
import java.util.Map;

/**
 * 泛化调用注册器
 */
public class MapperRegistry {

	private final Configuration configuration;

	public MapperRegistry(Configuration configuration) {
		this.configuration = configuration;
	}

	private final Map<String, MapperProxyFactory> knownMappers = new HashMap<>();

	public IGenericReference getMapper(String uri, GatewaySession gatewaySession) {
		final MapperProxyFactory mapperProxyFactory = knownMappers.get(uri);

		if (mapperProxyFactory == null) {
			throw new RuntimeException("Uri " + uri + " is not known to the MapperRegistry.");
		}
		try {
			return mapperProxyFactory.newInstance(gatewaySession);
		} catch (Exception e) {
			throw new RuntimeException("Error getting mapper instance. Cause: " + e, e);
		}
	}

	public void addMapper(HttpStatement httpStatement) {

		String uri = httpStatement.getUri();

		if (hasMapper(uri)) {//重复注册
			throw new RuntimeException("Uri " + uri + " is already known to the MapperRegistry.");
		}

		knownMappers.put(uri, new MapperProxyFactory(uri));

		//保存接口映射信息
		configuration.addHttpStatement(httpStatement);
	}

	public <T> boolean hasMapper(String uri) {
		return knownMappers.containsKey(uri);
	}

}
