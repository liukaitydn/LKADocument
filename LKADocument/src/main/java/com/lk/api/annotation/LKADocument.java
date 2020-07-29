package com.lk.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Import;
import com.lk.api.controller.LKADController;
import com.lk.api.filter.DataCheckConfig;
import com.lk.api.filter.DataCheckFilter;

/**
 * 	用在SpringBoot项目的启动类上的注解
 * 	作用：开启全局接口文档注解扫描
 * @author liukai
 */
@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Import({LKADController.class,DataCheckConfig.class,DataCheckFilter.class})
@ServletComponentScan
public @interface LKADocument {
	String basePackages() default "";
	String projectName() default "lkadoc接口文档";
	String description() default "智能、便捷、高效！";
	String serverNames() default "";
	String version() default "";
	boolean enabled() default true;
	boolean validation() default false;
}
