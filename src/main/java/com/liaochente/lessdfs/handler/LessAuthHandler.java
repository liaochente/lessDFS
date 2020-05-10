package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.protocol.LessMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理权限验证的handler
 */
public class LessAuthHandler extends SimpleChannelInboundHandler<LessMessage> {
    private static final Logger logger = LoggerFactory.getLogger(LessAuthHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LessMessage lessMessage) throws Exception {
        System.out.println();
        logger.debug("LessAuthHandler.channelRead0 lessMessage = {}", lessMessage);
    }
}
