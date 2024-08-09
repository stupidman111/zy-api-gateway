package com.zyy.gateway.bind;


import java.util.Map;

/**
 * 统一泛化调用接口
 */
public interface IGenericReference {

	String $invoke(Map<String, Object> params);
}
