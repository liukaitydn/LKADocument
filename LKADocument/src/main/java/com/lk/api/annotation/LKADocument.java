package com.lk.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;
import com.lk.api.controller.LKADController;

/**
 * 	用在SpringBoot项目的启动类上的注解
 * 	作用：开启全局接口文档注解扫描
 * @author liukai
 * @date 2019-12-11
 */
@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Import(LKADController.class)
public @interface LKADocument {
}
