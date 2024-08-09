package com.zyy.gateway.session.defaults;

import com.zyy.gateway.bind.IGenericReference;
import com.zyy.gateway.datasource.Connection;
import com.zyy.gateway.datasource.DataSource;
import com.zyy.gateway.mapping.HttpStatement;
import com.zyy.gateway.session.Configuration;
import com.zyy.gateway.session.GatewaySession;
import com.zyy.gateway.type.SimpleTypeRegistry;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.Map;

/**
 * 默认网关会话实现
 */
public class DefaultGatewaySession implements GatewaySession {

	private Configuration configuration;
	private String uri;
	private DataSource dataSource;

	public DefaultGatewaySession(Configuration configuration, String uri, DataSource dataSource) {
		this.configuration = configuration;
		this.uri = uri;
		this.dataSource = dataSource;
	}

	@Override
	public Object get(String methodName, Map<String, Object> params) {
		Connection connection = dataSource.getConnection();
		HttpStatement httpStatement = configuration.getHttpStatement(uri);

		String parameterType = httpStatement.getParameterType();//获取RPC参数类型

		/**
		 * 调用服务
		 */

		return connection.execute(methodName,
					new String[]{parameterType},
					new String[]{"ignore"},
					//是否为简单类型
					SimpleTypeRegistry.isSimpleType(parameterType) ? params.values().toArray() : new Object[]{params});
	}

	@Override
	public Object post(String methodName, Map<String, Object> params) {
		return get(methodName, params);
	}

	@Override
	public IGenericReference getMapper() {
		return configuration.getMapper(uri, this);
	}

	@Override
	public Configuration getConfiguration() {
		return configuration;
	}
}
