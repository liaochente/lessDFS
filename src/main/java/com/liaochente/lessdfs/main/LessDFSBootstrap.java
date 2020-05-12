package com.liaochente.lessdfs.main;

import java.lang.System;
import java.util.concurrent.TimeUnit;

import com.liaochente.lessdfs.handler.LessAuthHandler;
import com.liaochente.lessdfs.handler.LessDecodeHandler;
import com.liaochente.lessdfs.handler.LessFileDownloadHandler;
import com.liaochente.lessdfs.handler.LessFileUploadHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.logging.InternalLogLevel;

/**
 *
 */
public class LessDFSBootstrap {

    public static void main(String[] args) {
        LessDFSBootstrap.start();
    }

    private final static void start() {
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup();
        EventLoopGroup workerLoopGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossLoopGroup, workerLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 8)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            //进站handler
                            socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024*100, 0, 4,
                                    0, 4));
                            socketChannel.pipeline().addLast(new LengthFieldPrepender(4));
                            socketChannel.pipeline().addLast(new LessDecodeHandler());
                            socketChannel.pipeline().addLast(new LessAuthHandler());
                            socketChannel.pipeline().addLast(new LessFileUploadHandler());
                            socketChannel.pipeline().addLast(new LessFileDownloadHandler());
                            socketChannel.pipeline().addLast(new IdleStateHandler(60,60,120, TimeUnit.SECONDS));
                            //出站handler
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(8888).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            System.out.println("中断");
            e.printStackTrace();
        } finally {
            bossLoopGroup.shutdownGracefully();
            workerLoopGroup.shutdownGracefully();
        }
    }
}
