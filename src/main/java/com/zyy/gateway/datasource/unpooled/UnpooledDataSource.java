package com.zyy.gateway.datasource.unpooled;

import com.zyy.gateway.datasource.Connection;
import com.zyy.gateway.datasource.DataSource;
import com.zyy.gateway.datasource.DataSourceType;
import com.zyy.gateway.datasource.connection.DubboConnection;
import com.zyy.gateway.mapping.HttpStatement;
import com.zyy.gateway.session.Configuration;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;

/**
 * 无池化的连接池
 */
public class UnpooledDataSource implements DataSource {

	private Configuration configuration;

	private HttpStatement httpStatement;

	private DataSourceType dataSourceType;

	@Override
	public Connection getConnection() {

		switch (dataSourceType) {
			case HTTP:
				//TODO
				break;
			case Dubbo:
				//配置信息
				String application = httpStatement.getApplication();
				String interfaceName = httpStatement.getInterfaceName();
				//获取服务
				ApplicationConfig applicationConfig = configuration.getApplictionConfig(application);
				RegistryConfig registryConfig = configuration.getRegistryConfig(application);
				ReferenceConfig<GenericService> referenceConfig = configuration.getReferenceConfig(interfaceName);

				return new DubboConnection(applicationConfig, registryConfig, referenceConfig);

			default:
				break;
		}

		throw new RuntimeException("DataSourceType: " + dataSourceType + "没有对应的数据源");
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void setHttpStatement(HttpStatement httpStatement) {
		this.httpStatement = httpStatement;
	}

	public void setDataSourceType(DataSourceType dataSourceType) {
		this.dataSourceType = dataSourceType;
	}
}
