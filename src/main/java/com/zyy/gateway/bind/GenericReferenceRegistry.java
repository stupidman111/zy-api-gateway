package com.zyy.gateway.bind;

import com.zyy.gateway.session.Configuration;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.HashMap;
import java.util.Map;

/**
 * 泛化调用注册器
 */
public class GenericReferenceRegistry {

	private final Configuration configuration;

	public GenericReferenceRegistry(Configuration configuration) {
		this.configuration = configuration;
	}

	private final Map<String, GenericReferenceProxyFactory> knownGenericReference = new HashMap<>();

	public IGenericReference getGenericReference(String methodName) {
		GenericReferenceProxyFactory genericReferenceProxyFactory = knownGenericReference.get(methodName);

		if (genericReferenceProxyFactory == null) {
			throw new RuntimeException("Type" + methodName + " is not known to the GenericReferenceRegistry.");
		}
		return genericReferenceProxyFactory.newInstance(methodName);
	}

	public void addGenericReference(String application, String interfaceName, String methodName) {

		ApplicationConfig applictionConfig = configuration.getApplictionConfig(application);
		RegistryConfig registryConfig = configuration.getRegistryConfig(application);
		ReferenceConfig<GenericService> referenceConfig = configuration.getReferenceConfig(interfaceName);

		DubboBootstrap bootstrap = DubboBootstrap.getInstance()
				.application(applictionConfig)
				.registry(registryConfig)
				.reference(referenceConfig)
				.start();

		ReferenceConfigCache cache = ReferenceConfigCache.getCache();
		GenericService genericService = cache.get(referenceConfig);

		knownGenericReference.put(methodName, new GenericReferenceProxyFactory(genericService));
	}

}
