package com.zyy.gateway.socket;

import com.zyy.gateway.session.Configuration;
import com.zyy.gateway.session.defaults.DefaultGatewaySessionFactory;
import com.zyy.gateway.socket.handlers.GatewayServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * 会话 Channel 初始化器
 */
public class GatewayChannelInitializer extends ChannelInitializer<SocketChannel> {

	private final DefaultGatewaySessionFactory gatewaySessionFactory;

	public GatewayChannelInitializer(DefaultGatewaySessionFactory gatewaySessionFactory) {
		this.gatewaySessionFactory = gatewaySessionFactory;
	}

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast(new HttpRequestDecoder());//添加http请求编码器
		pipeline.addLast(new HttpResponseEncoder());//添加http请求解码器
		pipeline.addLast(new HttpObjectAggregator(1024 * 1024));//添加http对象聚合
		pipeline.addLast(new GatewayServerHandler(gatewaySessionFactory));//添加自定义服务 handler
	}
}
