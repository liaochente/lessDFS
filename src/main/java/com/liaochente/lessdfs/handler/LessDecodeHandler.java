package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.protocol.LessMessage;
import com.liaochente.lessdfs.util.LessMessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 解析报文并转换为LessMessage对象
 */
public class LessDecodeHandler extends ByteToMessageDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(LessDecodeHandler.class);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf == null) {
            LOG.debug("error: read emtpy message");
            //todo exception
            throw new RuntimeException("error: read emtpy message");
        }

        //创建消息对象
        LessMessage lessMessage = LessMessageUtils.readByteBufToLessMessage(byteBuf);

        LOG.debug("解析报文 >>> lessMessage = {}", lessMessage);

        list.add(lessMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("解码异常", cause);
        super.exceptionCaught(ctx, cause);
    }
}
