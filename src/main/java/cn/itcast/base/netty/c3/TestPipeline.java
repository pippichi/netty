package cn.itcast.base.netty.c3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class TestPipeline {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 通过channel拿到pipeline
                        ChannelPipeline pipeline = ch.pipeline();
                        // 添加入栈处理器（ChannelInboundHandlerAdapter） head -> h1 -> h2 -> h3 -> ... -> tail
                        pipeline.addLast("h1", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("1");
                                ByteBuf buf = (ByteBuf) msg;
                                String name = buf.toString(Charset.defaultCharset());
                                super.channelRead(ctx, name);
                            }
                        }).addLast("h2", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object name) throws Exception {
                                log.debug("2");
                                Student student = new Student(name.toString());
                                super.channelRead(ctx, student); // 将数据传递给下一个handler，如果不调用，调用链就会断开
                            }
                        });

                        pipeline.addLast("h7", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("7");
                                super.write(ctx, msg, promise);
                            }
                        });

                        pipeline.addLast("h3", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("3，结果：{}，class：{}", msg, msg.getClass());
//                                super.channelRead(ctx, msg); channelRead表示执行下一个入栈处理器 由于后续已经没有入栈处理器了所以这个方法可以不写了
//                                ch.writeAndFlush(ctx.alloc().buffer().writeBytes("server...".getBytes()));
                                ctx.writeAndFlush(ctx.alloc().buffer().writeBytes("server...".getBytes())); // 注意NioSocketChannel可以从tail往前找ChannelOutboundHandlerAdapter，ChannelHandlerContext是从当前handler往前找的。因此在这里ChannelHandlerContext只能找到h7而找不到h4、h5、h6
                            }
                        });

                        // 添加出栈处理器（ChannelOutboundHandlerAdapter） head -> .. -> h4 -> h5 -> h6 -> tail 出栈处理器由tail往前执行
                        pipeline.addLast("h4", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("4");
                                super.write(ctx, msg, promise);
                            }
                        }).addLast("h5", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("5");
                                super.write(ctx, msg, promise);
                            }
                        }).addLast("h6", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("6");
                                super.write(ctx, msg, promise);
                            }
                        });
                    }
                })
                .bind(8888);
    }

    @Data
    @AllArgsConstructor
    static class Student {
        private String name;
    }
}
