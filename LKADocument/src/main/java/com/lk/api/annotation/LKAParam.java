package com.lk.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 	用在方法或参数上或实体类属性的注解
 * 	作用：标识参数
 * @author liukai
 */
@Documented
@Target({ElementType.METHOD,ElementType.PARAMETER})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface LKAParam {
	String value() default "";
	/*入参model类型(如果设置该属性值，说明入参的是一个对象，其它属性就不用设置)*/
	Class<?> type() default Object.class;
	/*参数名称*/
	String name() default "";
	/*是否必须*/
	boolean required() default true;
	/*参数说明*/
	String description() default "";
	/*参数类型(基本类型和字符串)*/
	Class<?> dataType() default String.class;
	/*测试数据*/
	String testData() default "";
	/*参数位置
	 * header-请求头获取：@RequestHeader
     * query-请求对象获取：@RequestParam
     * rest-用于restful接口获取：@PathVariable*/
	String paramType() default "query";
	/*是否是数组*/
	boolean isArray() default false;
	/*分组**/
	String group() default "";
	
	
	String[] values() default{};
	/*参数名称*/
	String[] names() default{};
	/*是否必须*/
	boolean[] requireds() default{};
	/*参数说明*/
	String[] descriptions() default{};
	/*参数类型(基本类型和字符串)*/
	Class<?>[] dataTypes() default{};
	/*测试数据*/
	String[] testDatas() default{};
	/*参数位置
	 * header-请求头获取：@RequestHeader
     * query-请求对象获取：@RequestParam
     * rest-用于restful接口获取：@PathVariable*/
	String[] paramTypes() default{};
	/*是否是数组*/
	boolean[] isArrays() default{};
	
	/*数据校验规则*/
	String[] valids() default{};
	/*数据校验消息*/
	String[] msgs() default{};
	/*数值范围*/
	String range() default "";
	/*集合大小限制*/
	String size() default "";
	/*字符串大小限制*/
	String length() default "";
}
