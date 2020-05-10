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
            System.out.println("error: read emtpy message");
            //todo exception
            throw new RuntimeException("error: read emtpy message");
        }

        System.out.println("本次报文可读字节 : " + byteBuf.readableBytes());

        //解析头部
        LessMessageHeader header = this.readToLessMessageHeader(byteBuf);
        //读取body
        LessMessageBody body = this.readToLessMessageBody(header, byteBuf);
        //创建消息对象
        LessMessage lessMessage = new LessMessage();
        lessMessage.setHeader(header);
        lessMessage.setBody(body);

        System.out.println("读取到报文 lessMessage = " + lessMessage);

        list.add(lessMessage);
        //释放
        byteBuf.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("error: 有异常发生，处理异常...");
        super.exceptionCaught(ctx, cause);
    }

    private LessMessageHeader readToLessMessageHeader(final ByteBuf buf) {
        Integer magicCode = buf.readInt();
        if (magicCode != 0x76) {
            System.out.println("error: 文件头不正确");
            //todo exception
            throw new RuntimeException("error: 文件头不正确");
        }

        Integer length = buf.readInt();
        Long sessionId = buf.readLong();
        Byte type = buf.readByte();
        Byte priority = buf.readByte();
        //丢弃48位保留字节
        buf.readBytes(6);

        ByteBuf fileExtByteBuf = buf.readBytes(8);
        byte[] fileExtBytes = new byte[fileExtByteBuf.readableBytes()];
        fileExtByteBuf.readBytes(fileExtByteBuf);

        String fileExt = new String(fileExtBytes);

        LessMessageHeader header = new LessMessageHeader(magicCode, length, sessionId, LessMessageType.convert(type), priority, fileExt);
        return header;
    }

    private LessMessageBody readToLessMessageBody(final LessMessageHeader header, final ByteBuf buf) {
        LessMessageBody body = new LessMessageBody();
        if (LessMessageType.LOGIN_IN == header.getType()) {
            byte[] bodyBytes = new byte[buf.readableBytes()];
            buf.readBytes(bodyBytes);
            String password = new String(bodyBytes);
            LoginBO bo = new LoginBO();
            bo.setPassword(password);
            body.setBo(bo);
        }

        if (LessMessageType.HEARTBEAT_IN == header.getType()) {
            //empty body
        }

        if (LessMessageType.UPLOAD_FILE_IN == header.getType()) {
            ByteBuf bodyByteBuf = buf.readBytes(header.getLength());
            byte[] bodyBytes = new byte[bodyByteBuf.readableBytes()];
            bodyByteBuf.readBytes(bodyBytes);
            UploadFileBO bo = new UploadFileBO();
            bo.setData(bodyBytes);
            body.setBo(bo);
        }
        return body;
    }
}
