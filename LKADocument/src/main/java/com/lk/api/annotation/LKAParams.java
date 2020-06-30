package com.lk.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 	用在Controller方法上的注解
 * 	作用：标识参数集
 * @author liukai
 */
@Documented
@Target({ElementType.METHOD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface LKAParams {
	/*入参的参数集*/
	LKAParam[] value() default {};
}
