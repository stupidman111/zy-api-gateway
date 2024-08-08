package com.zyy.gateway.bind;


import com.zyy.gateway.mapping.HttpStatement;
import com.zyy.gateway.session.GatewaySession;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InterfaceMaker;
import org.apache.dubbo.rpc.service.GenericService;
import org.objectweb.asm.Type;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapperProxyFactory {

	private String uri;

	public MapperProxyFactory(String uri) {
		this.uri = uri;
	}

	private final Map<String, IGenericReference> genericReferenceCache = new ConcurrentHashMap<>();

	/**
	 * 通过这种方式，可以在运行时动态生成一个实现了特定接口的代理对象，并且在调用接口方法时通过 MapperProxy 来处理具体的调用逻辑。这在某些需要动态代理和泛化调用的场景中非常有用。
	 * @param gatewaySession
	 * @return
	 */
	public IGenericReference newInstance(GatewaySession gatewaySession) {
		return genericReferenceCache.computeIfAbsent(uri, k -> {

			HttpStatement httpStatement = gatewaySession.getConfiguration().getHttpStatement(uri);

			//泛化调用
			MapperProxy genericReferenceProxy = new MapperProxy(gatewaySession, uri);

			//创建接口
			InterfaceMaker interfaceMaker = new InterfaceMaker();
			interfaceMaker.add(new Signature(httpStatement.getMethodName(),
					Type.getType(String.class),
					new Type[]{Type.getType(String.class)}), null);
			Class<?> interfaceClass = interfaceMaker.create();

			//代理对象
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(Object.class);

			enhancer.setInterfaces(new Class[]{IGenericReference.class, interfaceClass});
			enhancer.setCallback(genericReferenceProxy);

			return (IGenericReference) enhancer.create();
		});
	}
}
