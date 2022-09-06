package cn.itcast.source;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

public class TestConnectionTimeout {
    public static void main(String[] args) {
        // 1.客户端通过 .option() 方法配置参数 给 SocketChannel 配置参数

        // 2.服务器端
//        new ServerBootstrap().option() // 是给 ServerSocketChannel 配置参数
//        new ServerBootstrap().childOption() // 是给 SocketChannel 配置参数

        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler());
            final ChannelFuture future = bootstrap.connect(new InetSocketAddress(8888));
            future.sync().channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
