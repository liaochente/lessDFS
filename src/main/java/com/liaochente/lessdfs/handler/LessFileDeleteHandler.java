package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.constant.LessConfig;
import com.liaochente.lessdfs.protocol.LessMessage;
import com.liaochente.lessdfs.protocol.LessMessageType;
import com.liaochente.lessdfs.protocol.body.data.DeleteFileInBodyData;
import com.liaochente.lessdfs.protocol.body.data.DownloadFileBodyData;
import com.liaochente.lessdfs.util.LessMessageUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;

/**
 * 处理文件删除
 */
public class LessFileDeleteHandler extends SimpleChannelInboundHandler<LessMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LessMessage lessMessage) throws Exception {
        if (lessMessage.getHeader().getType() == LessMessageType.DELETE_FILE_IN) {
            DeleteFileInBodyData bodyData = (DeleteFileInBodyData) lessMessage.getBody().getBo();
            String fileName = bodyData.getFileName();
            //查找要下载的文件
            File file = new File(LessConfig.FILE_ROOT_PATH + fileName);
            if (file.exists()) {
                file.delete();
            }
            channelHandlerContext.writeAndFlush(LessMessageUtils.writeDeleteFileOutDataToLessMessage());
        } else {
            channelHandlerContext.fireChannelRead(lessMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
