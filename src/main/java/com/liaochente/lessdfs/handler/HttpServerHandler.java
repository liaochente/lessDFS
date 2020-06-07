package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.disk.VirtualDirectoryFactory;
import com.liaochente.lessdfs.http.AmbilightTypeEnum;
import com.liaochente.lessdfs.http.ParamResolver;
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
 * /fileKey?ambilight/er/{scale} 等比缩放，指定图片宽高为原来的scale%
 * /fileKey?ambilight/erw/{scale} 按宽缩放，指定图片宽度为原来的scale%，高度不变
 * /fileKey?ambilight/erh/{scale} 按高缩放，指定图片高度为原来的scale%，宽度不变
 * /fileKey?ambilight/w/{width} 指定目标图片宽度为 Width，高度等比压缩
 * /fileKey?ambilight/h/{height} 指定目标图片高度为 Height，宽度等比压缩
 * /fileKey?ambilight/wh/{width}/{height} 忽略原图宽高比例，指定图片宽度为 Width，高度为 Height ，强行缩放图片，可能导致目标图片变形
 * 等比缩放图片，缩放后的图像，总像素数量不超过x
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final static Logger LOG = LoggerFactory.getLogger(HttpServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        // 获取请求的uri
        String uri = fullHttpRequest.uri();
        LOG.debug("http.uri = {}", uri);
        if ("/favicon.ico".equals(uri)) {
            channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND));
            return;
        }

        ParamResolver paramResolver = new ParamResolver(uri);
        LOG.debug("paramResolver = {}", paramResolver);

        String fileName = paramResolver.getFileUri();
        byte[] data = VirtualDirectoryFactory.searchFileToBytes(fileName);

        FullHttpResponse response = null;
        if (data == null) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        } else {
            //需要使用流光溢彩功能
            if (paramResolver.isAmbilight()) {
                if (paramResolver.getAmbilightTypeEnum() == AmbilightTypeEnum.SCALE_MODE_ER) {
                    data = ImageUtils.imageScale(data, paramResolver.getScale());
                } else if (paramResolver.getAmbilightTypeEnum() == AmbilightTypeEnum.SCALE_MODE_ER_W) {
                    data = ImageUtils.imageScaleWidth(data, paramResolver.getScale());
                } else if (paramResolver.getAmbilightTypeEnum() == AmbilightTypeEnum.SCALE_MODE_ER_H) {
                    data = ImageUtils.imageScaleHeight(data, paramResolver.getScale());
                } else if (paramResolver.getAmbilightTypeEnum() == AmbilightTypeEnum.SCALE_MODE_W) {
                    data = ImageUtils.imageScaleWidth2(data, paramResolver.getWidth());
                } else if (paramResolver.getAmbilightTypeEnum() == AmbilightTypeEnum.SCALE_MODE_H) {
                    data = ImageUtils.imageScaleHeight2(data, paramResolver.getHeight());
                } else if (paramResolver.getAmbilightTypeEnum() == AmbilightTypeEnum.SCALE_MODE_WH) {
                    data = ImageUtils.imageScale(data, paramResolver.getWidth(), paramResolver.getHeight());
                }
            }

            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, data.length);
            response.content().writeBytes(Unpooled.copiedBuffer(data));
        }

        //支持keep_alive
        if (HttpHeaderNames.KEEP_ALIVE.equals(fullHttpRequest.headers().get(HttpHeaderNames.CONNECTION))) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderNames.KEEP_ALIVE);
        }

        //返回响应消息
        ChannelFuture channelFuture = channelHandlerContext.writeAndFlush(response);

        //非keep_alive模式下关闭当前链接
        if (!HttpHeaderNames.KEEP_ALIVE.equals(fullHttpRequest.headers().get(HttpHeaderNames.CONNECTION))) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("http fail", cause);
        ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR)).addListener(ChannelFutureListener.CLOSE);
    }
}
