package com.zyy.gateway.session;

import com.zyy.gateway.bind.MapperRegistry;
import com.zyy.gateway.bind.IGenericReference;
import com.zyy.gateway.mapping.HttpStatement;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置项（贯穿整个会话周期）
 */
public class Configuration {

	private final MapperRegistry mapperRegistry = new MapperRegistry(this);

	private final Map<String, HttpStatement> httpStatements = new HashMap<>();

	private final Map<String, ApplicationConfig> applicationConfigMap = new HashMap<>();//RPC应用服务配置项（名称到应用程序的映射）

	private final Map<String, RegistryConfig> registryConfigMap = new HashMap<>();//RPC注册中心配置项目（名称到其注册中心的映射）

	private final Map<String, ReferenceConfig<GenericService>> referenceConfigMap = new HashMap<>();//RPC泛化服务配置项（名称到服务引用<泛化调用>的映射）

	public Configuration() {

		// TODO 后期需要从配置文件中获取

		ApplicationConfig application = new ApplicationConfig();
		application.setName("api-gateway-test");
		application.setQosEnable(false);

		RegistryConfig registry = new RegistryConfig();
		registry.setAddress("zookeeper://127.0.0.1:2181");
		registry.setRegister(false);

		ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
		reference.setInterface("cn.bugstack.gateway.rpc.IActivityBooth");
		reference.setVersion("1.0.0");
		reference.setGeneric("true");

		applicationConfigMap.put("api-gateway-test", application);
		registryConfigMap.put("api-gateway-test", registry);
		referenceConfigMap.put("cn.bugstack.gateway.rpc.IActivityBooth", reference);
	}

	//
	public ApplicationConfig getApplictionConfig(String applicationName) {
		return applicationConfigMap.get(applicationName);
	}

	public RegistryConfig getRegistryConfig(String applicationName) {
		return registryConfigMap.get(applicationName);
	}

	public ReferenceConfig<GenericService> getReferenceConfig(String interfaceName) {
		return referenceConfigMap.get(interfaceName);
	}

	public void addMapper(HttpStatement httpStatement) {
		mapperRegistry.addMapper(httpStatement);
	}

	public IGenericReference getMapper(String uri, GatewaySession gatewaySession) {
		return mapperRegistry.getMapper(uri, gatewaySession);
	}

	public void addHttpStatement(HttpStatement httpStatement) {
		httpStatements.put(httpStatement.getUri(), httpStatement);
	}

	public HttpStatement getHttpStatement(String uri) {
		return httpStatements.get(uri);
	}
}
