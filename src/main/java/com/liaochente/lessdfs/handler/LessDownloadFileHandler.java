package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.constant.LessStatus;
import com.liaochente.lessdfs.disk.VirtualDirectoryFactory;
import com.liaochente.lessdfs.protocol.LessMessage;
import com.liaochente.lessdfs.protocol.LessMessageType;
import com.liaochente.lessdfs.protocol.body.data.DownloadFileBodyData;
import com.liaochente.lessdfs.util.LessMessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 处理文件下载
 */
public class LessDownloadFileHandler extends SimpleChannelInboundHandler<LessMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(LessDownloadFileHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LessMessage lessMessage) throws Exception {
        if (lessMessage.getHeader().getType() == LessMessageType.DOWNLOAD_FILE_IN) {
            LOG.debug("对方想要下载文件 >>> {}", ((DownloadFileBodyData) lessMessage.getBody().getBo()).getFileName());

            DownloadFileBodyData bodyData = (DownloadFileBodyData) lessMessage.getBody().getBo();
            String fileName = bodyData.getFileName();
            String filePath = VirtualDirectoryFactory.searchFile(fileName);
            Path path = Paths.get(filePath);
            //查找要下载的文件
            if (path.toFile().exists()) {
                byte[] data = Files.readAllBytes(path);
                channelHandlerContext.writeAndFlush(LessMessageUtils.writeDownloadFileOutDataToLessMessage(lessMessage.getHeader().getSessionId(),
                        fileName, data));
            } else {
                channelHandlerContext.writeAndFlush(LessMessageUtils.writeErrorToLessMessage(LessMessageType.DOWNLOAD_FILE_OUT, lessMessage.getHeader().getSessionId(), LessStatus.NOT_FOUND));
            }
        } else {
            channelHandlerContext.fireChannelRead(lessMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
