package com.liaochente.lessdfs.main;

import com.liaochente.lessdfs.constant.LessConfig;
import com.liaochente.lessdfs.disk.VirtualDirectoryFactory;
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
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

/**
 * LessDFS启动引导
 */
public class LessDFSBootstrap {

    private final static Logger LOG = LoggerFactory.getLogger(LessDFSBootstrap.class);

    private final static CountDownLatch ACTIVE_SERVER_COUNT = new CountDownLatch(2);

    public static void main(String[] args) {
        try {
            LessConfig.init();
            VirtualDirectoryFactory.init();
        } catch (IOException | IllegalAccessException e) {
            LOG.error("初始化配置文件失败", e);
            return;
        }

        LessDFSBootstrap.start();
    }

    /**
     * 启动服务
     */
    private final static void start() {
        Thread fileServerThread = new Thread("文件服务器 - 启动线程") {
            @Override
            public void run() {
                startFileServer();
                ACTIVE_SERVER_COUNT.countDown();
            }
        };
        fileServerThread.start();

        if (LessConfig.isHttp()) {
            Thread httpServerThread = new Thread("http服务器 - 启动线程") {
                @Override
                public void run() {
                    startHttpServer();
                    ACTIVE_SERVER_COUNT.countDown();
                }
            };

            httpServerThread.start();
        } else {
            ACTIVE_SERVER_COUNT.countDown();
        }

        try {
            ACTIVE_SERVER_COUNT.await();
        } catch (InterruptedException e) {
            LOG.error("ACTIVE_SERVER_COUNT - InterruptedException", e);
        }
    }

    /**
     * 启动文件服务器
     */
    private final static void startFileServer() {
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup(LessConfig.getBossgroup());
        EventLoopGroup workerLoopGroup = new NioEventLoopGroup(LessConfig.getWorkgroup());

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossLoopGroup, workerLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, LessConfig.getSoBacklog())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));

                            socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(LessConfig.getMaxFrameLength(), 0, 4,
                                    0, 4));
                            socketChannel.pipeline().addLast(new LengthFieldPrepender(4));

                            socketChannel.pipeline().addLast(new LessDecodeHandler());
                            socketChannel.pipeline().addLast(new LessAuthHandler());
                            socketChannel.pipeline().addLast(new LessUploadFileHandler());
                            socketChannel.pipeline().addLast(new LessDownloadFileHandler());
                            socketChannel.pipeline().addLast(new LessDeleteFileHandler());
                            socketChannel.pipeline().addLast(new LessExceptionHandler());
//                            socketChannel.pipeline().addLast(new IdleStateHandler(60,60,120, TimeUnit.SECONDS));

                        }
                    });
            ChannelFuture future = serverBootstrap.bind(LessConfig.getPort()).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOG.error("LessDFS 服务中断", e);
        } finally {
            bossLoopGroup.shutdownGracefully();
            workerLoopGroup.shutdownGracefully();
        }
    }

    /**
     * 启动Http服务器
     */
    private final static void startHttpServer() {
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup(LessConfig.getBossgroup());
        EventLoopGroup workerLoopGroup = new NioEventLoopGroup(LessConfig.getWorkgroup());

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossLoopGroup, workerLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, LessConfig.getSoBacklog())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            socketChannel.pipeline().addLast(new HttpServerCodec());
                            socketChannel.pipeline().addLast("httpAggregator",new HttpObjectAggregator(512*1024));
                            socketChannel.pipeline().addLast(new ChunkedWriteHandler());
                            socketChannel.pipeline().addLast(new HttpServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(LessConfig.getHttpPort()).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOG.error("LessDFS 服务中断", e);
        } finally {
            bossLoopGroup.shutdownGracefully();
            workerLoopGroup.shutdownGracefully();
        }
    }
}
