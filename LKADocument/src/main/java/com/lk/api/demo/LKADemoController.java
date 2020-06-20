package com.lk.api.demo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lk.api.annotation.ContentType;
import com.lk.api.annotation.LKAGroup;
import com.lk.api.annotation.LKAMethod;
import com.lk.api.annotation.LKAModel;
import com.lk.api.annotation.LKAParam;
import com.lk.api.annotation.LKAParams;
import com.lk.api.annotation.LKARespose;
import com.lk.api.annotation.LKAResposes;
import com.lk.api.annotation.LKAType;
import com.lk.api.annotation.ParamType;
import com.sun.prism.impl.BaseMesh.FaceMembers;

/**
 * LKADocument为swagger大部分注解做了兼容处理，只需修改引入的包路径为com.lk.api.*
 * LKAType注解:用来描述接口对应的处理类
 * value:类的作用（必填）
 * description：类的描述（选填）
 * hidden：是否在UI界面隐藏该类的信息，默认为false（选填）
 */
@LKAType(value="第一个测式类",description="用来演示LKADocument",hidden=false)
@RestController
@RequestMapping("lkadocument/demo")
public class LKADemoController {
	
	/*
	 * LKAMethod注解：用来描述接口信息
	 * value：接口的作用（必填）
	 * description：接口的描述（选填）
	 * contentType：请求头ContentType类型，默认为application/x-www-form-urlencoded（选填）
	 * author：作者（选填）
	 * createTime：接口创建时间（选填）
	 * updateTime：接口修改时间（选填）
	 * hidden：是否在UI界面隐藏该接口，默认为false（选填）
	 * version：接口版本号，如果项目版本号相同，在UI界面会标记为新接口（选填）
	 */
	@LKAMethod(value="登录1",description="用户登录验证",contentType=ContentType.URLENCODED,
			author="liukai",createTime="2020-6-20",updateTime="2020-6-20",hidden=false,version="1.0")
	@LKAParam(names= {"name","pwd"},values= {"用户名","密码"})
	@LKARespose(names= {"code","msg"},values= {"状态码","消息"})
	@PostMapping("login")
	public Map<String,Object> login(String name,String pwd) {
		Map<String,Object> map = new HashMap<>();
		map.put("code",200);
		map.put("msg","登录成功，欢迎"+name+"光临本系统");
		return map;
	}
	
