package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.constant.LessConfig;
import com.liaochente.lessdfs.constant.LessStatus;
import com.liaochente.lessdfs.protocol.LessMessage;
import com.liaochente.lessdfs.util.LessMessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理权限验证的handler
 */
public class LessAuthHandler extends SimpleChannelInboundHandler<LessMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(LessAuthHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LessMessage lessMessage) throws Exception {
        LOG.debug("收到{}, 进行认证, lessMessage = {}", lessMessage.getHeader().getType().getName(), lessMessage);
        if (LessConfig.getPassword().equals(lessMessage.getHeader().getPassword())) {
            LOG.debug("认证通过");
            channelHandlerContext.fireChannelRead(lessMessage);
        } else {
            LOG.debug("认证不通过，返回错误信息");
            channelHandlerContext.writeAndFlush(LessMessageUtils.writeErrorToLessMessage(lessMessage.getHeader().getType(), lessMessage.getHeader().getSessionId(), LessStatus.FAIL));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
