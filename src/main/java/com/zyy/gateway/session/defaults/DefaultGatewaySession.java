package com.zyy.gateway.session.defaults;

import com.zyy.gateway.bind.IGenericReference;
import com.zyy.gateway.mapping.HttpStatement;
import com.zyy.gateway.session.Configuration;
import com.zyy.gateway.session.GatewaySession;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;

/**
 * 默认网关会话实现
 */
public class DefaultGatewaySession implements GatewaySession {

	private Configuration configuration;

	public DefaultGatewaySession(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public Object get(String uri, Object parameter) {

		//配置信息
		HttpStatement httpStatement = configuration.getHttpStatement(uri);
		String application = httpStatement.getApplication();
		String interfaceName = httpStatement.getInterfaceName();

		//获取基础服务（创建成本高，内存存放获取）
		ApplicationConfig applictionConfig = configuration.getApplictionConfig(application);
		RegistryConfig registryConfig = configuration.getRegistryConfig(application);
		ReferenceConfig<GenericService> reference = configuration.getReferenceConfig(interfaceName);

		//构建 Dubbo（消费端）服务
		DubboBootstrap bootstrap = DubboBootstrap.getInstance();
		bootstrap.application(applictionConfig)
				.registry(registryConfig)
				.reference(reference)
				.start();
		//获取泛化调用服务
		ReferenceConfigCache cache = ReferenceConfigCache.getCache();
		GenericService genericService = cache.get(reference);

		return genericService.$invoke(httpStatement.getMethodName(),
				new String[]{"java.lang.String"},
				new Object[]{"zy"});
	}

	@Override
	public IGenericReference getMapper(String uri) {
		return configuration.getMapper(uri, this);
	}

	@Override
	public Configuration getConfiguration() {
		return configuration;
	}
}
