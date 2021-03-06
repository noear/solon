package org.noear.nami;

/**
 * Nami - 编码器
 *
 * @author noear
 * @since 1.2
 * */
public interface Encoder {
    /**
     * 编码
     * */
    String enctype();

    /**
     * 序列化
     * */
    byte[] encode(Object obj);

    /**
     * 预处理
     * */
    void pretreatment(Context ctx);
}
