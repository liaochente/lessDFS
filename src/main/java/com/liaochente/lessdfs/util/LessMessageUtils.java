package com.liaochente.lessdfs.util;

import com.liaochente.lessdfs.constant.LessStatus;
import com.liaochente.lessdfs.protocol.LessMessage;
import com.liaochente.lessdfs.protocol.LessMessageBody;
import com.liaochente.lessdfs.protocol.LessMessageHeader;
import com.liaochente.lessdfs.protocol.LessMessageType;
import com.liaochente.lessdfs.protocol.body.data.DeleteFileInBodyData;
import com.liaochente.lessdfs.protocol.body.data.DownloadFileBodyData;
import com.liaochente.lessdfs.protocol.body.data.UploadFileInBodyData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 报文处理工具
 */
public class LessMessageUtils {

    private final static Logger LOG = LoggerFactory.getLogger(LessMessageUtils.class);

    /**
     * 报文头
     */
    public final static Integer MAGIC_CODE = 0x294;

    /**
     * 生成操作失败应答报文
     *
     * @param lessStatus
     * @return
     */
    public final static ByteBuf writeErrorToLessMessage(LessMessageType type, long sessionId, LessStatus lessStatus) {
        ByteBuf byteBuf = Unpooled.buffer(20);
        byteBuf.writeInt(MAGIC_CODE);
        byteBuf.writeLong(sessionId);
        byteBuf.writeByte((byte) type.getType());
        byteBuf.writeByte(0);
        byteBuf.writeByte((byte) lessStatus.getStatus());
        byteBuf.writeInt(lessStatus.getMessage().length());
        byteBuf.writeBytes(lessStatus.getMessage().getBytes());
        return byteBuf;
    }

    /**
     * 生成文件上传应答报文
     *
     * @param fileName
     * @param fileExt
     * @return
     */
    public final static ByteBuf writeUploadFileOutDataToLessMessage(long sessionId, String fileName, String fileExt) {
        ByteBuf byteBuf = Unpooled.buffer(20);
        byteBuf.writeInt(MAGIC_CODE);
        byteBuf.writeLong(sessionId);
        byteBuf.writeByte((byte) LessMessageType.UPLOAD_FILE_OUT.getType());
        byteBuf.writeByte(0);
        byteBuf.writeByte((byte) LessStatus.OK.getStatus());
        byteBuf.writeInt(fileName.length());
        byteBuf.writeBytes(fileName.getBytes());
        byteBuf.writeInt(fileExt.length());
        byteBuf.writeBytes(fileExt.getBytes());

        return byteBuf;
    }

    /**
     * 生成文件下载应答报文
     *
     * @param fileName
     * @param data
     * @return
     */
    public final static ByteBuf writeDownloadFileOutDataToLessMessage(long sessionId, String fileName, byte[] data) {
        ByteBuf byteBuf = Unpooled.buffer(20);
        byteBuf.writeInt(MAGIC_CODE);
        byteBuf.writeLong(sessionId);
        byteBuf.writeByte((byte) LessMessageType.DOWNLOAD_FILE_OUT.getType());
        byteBuf.writeByte(0);
        byteBuf.writeByte((byte) LessStatus.OK.getStatus());
        byteBuf.writeInt(fileName.length());
        byteBuf.writeBytes(fileName.getBytes());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);

        return byteBuf;
    }

    /**
     * 生成文件删除应答报文
     *
     * @return
     */
    public final static ByteBuf writeDeleteFileOutDataToLessMessage(long sessionId) {
        ByteBuf byteBuf = Unpooled.buffer(20);
        byteBuf.writeInt(MAGIC_CODE);
        byteBuf.writeLong(sessionId);
        byteBuf.writeByte((byte) LessMessageType.DELETE_FILE_OUT.getType());
        byteBuf.writeByte(0);
        byteBuf.writeByte((byte) LessStatus.OK.getStatus());
        return byteBuf;
    }

    /**
     * 读取缓冲区字节数据并转换为LessMessage Object
     *
     * @param buf
     * @return
     */
    public final static LessMessage readByteBufToLessMessage(final ByteBuf buf) {
        //解析头部
        LessMessageHeader header = readByteBufToLessMessageHeader(buf);
        //读取body
        LessMessageBody body = readByteBufToLessMessageBody(header, buf);
        //创建消息对象
        LessMessage lessMessage = new LessMessage();
        lessMessage.setHeader(header);
        lessMessage.setBody(body);
        return lessMessage;
    }

    /**
     * 读取缓冲区字节数据并转换为LessMessageHeader Object
     *
     * @param buf
     * @return
     */
    private final static LessMessageHeader readByteBufToLessMessageHeader(final ByteBuf buf) {
        Integer magicCode = buf.readInt();
        LOG.debug("读取到头部标记 magicCode={}", magicCode);
        if (!MAGIC_CODE.equals(magicCode)) {
            LOG.debug("error|文件头不正确");
            //todo exception
            throw new RuntimeException("error: 文件头不正确");
        }
        long sessionId = buf.readLong();
        LOG.debug("读取到会话标记 sessionId={}", sessionId);

        byte type = buf.readByte();

        LOG.debug("读取到操作标记 type={}", type);

        byte priority = buf.readByte();
        byte status = buf.readByte();

        int pwdLength = buf.readInt();
        byte[] pwdBytes = new byte[pwdLength];
        buf.readBytes(pwdBytes);
        String password = new String(pwdBytes);

        LessMessageHeader header = new LessMessageHeader(magicCode, sessionId, LessMessageType.convert(type), priority, LessStatus.convert(status), password);
        return header;
    }

    /**
     * 读取缓冲区字节数据并转换为LessMessageBody Object
     *
     * @param header
     * @param buf
     * @return
     */
    private final static LessMessageBody readByteBufToLessMessageBody(final LessMessageHeader header, final ByteBuf buf) {
        LessMessageBody body = new LessMessageBody();

        if (LessMessageType.HEARTBEAT_IN == header.getType()) {
            //empty body
        }

        if (LessMessageType.UPLOAD_FILE_IN == header.getType()) {
            byte[] tempBytes;
            int fileExtLength = buf.readInt();
            tempBytes = new byte[fileExtLength];
            buf.readBytes(tempBytes);
            String fileExt = new String(tempBytes);

            int dataLength = buf.readInt();
            byte[] data = new byte[dataLength];
            buf.readBytes(data);

            UploadFileInBodyData bodyData = new UploadFileInBodyData();
            bodyData.setFileExt(fileExt);
            bodyData.setData(data);
            body.setBo(bodyData);
        }

        if (LessMessageType.DOWNLOAD_FILE_IN == header.getType()) {
            int filePathLength = buf.readInt();

            byte[] tempBytes = new byte[filePathLength];

            buf.readBytes(tempBytes);
            String filePath = new String(tempBytes);
            DownloadFileBodyData bodyData = new DownloadFileBodyData();
            bodyData.setFileName(filePath);
            body.setBo(bodyData);
        }

        if (LessMessageType.DELETE_FILE_IN == header.getType()) {
            int filePathLength = buf.readInt();

            byte[] tempBytes = new byte[filePathLength];

            buf.readBytes(tempBytes);
            String filePath = new String(tempBytes);
            DeleteFileInBodyData bodyData = new DeleteFileInBodyData();
            bodyData.setFileName(filePath);
            body.setBo(bodyData);
        }
        return body;
    }

}