	@LKAMethod(value="登录2")
	@LKAParam(names= {"name","pwd"},values= {"用户名","密码"})
	@LKARespose(names= {"code","msg"},values= {"状态码","消息"})
	@PostMapping("test")
	public Map<String,Object> login2(String name,String pwd) {
		Map<String,Object> map = new HashMap<>();
		map.put("code",200);
		map.put("msg","登录成功，欢迎"+name+"光临本系统");
		return map;
	}
	
	
	/*
	 * 以下两种写法是完全等价的，带s复数注解代表可以一个注解设置多个参数
	 * LKAParam注解：用来描述请求参数信息
	 * name：参数名称（必填）
	 * value：参数作用（必填）
	 * description：参数的描述（选填）
	 * dataType：参数数据类型，默认String（选填）
	 * required：是否必传，默认为true（选填）
	 * (更简便的用法是在参数名后加"-n"代表不是必传，不加默认是必传)
	 * paramType：参数位置，query、header、path三选一，默认为query（选填）
	 * isArray：是否是集合或数组，默认false（选填）
	 * testData：测试数据（选填）
	 */
	@LKAMethod(value="获取用户列表")
	@LKAParam(names= {"name","age-n","roleType-n","token"},
			values= {"用户名","年龄","角色类型","授权token"},
			descriptions= {"支持模糊匹配","范围0-120","1-经理，2-主管，3-普通员工","授权token"},
			dataTypes= {"String","int","int","String"},
			//requireds= {true,false,false,true},
			paramTypes= {ParamType.QUERY,ParamType.QUERY,ParamType.PATH,ParamType.HEADER},
			testDatas= {"liu","20","1","38e12c99"},
			isArrays= {false})
	/* 
	 * @LKAParams({
		@LKAParam(name="name",value="用户名",required=true,description="支持模糊匹配",
				dataType="String",paramType=ParamType.QUERY,isArray=false,testData="liu"),
		@LKAParam(name="age",value="年龄",required=false,description="范围0-120",
				dataType="int",testData="20"),
		@LKAParam(name="roleType-n",value="角色类型",description="1-经理，2-主管，3-普通员工",
				dataType="int",paramType=ParamType.PATH,testData="1"),
		@LKAParam(name="token",value="授权token",description="授权token",
				paramType=ParamType.HEADER,testData="38e12c99")
	})*/
	@LKARespose(names= {"code","msg"},values= {"状态码","消息"})
	@PostMapping("getUsers/{roleType}")
	public Map<String,Object> getUsers(
			String name,
			Integer age,
			@PathVariable("roleType")Integer roleType,
			@RequestHeader("token")String token) {
		Map<String,Object> map = new HashMap<>();
		map.put("code",200);
		map.put("msg","获取信息成功！name="+name+",age="+age+",roleType="+roleType+",token="+token);
		return map;
	}
	
	
	/*
	 * 数组传参注意事项
	 * 1.isArray要设置成true，代表是数组
	 * 2.dataType参数类型后面要加“[]”
	 * 3.接口调试时要勾选“阻止深度序列化”
	 */
	@LKAMethod(value="数组传参")
	@LKAParam(name="ids",value="用户id",isArray=true,dataType="String[]")
	@LKARespose(names= {"code","msg"},values= {"状态码","消息"})
	@PostMapping("arrTest")
	public Map<String,Object> arrTest(String[] ids) {
		String arr = "";
		if(ids != null) {
			for (String id : ids) {
				arr = arr+","+id;
			}
		}
		Map<String,Object> map = new HashMap<>();
		map.put("code",200);
		map.put("msg","获取信息成功！ids="+arr);
		return map;
	}
	
	
	/*
	 * 文件上传(支持单个或批量上传)注意事项
	 * 1.如果是批量上传isArray要设置成true，代表是数组
	 * 2.单个文件上传dataType类型要设置成"file",批量上传dataType参数类型要设置成“file[]”
	 * 3.前端需要把from表单的enctype属性设置成'multipart/form-data'
	 * 4.请求类型必须是"post"
	 */
	@LKAMethod(value="文件批量上传")
	@LKAParam(name= "files",value="上传文件",isArrays= true,dataType="file[]")
	@LKARespose(names= {"code","msg"},values= {"状态码","消息"})
	@PostMapping("fileUpload")
	public Map<String,Object> fileUpload(MultipartFile[] files) {
		String fileNames = "";
		if(files != null) {
			for (MultipartFile f : files) {
				fileNames = fileNames + ","+f.getOriginalFilename();
			}
		}
		//上传后续业务处理：略
		Map<String,Object> map = new HashMap<>();
		map.put("code",200);
		map.put("msg","上传文件成功！,files="+fileNames);
		return map;
	}
	
	
	/*
	 * 文件下载注意事项
	 * LKAMethod注解里面的download属性要设置成true,代表是下载的API
	 */
	@LKAMethod(value="文件下载",download=true)
	@PostMapping("fileDownload")
	public void fileDownload(HttpServletResponse response) throws Exception {
	      String path = "D:\\test.pdf";
	      File file = new File(path);
	      String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1).toUpperCase();
	      InputStream fis = new BufferedInputStream(new FileInputStream(path));
	      byte[] buffer = new byte[fis.available()];
	      fis.read(buffer);
	      fis.close();
	      response.reset();
	      response.addHeader("Content-Disposition", "attachment;filename=" + new String(file.getName().getBytes()));
	      response.addHeader("Content-Length", "" + file.length());
	      OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
	      response.setContentType("application/octet-stream");
	      toClient.write(buffer);
	      toClient.flush();
	      toClient.close();
	  }
	
	
	/*
	 * 复杂的对象传参
	 * 注意事项:
	 * 1.如果入参是对象，并且对象上定义了@LKAModel注解，无需定义@LKAParam，可以自动被扫描到
	 * 2.如果接收请求参数对象带嵌套对象需把contentType属性设置为"application/json",
	 * 3.如果contentType="application/json"，需在接收对象前面加@RequestBody注解
	 */
	@LKAMethod(value="复杂的对象传参",contentType=ContentType.JSON)
	@LKAResposes({
		@LKARespose(names= {"code","msg"},values= {"状态码","消息"}),
		@LKARespose(type=User.class,value= "用户对象")
	})
	@PostMapping("addUser")
	public Map<String,Object> addUser(@RequestBody User user) {
		Map<String,Object> map = new HashMap<>();
		map.put("code",200);
		map.put("msg","操作成功！");
		map.put("user",user);
		return map;
	}
	
	
	/*
	 * 对象参数分组
	 * LKAGroup注解:用来指定对象的哪组参数来作为入参，这里指定了a组参数
	 */
	@LKAMethod(value="对象参数分组",contentType=ContentType.JSON,version="1.0")
	@LKAResposes({
		@LKARespose(names= {"code","msg"},values= {"状态码","消息"}),
		@LKARespose(type=User.class,value= "用户对象")
	})
	@PostMapping("getUser")
	public Map<String,Object> getUser(@RequestBody @LKAGroup("a") User user) {
		Map<String,Object> map = new HashMap<>();
		map.put("code",200);
		map.put("msg","操作成功！");
		map.put("user",user);
		return map;
	}
	
	@LKAMethod(value="响应参数基本用法")
	@LKAParam(names= {"name","pwd"},values= {"用户名","密码"})
	/*
	 * 以下两种用法完全等价，带s的复数属性可以描述多个参数
	 * LKARespose注解：用来描述响应参数信息
	 * name：参数名称（必填）
	 * value：参数作用（必填）
	 * description：参数的描述（选填）
	 * dataType：参数数据类型，默认String（选填）
	 * isArray：是否是集合或数组，默认false（选填）
	 */
	@LKARespose(names= {"code","msg"},values= {"状态码","响应消息"},descriptions= {"200-成功,其它-失败","响应结果弹窗信息"})
	/*@LKAResposes({
		@LKARespose(name="code",value="状态码",description="200-成功,其它-失败",dataType="String",
				isArray=false),
		@LKARespose(name="msg",value="响应消息")
	})*/
	@PostMapping("resTest")
	public Map<String,Object> resTest(String name,String pwd) {
		Map<String,Object> map = new HashMap<>();
		map.put("code",200);
		map.put("msg","登录成功，欢迎"+name+"光临本系统");
		return map;
	}
}
