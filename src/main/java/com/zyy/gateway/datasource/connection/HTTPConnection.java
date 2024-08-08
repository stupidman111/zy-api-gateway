package com.zyy.gateway.datasource.connection;

import com.zyy.gateway.datasource.Connection;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;

/**
 * HTTP Connection
 */
public class HTTPConnection implements Connection {

	private final HttpClient httpClient;
	private PostMethod postMethod;

	public HTTPConnection(String uri) {
		httpClient = new HttpClient();
		postMethod = new PostMethod();

		postMethod.addRequestHeader("accept", "*/*");
		postMethod.addRequestHeader("connection", "Keep-Alive");
		postMethod.addRequestHeader("Content-Type", "application/json;charset=GBK");
		postMethod.addRequestHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36");
	}

	@Override
	public Object execute(String method, String[] parameterTypes, String[] parameterNames, Object[] args) {
		String res = "";
		try {
			int code = httpClient.executeMethod(postMethod);
			if (code == 200) {
				res = postMethod.getResponseBodyAsString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
}
