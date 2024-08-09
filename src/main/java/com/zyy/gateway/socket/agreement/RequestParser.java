package com.zyy.gateway.socket.agreement;

import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 请求解析器，解析 HTTP 请求，GET/POST from-data/raw-json
 */
public class RequestParser {

	private final FullHttpRequest request;

	public RequestParser(FullHttpRequest request) {
		this.request = request;
	}

	public Map<String, Object> parse() {
		//获取Content-Type
		String contentType = getContentType();
		//获取请求类型
		HttpMethod method = request.method();

		if (HttpMethod.GET.equals(method)) {//请求URI中的查询参数解析成一个Map<String, Object>对象（eg：URI=/search?query=java&sort=asc，那么最后 map中为{query, java}、{sort, asc}
			Map<String, Object> parameterMap = new HashMap<>();
			QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
			decoder.parameters().forEach((key, value) -> parameterMap.put(key, value.get(0)));
			return parameterMap;
		} else if (HttpMethod.POST.equals(method)) {
			switch (contentType) {
				case "multipart/form-data":	//表单
					Map<String, Object> parameterMap = new HashMap<>();
					HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
					decoder.offer(request);
					decoder.getBodyHttpDatas().forEach(data -> {
						Attribute attr = (Attribute) data;
						try {
							parameterMap.put(data.getName(), attr.getValue());
						} catch (IOException ignore) {
						}
					});
					return parameterMap;
				case "application/json": //json
					ByteBuf byteBuf = request.content().copy();
					if (byteBuf.isReadable()) {
						String content = byteBuf.toString(StandardCharsets.UTF_8);
						return JSON.parseObject(content);
					}
					break;
				default:
					throw new RuntimeException("未实现的协议类型 Content-Type: " + contentType);
			}
		}
		throw new RuntimeException("未实现的请求类型 HttpMethod: " + method);
	}

	/**
	 * 获取 MIME 类型，如果有字符集信息则省略掉
	 * @return
	 */
	private String getContentType() {
		Optional<Map.Entry<String, String>> header = request.headers().entries().stream().filter(
				val -> val.getKey().equals("Content-Type")
		).findAny();

		Map.Entry<String, String> entry = header.orElse(null);
		assert entry != null;

		//application/json、multipart/form-data
		String contentType = entry.getValue();
		int idx = contentType.indexOf(";");
		if (idx > 0) {
			return contentType.substring(0, idx);
		} else {
			return contentType;
		}
	}
}
