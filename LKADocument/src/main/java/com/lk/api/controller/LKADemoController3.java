package com.lk.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lk.api.annotation.ApiOperation;
import com.lk.api.annotation.ContentType;
import com.lk.api.annotation.LKAGroup;
import com.lk.api.annotation.LKAMethod;
import com.lk.api.annotation.LKAModel;
import com.lk.api.annotation.LKAParam;
import com.lk.api.annotation.LKAProperty;
import com.lk.api.annotation.LKARespose;
import com.lk.api.annotation.LKAResposes;
import com.lk.api.annotation.LKAType;
import com.lk.api.annotation.Lkad;
import com.lk.api.annotation.ParamType;
import com.lk.api.demo.MiniUser;

@LKAType("测试类3")
@RestController
@RequestMapping("demo3")
public class LKADemoController3 {
	
	/**
     *	说明：入参属性名后面加上'-n'代表不是必传字段,例如下面"id-n"那么代表入参id不是必须的
	 */
	@PostMapping("/V1.0/mini/register")
	@ApiOperation(value="小程序登录/注册",contentType=Lkad.JSON)
	@LKAResposes({
		@LKARespose(name="code",value="返回编码"),
		@LKARespose(value="result",name="result"),
		@LKARespose(value="前后端交互的唯一标识,请置于请求头中,作为接下来每个接口请求的认证标识",name="x-token",parentName="result"),
		@LKARespose(value="用户ID",name="id",parentName="result"),
		@LKARespose(value="用户类型(1-客户,2-招商员)",name="userType",parentName="result"),
	})
	public Map<String,Object> test1(@LKAGroup("A") @RequestBody MiniUser user) {
		Map<String,Object> map = new HashMap<>();
		return map;
	}
	
	/**
	 * 说明：如果aaa和bbb参数的父级节点是一样的，可以直接使用parentNames={"result"}或parentName="result"来指定
	 * */
	@LKAMethod("测试方法2")
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
	

	@LKAMethod("测试方法3")
	@PostMapping("test3")
	public void test3(Integer id,String name,Integer age) {
		
	}
	@LKAMethod("测试方法3")
	@PostMapping("test4")
	public void test4(Integer id,String name,Integer age) {
		
	}
	@LKAMethod("测试方法3")
	@PostMapping("test5")
	public void test5(Integer id,String name,Integer age) {
		
	}
	@LKAMethod("测试方法3")
	@PostMapping("test6")
	public void test6(Integer id,String name,Integer age) {
		
	}
	@LKAMethod("测试方法3")
	@PostMapping("test7")
	public void test7(Integer id,String name,Integer age) {
		
	}
	@LKAMethod("测试方法3")
	@PostMapping("test8")
	public void test8(Integer id,String name,Integer age) {
		
	}
	@LKAMethod("测试方法3")
	@PostMapping("test9")
	public void test9(Integer id,String name,Integer age) {
		
	}
	@LKAMethod("测试方法3")
	@PostMapping("test10")
	public void test10(Integer id,String name,Integer age) {
		
	}
	@LKAMethod("测试方法3")
	@PostMapping("test11")
	public void test11(Integer id,String name,Integer age) {
		
	}
	@LKAMethod("测试方法3")
	@PostMapping("test12")
	public void test12(Integer id,String name,Integer age) {
		
	}
	@LKAMethod("测试方法3")
	@PostMapping("test13")
	public void test13(Integer id,String name,Integer age) {
		
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
