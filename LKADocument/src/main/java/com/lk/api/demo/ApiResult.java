package com.lk.api.demo;

import java.util.HashMap;
import java.util.Map;

import com.lk.api.annotation.LKAModel;
import com.lk.api.annotation.LKAProperty;

@LKAModel
public class ApiResult {
	@LKAProperty(value="响应状态",description="200-正常,其它-错误")
	private String code;
	@LKAProperty(value="响应消息")
	private String msg;
	@LKAProperty(value="响应数据")
	private Map<String,Object> result = new HashMap<>();
	
	private ApiResult() {}
	
	public static ApiResult put(String key,Object value) {
		ApiResult res = new ApiResult();
		res.getResult().put(key, value);
		return res;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Map<String, Object> getResult() {
		return result;
	}
	public void setResult(Map<String, Object> result) {
		this.result = result;
	}
}
