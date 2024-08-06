package com.zyy.gateway.bind;


import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InterfaceMaker;
import org.apache.dubbo.rpc.service.GenericService;
import org.objectweb.asm.Type;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GenericReferenceProxyFactory {

	/**
	 * 泛化调用服务
	 */
	private final GenericService genericService;

	/**
	 * 名称到统一泛化调用接口的映射
	 */
	private final Map<String, IGenericReference> genericReferenceCache = new ConcurrentHashMap<>();

	public GenericReferenceProxyFactory(GenericService genericService) {
		this.genericService = genericService;
	}

	/**
	 * 通过这种方式，可以在运行时动态生成一个实现了特定接口的代理对象，并且在调用接口方法时通过 GenericReferenceProxy 来处理具体的调用逻辑。这在某些需要动态代理和泛化调用的场景中非常有用。
	 * @param method
	 * @return
	 */
	public IGenericReference newInstance(String method) {
		return genericReferenceCache.computeIfAbsent(method, k -> {

			GenericReferenceProxy genericReferenceProxy = new GenericReferenceProxy(genericService, method);

			//InterfaceMaker 是 CGLIB 提供的工具类，用于动态生成接口。这里，通过 add 方法添加了一个方法签名，
			InterfaceMaker interfaceMaker = new InterfaceMaker();
			interfaceMaker.add(new Signature(method, Type.getType(String.class), new Type[]{Type.getType(String.class)}), null);

			//调用 create 方法生成接口类。这个接口类包含了上一步添加的方法。
			Class<?> interfaceClass = interfaceMaker.create();

			//Enhancer 是 CGLIB 的核心类，用于生成动态代理类。这里设置了代理类的超类为 Object。
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(Object.class);

			//设置代理类实现的接口。这里代理类实现了两个接口：IGenericReference 和动态生成的 interfaceClass。
			enhancer.setInterfaces(new Class[]{IGenericReference.class, interfaceClass});

			//设置回调，即方法调用的拦截器
			enhancer.setCallback(genericReferenceProxy);

			return (IGenericReference) enhancer.create();
		});
	}
}
