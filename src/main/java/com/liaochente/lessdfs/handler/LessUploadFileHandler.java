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
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.liaochente.lessdfs.constant.LessConfig.FILE_INDEX_MAP;

/**
 * 处理文件上传的handler
 */
public class LessUploadFileHandler extends SimpleChannelInboundHandler<LessMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(LessUploadFileHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LessMessage lessMessage) throws Exception {
        if (lessMessage.getHeader().getType() == LessMessageType.UPLOAD_FILE_IN) {
            LOG.debug("处理报文 >>> {}", lessMessage);

            UploadFileInBodyData bodyData = (UploadFileInBodyData) lessMessage.getBody().getBo();
            byte[] data = bodyData.getData();
            ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
            byteBuffer.put(data);
            byteBuffer.flip();

            String groupPath = LessConfig.group;

            String realSavePath = LessConfig.storegeDir + groupPath;

            String fileExt = bodyData.getFileExt();

            String fileName = UUID.randomUUID().toString().replaceAll("-", "");

            File file = new File(realSavePath);
            file.mkdirs();

            file = new File(realSavePath + fileName);

            FileChannel fileChannel = new FileOutputStream(file, false).getChannel();
            fileChannel.write(byteBuffer);

            byteBuffer.clear();
            fileChannel.close();

            Map<String, String> indexMap = new ConcurrentHashMap<>();
            indexMap.put("fileExt", fileExt);
            indexMap.put("absolutePath", realSavePath + fileName);
            indexMap.put("groupPath", LessConfig.group);

            FILE_INDEX_MAP.put(groupPath + fileName, indexMap);

            channelHandlerContext.writeAndFlush(LessMessageUtils.writeUploadFileOutDataToLessMessage(groupPath + fileName, fileExt));
        } else {
            channelHandlerContext.fireChannelRead(lessMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
