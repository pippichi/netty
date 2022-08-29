package cn.itcast.netty.c2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;

@Slf4j
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException, IOException {
//        testChannel();
        testChannelFuture();
    }

    private static void testChannelFuture() throws InterruptedException {
        // 2.带有Future、Promise的类型都是有异步方法配套使用，用来处理结果
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override // 在连接建立后被调用
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // 1.连接到服务器
                // 异步非阻塞，main发起了调用，真正执行connect的是 .group(new NioEventLoopGroup()) 中的nio线程
                .connect(new InetSocketAddress(8888)); // 也许是1秒后成功建立连接

        // 2.1.使用sync方法同步处理结果
        /*// sync方法会阻塞当前线程，直到 .connect(new InetSocketAddress(8888)) 成功建立了连接
        channelFuture.sync(); // 这句注释之后就无法成功发送数据，因为拿到的channel根本不是连接建立成功之后的channel
        // 无阻塞向下执行获取channel
        final Channel channel = channelFuture.channel();
        log.debug("channel:{}", channel);
        channel.writeAndFlush("hello, world");*/

        // 2.2.使用addListener(回调对象)方法异步处理结果
        channelFuture.addListener(new ChannelFutureListener() {
            @Override // 在nio线程连接建立好之后，会调用该方法
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                final Channel channel = channelFuture.channel();
                log.debug("channel:{}", channel);
                channel.writeAndFlush("hello, world");
            }
        });
    }

    private static void testChannel() throws InterruptedException, IOException {
        Channel channel = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override // 在连接建立后被调用
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress(8888))
                .sync()
                .channel();
        System.out.println(channel);
        channel.writeAndFlush("hello, world");
        System.in.read();
    }


}
