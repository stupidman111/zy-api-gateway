package com.zyy.gateway.socket;

import com.zyy.gateway.session.defaults.DefaultGatewaySessionFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

/**
 * 网关会话服务：实现 Callable接口，后续提交给线程池
 */
public class GatewaySocketServer implements Callable<Channel> {
	private final Logger logger = LoggerFactory.getLogger(GatewaySocketServer.class);

	private DefaultGatewaySessionFactory gatewaySessionFactory;

	private final EventLoopGroup boss = new NioEventLoopGroup(1);
	private final EventLoopGroup worker = new NioEventLoopGroup();
	private Channel channel;

	public GatewaySocketServer(DefaultGatewaySessionFactory gatewaySessionFactory) {
		this.gatewaySessionFactory = gatewaySessionFactory;
	}

	@Override
	public Channel call() throws Exception {
		ChannelFuture channelFuture = null;

		try {
			ServerBootstrap b = new ServerBootstrap();//创建服务
			b.group(boss, worker)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 128)
					.childHandler(new GatewayChannelInitializer(gatewaySessionFactory));

			channelFuture = b.bind(new InetSocketAddress(7397)).syncUninterruptibly();
			this.channel = channelFuture.channel();
		} catch (Exception e) {
			logger.error("socker server start error.", e);
		} finally {
			if (null != channelFuture && channelFuture.isSuccess()) {
				logger.info("socker server start done.");
			} else {
				logger.error("socket server start error.");
			}
		}

		return channel;
	}
}
