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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * LessDFS启动引导
 */
public class LessDFSBootstrap {

    private final static Logger LOG = LoggerFactory.getLogger(LessDFSBootstrap.class);

    public static void main(String[] args) {
        try {
            LessConfig.init();
            VirtualDirectoryFactory.init();
            CacheFactory.init();
        } catch (IOException | IllegalAccessException e) {
            LOG.error("初始化配置文件失败", e);
            return;
        }
        LessDFSBootstrap.start();
    }

    private final static void start() {
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
}
