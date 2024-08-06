package com.zyy.gateway.test;

import com.zyy.gateway.session.Configuration;
import com.zyy.gateway.session.GenericReferenceSessionFactoryBuilder;
import com.zyy.gateway.session.SessionServer;
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
	public void test() throws ExecutionException, InterruptedException {
		Configuration configuration = new Configuration();
		configuration.addGenericReference("api-gateway-test", "cn.bugstack.gateway.rpc.IActivityBooth", "sayHi");

		GenericReferenceSessionFactoryBuilder genericReferenceSessionFactoryBuilder = new GenericReferenceSessionFactoryBuilder();
		Future<Channel> future = genericReferenceSessionFactoryBuilder.build(configuration);

		logger.info("服务启动完成 {}", future.get().id());

		Thread.sleep(Long.MAX_VALUE);
	}
}
