package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.protocol.LessMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理权限验证的handler
 */
public class LessAuthHandler extends SimpleChannelInboundHandler<LessMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LessMessage lessMessage) throws Exception {

    }
}
