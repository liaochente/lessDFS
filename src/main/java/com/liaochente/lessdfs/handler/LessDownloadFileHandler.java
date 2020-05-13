package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.constant.LessConfig;
import com.liaochente.lessdfs.constant.LessStatus;
import com.liaochente.lessdfs.protocol.LessMessage;
import com.liaochente.lessdfs.protocol.LessMessageType;
import com.liaochente.lessdfs.protocol.body.data.DownloadFileBodyData;
import com.liaochente.lessdfs.util.LessMessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;

import static com.liaochente.lessdfs.constant.LessConfig.FILE_INDEX_MAP;

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
            //查找要下载的文件
            File file = new File(LessConfig.storageDir + fileName);
            if (file.exists()) {
                FileChannel fileChannel = new FileInputStream(file).getChannel();
                ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
                fileChannel.read(byteBuffer);

                byte[] data = byteBuffer.array();

                byteBuffer.clear();
                fileChannel.close();

                Map<String, String> indexMap = FILE_INDEX_MAP.get(fileName);
                String fileExt = indexMap.get("fileExt");
                channelHandlerContext.writeAndFlush(LessMessageUtils.writeDownloadFileOutDataToLessMessage(fileName, fileExt, data));
            } else {
                channelHandlerContext.writeAndFlush(LessMessageUtils.writeErrorToLessMessage(LessMessageType.DOWNLOAD_FILE_OUT, LessStatus.NOT_FOUND));
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
