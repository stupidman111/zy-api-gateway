package com.zyy.gateway.session;


import io.netty.channel.Channel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 网关会话工厂接口
 */
public interface GatewaySessionFactory {

	GatewaySession openSession(String uri);
}
