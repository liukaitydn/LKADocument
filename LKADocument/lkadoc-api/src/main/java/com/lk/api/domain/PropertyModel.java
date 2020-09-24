package com.lk.api.domain;

public class PropertyModel {
	private ModelModel modelModel;
	private String dataType;
	private String description;
	private String name;
	private boolean required = true;
	private String paramType;
	private boolean array = false;
	private String value;
	private String testData;
	public String getTestData() {
		return testData;
	}
	public void setTestData(String testData) {
		this.testData = testData;
	}
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
	public boolean getRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public String getParamType() {
		return paramType;
	}
	public void setParamType(String paramType) {
		this.paramType = paramType;
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
}
