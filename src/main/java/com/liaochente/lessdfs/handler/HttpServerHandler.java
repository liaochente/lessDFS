package com.liaochente.lessdfs.handler;

import com.liaochente.lessdfs.cache.LRUFileCaches;
import com.liaochente.lessdfs.disk.VirtualDirectoryFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        byte[] data = LRUFileCaches.getCacheBytes(fileName);
        if (data == null) {
            String filePath = VirtualDirectoryFactory.searchFile(fileName);
            Path path = Paths.get(filePath);
            if (path.toFile().exists()) {
                data = Files.readAllBytes(path);
            }
        }

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
