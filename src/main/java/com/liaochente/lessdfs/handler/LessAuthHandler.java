package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.constant.LessStatus;
import com.liaochente.lessdfs.protocol.LessMessage;
import com.liaochente.lessdfs.protocol.LessMessageType;
import com.liaochente.lessdfs.protocol.body.data.AuthInBodyData;
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

    private static final Long FIXED_SESSION_ID = 654321L;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LessMessage lessMessage) throws Exception {
        if (lessMessage.getHeader().getType() == LessMessageType.LOGIN_IN) {
            LOG.debug("收到认证请求 lessMessage = {}", lessMessage);
            if ("123456".equals(((AuthInBodyData) lessMessage.getBody().getBo()).getPassword())) {
                LOG.debug("认证验证通过，返回携带sessionId的信息");
                channelHandlerContext.writeAndFlush(LessMessageUtils.writeAuthOutDataToLessMessage(FIXED_SESSION_ID));
            } else {
                LOG.debug("认证验证不通过，返回错误信息");
                channelHandlerContext.writeAndFlush(LessMessageUtils.writeErrorToLessMessage(LessMessageType.LOGIN_OUT, LessStatus.FAIL));
            }
        } else {
            LOG.debug("收到{}, 进行认证, lessMessage = {}", lessMessage.getHeader().getType().getName(), lessMessage);
            if (FIXED_SESSION_ID.equals(lessMessage.getHeader().getSessionId())) {
                //校验通过不处理，需要手动执行fireChannelRead，才能进入下一个handler处理
                LOG.debug("认证通过");
                channelHandlerContext.fireChannelRead(lessMessage);
            } else {
                LOG.debug("认证不通过，返回错误信息");
                channelHandlerContext.writeAndFlush(LessMessageUtils.writeErrorToLessMessage(LessMessageType.LOGIN_OUT, LessStatus.FAIL));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
