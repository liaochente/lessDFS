package com.liaochente.lessdfs.main;

import java.io.IOException;
import java.lang.System;

import com.liaochente.lessdfs.constant.LessConfig;
import com.liaochente.lessdfs.handler.*;
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

/**
 *
 */
public class LessDFSBootstrap {

    public static void main(String[] args) {
        try {
            LessConfig.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LessDFSBootstrap.start();
    }

    private final static void start() {
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup();
        EventLoopGroup workerLoopGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossLoopGroup, workerLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, LessConfig.soBacklog)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            //进站handler
                            socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(LessConfig.maxFrameLength, 0, 4,
                                    0, 4));
                            socketChannel.pipeline().addLast(new LengthFieldPrepender(4));
                            socketChannel.pipeline().addLast(new LessDecodeHandler());
                            socketChannel.pipeline().addLast(new LessAuthHandler());
                            socketChannel.pipeline().addLast(new LessUploadFileHandler());
                            socketChannel.pipeline().addLast(new LessDownloadFileHandler());
                            socketChannel.pipeline().addLast(new LessDeleteFileHandler());
//                            socketChannel.pipeline().addLast(new IdleStateHandler(60,60,120, TimeUnit.SECONDS));
                            //出站handler
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(8888).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossLoopGroup.shutdownGracefully();
            workerLoopGroup.shutdownGracefully();
        }
    }
}
