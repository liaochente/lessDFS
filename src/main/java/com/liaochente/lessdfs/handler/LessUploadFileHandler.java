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
            LessConfig.VirtualDirectory virtualDirectory = LessConfig.getVirtualDirectory();
            String groupPath = LessConfig.getGroup();
            String fileExt = bodyData.getFileExt();
            /*
                存储路径规则：虚拟目录的实际路径 + 随机用户名
                返给客户端用于下载的key = 虚拟目录的虚拟名称 + 随机用户名
             */
            String fileName = UUID.randomUUID().toString().replaceAll("-", "");
            String filePath = virtualDirectory.getRealPath().toString() + "/" + fileName;
            Files.write(Paths.get(filePath), data);
            String shortName = virtualDirectory.getVirtualPath() + "/" + fileName;
            LOG.debug("文件保存成功，返回文件key={}", shortName);
            channelHandlerContext.writeAndFlush(LessMessageUtils.writeUploadFileOutDataToLessMessage(lessMessage.getHeader().getSessionId(), shortName, fileExt));
        } else {
            channelHandlerContext.fireChannelRead(lessMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
