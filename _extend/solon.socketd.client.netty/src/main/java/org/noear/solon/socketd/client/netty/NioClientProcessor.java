package org.noear.solon.socketd.client.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ListenerProxy;

public class NioClientProcessor extends SimpleChannelInboundHandler<Message> {
    Session session;
    public NioClientProcessor(Session session){
        this.session = session;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        ListenerProxy.getGlobal().onMessage(session, msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        ListenerProxy.getGlobal().onOpen(session);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        ListenerProxy.getGlobal().onClose(session);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ListenerProxy.getGlobal().onError(session, cause);

        //cause.printStackTrace();
        ctx.close();
    }
}