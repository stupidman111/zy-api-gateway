package com.zyy.gateway.test;

import com.zyy.gateway.mapping.HttpCommandType;
import com.zyy.gateway.mapping.HttpStatement;
import com.zyy.gateway.session.Configuration;
import com.zyy.gateway.session.GatewaySession;
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

	@Test
	public void test_gateway() throws ExecutionException, InterruptedException {
		//1. 创建配置信息加载注册
		Configuration configuration = new Configuration();

		HttpStatement httpStatement01 = new HttpStatement(
				"api-gateway-test",
				"cn.bugstack.gateway.rpc.IActivityBooth",
				"sayHi",
				"java.lang.String",
				"/wg/activity/sayHi",
				HttpCommandType.GET);

		HttpStatement httpStatement02 = new HttpStatement(
				"api-gateway-test",
				"cn.bugstack.gateway.rpc.IActivityBooth",
				"insert",
				"cn.bugstack.gateway.rpc.dto.XReq",
				"/wg/activity/insert",
				HttpCommandType.POST);

		configuration.addMapper(httpStatement01);
		configuration.addMapper(httpStatement02);

		//2. 基于配置构建会话工厂
		DefaultGatewaySessionFactory gatewaySessionFactory = new DefaultGatewaySessionFactory(configuration);

		//3. 创建启动网关网络服务
		GatewaySocketServer server = new GatewaySocketServer(gatewaySessionFactory);

		Future<Channel> future = Executors.newFixedThreadPool(2).submit(server);
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
