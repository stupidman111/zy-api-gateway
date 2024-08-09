package com.zyy.gateway.session.defaults;

import com.zyy.gateway.datasource.DataSource;
import com.zyy.gateway.datasource.unpooled.UnpooledDataSourceFactory;
import com.zyy.gateway.session.Configuration;
import com.zyy.gateway.session.GatewaySession;
import com.zyy.gateway.session.GatewaySessionFactory;

/**
 * 泛化调用会话工厂
 */
public class DefaultGatewaySessionFactory implements GatewaySessionFactory {

	private final Configuration configuration;

	public DefaultGatewaySessionFactory(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public GatewaySession openSession(String uri) {
		UnpooledDataSourceFactory dataSourceFactory = new UnpooledDataSourceFactory();
		dataSourceFactory.setProperties(configuration, uri);//默认设置 Dubbo 数据源，这里后续应该会使用一种 类似 switch 的方式根据请求信息选择数据源
		DataSource dataSource = dataSourceFactory.getDataSource();

		return new DefaultGatewaySession(configuration, uri, dataSource);
	}
}
