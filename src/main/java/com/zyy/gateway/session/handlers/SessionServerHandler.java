package com.zyy.gateway.session.handlers;

import com.zyy.gateway.bind.IGenericReference;
import com.zyy.gateway.session.BaseHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zyy.gateway.session.Configuration;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionServerHandler extends BaseHandler<FullHttpRequest> {

	private final Logger logger = LoggerFactory.getLogger(SessionServerHandler.class);

	private final Configuration configuration;

	public SessionServerHandler(Configuration configuration) {
		this.configuration = configuration;
	}


	@Override
	protected void session(ChannelHandlerContext ctx, final Channel channel, FullHttpRequest request) {
		logger.info("网关接收请求 uri: {} method: {}", request.uri(), request.method());

		//对 icon 的请求直接返回
		String methodName = request.uri().substring(1);
		if ("favicon.ico".equals(methodName)) return;;

		//response
		DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

		//服务泛化调用
		IGenericReference reference = configuration.getGenericReference("sayHi");
		String result = reference.$invoke("test") + " " + System.currentTimeMillis();

		//设置回写内容
		response.content().writeBytes(JSON.toJSONBytes(result, SerializerFeature.PrettyFormat));

		//头部信息设置
		HttpHeaders heads = response.headers();
		heads.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + "; charset=UTF-8");//返回内容类型
		heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());//响应体长度
		heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);//设置长连接
		//配置跨域访问
		heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "*");
		heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE");
		heads.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");

		channel.writeAndFlush(response);
	}
}
