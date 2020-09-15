package com.lk.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 	用在实体类上的注解
 * 	作用：标识对象类型
 * @author liukai
 */
@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiModel {
	/*对象描述*/
	String value() default "";
	/*对象描述*/
	String description() default "";
}
