package org.noear.solon.core.wrap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 方法容器
 *
 * @author noear
 * @since 1.0
 * */
public interface MethodHolder {
    /**
     * 获取函数
     * */
    Method getMethod();
    /**
     * 获取函数参数
     */
    ParamWrap[] getParamWraps();
    /**
     * 获取函数反回类型
     */
    Class<?> getReturnType();

    /**
     * 获取函数所有注解
     * */
    Annotation[] getAnnotations();

    /**
     * 获取函数某种注解
     * */
    <T extends Annotation> T getAnnotation(Class<T> type);
}
