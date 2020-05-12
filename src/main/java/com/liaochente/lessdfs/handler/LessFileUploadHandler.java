package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.constant.LessConfig;
import com.liaochente.lessdfs.protocol.LessMessage;
import com.liaochente.lessdfs.protocol.LessMessageType;
import com.liaochente.lessdfs.protocol.body.data.UploadFileInBodyData;
import com.liaochente.lessdfs.util.LessMessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.UUID;

/**
 * 处理文件上传的handler
 */
public class LessFileUploadHandler extends SimpleChannelInboundHandler<LessMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(LessFileUploadHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LessMessage lessMessage) throws Exception {
        if (lessMessage.getHeader().getType() == LessMessageType.UPLOAD_FILE_IN) {
            LOG.debug("处理报文 >>> {}", lessMessage);

            UploadFileInBodyData bodyData = (UploadFileInBodyData) lessMessage.getBody().getBo();
            byte[] data = bodyData.getData();
            ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
            byteBuffer.put(data);
            byteBuffer.flip();

            String groupPath = LessConfig.GROUP;

            String realSavePath = LessConfig.FILE_ROOT_PATH + groupPath;

            String fileExt = bodyData.getFileExt();

            String fileName = UUID.randomUUID().toString().replaceAll("-", "");

            File file = new File(realSavePath);
            file.mkdirs();

            file = new File(realSavePath + fileName);

            FileChannel fileChannel = new FileOutputStream(file, false).getChannel();
            fileChannel.write(byteBuffer);

            byteBuffer.clear();
            fileChannel.close();

            channelHandlerContext.writeAndFlush(LessMessageUtils.writeUploadFileOutDataToLessMessage(groupPath + fileName, fileExt));
        } else {
            channelHandlerContext.fireChannelRead(lessMessage);
        }
    }
}
