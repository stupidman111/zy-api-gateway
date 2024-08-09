package com.zyy.gateway.socket.handlers;

import com.zyy.gateway.bind.IGenericReference;
import com.zyy.gateway.session.GatewaySession;
import com.zyy.gateway.session.defaults.DefaultGatewaySessionFactory;
import com.zyy.gateway.socket.BaseHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zyy.gateway.socket.agreement.RequestParser;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 网关服务childHandler---根据http请求，获取泛化调用，返回结果
 */
public class GatewayServerHandler extends BaseHandler<FullHttpRequest> {

	private final Logger logger = LoggerFactory.getLogger(GatewayServerHandler.class);

	private final DefaultGatewaySessionFactory gatewaySessionFactory;

	public GatewayServerHandler(DefaultGatewaySessionFactory gatewaySessionFactory) {
		this.gatewaySessionFactory = gatewaySessionFactory;
	}


	@Override
	protected void session(ChannelHandlerContext ctx, final Channel channel, FullHttpRequest request) {
		logger.info("网关接收请求 uri: {} method: {}", request.uri(), request.method());


		//解析请求参数
		Map<String, Object> requestObj = new RequestParser(request).parse();

		//返回信息控制：简单处理
		String uri = request.uri();
		int idx = uri.indexOf("?");
		uri = idx > 0 ? uri.substring(0, idx) : uri;//将请求参数部分甩掉
		if ("favicon.ico".equals(uri)) return;;


		//开启网关会话（抽象），并调用映射获取泛化调用引用，执行泛化调用
		GatewaySession gatewaySession = gatewaySessionFactory.openSession(uri);
		IGenericReference reference = gatewaySession.getMapper();
		String result = reference.$invoke(requestObj) + " " + System.currentTimeMillis();

		// 返回信息处理
		DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

		// 设置回写数据
		response.content().writeBytes(JSON.toJSONBytes(result, SerializerFeature.PrettyFormat));

		// 头部信息设置
		HttpHeaders heads = response.headers();
		// 返回内容类型
		heads.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + "; charset=UTF-8");
		// 响应体的长度
		heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
		// 配置持久连接
		heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		// 配置跨域访问
		heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
		heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE");
		heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");

		channel.writeAndFlush(response);
	}
}
