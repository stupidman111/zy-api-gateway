package com.zyy.gateway.test;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;
import org.junit.Test;

public class DubboRPCTest {

	@Test
	public void test_rpc() {
		ApplicationConfig application = new ApplicationConfig();
		application.setName("api-gateway-test");
		application.setQosEnable(false);

		RegistryConfig registry = new RegistryConfig();
		registry.setAddress("zookeeper://10.105.10.202:2181");
		registry.setRegister(false);

		ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
		reference.setInterface("cn.bugstack.gateway.rpc.IActivityBooth");
		reference.setVersion("1.0.0");
		reference.setGeneric("true");

		DubboBootstrap bootstrap = DubboBootstrap.getInstance();
		bootstrap.application(application)
				.registry(registry)
				.reference(reference)
				.start();

		ReferenceConfigCache cache = ReferenceConfigCache.getCache();
		GenericService genericService = cache.get(reference);

		Object result = genericService.$invoke("sayHi", new String[]{"java.lang.String"}, new Object[]{"world"});

		System.out.println(result);
	}
}
