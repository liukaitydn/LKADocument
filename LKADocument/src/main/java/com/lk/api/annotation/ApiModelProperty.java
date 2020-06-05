package com.lk.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;

/**
 * 	用在方法或参数上或实体类属性的注解
 * 	作用：标识参数
 * @author liukai
 * @date 2019-12-11
 */
@Documented
@Target({ElementType.METHOD,ElementType.FIELD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiModelProperty {
	/**入参model类型(如果设置该属性值，说明入参的是一个对象，其它属性就不用设置)*/
	Class<?> type() default Object.class;
	
	/**参数名称*/
	String value() default "";
	
	/**是否必须*/
	boolean required() default true;
	
	/**参数说明*/
	String description() default "";
	
	/**测试数据*/
	String testData() default "";
	
	/**参数位置
	 * header-请求头获取：@RequestHeader
     * query-请求对象获取：@RequestParam
     * rest-用于restful接口获取：@PathVariable*/
	String paramType() default "query";
	
	/**是否是数组*/
	boolean isArray() default false;
	
	/**分组**/
	String[] groups() default {};
	
	/**是否隐藏**/
	boolean hidden() default false;
}
