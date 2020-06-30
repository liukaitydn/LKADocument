package com.lk.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 	用在controller类上的注解
 * 	作用：标识接口类型
 * @author liukai
 */
@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface LKAType {
	/*类名称*/
	String value()  default "";
	/*类描述*/
	String description() default "";
	
	/*是否隐藏**/
	boolean hidden() default false;
	
	//String version() default "v1.0";
}
