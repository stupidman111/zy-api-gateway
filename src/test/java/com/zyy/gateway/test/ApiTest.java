package com.zyy.gateway.test;

import com.zyy.gateway.mapping.HttpCommandType;
import com.zyy.gateway.mapping.HttpStatement;
import com.zyy.gateway.session.Configuration;
import com.zyy.gateway.session.defaults.DefaultGatewaySessionFactory;
import com.zyy.gateway.socket.GatewaySocketServer;
import io.netty.channel.Channel;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class ApiTest {

	private final Logger logger = LoggerFactory.getLogger(ApiTest.class);

	/**
	 * 测试： 访问"http://localhost:7397/sayHi" 会被网关拦截后调用对应的 RPC服务，封装结果到 HTTP响应中返回
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	@Test
	public void test_gateway() throws ExecutionException, InterruptedException {

		//1.创建配置项
		Configuration configuration = new Configuration();
		HttpStatement httpStatement = new HttpStatement(
				"api-gateway-test",
				"cn.bugstack.gateway.rpc.IActivityBooth",
				"sayHi",
				"/wg/activity/sayHi",
				HttpCommandType.GET);
		configuration.addMapper(httpStatement);

		//2.创建默认会话工厂
		DefaultGatewaySessionFactory defaultGatewaySessionFactory = new DefaultGatewaySessionFactory(configuration);

		//3.创建服务端实例
		GatewaySocketServer gatewaySocketServer = new GatewaySocketServer(defaultGatewaySessionFactory);
		Future<Channel> future = Executors.newFixedThreadPool(2).submit(gatewaySocketServer);
		Channel channel = future.get();

		if (null == channel) throw new RuntimeException("netty server start error channel is null");

		while (!channel.isActive()) {
			logger.info("netty server gateway start Ing ...");
			Thread.sleep(500);
		}
		logger.info("netty server gateway start Done! {}", channel.localAddress());

		Thread.sleep(Long.MAX_VALUE);

	}
}
