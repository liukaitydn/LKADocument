package com.lk.api.domain;

import java.util.ArrayList;
import java.util.List;

import com.lk.api.annotation.LKAProperty;

public class MethodModel {
	private String name;
	private String description;
	private List<ParamModel> request = new ArrayList<ParamModel>();
	private List<ResposeModel> respose = new ArrayList<ResposeModel>();
	private String value;
	private String requestType;
	private String url;
	private String version;
	private String contentType;
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<ParamModel> getRequest() {
		return request;
	}
	public void setRequest(List<ParamModel> request) {
		this.request = request;
	}
	public List<ResposeModel> getRespose() {
		return respose;
	}
	public void setRespose(List<ResposeModel> respose) {
		this.respose = respose;
	}
	
	
}
