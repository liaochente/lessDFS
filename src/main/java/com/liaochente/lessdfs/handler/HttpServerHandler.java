package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.disk.VirtualDirectoryFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final static Logger LOG = LoggerFactory.getLogger(HttpServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        // 获取请求的uri
        String uri = fullHttpRequest.uri();
        LOG.debug("http.uri = {}", uri);

        //先从缓存读取
        String fileName = uri.substring(1);

        //先从缓存读取
        byte[] data = VirtualDirectoryFactory.searchFileToBytes(fileName);

        // 创建http响应
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        if (HttpHeaderNames.KEEP_ALIVE.equals(fullHttpRequest.headers().get(HttpHeaderNames.CONNECTION))) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderNames.KEEP_ALIVE);
        }
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, data.length);
        response.content().writeBytes(Unpooled.copiedBuffer(data));
        ChannelFuture channelFuture = channelHandlerContext.writeAndFlush(response);
        if (!HttpHeaderNames.KEEP_ALIVE.equals(fullHttpRequest.headers().get(HttpHeaderNames.CONNECTION))) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
