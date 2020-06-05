package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.disk.VirtualDirectoryFactory;
import com.liaochente.lessdfs.util.ImageUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http服务
 * <p>
 * /fileKey 内联，默认浏览器展示
 * /fileKey?attachment 附件形式
 * /fileKey?ambilight 图片处理
 * /fileKey?ambilight/scale 等比缩放，指定图片宽高为原来的scale%
 * /fileKey?ambilight/w/scale 按宽缩放，指定图片宽度为原来的scale%，高度不变
 * /fileKey?ambilight/h/scale 按高缩放，指定图片高度为原来的scale%，宽度不变
 * /fileKey?ambilight/w/scale 指定目标图片宽度为 Width，高度等比压缩
 * /fileKey?ambilight/h/scale 指定目标图片高度为 Height，宽度等比压缩
 * 限定缩略图的宽度和高度的最大值分别为 Width 和 Height，进行等比缩放
 * 限定缩略图的宽度和高度的最小值分别为 Width 和 Height，进行等比缩放
 * 忽略原图宽高比例，指定图片宽度为 Width，高度为 Height ，强行缩放图片，可能导致目标图片变形
 * 等比缩放图片，缩放后的图像，总像素数量不超过 Area
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final static Logger LOG = LoggerFactory.getLogger(HttpServerHandler.class);

    private class InternalCommand {
        private String uri;

        private boolean ambilight;

        private boolean attachment;

        private int scale;

        public InternalCommand(String uri) {
            if(uri.indexOf("/") == 0) {
                uri = uri.substring(1);
            }
            String[] uris = uri.split("\\?");
            this.uri = uris[0];

            String command = uris[1];
            ambilight = command.indexOf("ambilight") > -1;
            attachment = command.indexOf("attachment") > -1;
            if(ambilight) {
                scale = Integer.parseInt(command.substring("ambilight/".length()));
            }
        }

        public String getUri() {
            return uri;
        }

        public boolean isAmbilight() {
            return ambilight;
        }

        public int getScale() {
            return scale;
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("[");
            sb.append("uri=");
            sb.append(uri);
            sb.append(",");
            sb.append("ambilight=");
            sb.append(ambilight);
            sb.append(",");
            sb.append("attachment=");
            sb.append(attachment);
            sb.append(",");
            sb.append("scale=");
            sb.append(scale);
            sb.append("]");
            return sb.toString();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        // 获取请求的uri
        String uri = fullHttpRequest.uri();
        LOG.debug("http.uri = {}", uri);
        if("/favicon.ico".equals(uri)) {
            channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND));
            return;
        }

        InternalCommand command = new InternalCommand(uri);
        LOG.debug("command = {}", command);
        String fileName = command.getUri();
        //先从缓存读取
        byte[] data = VirtualDirectoryFactory.searchFileToBytes(fileName);

        FullHttpResponse response = null;
        if (data == null) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        } else {
            if(command.isAmbilight()) {
                data = ImageUtils.imageScale(data, command.getScale());
            }
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, data.length);
            response.content().writeBytes(Unpooled.copiedBuffer(data));
        }

        if (HttpHeaderNames.KEEP_ALIVE.equals(fullHttpRequest.headers().get(HttpHeaderNames.CONNECTION))) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderNames.KEEP_ALIVE);
        }

        //返回响应消息
        ChannelFuture channelFuture = channelHandlerContext.writeAndFlush(response);
        if (!HttpHeaderNames.KEEP_ALIVE.equals(fullHttpRequest.headers().get(HttpHeaderNames.CONNECTION))) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR));
    }
}
