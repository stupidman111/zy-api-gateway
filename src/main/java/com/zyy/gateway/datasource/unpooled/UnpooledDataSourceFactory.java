package com.zyy.gateway.datasource.unpooled;

import com.zyy.gateway.datasource.DataSource;
import com.zyy.gateway.datasource.DataSourceFactory;
import com.zyy.gateway.datasource.DataSourceType;
import com.zyy.gateway.session.Configuration;

/**
 * 无池化的连接池工厂
 */
public class UnpooledDataSourceFactory implements DataSourceFactory {

	protected UnpooledDataSource dataSource;

	public UnpooledDataSourceFactory() {
		this.dataSource = new UnpooledDataSource();
	}


	@Override
	public void setProperties(Configuration configuration, String uri) {
		this.dataSource.setConfiguration(configuration);
		this.dataSource.setDataSourceType(DataSourceType.Dubbo);//这里后续应该会使用一种 类似 switch 的方式根据请求信息选择数据源
		this.dataSource.setHttpStatement(configuration.getHttpStatement(uri));
	}

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}
}
