package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.bo.BaseBO;
import com.liaochente.lessdfs.bo.LoginBO;
import com.liaochente.lessdfs.bo.UploadFileBO;
import com.liaochente.lessdfs.protocol.LessMessage;
import com.liaochente.lessdfs.protocol.LessMessageBody;
import com.liaochente.lessdfs.protocol.LessMessageHeader;
import com.liaochente.lessdfs.protocol.LessMessageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.List;

/**
 * 解析报文并转换为LessMessage对象
 */
public class LessDecodeHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf == null) {
            //todo exception
            return;
        }

        System.out.println("本次报文长度 : " + byteBuf.readableBytes());

        if (byteBuf.readableBytes() == 2) {
            System.out.println(byteBuf.toString());
        }
        //解析头部
        Integer magicCode = byteBuf.readInt();
        if (magicCode != 0x76) {
            System.out.println("文件头不正确");
            //todo exception
            return;
        }

        Integer length = byteBuf.readInt();
        Long sessionId = byteBuf.readLong();
        Byte type = byteBuf.readByte();
        Byte priority = byteBuf.readByte();
        LessMessageHeader header = new LessMessageHeader(magicCode, length, sessionId, LessMessageType.convert(type), priority);
        //丢弃48位保留字节
        byteBuf.readBytes(6);

        //读取body
        LessMessageBody body = new LessMessageBody();

        if (LessMessageType.LOGIN_IN == header.getType()) {
            System.out.println("剩余可读=" + byteBuf.readableBytes());
            byte[] bodyByteBuf = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bodyByteBuf);
            String loginParams = new String(bodyByteBuf);
            LoginBO bo = new LoginBO();
            bo.setUsername(loginParams);
            body.setBo(bo);
        }

        if (LessMessageType.HEARTBEAT_IN == header.getType()) {
            //empty body
        }

        if (LessMessageType.UPLOAD_FILE_IN == header.getType()) {
            ByteBuf bodyByteBuf = byteBuf.readBytes(header.getLength());
            byte[] data = new byte[bodyByteBuf.readableBytes()];
            bodyByteBuf.readBytes(data);
            UploadFileBO bo = new UploadFileBO();
            bo.setData(data);
            body.setBo(bo);
        }

        LessMessage lessMessage = new LessMessage();
        lessMessage.setHeader(header);
        lessMessage.setBody(body);
        System.out.println("读取到报文 lessMessage = " + lessMessage);
        list.add(lessMessage);
    }
}
