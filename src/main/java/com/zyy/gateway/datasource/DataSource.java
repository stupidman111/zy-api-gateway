package com.zyy.gateway.datasource;

/**
 * 数据源接口：RPC、HTTP 都当做连接的数据资源使用
 */
public interface DataSource {

	Connection getConnection();
}
