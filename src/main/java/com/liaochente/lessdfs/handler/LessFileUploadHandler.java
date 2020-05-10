package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.protocol.LessMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理文件上传的handler
 */
public class LessFileUploadHandler extends SimpleChannelInboundHandler<LessMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LessMessage lessMessage) throws Exception {
        System.out.println("LessFileUploadHandler.readObject = " + lessMessage);
    }
}
