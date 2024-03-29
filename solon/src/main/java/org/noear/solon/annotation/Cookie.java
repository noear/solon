package org.noear.solon.annotation;

import org.noear.solon.core.Constants;

import java.lang.annotation.*;

/**
 * 请求 Cookie（主要修饰参数，很少用到）
 *
 * @author noear
 * @since 1.10
 * */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cookie {
    /**
     * 名字
     * */
    @Alias("name")
    String value() default "";
    /**
     * 名字
     * */
    @Alias("value")
    String name() default "";
    /**
     * 必须的(只做标识，不做检查)
     * */
    boolean required() default false;
    /**
     * 默认值
     * */
    String defaultValue() default Constants.PARM_UNDEFINED_VALUE;
}
