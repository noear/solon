package org.noear.nami.channel.socketd;

import org.noear.nami.Decoder;
import org.noear.nami.Encoder;
import org.noear.nami.Nami;
import org.noear.nami.common.Constants;
import org.noear.nami.common.ContentTypes;
import org.noear.socketd.transport.client.ClientSession;

import java.util.function.Supplier;

/**
 * 代理工具
 *
 * @author noear
 * @since 1.10
 */
public class ProxyUtils {
    //真正使用的是 session，服务地址只是占个位
    private static final String VIRTUAL_SERVER = "sd://nami";

    /**
     * 创建接口
     * */
    public static <T> T create(Supplier<ClientSession> sessions, Encoder encoder, Decoder decoder, Class<T> service) {
        return Nami.builder()
                .encoder(encoder)
                .decoder(decoder)
                .headerSet(Constants.HEADER_ACCEPT, ContentTypes.JSON_VALUE) //相当于指定默认解码器 //如果指定不同的编码器，会被盖掉
                .headerSet(Constants.HEADER_CONTENT_TYPE, ContentTypes.JSON_VALUE) //相当于指定默认编码器
                .channel(new SocketdChannel(sessions))
                .upstream(() -> VIRTUAL_SERVER)
                .create(service);
    }
}
