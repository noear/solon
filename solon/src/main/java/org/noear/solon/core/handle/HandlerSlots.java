package org.noear.solon.core.handle;

import org.noear.solon.Utils;
import org.noear.solon.annotation.Mapping;

import java.util.Set;

/**
 * 通用处理接口接收槽
 *
 * @author noear
 * @since 1.0
 * */
public interface HandlerSlots {
    /**
     * 添加前置处理
     */
    default void before(String expr, MethodType method, int index, Handler handler) {
    }

    /**
     * 添加后置处理
     */
    default void after(String expr, MethodType method, int index, Handler handler) {
    }


    /**
     * 添加主体处理
     */
    void add(String expr, MethodType method, Handler handler);

    default void add(Mapping mapping, Set<MethodType> methodTypes, Handler handler) {
        String path = Utils.annoAlias(mapping.value(), mapping.path());

        for (MethodType m1 : methodTypes) {
            add(path, m1, handler);
        }
    }
}
