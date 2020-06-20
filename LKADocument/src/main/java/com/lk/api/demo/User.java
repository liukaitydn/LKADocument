package com.lk.api.demo;

import java.util.List;

import com.lk.api.annotation.LKAModel;
import com.lk.api.annotation.LKAProperty;

/**
 * 因为该接口只用到了User对象的name、age和addresses参数这三个参数，
 * 我们可以把这三个参数分为一组，组名不能够重复，其它是没有任何限制的，
 * 只需要是合法的字符串即可。为了区分，你可以用接口名称来命名。在这里演示
 * 我就用a,b,c,d的来命名吧,LKAProperty注解的groups就是用来设置
 * 组名的，如下：
 */
@LKAModel
public class User {
	@LKAProperty(value="用户ID",hidden=true)//hidden设置成true，该不会在UI界面展示
	private Integer id;
	@LKAProperty(value="用户名称",testData="张三",groups= {"a"})
	private String name;
	@LKAProperty(value="年龄",required=false,groups= {"a"},testData="22")
	private String age;
	@LKAProperty(type=Role.class,value="角色对象")
	private Role role;
	@LKAProperty(value="用户爱好",isArray=true,testData="运动")
	private String[] likes;
	@LKAProperty(type=Address.class,isArray=true,value="地址信息",groups= {"a"})
	private List<Address> addresses;
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
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public String[] getLikes() {
		return likes;
	}
	public void setLikes(String[] likes) {
		this.likes = likes;
	}
	public List<Address> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}
}
