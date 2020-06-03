package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.disk.VirtualDirectoryFactory;
import com.liaochente.lessdfs.protocol.LessMessage;
import com.liaochente.lessdfs.protocol.LessMessageType;
import com.liaochente.lessdfs.protocol.body.data.UploadFileInBodyData;
import com.liaochente.lessdfs.util.LessMessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理文件上传的handler
 */
public class LessUploadFileHandler extends SimpleChannelInboundHandler<LessMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(LessUploadFileHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LessMessage lessMessage) throws Exception {
        if (lessMessage.getHeader().getType() == LessMessageType.UPLOAD_FILE_IN) {
            LOG.debug("收到上传报文请求 lessMessage = {}", lessMessage);

            UploadFileInBodyData bodyData = (UploadFileInBodyData) lessMessage.getBody().getBo();
            byte[] data = bodyData.getData();
            String fileExt = bodyData.getFileExt();
            String fileName = VirtualDirectoryFactory.addFile(data, fileExt);

            LOG.debug("文件保存成功，返回文件key={}", fileName);
            channelHandlerContext.writeAndFlush(LessMessageUtils.writeUploadFileOutDataToLessMessage(lessMessage.getHeader().getSessionId(), fileName, fileExt));
        } else {
            channelHandlerContext.fireChannelRead(lessMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
