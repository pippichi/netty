package cn.itcast.chatserver.server;

import cn.itcast.chatserver.protocol.ProtocolFrameDecoder;
import cn.itcast.chatserver.server.handler.*;
import cn.itcast.customprotocol.protocol.MessageCodecSharable;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
//        MessageCodec MESSAGE_CODEC = new MessageCodec();
        final MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        final LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
        final ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();
        final GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();
        final GroupJoinRequestMessageHandler GROUP_JOIN_HANDLER = new GroupJoinRequestMessageHandler();
        final GroupMembersRequestMessageHandler GROUP_MEMBERS_HANDLER = new GroupMembersRequestMessageHandler();
        final GroupQuitRequestMessageHandler GROUP_QUIT_HANDLER = new GroupQuitRequestMessageHandler();
        final GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();
        final QuitHandler QUIT_HANDLER = new QuitHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    // ????????????????????? ????????????????????? ??? ?????????????????????
                    // 5s ?????????????????????channel??????????????????????????? IdleState#READER_IDLE ??????
                    ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                    // ChannelDuplexHandler ??????????????????????????????????????????
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        // ????????????????????????????????? IdleState#READER_IDLE ??????
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            // ????????????????????????
                            if (event.state() == IdleState.READER_IDLE) {
                                log.debug("?????? 5s ?????????????????????");
                            }
                        }
                    });
                    ch.pipeline().addLast(LOGIN_HANDLER);
                    ch.pipeline().addLast(CHAT_HANDLER);
                    ch.pipeline().addLast(GROUP_CREATE_HANDLER);
                    ch.pipeline().addLast(GROUP_JOIN_HANDLER);
                    ch.pipeline().addLast(GROUP_MEMBERS_HANDLER);
                    ch.pipeline().addLast(GROUP_QUIT_HANDLER);
                    ch.pipeline().addLast(GROUP_CHAT_HANDLER);
                    ch.pipeline().addLast(QUIT_HANDLER);
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(8888)).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }
}
