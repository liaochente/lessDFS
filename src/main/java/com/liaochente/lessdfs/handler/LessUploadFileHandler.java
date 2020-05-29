package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.cache.CacheFactory;
import com.liaochente.lessdfs.disk.StorageNode;
import com.liaochente.lessdfs.disk.VirtualDirectoryFactory;
import com.liaochente.lessdfs.protocol.LessMessage;
import com.liaochente.lessdfs.protocol.LessMessageType;
import com.liaochente.lessdfs.protocol.body.data.UploadFileInBodyData;
import com.liaochente.lessdfs.util.LessMessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

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
            String fileName = UUID.randomUUID().toString().replaceAll("-", "");

            StorageNode storageNode = VirtualDirectoryFactory.getBestStorageNode(data, fileExt);
            String absolutePath = storageNode.getAbsolutePath();
            String filePath = absolutePath + "/" + fileName;
            Files.write(Paths.get(filePath), data);

            //file key: 用于返给客户端使用
            StringBuffer shotName = new StringBuffer(storageNode.getVirtualDirectoryDrive());
            shotName.append("/");
            shotName.append(storageNode.getParentDrive());
            shotName.append("/");
            shotName.append(storageNode.getDrive());
            shotName.append("/");
            shotName.append(fileName);

            CacheFactory.addCache(shotName.toString(), data, fileExt);

//            String groupPath = LessConfig.getGroup();

            LOG.debug("文件保存成功，返回文件key={}", shotName);
            channelHandlerContext.writeAndFlush(LessMessageUtils.writeUploadFileOutDataToLessMessage(lessMessage.getHeader().getSessionId(), shotName.toString(), fileExt));
        } else {
            channelHandlerContext.fireChannelRead(lessMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
