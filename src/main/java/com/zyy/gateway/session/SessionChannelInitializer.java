package com.zyy.gateway.session;

import com.zyy.gateway.session.handlers.SessionServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;


public class SessionChannelInitializer extends ChannelInitializer<SocketChannel> {

	private final Configuration configuration;

	public SessionChannelInitializer(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast(new HttpRequestDecoder());//添加http请求编码器
		pipeline.addLast(new HttpResponseEncoder());//添加http请求解码器
		pipeline.addLast(new HttpObjectAggregator(1024 * 1024));//添加http对象聚合
		pipeline.addLast(new SessionServerHandler(configuration));//添加自定义服务 handler
	}
}
