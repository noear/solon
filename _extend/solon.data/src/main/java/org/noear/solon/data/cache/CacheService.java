package org.noear.solon.data.cache;

/**
 * 缓存服务接口（用于支持@Cache相关注解）
 *
 * @author noear
 * @since 1.0
 * */
public interface CacheService {
    /**
     * 保存
     *
     * @param key 缓存键
     * @param obj 对象
     * @param seconds 秒数
     */
    void store(String key, Object obj, int seconds);

    /**
     * 获取
     *
     * @param key 缓存键
     */
    Object get(String key);

    /**
     * 移除
     *
     * @param key 缓存键
     */
    void remove(String key);
}
