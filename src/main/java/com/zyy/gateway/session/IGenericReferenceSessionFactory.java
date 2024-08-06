package com.zyy.gateway.session;


import io.netty.channel.Channel;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 泛化调用会话工厂接口
 */
public interface IGenericReferenceSessionFactory {

	Future<Channel> openSession() throws ExecutionException, InterruptedException;
}
