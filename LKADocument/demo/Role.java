package com.lk.api.demo;

import com.lk.api.annotation.LKAModel;
import com.lk.api.annotation.LKAProperty;

@LKAModel
public class Role {
	@LKAProperty(value="角色ID",testData="1")
	private Integer id;
	@LKAProperty(value="角色名称",testData="经理")
	private String name;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
