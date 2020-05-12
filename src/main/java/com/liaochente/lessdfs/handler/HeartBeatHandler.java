package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.protocol.LessMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理心跳
 */
public class HeartBeatHandler extends SimpleChannelInboundHandler<LessMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LessMessage lessMessage) throws Exception {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }
}
