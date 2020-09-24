
# 1. 前言
&emsp;&emsp;Lkadoc接口文档生成框架开源以来，备受好评，后期还会给lkadoc整个开源社区，方便大家讨论和解决问题。Lkadoc的前身叫lkadocument，个人感觉名字太长，不利于工具的推广，所以简化了名称为Lkadoc。名称的含义也很简单，Lk是我名字首字母，a代表接口（api）的意思，doc当然就是指文档（document）了。如果读者有使用过lkadocument的经验就知道，lkadocument的版本分奇数和偶数两种版本，奇数版自带导出PDF所需字体，所以jar包比较大，偶数版没有带字体文件，体积较小。
&emsp;&emsp;lkadocument发布最新版为1.1.4，也是lkadocument最后一个版本，以后发布的版本工具名称改为lkadoc。因为增加的功能比较多，特别是新增了数据校验等激动人心的功能，所以lkadoc第一个版本直接升级到1.2.0，从1.2.0版开始就不区分奇偶版了，统一为不带字体的，这样jar包体积更少，大家在下载使用时更顺畅。但需要检查系统是否存在simsun.ttc字体，如果系统没有这个字体的话，导出PDF文档中文不能正确显示。 windows系统字体路径：C:/Windows/fonts/simsun.ttc，linux系统字体路径：/usr/share/fonts/win/simsun.ttc，mac系统字体路径：/System/Library/Fonts/simsun.ttc。


# 2. 介绍
&emsp;&emsp;Lkadoc是一款能够基于注解自动生成带调试功能的接口文档工具，生成的UI界面简约大方，对接口描述一目了然，自面世以来深受大家的推崇和喜爱，大大提高了后端开发效率，减小了前端和后端接口对接的沟通成本。很多以前使用swagger的读者改成了lkadoc方案，反应都说比swagger好用太多了。下面简单的对lkadoc特色功能介绍一下：
    - 支持导出成PDF和MD格式的文档
    - 支持在线调试API（包括数组入参、上传文件、文件下载均支持）
    - 支持对接口进行压力测试
    - 支持多项目接口文档聚合展示
    - 支持自动识别对象入参或出参，可实现接口零注解
    - 支持任何复杂的对象参数结构，理论上支持最大10层的参数结构嵌套（防止嵌套出现死循环，做了10层限制）
    - 支持一条注解描述“多个”参数，支持一条注解描述“多层”参数结构（大大简化注解数量）
    - 支持JSON格式化展示请求参数和响应参数（参数结构一目了然，减少前后端沟通成本，减少失误）
    - 支持版本控制新接口标记显示（方便定位新接口）
    - 支持接口名称和参数添加标签（特殊说明可以加标签提示，减少前后端沟通成本）
    - 支持对象属性分组（包括父类继承属性）
    - 支持对请求参数进行数据校验（支持正则匹配、非空、非null、集合非空、字符串长度、数值范围等常用校验规则，如果关闭接口文档功能不会影响数据校验功能。）
    - 更多功能...

### 版本更新说明：

#### lkadoc 1.2.4于2020年9月24日发布：

1. 修复项目在使用shiro等框架时application/json方式对象参数数据校验失效的BUG

2. 修复map集合入参时在自动检测参数paramType类型报数组越界异常的BUG

3. 优化一条注解描述多个参数时，如果哪个参数无需数据校验可在valids属性中用空字符串“”表示

4. 优化入参value属性值前面加“n~”代表非必传参数。例如：
   优化之前:
   @LKAParam(values={"姓名","年龄"},testDatas={"张三","22"},requireds={true,false})
   @LKAProperty(value="id主键",testData="1001",required=false)

   优化之后:
   @LKAParam(values={"姓名^张三","n~年龄^22"})
   @LKAProperty(value="n~id主键^1001")

