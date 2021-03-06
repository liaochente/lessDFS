package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.disk.VirtualDirectoryFactory;
import com.liaochente.lessdfs.protocol.LessMessage;
import com.liaochente.lessdfs.protocol.LessMessageType;
import com.liaochente.lessdfs.protocol.body.data.DeleteFileInBodyData;
import com.liaochente.lessdfs.util.LessMessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理文件删除
 */
public class LessDeleteFileHandler extends SimpleChannelInboundHandler<LessMessage> {

    private final static Logger LOG = LoggerFactory.getLogger(LessDeleteFileHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LessMessage lessMessage) throws Exception {
        if (lessMessage.getHeader().getType() == LessMessageType.DELETE_FILE_IN) {
            DeleteFileInBodyData bodyData = (DeleteFileInBodyData) lessMessage.getBody().getBo();
            String fileName = bodyData.getFileName();
            VirtualDirectoryFactory.removeFile(fileName);
            channelHandlerContext.writeAndFlush(LessMessageUtils.writeDeleteFileOutDataToLessMessage(lessMessage.getHeader().getSessionId()));
        } else {
            channelHandlerContext.fireChannelRead(lessMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
