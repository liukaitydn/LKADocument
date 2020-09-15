package com.lk.api.domain;

public class ResposeModel {
	private ModelModel modelModel;
	private String dataType;
	private String description;
	private String name;
	private String version;
	private boolean array;
	private String parentName;
	private String value;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public ModelModel getModelModel() {
		return modelModel;
	}
	public void setModelModel(ModelModel modelModel) {
		this.modelModel = modelModel;
	}
	public boolean getArray() {
		return array;
	}
	public void setArray(boolean array) {
		this.array = array;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
}
