package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.constant.LessConfig;
import com.liaochente.lessdfs.protocol.LessMessage;
import com.liaochente.lessdfs.protocol.LessMessageType;
import com.liaochente.lessdfs.protocol.body.data.UploadFileInBodyData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;

/**
 * 处理文件上传的handler
 */
public class LessFileUploadHandler extends SimpleChannelInboundHandler<LessMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(LessFileUploadHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LessMessage lessMessage) throws Exception {
        LOG.debug("开始运行");
        if (lessMessage.getHeader().getType() == LessMessageType.UPLOAD_FILE_IN) {
            LOG.debug("处理报文 >>> {}", lessMessage);

            UploadFileInBodyData bodyData = (UploadFileInBodyData) lessMessage.getBody().getBo();
            byte[] data = bodyData.getData();
            ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
            byteBuffer.put(data);
            byteBuffer.flip();

            String newFileName = UUID.randomUUID().toString().replaceAll("-", "");
            File file = new File(LessConfig.FILE_ROOT_PATH + LessConfig.GROUP);
            file.mkdirs();

            file = new File(LessConfig.FILE_ROOT_PATH + LessConfig.GROUP + newFileName + "." + bodyData.getFileExt());

            FileChannel fileChannel = new FileOutputStream(file, false).getChannel();
            fileChannel.write(byteBuffer);

            byteBuffer.clear();
            fileChannel.close();
        }
    }
}
