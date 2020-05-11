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

        LOG.debug("本次报文可读字节 : {}", byteBuf.readableBytes());

        //创建消息对象
        LessMessage lessMessage = LessMessageUtils.readByteBufToLessMessage(byteBuf);

        try {
            LOG.debug("读取到报文 lessMessage = {}", lessMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        list.add(lessMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("error: 有异常发生，处理异常...");
        LOG.error("解码异常", cause);
        super.exceptionCaught(ctx, cause);
    }
}