#### lkadoc 1.2.3于2020年9月15日发布：
1. 修复项目不能带中文路径的BUG
2. 修复找不到文件路径报空指针的BUG
3. 修复UI界面给父级节点添加标签异常BUG
4. 增加LKAMethod注解token属性，用于标识该接口是否需要token授权验证
5. 增加UI界面选项页功能
6. 拆分lkadoc为api、annotation两个模块。可分别使用以下maven配置：
```
<dependency>
	<groupId>com.github.liukaitydn</groupId>
	<artifactId>lkadoc-annotations</artifactId>
	<version>1.2.3</version>
</dependency>
<dependency>
	<groupId>com.github.liukaitydn</groupId>
	<artifactId>lkadoc-api</artifactId>
	<version>1.2.3</version>
</dependency>
```
其中lkadoc-annotations是单独的一个模块，如果工程只用到注解可以只添加lkadoc-annotations模块即可。lkadoc-api模块依赖lkadoc-annotations模块，当然工程只添加lkadoc-api模块的maven配置也会自动把lkadoc-annotations模块引入到当前工程。
#### lkadoc 1.2.2 修复1.2.1不能导出pdf和md的功能
#### lkadoc 1.2.1 于2020年8月5日发布(有bug，导出有问题，请使用1.2.2版)：
1. 增加md格式文档导出
2. 增加对父节点参数名称的复制功能
3. 增加对父节点参数添加标签功能，如果父节点标签为删除标签，那么在调试该接口时不会把该父节点参数及所有子参数传到后台![lkdoc首页](https://img-blog.csdnimg.cn/20200805175608721.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70#pic_center)
# 3. 准备工作

## 3.1 在SpringBoot项目中引入两个jar包

```xml
<!--Lkadoc包-->
<dependency>
	<groupId>com.github.liukaitydn</groupId>
	<artifactId>lkadoc-api</artifactId>
	<version>1.2.3</version>
</dependency>
<!--itextpdf包，把接口信息导出成PDF文档是基于itextpdf来实现的，所以要引入这个包-->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextpdf</artifactId>
    <version>5.5.13</version>
</dependency>
```

## 3.2 在SpringBoot项目启动类上加上@LKADocument注解

```java
@LKADocument
@SpringBootApplication
public class LKADemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(LKADemoApplication.class, args);
    }
}
```

## 3.3 在application.yml文件中添加如下配置

```yaml
lkad:
#要扫描接口的包路径，多个用","号隔开，指定父包可以扫描所有父包下的子包（必配）
 basePackages: com.lkad.api
#项目名称（选配）
 projectName: Lkadoc测试项目
#项目描述（选配）
 description: 智能、便捷、高效
#要聚合的项目地址，"-"前面是项目名称（可省略），后面是项目的地址（也可以用域名），多个用","号隔开，用来聚合其它项目的接口信息，可以在UI界面切换（选配）
 serverNames: 租房系统-192.168.0.77:9010,缴费系统-192.168.0.77:8888
#项目的版本号（选配）
 version: 1.0
#接口文档启动开关,true是开启，false是禁用,默认为开启，此开关对数据校验没有影响（选配）
 enabled: true
```

或者在启动类注解@LKADocument上设置如下属性（和上面配置二选一即可,效果一模一样）

```java
@LKADocument(basePackages="com.lkad.api",projectName="Lkadoc测试项目",description="智能、便捷、高效",version="1.0",serverNames="租房系统-192.168.0.77:9010,缴费系统-192.168.0.77:8888",enabled=true)
@SpringBootApplication
public class LKADemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(LKADemoApplication.class,args);
    }
}
```

## 3.4 准备测试代码
```java
package com.lkad.api;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lk.api.annotation.*;

@LKAType(value="第一个测试类")
@RestController
@RequestMapping("lkadocument/demo")
public class LKADemoController {
 
    @LKAMethod(value="登录")
    @LKAParam(names= {"name","pwd"},values= {"用户名","密码"})
    @LKARespose(names= {"code","msg"},values= {"状态码","消息"})
    @PostMapping("login")
    public Map<String,Object> login(String name,String pwd) {
        Map<String,Object> map = new HashMap<>();
        map.put("code",200);
        map.put("msg","登录成功，欢迎"+name+"光临本系统");
        return map;
    }
}
```

## 3.5 打开浏览器，输入地址http://127.0.0.1:8080/lkadoc.html 查看效果如下：

![](https://img-blog.csdnimg.cn/2020073115413653.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

# 4. 基础入门

## 4.1 LKAType注解

```yaml
LKAType注解:用来描述接口对应的处理类
#常用属性：
value:类的作用（必配）
description:类的描述（选配）
hidden:是否在UI界面隐藏该类的信息，默认为false（选配）
```

*注意：Lkadoc为swagger大部分注解做了兼容处理，只需修改引入的包路径为com.lk.api.*即可*

## 4.2 LKAMethod注解

```yaml
LKAMethod注解:用来描述接口信息
#常用属性：
value:接口的作用（必配）
description:接口的描述（选配）
contentType:请求头ContentType类型，默认为application/x-www-form-urlencoded（选配）
author:作者（选配）
createTime:接口创建时间（选配）
updateTime:接口修改时间（选配）
hidden:是否在UI界面隐藏该接口，默认为false（选配）
version:接口版本号，如果项目版本号相同，在UI界面会标记为新接口（选配）
download:是否是下载的方法，默认是false（选配）
token:是否需要token授权验证，默认是true（选配）
```

### 4.2.1 LKAType和LKAMethod演示代码

```java
package com.lkad.api;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lk.api.annotation.*;
import com.lk.api.constant.ContentType;

@LKAType(value="第一个测式类",description="用来演示LKADocument",hidden=false)
@RestController
@RequestMapping("lkadocument/demo")
public class LKADemoController {
 
    @LKAMethod(value="登录",description="用户登录验证",contentType=ContentType.URLENCODED,
author="liukai",hidden=false,version="1.0",download=false,createTime="2020-7-20",updateTime="2020-7-20")
    @LKAParam(names= {"name","pwd"},values= {"用户名","密码"})
    @LKARespose(names= {"code","msg"},values= {"状态码","消息"})
    @PostMapping("login")
    public Map<String,Object> login(String name,String pwd) {
        Map<String,Object> map = new HashMap<>();
        map.put("code",200);
        map.put("msg","登录成功，欢迎"+name+"光临本系统");
        return map;
    }
}
```

效果图：

![](https://img-blog.csdnimg.cn/20200731154236309.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

## 4.3 运用版本号快速定位新接口
&emsp;&emsp;大家应该还有印象，在用@LKADocument注解配置项目信息时有一个version属性用来设置项目的版本号，然后@LKAMethod注解也有一个version属性用来设置接口的版本号，在实际工作中，往往一个项目版本升级并不代表所有接口都需要升级，也可能会增加一些新接口。那么我们怎么才能在众多的接口中定位哪一个接口是新接口或最新修改的接口呢？很简单，我们只需把新接口或最新修改的接口的@LKAMethod注解version属性的版本值设置和@LKADocument注解的version属性的版本值设置成一致就可以了，这样Lkadoc会用红色标记出新接口。那么后端在和前端同事对接口时就可以很快定位哪些是新接口了。


### 4.3.1 案例：
```java
package com.lkad.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lk.api.annotation.*;

/**
 * 在这里我们再准备一个测试
 */
@LKAType("Hello类")
@RestController
public class HelloController {
    /**
     * 注意：这个接口并没有设置version版本值，但4.2.1那个测试代码的登录接口设置了version值和项目的
     * version值一致，我们对比一下看看两个接口在UI展示有什么不一样。
     */
    @LKAMethod(value="Hello方法")
    @GetMapping("hello")
    public String hello() {
        return "hello Lkadoc!";
    }
}
```

效果图：

![](https://img-blog.csdnimg.cn/20200731154302326.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

## 4.4 LKAParam和LKAParams注解

### 4.4.1 常用属性解析

```yaml
LKAParam/LKAParams:描述请求参数信息，LKAParams注解可以包含多个LKAParam注解，用来描述多个请求参数。

#常用属性,带s复数属性代表可以设置多个参数,但要注意参数顺序。带s和不带s设置时只能二选一
name/names:参数名称（用name设置参数名称时必配;用names设置参数名称时可省略，但JDK版本要1.8以上，编译的时候还要加上–parameters参数，这样Lkadoc可自动获取到参数名称,否则必配）
#例如:
#单个参数配置:
#@LKAParam(name="name",...)
#多个参数配置:
#@LKAParam(names={"name","pwd","age"},...)//这里如果和接口入参顺序一样，可省略不用配置
    #或者
#@LKAParams({
    #@LKAParam(name="name",...),
    #@LKAParam(name="pwd",...),
    #@LKAParam(name="age",...)
#})

value/values:参数作用（必配）
#例如:
#单个参数配置:
#@LKAParam(name="name",value="用户名")
#多个参数配置:
#@LKAParam(values={"用户名","密码","年龄"})//这里把names省略了(注意入参顺序)，可自动获取
    #或者
#@LKAParams({
    #@LKAParam(name="name",value="用户名"),
    #@LKAParam(name="pwd",value="密码"),
    #@LKAParam(name="age",value="年龄")
#})

description/descriptions:参数的描述（选配）
#例如：和value/values用法一样（略）

dataType/dataTypes:数据类型，（用dataType配置时默认值String.class;用dataTypes配置时可自动获取参数的数据类型，可省略不配置，但要注意参数的顺序。）（选配）
#例如:
#单个参数配置:
#@LKAParam(name="name",value="用户名",dataType=String.class)//这里可省略，因为默认是String
#多个参数配置:
#@LKAParam(...,dataTypes={String.class,String.class,Integer.class},...) //这里如果和接口入参顺序和数量一致的话，也可以省略，可自动获取
    #或者
#@LKAParams({
    #@LKAParam(name="name",value="用户名",dataType=String.class),
    #@LKAParam(name="pwd",value="密码",dataType=String.class),
    #@LKAParam(name="age",value="年龄",dataType=Integer.class)
#})

required/requireds:是否必传，默认为true（选配）(更简便的用法是在参数名后加"-n"代表不是必传，不加默认是必传)
#例如:
#@LKAParam(name="name",value="用户名",required=false)
#或者
#@LKAParam(name="name-n",value="用户名") //参数名称后面加"-n"代表不是必传，不加默认是必传

paramType/paramTypes:参数位置，query、header、path三选一，（用paramType配置时默认为query;用paramTypes配置时Lkadoc可根据参数注解@PathVariable、@RequestHeader自动获取参数位置，可省略不配）

isArray/isArrays:是否是集合或数组，默认false（选配）

testData/testDatas:测试数据（选配）(更简便的用法是在value/values后面的"^"符号后面加上测试数据)
#例如:
#@LKAParam(name="name",value="用户名",required=false,testData="张三")
#或者
#@LKAParam(name="name-n",value="用户名^张三") //在value/values后面的"^"符号后面加上测试数据

type:入参对象类型（当接口请求参数是一个对象时使用，但一般不需要设置，可自动识别）

group:和type配合使用，对象参数分组，可过滤没必要的参数

#数据校验相关属性（后面会详细讲解）
valids:数据校验常用规则或正则匹配
msgs:数据校验消息
range:数值范围限制判断
size:集合、数组大小限制判断
length:字符串长度限制判断
```

### 4.4.2 测试代码

```java
/**
* 在LKADemoController类中加一个测试接口getUsers
* 说明：
* dataType和paramType均可自动获取,所以可省略不配
* required在name后用"-n"代替，所以可以省略不配
* testData可在value后面的"^"加上，所以可以省略不配
*/
@LKAMethod("获取用户信息")
@LKAParam(names= {"name","age-n","roleType-n","token"},values= {"用户名^张三","年龄^22","角色类型^1","授权token^aa"})
@LKARespose(names= {"code","msg","data"},values= {"状态码","消息","数据"})
@PostMapping("getUsers/{roleType}")
public Map<String,Object> getUsers(
    String name,
    Integer age,
    @PathVariable("roleType")Integer roleType,//path参数
    @RequestHeader("token")String token) { //header参数
    Map<String,Object> map = new HashMap<>();
    map.put("code",200);
    map.put("msg","获取信息成功");
    map.put("data","姓名："+name+",年龄："+age+",角色类型："+(roleType==1?"经理":"员工")+",token："+token);
    return map;
}
```

效果图:

![](https://img-blog.csdnimg.cn/20200731154334748.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

## 4.5 调试API功能

### 4.5.1 测试API请求
&emsp;&emsp;Lkadoc支持对单个接口进入调试，我们只需要准备好请求参数的测试数据，然后点击"测试API请求"按钮，就可以在调试窗口看到结果信息了。


![](https://img-blog.csdnimg.cn/20200731154359423.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

### 4.5.2 压力测试
&emsp;&emsp;我们可以通过选择执行方式来决定接口采用"同步"还是"异步"执行，如果选择"同步"测试，可以选择执行次数，和时间间隔，这样可以模拟对接口进行压力测试。如果选择"异步"测试，可以选择执行次数，这样可以模拟对接口进行并发测试。所有测试结果会打印在调试窗口中。
![](https://img-blog.csdnimg.cn/20200731154418627.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

## 4.6 全局token锁定
&emsp;&emsp;我们在调试需要token授权的接口时,需要在每个需要授权接口的请求头带上一个类似token的参数，调试时非常不方便，所以Lkadoc提供了一个可以给全局接口锁定一个请求头参数，这样就不需要在每一个需要授权的接口中去设置这个授权参数了。


![](https://img-blog.csdnimg.cn/20200731154447314.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

## 4.7 数组传参
### 4.7.1 注意事项
```
数组传参注意事项
1.isArray要设置成true，代表是数组
2.参数类型dataType一定要是一个数组类型，例如：String[].class
3.接口调试时要勾选“阻止深度序列化”
```
### 4.7.2 测试代码
```java
/**在LKADemoController类中加一个测试接口*/
@LKAMethod(value="数组传参")
@LKAParam(name="ids",value="用户id",isArray=true,dataType=String[].class)
@LKARespose(names= {"code","msg","data"},values= {"状态码","消息","数据"})
@PostMapping("arrTest")
public Map<String,Object> arrTest(String[] ids) {
    String arr = "";
    if(ids != null) {
        for (String id : ids) {
            if("".equals(arr)) {
                arr = id;
            }else {
                arr = arr+","+id;
            }
        }
    }
    Map<String,Object> map = new HashMap<>();
    map.put("code",200);
    map.put("msg","获取信息成功");
    map.put("data","ids="+arr);
    return map;
}
```

效果图:

![](https://img-blog.csdnimg.cn/20200731154506448.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

## 4.8 文件上传

### 4.8.1 注意事项
```
文件上传(支持单个或批量上传)注意事项
1.如果是批量上传isArray要设置成true，代表是数组
2.单个文件上传dataType类型要设置成"MultipartFile.class",批量上传dataType参数类型要设置成“MultipartFile[].class”
3.前端需要把from表单的enctype属性设置成'multipart/form-data'
4.请求类型必须是"post"
```

### 4.8.2 测试代码

```java
/**在LKADemoController类中加一个测试接口*/
@LKAMethod(value="文件批量上传",contentType=ContentType.FORMDATA)
@LKAParam(name= "files",value="上传文件",isArrays= true,dataType=MultipartFile[].class)
@LKARespose(names= {"code","msg","data"},values= {"状态码","消息","数据"})
@PostMapping("fileUpload")
public Map<String,Object> fileUpload(MultipartFile[] files) {
    String fileNames = "";
    if(files != null) {
        for (MultipartFile f : files) {
            if("".equals(fileNames)) {
                fileNames = f.getOriginalFilename();
            }else {
                fileNames = fileNames + ","+f.getOriginalFilename();
            }
        }
    }
    //上传后续业务处理：略
    Map<String,Object> map = new HashMap<>();
    map.put("code",200);
    map.put("msg","上传文件成功!");
    map.put("data","文件名："+fileNames);
    return map;
}
```

效果图:

![](https://img-blog.csdnimg.cn/20200731154524535.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

## 4.9 文件下载

### 4.9.1 注意事项
&emsp;&emsp;LKAMethod注解里面的download属性要设置成true,代表是下载的接口
### 4.9.2 测试代码

```java
/**在LKADemoController类中加一个测试接口*/
@LKAMethod(value="文件下载",download=true)
@PostMapping("fileDownload")
public void fileDownload(HttpServletResponse response) throws Exception {
    String path = "D:\\test.txt";
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
```

![](https://img-blog.csdnimg.cn/20200731154542920.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

# 5. 高级应用

## 5.1 LKAModel注解

```yaml
LKAModel注解:用来标识需要扫描的实体类
```

## 5.2 LKAProperty注解

```yaml
LKAProperty注解:用来描述实体类的属性信息
#常用属性
value:属性的作用
description:属性的描述
hidden:是否在UI界面隐藏该属性，默认为false（选填）
testData:测试数据（选配）(更简便的用法是在value后面的"^"符号后面加上测试数据)
required:是否必传，默认为true（选配）(更简便的用法是在groups属性里面的组名后面加"-n"代表不是必传，不加默认是必传)
isArray:是否是数组或集合
type:当属性为对象类型时，需用type来指定
groups:用来进行参数分组
#数据校验相关属性（后面会详细讲解）
valids:数据校验常用规则或正则匹配
msgs:数据校验消息
range:数值范围限制判断
size:集合、数组大小限制判断
length:字符串长度限制判断
```

## 5.3 基本对象入参

### 5.3.1 注意事项
&emsp;&emsp;当我们入参是一个对象时，如果该对象上有@LKAModel注解，并且它的属性上有@LKAProperty注解，那么Lkadoc会去自动扫描这个对象信息，我们无需在接口上加额外的注解去描述对象参数。这样如果我们用对象去操作入参的话，可以大大减少接口上的注解数量，显得更加简洁。


### 5.3.2 测试代码

```java
/**准备一个角色对象*/
@LKAModel
public class Role {
    @LKAProperty(value="角色id^1")
    private Integer id;
    @LKAProperty(value="角色^名称")
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

/**在LKADemoController类中加一个测试接口*/
@LKAMethod("基本对象入参")
@GetMapping("getRole")
public Role getRole(Role role) {
    return role;
}
```

效果图：
![](https://img-blog.csdnimg.cn/20200731154641377.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

## 5.4 复杂的对象入参

### 5.4.1 注意事项

```
1.复杂的对象需把@LKAMethod注解的contentType属性设置为"application/json"
2.如果contentType="application/json"，需在接收对象参数前面加@RequestBody注解
3.如果contentType="application/json"，那么接口的请求类型不能是get
4.如果对象参数是实体类型需要用@LKAProperty注解的type属性来指定类型
5.如果对象参数是数组或List或Set集合需要把@LKAProperty注解的isArray设置成true
```

### 5.4.2 测试代码
```java
/**再增加两个对象address和User，加上之前Role一共有3个对象*/
@LKAModel
public class Address {
    @LKAProperty(value="地址ID^1")
    private Integer id;
    @LKAProperty(value="地址信息^深圳市龙华区")
    private String info;
    ....get/set方法.....
}

@LKAModel
public class User {
    @LKAProperty(value="用户ID",hidden=true)//hidden设置成true，该属性不会在UI界面展示
    private Integer id;
    @LKAProperty(value="用户名称^张三")
    private String name;
    @LKAProperty(value="年龄^20",required=false,description="范围0-120")
    private String age;
    @LKAProperty(value="角色对象",type=Role.class)
    private Role role;
    @LKAProperty(value="用户爱好^运动",isArray=true)
    private String[] likes;
    @LKAProperty(value="地址信息",isArray=true,type=Address.class)
    private List<Address> addresses;
    ....get/set方法.....
}

/**在LKADemoController类中加一个测试接口*/
@LKAMethod(value="复杂的对象传参",contentType=ContentType.JSON)
@PostMapping("addUser")
public User addUser(@RequestBody User user) {
    return user;
}    
```

效果图1：

![](https://img-blog.csdnimg.cn/2020073115470261.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

效果图2：

![](https://img-blog.csdnimg.cn/20200731154718870.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

## 5.5 对象参数分组
    	我们感受到了用对象接收请求参数更具便利性，那怎么去过滤对象属性呢？例如：有一个查询接口，只用到user对象的name、age和addresses属性对象的info参数，但对于前端的友好度，我们不需要展示所有对象属性到UI界面，这时我们就可以用分组来实现.


### 5.5.1 注意事项
```
1.分组可以用@LKAProperty注解的groups属性来设置组名
2.一个属性可以属于多个组，组名不能重复
3.组名没有任何限制，只要不是空白的字符串即可
4.如果用到嵌套对象里面属性，嵌套对象名称和对应属性上都要设置相同的组名
5.入参对象需要用@LKAGroup注解来指定对象是哪组参数用来作为入参
```

### 5.5.2 测试代码

```java
/**这里我们设置一个组名叫addUser（和接口名保持一致，方便区分）,分别用到user对象的name参数、age参数和addresses属性对象的info参数*/
@LKAModel
public class User {
    @LKAProperty(value="用户ID",hidden=true)//hidden设置成true，该不会在UI界面展示
    private Integer id;
    @LKAProperty(value="用户名称^张三",groups= {"addUser"})
    private String name;
    @LKAProperty(value="年龄^20",required=false,description="范围0-120",groups= {"addUser"})
    private String age;
    @LKAProperty(value="角色对象",type=Role.class)
    private Role role;
    @LKAProperty(value="用户爱好^运动",isArray=true)
    private String[] likes;
    @LKAProperty(value="地址信息",isArray=true,type=Address.class,groups= {"addUser"})
    private List<Address> addresses;
    .......get/set.......
}
@LKAModel
public class Address {
    @LKAProperty(value="地址ID",testData="5")
    private Integer id;
    @LKAProperty(value="地址信息",testData="深圳市龙华区",groups= {"addUser"})
    private String info;
    .......get/set.......
}


/**在LKADemoController类中的addUser接口入参对象User前面加一个注解@LKAGroup("addUser")*/
@LKAMethod(value="复杂的对象传参",contentType=ContentType.JSON)
@PostMapping("addUser")
public User addUser(@RequestBody @LKAGroup("addUser") User user) {
    return user;
}
```

效果图：

![](https://img-blog.csdnimg.cn/20200731154746766.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

## 5.6 响应参数的基本用法
### 5.6.1 LKARespose/LKAResposes注解

```yaml
LKARespose/LKAResposes:描述响应参数信息，LKAResposes注解可以包含多个LKARespose注解，用来描述多个响应参数。
#常用属性
name/names:参数名称，和type参数二选一（必填）
#例如:
#单个参数配置:
#@LKARespose(name="code",...)
#多个参数配置:
#@LKARespose(names={"code","msg","data"},...)
    #或者
#@LKAResposes({
    #@LKARespose(name="code",...),
    #@LKARespose(name="msg",...),
    #@LKARespose(name="data",...)
#})
value/values:参数作用（必填）
description/descriptions:参数的描述（选填）
dataType/dataTypes:参数数据类型，默认String.class（选填）
isArray/isArrays:是否是集合或数组，默认false（选填）
type:出参对象类型，和name/names参数二选一（必填）
group:和type配合使用，对象参数分组，可过滤没必要的参数（选填）
#父参数
parentName:父参名称（选填）
parentValue:父参作用（选填）
parentDescription:父参描述（选填）
parentIsArray:父参是否是数组或集合（选填）
#爷参数
grandpaName:爷参名称（选填）
grandpaValue:爷参作用（选填）
grandpaDescription:爷参描述（选填）
grandpaIsArray:爷参是否是数组或集合（选填）
```
### 5.6.2 简单Map集合出参
```java
/**
 我们回过头来看之前用过的一个接口-获取用户信息，这个接口响应参数是一个Map集合,这个Map集合结构比较简单，就  三个不带嵌套结构的属性，这个描述非常简单，一条@LKARespose注解搞定。
*/
@LKAMethod("获取用户信息")
@LKAParam(names= {"name","age-n","roleType-n","token"},values= {"用户名^张三","年龄^22","角色类型^1","授权token^aa"})
@LKARespose(names= {"code","msg","data"},values= {"状态码","消息","数据"})
@PostMapping("getUsers/{roleType}")
public Map<String,Object> getUsers(
    String name,
    Integer age,
    @PathVariable("roleType")Integer roleType,//path参数
    @RequestHeader("token")String token) { //header参数
    Map<String,Object> map = new HashMap<>();
    map.put("code",200);
    map.put("msg","获取信息成功");
    map.put("data","姓名："+name+",年龄："+age+",角色类型："+(roleType==1?"经理":"员工")+",token："+token);
    return map;
}
```
效果图：

![](https://img-blog.csdnimg.cn/20200731154807202.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

### 5.6.3 对象出参
```java
/**
如果出参是一个对象，且对象有加@LKAModel及属性有加@LKAProperty注解，那么Lkadoc会自动扫描该出参对象，我们可以看如下接口入参和出参都是一个对象，那么就不需注解去描述出参和入参了，非常简洁。
*/
@LKAMethod(value="复杂的对象传参",contentType=ContentType.JSON)
@PostMapping("addUser")
public User addUser(@RequestBody @LKAGroup("addUser") User user) {
    return user;
}
```

效果图1：

![](https://img-blog.csdnimg.cn/20200731154830120.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

效果图2：
![](https://img-blog.csdnimg.cn/20200731154848518.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

## 5.7 复杂的Map结构出参

### 5.7.1 说明
&emsp;&emsp;当一个接口出参是一个带嵌套结构的Map集合，我们该怎么描述它呢？如果涉及到对象，我们可以用@LKARespose注解的type属性去指定对象类型，如果涉及到多层嵌套结构，我们可以通过@LKARespose注解的parentXXX和grandpaXXX属性来指定。

### 5.7.2 测试代码
```java
/**在LKADemoController类中加一个测试接口*/
@LKAMethod(value="响应参数复杂的Map结构用法")
@LKAResposes({
    @LKARespose(names= {"code","msg"},values= {"状态码","消息"}),
    @LKARespose(name="total",value="总记录数",parentName="result",parentValue="响应数据"),
    @LKARespose(type=User.class,parentName="users",parentIsArray=true,parentValue="用户对象列表",grandpaName="result")
})
@GetMapping("getMap")
public Map<String,Object> getMap() {
    Map<String,Object> map = new HashMap<>();
    map.put("code",200);
    map.put("msg","操作成功！");
    Map<String,Object> data = new HashMap<>();
    data.put("total",10);
    List<User> users = new ArrayList<>();
    User user1 = new User();
    user1.setName("张三");
    User user2 = new User();
    user2.setName("李四");
    users.add(user1);
    users.add(user2);
    data.put("users",users);
    map.put("result",data);
    return map;
}
```

表格展示效果图：

![](https://img-blog.csdnimg.cn/20200731154909163.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)
JSON格式化展示效果图：

![](https://img-blog.csdnimg.cn/20200731154924489.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

### 5.7.3 超过3层嵌套结构用法技巧

&emsp;&emsp;通过parentXXX和grandPaXXX相关属性用一条@LKARespose注解只能一次性描述1到3级节点，如果有5级或者10级节点该怎么办？ 也有解决办法，非常简单，但注解可能会比较多，例如有这么一个结构{a:{b:{c:{d:1}}}},我们可以这么做:
 @LKARespose(name="a",value="一级"),
 @LKARespose(name="b",value="二级",parentName="a"),
 @LKARespose(name="c",value="三级",parentName="b"),
 @LKARespose(name="d",value="四级",parentName="c")


### 5.7.4 测试代码
```java
/**在LKADemoController类中加一个测试接口*/
@LKAMethod(value="超过3层嵌套结构用法技巧")
@LKAResposes({
    @LKARespose(name="a",value="一级"),
    @LKARespose(name="b",value="二级",parentName="a"),
    @LKARespose(name="c",value="三级",parentName="b"),
    @LKARespose(name="d",value="四级",parentName="c")
})
@GetMapping("getMoreMap")
public Map<String,Object> getMoreMap(){
    Map<String,Object> mapa= new HashMap<>();
    Map<String,Object> mapb= new HashMap<>();
    Map<String,Object> mapc= new HashMap<>();
    Map<String,Object> mapd= new HashMap<>();
    mapa.put("a",mapb);
    mapb.put("b",mapc);
    mapc.put("c",mapd);
    mapd.put("d",1);
    return mapa;
}
```

效果图：

![](https://img-blog.csdnimg.cn/20200731154943829.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

## 5.8 复杂的对象结构出参

### 5.8.1 准备一个响应封装对象

```java
@LKAModel
public class ApiResult {
    @LKAProperty(value="响应状态",description="200-正常,其它-错误")
    private String code;
    @LKAProperty(value="响应消息")
    private String msg;
    @LKAProperty(value="响应数据")
    private Map<String,Object> result = new HashMap<>();
    
    private ApiResult() {}
    
    public static ApiResult ok() {
        ApiResult res = new ApiResult();
        return res;
    }
    
    public ApiResult put(String key,Object value) {
        this.result.put(key, value);
        return this;
    }
    ..........get/set方法...........
}
```

### 5.8.2 准备一个测试接口

```java
/**
这个方法其实和上面5.7.2那个测试方法响应参数结构是一样的，不一样的地方是一个是Map,一个是ApiResut对象。但是我们发现这个方法在响应参数描述是少用一个注解：
@LKARespose(names= {"code","msg"},values= {"状态码","消息"})
这是因为ApiResult对象已经通过@LKAProperty注解描述过"code","msg"属性了，Lkadoc会去自动扫描带有@LKAModel注解的响应对象。还有如果@LKARespose注解描述的参数和对象里面的属性一致的话，@LKARespose注解描述的参数会覆盖掉对象里面的属性
*/
@LKAMethod(value="响应参数复杂的对象结构用法")
@LKAResposes({
    @LKARespose(name="total",value="总记录数",parentName="result",parentValue="响应数据"),
    @LKARespose(type=User.class,parentName="users",parentIsArray=true,parentValue="用户对象列表",grandpaName="result")
})
@PostMapping("getObj")
public ApiResult getObj() {
    List<User> users = new ArrayList<>();
    User user1 = new User();
    user1.setName("张三");
    User user2 = new User();
    user2.setName("李四");
    users.add(user1);
    users.add(user2);
    return ApiResult.ok().put("total",10).put("users",users);
}
```

效果图：

![](https://img-blog.csdnimg.cn/20200731155006871.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

## 5.9 响应参数对象属性分组
&emsp;&emsp;@LKARespose注解的group属性也可以实现响应参数分组，使用原理和请求参数分组是一样的。

### 5.9.1 准备一个对象属性过滤器

```java
/**
对象字段过滤器(为了模拟真实业务场景加了这个工具，与Lkadoc没有任何关联。)
这里用到了一个fastJson的jar工具
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.31</version>
</dependency>
*/
public class FieldsFilter {

    public static<T> Map<String,Object> filter(T object,List<String> fieldNames){
        if(object == null){
            return null;
        }
        Map<String,Object> map = new HashMap<>();
        //把object转换成json对象
        JSONObject json = JSON.parseObject(JSON.toJSONString(object));
        //判断要过滤的字段
        for (String fieldName : fieldNames) {
            Object value = json.get(fieldName);
            map.put(fieldName,value);
        }
        return map;
    }

    public static<T> List<Map<String,Object>> filter(List<T> list,List<String> fields){
        if(list == null || list.size() == 0){
            return null;
        }
        List<Map<String,Object>> maps = new ArrayList<>();
        for (Object object : list) {
            Map<String, Object> filter = filter(object, fields);
            maps.add(filter);
        }
        return maps;
    }
}
```

### 5.9.2 给对象属性分组

```java
/**
我们可以看到，这里一共有两个分组，一个是'addUser'给之前一个叫addUser接口入参数使用的，另一个是'getObj'这个就是我们等下要演示响应对象属性分组用的。'getObj'组包含的属性有User对象的用户名称、年龄、用户爱好，Role对象的角色名称。
*/
@LKAModel
public class User {
    @LKAProperty(value="用户ID",hidden=true)//hidden设置成true，该不会在UI界面展示
    private Integer id;
    @LKAProperty(value="用户名称^张三",groups= {"addUser","getObj"})
    private String name;
    @LKAProperty(value="年龄^20",required=false,description="范围0-120",groups= {"addUser","getObj"})
    private String age;
    @LKAProperty(value="角色对象",type=Role.class,groups= {"getObj"})
    private Role role;
    @LKAProperty(value="用户爱好^运动",isArray=true,groups= {"getObj"})
    private String[] likes;
    @LKAProperty(value="地址信息",isArray=true,type=Address.class,groups= {"addUser"})
    private List<Address> addresses;
    ..........get/set方法...........
}

@LKAModel
public class Role {
    @LKAProperty(value="角色id^1")
    private Integer id;
    @LKAProperty(value="角色名称^经理",groups= {"getObj"})
    private String name;
    ..........get/set方法...........
}
```

### 5.9.3 测试代码： 

```java
/**我们修改一下5.8.2的测试接口如下*/
@LKAMethod(value="响应参数复杂的对象结构用法")
@LKAResposes({
  @LKARespose(name="total",value="总记录数",parentName="result",parentValue="响应数据"),
  @LKARespose(type=User.class,group="getObj",parentName="users",parentIsArray=true,parentValue="用户对象列表",grandpaName="result")
})
@PostMapping("getObj")
public ApiResult getObj() {
    List<User> users = new ArrayList<>();
    User user1 = new User();
    user1.setName("张三");
    User user2 = new User();
    user2.setName("李四");
    users.add(user1);
    users.add(user2);
    Role role = new Role();
    role.setId(1);
    role.setName("经理");
    user1.setRole(role);
    return ApiResult.ok().put("total",10).put("users",        FieldsFilter.filter(users,Arrays.asList("name","age","likes","role")));
}
```

效果图：

![](https://img-blog.csdnimg.cn/20200731155031408.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

# 6. 辅助功能

## 6.1 自定义接口或属性标签
&emsp;&emsp;如果我们要对某个特定的接口或属性增加一些说明信息，例如某个接口新增加了一个属性或修改了某个属性等等，这时我们可以在UI界面给接口或属性增加相应的标签即可。
如果想删除该标签，就在接口或属性名称上再次双击就可以删除了。


![](https://img-blog.csdnimg.cn/20200731155046949.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

![](https://img-blog.csdnimg.cn/20200731155103181.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

## 6.2 导出PDF或MarkDown文档
&emsp;&emsp;如果需提供接口文档给第三方进行对接，可以使用Lkadoc的导出功能，Lkadoc支持导出标准化格式的PDF或MarkDown接口文档，功能非常强大，能满足大部分场景需求。（目前只支持导出本地项目的接口）
&emsp;&emsp;导出PDF文档前需要检查系统是否存在simsun.ttc字体，如果系统没有这个字体的话，导出PDF文档中文不能正确显示。 windows系统字体路径：C:/Windows/fonts/simsun.ttc
linux系统字体路径：/usr/share/fonts/win/simsun.ttc
mac系统字体路径：/System/Library/Fonts/simsun.ttc

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200805180724360.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)


PDF效果截图：

![](https://img-blog.csdnimg.cn/20200731155150650.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)MD效果截图
![MD效果图](https://img-blog.csdnimg.cn/20200805180546345.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70#pic_center)


## 6.3 多项目聚合
&emsp;&emsp;当我们在使用微服务或多个子项目时，我们可以把多个项目的接口文档信息聚合到一个UI界面，只需要在@LKADocument注解配置serverNames属性即可：
&emsp;&emsp;serverNames="租房系统-192.168.0.77:9010,缴费系统-192.168.0.77:8888"
&emsp;&emsp;多个项目之间用英文“,”号隔开，“-”符号左右是项目名称，右边是项目地址，也可以是域名，这样我们就可以在UI界面自由的在当前项目和配置好的其它项目切换接口信息了。

切换项目后的效果图：
![](https://img-blog.csdnimg.cn/20200731155208356.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

## 6.4 UI风格切换

```
如果你对某一种UI界面颜色腻了，Lkadoc还支持切换不同风格的颜色，满足你不安分的心。
```

切换风格效果图：

![](https://img-blog.csdnimg.cn/20200731155224937.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

# 7. 数据校验

&emsp;&emsp;数据校验是Lkadoc 1.2.0版新增加的模块，基于过滤器和拦截器实现的功能，支持query、path、header入参校验，能满足大部分业务场景。数据校验与接口文档是完全独立的两个模块，当把接口文档功能模块关闭并不会影响数据校验模块。


## 7.1 准备工作
&emsp;&emsp;需要在@LKADocument注解(在application文件中配置无效)把validation属性设置成true，代表开启数据校验功能，默认是关闭状态。例:@LKADocument(validation=true)


## 7.2 规则说明

```yaml
#数据校验主要是依靠@LKAProperty和@LKAParam注解的如下属性实现的
#一条@LKAParam注解描述多个参数，那么只能给每个参数加一个校验规则
#一条@LKAParam注解描述一个参数，那么可以给每个参数加多个校验规则

valids:数据校验常用规则或正则匹配，一个参数可设置多个规则
#valids可接收一个数字串数组，Lkadoc里面预置了一个校验常量类V，里面的常量有：
    V.NOTNULL:对象、集合、数组、字符串、包装类不能为null，字符串可以为空
    V.NOTBLANK:字符串不能为null也不能为空（一般只用在字符串）
    V.NULL:对象、集合、数组、字符串、包装类必须为null
    V.NOTEMPTY:集合、数组不能为null，元素个数不能为0；字符串不能为null也不能为空;对象不能为null
    V.URL:参数必须是一个URL
    V.EMAIL:参数必须是email格式
    V.PAST:日期必须在当前日期的过去
    V.FUTURE:日期必须在当前日期的未来
    正则表达式:例如11位手机号-^[1]\\d{10}$

msgs:数据校验消息，和valids规则一一对应，如果msgs没有设置，会有默认错误提示

range:数值范围限制判断，包括整数和小数
#例如: 
#range="1-10^取值范围是1到10":"-"左边代表最小值，右边代表最大值，"^"右边代表提示消息。
#range="*-10^取值不能大10":"-"左边如果是"*"号代表不限制最小值，右边代表最大值，"^"右边代表提示消息。
#range="1-*":"-"左边代表最小值，右边如果是"*"号代表不限制最大值，如果后面没有"^"会有默认错误提示。

size:集合、数组大小限制判断
#用法和range一样

length:字符串长度限制判断
#用法和range一样
```

## 7.3 ValidDataException异常对象

### 7.3.1 说明
&emsp;&emsp;如果接口有参数数据校验不通过，Lkadoc会抛出一个ValidDataException异常，可通过该异常对象的getMessage()方法获取所有没有校验通过的错误提示信息字符串，多个会用“;”隔开。除此之外ValidDataException异常对象还可以通过getErrors()方法获取所有没有校验通过的错误信息的Map集合，key对应的是参数名称，value对应的是校验错误信息。大家可以很方便的定制化返回错误结果信息。


### 7.3.2 在全局异常中处理校验信息

```java
package com.lkad;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import com.lk.api.exception.ValidDataException;

/**
 * 注意事项:
 * 1.全局异常的包位置必须要在抛出异常接口位置的上层或同层
 * 2.全局异常的包位置必须要在启动类的下层或同层
 */
@ControllerAdvice
public class ExceptionController {
    
     /**
     *     定义要捕获的异常可以多个 @ExceptionHandler({})
     *  下面只是一个演示代码，仅供参考！
     * @return 响应结果
     */
    @ExceptionHandler(ValidDataException.class)
    @ResponseBody
    public Map<String,Object> validExceptionHandler(ValidDataException e) {
        Map<String, String> errors = e.getErrors();
        Set<String> keySet = errors.keySet();
        for (String key : keySet) {
            String value = errors.get(key);
            System.out.println(key+"-"+value);
        }
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("code",500);
        map.put("message",e.getMessage());
        return map;
    }
}
```

## 7.4 案例演示

### 7.4.1 NOTNULL

```
1.支持对集合、数组、字符串、对象、包装类请求参数判断不能为null,否则抛出ValidDataException
2.字符串可以为空串
3.不支持path参数校验，因为如果path参数为null的话，会改变url地址
4.如果没有设置msgs校验错误提示信息会有默认提示-xxx值不能为NULL。(xxx代表参数名称)
```

测试代码

```java 
package com.lkad.api;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lk.api.annotation.*;
import com.lk.api.constant.V;

@LKAType("数据校验功能演示")
@RestController
@RequestMapping("valids")
public class ValidsController {
    
    /**没有加msgs校验提示信息，Lkadoc会有默认提示信息*/
    @LKAMethod("NOTNULL")
    @LKAParam(names= {"name","pwd","age"},values= {"用户名","密码","年龄"},valids= {V.NOTNULL,V.NOTNULL,V.NOTNULL})
    @PostMapping("testNotNull")
    public Map<String,Object> testNotNull(String name,String email,Integer age) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("code",200);
        map.put("msg","登录成功");
        return map;
    }
}
```

效果图

![](https://img-blog.csdnimg.cn/20200731155247422.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

全局异常在控制台打印的信息

![](https://img-blog.csdnimg.cn/20200731155305550.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

### 7.4.2 NOTBLANK

```
1.支持对字符串参数判断不能为null也不能为空串,否则抛出ValidDataException
2.不支持path参数校验，因为如果path参数为null的话，会改变url地址
3.如果没有设置msgs校验错误提示信息会有默认提示-xxx值不能为空。(xxx代表参数名称)
```

测试代码

```java
/**在ValidsController类增加如下测试接口*/
@LKAMethod("NOTBLANK")
@LKAParams({
    /**
        注意：
        1.name设置了msgs提示信息，默认提示信息会被msgs信息覆盖掉
        2.@LKAParam只描述一个参数的话，支持设置多个校验规则
    */
    @LKAParam(name="name",value="用户名",valids= {V.NOTBLANK},msgs= {"用户名不能为空"}),
    @LKAParam(name="pwd",value="密码",valids= {V.NOTBLANK}),
    @LKAParam(name="age",value="年龄",dataType=Integer.class,valids= {V.NOTNULL})
})
@PostMapping("testNotBlank")
public Map<String,Object> testNotBlank(String name,String email,Integer age) {
    Map<String,Object> map = new HashMap<String, Object>();
    map.put("code",200);
    map.put("msg","登录成功");
    return map;
}
```

效果图

![](https://img-blog.csdnimg.cn/20200731155327350.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

全局异常在控制台打印的信息

![](https://img-blog.csdnimg.cn/20200731155411712.png)

### 7.4.3 NULL

```
1.支持对集合、数组、字符串、对象、包装类请求参数判断只能为null。否则抛出ValidDataException
2.如果没有设置msgs校验错误提示信息会有默认提示-xxx值只能为NULL。(xxx代表参数名称)
```

测试代码

```java
/**在ValidsController类增加如下测试接口*/
@LKAMethod("NULL")
@LKAParam(name= "age",value= "年龄",dataType=Integer.class,valids= {V.NULL})
@PostMapping("testNull")
public Map<String,Object> testNull(Integer age) {
    Map<String,Object> map = new HashMap<String, Object>();
    map.put("code",200);
    map.put("msg","操作成功");
    return map;
}
```

效果图

![](https://img-blog.csdnimg.cn/2020073115550815.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

全局异常在控制台打印的信息

![](https://img-blog.csdnimg.cn/20200731155524471.png)

### 7.4.4 NOTEMPTY

```
1.支持对集合、数组判断不能为null，元素个数不能为0；字符串不能为null也不能为空;对象、包装类不能为null。否则抛出ValidDataException
2.如果没有设置msgs校验错误提示信息会有默认提示-xxx值不能为NULL/空。(xxx代表参数名称)
```

测试代码

```java
@LKAModel
public class User {
    @LKAProperty(value="用户ID",hidden=true)//hidden设置成true，该不会在UI界面展示
    private Integer id;
    @LKAProperty(value="用户名称^张三",valids= {V.NOTEMPTY})
    private String name;
    @LKAProperty(value="年龄^20",valids= {V.NOTEMPTY})
    private String age;
    @LKAProperty(value="角色对象",type=Role.class,valids= {V.NOTEMPTY})
    private Role role;
    @LKAProperty(value="用户爱好^运动",isArray=true,valids= {V.NOTEMPTY})
    private String[] likes;
    @LKAProperty(value="地址信息",isArray=true,type=Address.class,valids= {V.NOTEMPTY})
    private List<Address> addresses;
    ....get/set方法....
}

/**在ValidsController类增加如下测试接口*/
@LKAMethod(value="NOTEMPTY",contentType=ContentType.JSON)
@PostMapping("testNotEmpty")
public Map<String,Object> testNotEmpty(@RequestBody User user) {
    Map<String,Object> map = new HashMap<String, Object>();
    map.put("code",200);
    map.put("msg","操作成功");
    return map;
}
```

效果图

![](https://img-blog.csdnimg.cn/20200731155546866.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

### 7.4.5 URL、EMAIL、PAST、FUTURE

```
1.URL:参数必须是一个URL，可以为null。否则抛出ValidDataException
2.EMAIL:参数必须是email格式，可以为null。否则抛出ValidDataException
3.PAST:日期必须在当前日期的过去，可以为null。否则抛出ValidDataException
4.FUTURE:日期必须在当前日期的未来，可以为null。否则抛出ValidDataException
```

测试代码

```java
@LKAModel
public class Emp {
@LKAProperty(value="生日",valids= {V.PAST,V.NOTNULL},msgs= {"生日必须在过去","请填写生日信息"})
    private Date birthday;
    @LKAProperty(value="退休日期",valids= {V.FUTURE,V.NOTNULL})
    private Date retirementDate;
    @LKAProperty(value="个人主页",valids= {V.URL})
    private String url;
    @LKAProperty(value="邮箱",valids= {V.EMAIL})
    private String email;
    ....get/set方法....
}

/**在ValidsController类增加如下测试接口*/
@LKAMethod(value="URL、EMAIL、PAST、FUTURE",contentType=ContentType.JSON)
@PostMapping("testUEPF")
public Map<String,Object> testUEPF(@RequestBody Emp emp) {
    Map<String,Object> map = new HashMap<String, Object>();
    map.put("code",200);
    map.put("msg","操作成功");
    return map;
}
```

效果图

![](https://img-blog.csdnimg.cn/2020073115561290.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

### 7.4.6 正则表达式
&emsp;&emsp;Lkadoc还支持正则表达式的匹配，参数值可以为null。正则没有匹配上则抛出ValidDataException

测试代码

```java
@LKAModel
public class Emp {

@LKAProperty(value="生日",valids= {V.PAST,V.NOTNULL},msgs= {"生日必须在过去","请填写生日信息"})
    private Date birthday;
    @LKAProperty(value="退休日期",valids= {V.FUTURE,V.NOTNULL})
    private Date retirementDate;
    @LKAProperty(value="个人主页",valids= {V.URL})
    private String url;
    @LKAProperty(value="邮箱",valids= {V.EMAIL})
    private String email;
    @LKAProperty(value="手机号",valids= {"^[1]\\d{10}$",V.NOTNULL},msgs= {"手机号必须是11位","手机号不能为null"})
    private String mobile;
    ....get/set方法....
}

@LKAMethod(value="URL、EMAIL、PAST、FUTURE",contentType=ContentType.JSON)
@PostMapping("testUEPF")
public Map<String,Object> testUEPF(@RequestBody Emp emp) {
    Map<String,Object> map = new HashMap<String, Object>();
    map.put("code",200);
    map.put("msg","操作成功");
    return map;
}
```

效果图

![](https://img-blog.csdnimg.cn/2020073115562816.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

### 7.4.7 range、size、length

```
range、size、length是单独的属性，可以和valids规则同时存在,参数可以为null
range:数值范围限制判断，包括整数和小数
#例如: 
#range="1-10^取值范围是1到10":"-"左边代表最小值，右边代表最大值，"^"右边代表提示消息。
#range="*-10^取值不能大10":"-"左边如果是"*"号代表不限制最小值，右边代表最大值，"^"右边代表提示消息。
#range="1-*":"-"左边代表最小值，右边如果是"*"号代表不限制最大值，如果后面没有"^"会有默认错误提示。

size:集合、数组大小限制判断
#用法和range一样

length:字符串长度限制判断
#用法和range一样
```

测试代码

```java
@LKAModel
public class Emp {
    @LKAProperty(value="年龄",range="1-130^年龄取值必须在1到130之间")
    private Integer age;
    @LKAProperty(value="爱好",size="2-*^至少填写2个爱好")
    private String[] like;
    @LKAProperty(value="姓名",length="2-6")
    private String name;
    ....get/set方法....
}

@LKAMethod(value="range、size、length",contentType=ContentType.JSON)
@PostMapping("testRSL")
public Map<String,Object> testRSL(@RequestBody Emp emp) {
    Map<String,Object> map = new HashMap<String, Object>();
    map.put("code",200);
    map.put("msg","登录成功");
    return map;
}
```

效果图

![](https://img-blog.csdnimg.cn/20200731155738267.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

全局异常在控制台打印的信息

![](https://img-blog.csdnimg.cn/20200731155753286.png)

### 7.4.8 参数分组

&emsp;&emsp;数据校验也支持参数分组，如要入参是一个对象，且对象设置了分组，那么只有组内属性才会进行数据校验。具体可参考高级应用里面的对象参数分组。


```java
@LKAModel
public class Emp {
    @LKAProperty(value="年龄",range="1-130^年龄取值必须在1到130之间",groups= {"testRSL"})
    private Integer age;
    @LKAProperty(value="爱好",size="2-*^至少填写2个爱好")
    private String[] like;
    @LKAProperty(value="姓名",length="2-6")
    private String name;
    ....get/set方法....
}

@LKAMethod(value="range、size、length",contentType=ContentType.JSON)
@PostMapping("testRSL")
public Map<String,Object> testRSL(@RequestBody @LKAGroup("testRSL") Emp emp) {
    Map<String,Object> map = new HashMap<String, Object>();
    map.put("code",200);
    map.put("msg","登录成功");
    return map;
}
```
效果图
![](https://img-blog.csdnimg.cn/20200731155815625.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2xpdWthaXR5ZG4=,size_16,color_FFFFFF,t_70)

项目开源地址：https://github.com/liukaitydn/LKADocument

**如果大家学得好用，记得给星哦**