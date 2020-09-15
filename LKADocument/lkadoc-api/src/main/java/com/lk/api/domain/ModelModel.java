package com.lk.api.domain;

import java.util.ArrayList;
import java.util.List;

public class ModelModel {
	private String name;
	private String description;
	private List<PropertyModel> propertyModels = new ArrayList<PropertyModel>();
	private String value;
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
	public List<PropertyModel> getPropertyModels() {
		return propertyModels;
	}
	public void setPropertyModels(List<PropertyModel> propertyModels) {
		this.propertyModels = propertyModels;
	}
}
