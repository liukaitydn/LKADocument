package com.lk.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lk.api.annotation.ContentType;
import com.lk.api.annotation.LKAMethod;
import com.lk.api.annotation.LKAModel;
import com.lk.api.annotation.LKAParam;
import com.lk.api.annotation.LKAProperty;
import com.lk.api.annotation.LKARespose;
import com.lk.api.annotation.LKAResposes;
import com.lk.api.annotation.LKAType;
import com.lk.api.annotation.Lkad;
import com.lk.api.annotation.ParamType;

@LKAType("测试类")
@RestController
public class LKADemoController {
	
	/**
     *	说明：入参属性名后面加上'-n'代表不是必传字段,例如下面"id-n"那么代表入参id不是必须的
	 */
	@LKAMethod(value="测试方法1",contentType=ContentType.JSON,author="刘凯",createTime="2020-6-5",updateTime="2020-6-5")
	@LKAParam(names={"id-n","name","age"},values={"用户ID","用户姓名","用户年龄"},dataTypes={"Integer","String","Integer"},paramTypes= {ParamType.QUERY})
	@LKAResposes({
		@LKARespose(names= {"code","msg"},values= {"状态码","消息"}),
		@LKARespose(type=User.class,parentName="result"),
	})
	@GetMapping("test1")
	public Map<String,Object> test1(Integer id,String name,Integer age) {
		Map<String,Object> map = new HashMap<>();
		User user = new User();
		user.setId(id);
		user.setAge(age);
		user.setName(name);
		map.put("code",200);
		map.put("msg","操作成功");
		map.put("result",user);
		return map;
	}
	
	/**
	 * 说明：如果aaa和bbb参数的父级节点是一样的，可以直接使用parentNames={"result"}或parentName="result"来指定
	 * */
	@LKAMethod("测试方法测试方法测试方法测试方法测试方法测试方法2")
	@LKAParam(names={"id-n","name","age"},values={"用户ID","用户姓名","用户年龄"},dataTypes={"Integer","String","Integer"})
	@LKAResposes({
		@LKARespose(names= {"aaa","bbb"},values= {"data1","data2"},parentNames= {"result","result"}),
		@LKARespose(type=User.class,parentName="user",grandpaName="result"),
	})
	@PostMapping("test2")
	public ApiResult test2(Integer id,String name,Integer age) {
		ApiResult res = new ApiResult();
		User user = new User();
		user.setId(id);
		user.setAge(age);
		user.setName(name);
		
		res.setCode(200);
		res.setMessage("操作成功");
		res.put("user",user);
		res.put("aaa","aaa");
		res.put("bbb","bbb");
		return res;
	}
	
	@LKAModel
	class User{
		@LKAProperty(value="主键ID")
		private Integer id;
		@LKAProperty(value="年龄")
		private Integer age;
		@LKAProperty(value="姓名")
		private String name;
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public Integer getAge() {
			return age;
		}
		public void setAge(Integer age) {
			this.age = age;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	@LKAModel
	class ApiResult{
		@LKAProperty(value = "状态码")
	    private int code;
		@LKAProperty(value = "返回消息")
	    private String message;
		@LKAProperty(value = "返回数据")
	    private Map<String,Object> result = new HashMap<>();
		
		public void put(String key,Object value){
	        this.result.put(key,value);
	    }
		
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public Map<String, Object> getResult() {
			return result;
		}
		public void setResult(Map<String, Object> result) {
			this.result = result;
		}
	}
}
