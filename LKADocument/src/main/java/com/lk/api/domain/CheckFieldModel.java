package com.lk.api.domain;

public class CheckFieldModel {
	private String url;
	private String fieldName;
	private String[] valids;
	private String paramType;
	private String[] msgs;
	private Class<?> cls;
	private String range;
	private String size;
	private String length;
	
	public String getRange() {
		return range;
	}
	public void setRange(String range) {
		this.range = range;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getParamType() {
		return paramType;
	}
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	public String[] getValids() {
		return valids;
	}
	public void setValids(String[] valids) {
		this.valids = valids;
	}
	public String[] getMsgs() {
		return msgs;
	}
	public void setMsgs(String[] msgs) {
		this.msgs = msgs;
	}
	public Class<?> getCls() {
		return cls;
	}
	public void setCls(Class<?> cls) {
		this.cls = cls;
	}
}
