package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.protocol.LessMessage;
import com.liaochente.lessdfs.protocol.LessMessageType;
import com.liaochente.lessdfs.protocol.body.data.DownloadFileBodyData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理文件下载
 */
public class LessFileDownloadHandler extends SimpleChannelInboundHandler<LessMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(LessFileDownloadHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LessMessage lessMessage) throws Exception {
        if (lessMessage.getHeader().getType() == LessMessageType.DOWNLOAD_FILE_IN) {
            LOG.debug("处理报文 >>> {}", lessMessage);
            LOG.debug("对方想要下载文件 >>> {}", ((DownloadFileBodyData) lessMessage.getBody().getBo()).getFileName());
        } else {
            channelHandlerContext.fireChannelRead(lessMessage);
        }
    }
}
