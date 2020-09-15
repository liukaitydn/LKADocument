package com.lk.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 	用在Controller方法上的注解
 * 	作用：标识方法
 * @author liukai
 */
@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiOperation {
	/*方法名称*/
	String value() default "";
	/*方法描述*/
	String notes() default "";
	/*版本号*/
	String version() default "暂无";
	/*contentType类型*/
	String contentType() default "application/x-www-form-urlencoded";
	
	/*是否隐藏*/
	boolean hidden() default false;
	/*作者*/
	String author() default "";
	/*创建时间*/
	String createTime() default "";
	/*修改时间*/
	String updateTime() default "";
	/*是否是下载方法*/
	boolean download() default false;
	/*是否需要token验证*/
	boolean token() default true;
}
