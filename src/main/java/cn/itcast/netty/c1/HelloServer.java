package cn.itcast.netty.c1;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServer {
    public static void main(String[] args) {
        // 启动器，负责组装netty组件，启动服务器
        new ServerBootstrap()
                // BossEventLoop，WorkerEventLoop(selector, thread), group组
                .group(new NioEventLoopGroup())
                // 选择服务器的ServerSocketChannel实现
                .channel(NioServerSocketChannel.class)
                // boss 负责处理连接worker(child)负责处理读写
                .childHandler(
                        // channel代表和客户端进行数据读写的通道 Initializer初始化，负责添加别的handler
                        new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nsc) throws Exception {
                        // 添加具体handler
                        nsc.pipeline().addLast(new StringDecoder()); // 将ByteBuf转换为字符串
                        nsc.pipeline().addLast(new ChannelInboundHandlerAdapter() { // 自定义handler
                            @Override // 读事件
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                // 打印上一步转换好的字符串
                                log.info((String) msg);
                            }
                        });
                    }
                })
                // 绑定监听端口
                .bind(8888);
    }
}
