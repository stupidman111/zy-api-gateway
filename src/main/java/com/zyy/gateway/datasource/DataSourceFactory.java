package com.zyy.gateway.datasource;

import com.zyy.gateway.session.Configuration;

/**
 * 数据源工厂
 */
public interface DataSourceFactory {

	void setProperties(Configuration configuration, String uri);

	DataSource getDataSource();
}
