package cn.itcast.advance.c1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Random;

@Slf4j
public class TestFixedLengthFrameDecoderClient {
    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel sc) throws Exception {
                    sc.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG))
                            .addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ByteBuf buf = ctx.alloc().buffer();
                                    char c = '0';
                                    Random r = new Random();
                                    for (int i = 0; i < 10; ++i) {
                                        byte[] bytes = genMsg(c, r.nextInt(10) + 1, 10);
                                        ++c;
                                        buf.writeBytes(bytes);
                                    }
                                    ctx.writeAndFlush(buf);
                                }
                            });
                }
            });
            final ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(8888)).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }

    private static byte[] genMsg(char c, int length, int totalLength) {
        byte[] ans = new byte[totalLength];
        int counter = 0;
        for (int i = 0; i < totalLength; ++i) {
            if (counter < length) {
                ans[i] = (byte) c;
            } else {
                ans[i] = '_';
            }
            ++counter;
        }
        return ans;
    }
}
