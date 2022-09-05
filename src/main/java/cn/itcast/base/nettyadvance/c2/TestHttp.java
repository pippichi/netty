package cn.itcast.base.nettyadvance.c2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class TestHttp {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG))
                            .addLast(new HttpServerCodec())
//                            .addLast(new ChannelInboundHandlerAdapter() {
//                                @Override
//                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                    log.debug("{}", msg.getClass());
//
//                                    // 不方便，不推荐。可以使用SimpleChannelInboundHandler过滤接收关心的那部分信息
//                                    if (msg instanceof HttpRequest) { // 请求行，请求头
//                                    } else if (msg instanceof HttpContent) { // 请求体
//                                    }
//                                }
//                            });
                            .addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
                                    // 获取请求
                                    log.debug("请求行：{}", msg.uri());
                                    log.debug("请求头：{}", msg.headers());

                                    // 返回响应
                                    DefaultFullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                                    byte[] bytes = "<h1>hello, world!</h1>".getBytes();
                                    response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, bytes.length); // 如果不设定内容长度，浏览器会一直等待接收数据（标签页一直转圈圈）
                                    response.content().writeBytes(bytes);

                                    // 写回响应
                                    ctx.writeAndFlush(response);
                                }
                            });
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(8080)).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }
}
