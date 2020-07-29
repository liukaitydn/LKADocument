package com.lk.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 	用在方法上的注解
 * 	作用：标识出参
 * @author liukai
 */
@Documented
@Target({ElementType.METHOD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface LKARespose {
	String value() default "";
	/*出参model类型(如果设置该属性值，说明出参的是一个实体对象，其它属性就不用设置)*/
	Class<?> type() default Object.class;
	/*参数名称*/
	String name() default "";
	/*参数说明*/
	String description() default "";
	/*参数类型(基本类型和字符串)*/
	Class<?> dataType() default String.class;
	/*是否是数组*/
	boolean isArray() default false;
	/*父参数*/
	String parentName() default "";
	String parentValue() default "";
	String parentDescription() default "";
	boolean parentIsArray() default false;
	
	/*爷参数*/
	String grandpaName() default "";
	String grandpaValue() default "";
	String grandpaDescription() default "";
	boolean grandpaIsArray() default false;
	
	/*分组*/
	String group() default "";
	
	String[] names() default {};
	String[] descriptions() default{};
	Class<?>[] dataTypes() default {};
	String[] values() default{};
	boolean[] isArrays() default{};
	
	String[] parentNames() default{};
	String[] parentDescriptions() default{};
	boolean[] parentIsArrays() default{};
	String[] parentValues() default {};
	
	String[] grandpaNames() default{};
	String[] grandpaDescriptions() default{};
	boolean[] grandpaIsArrays() default{};
	String[] grandpaValues() default {};
	
	
}
