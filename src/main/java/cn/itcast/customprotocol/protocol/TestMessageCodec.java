package cn.itcast.customprotocol.protocol;

import cn.itcast.chatserver.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0), // 设置粘包半包处理器
                new LoggingHandler(LogLevel.DEBUG),
                new MessageCodec()
        );

        // encode
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
        channel.writeOutbound(message);

        // decode
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, byteBuf);
//        // 入栈
//        channel.writeInbound(byteBuf);
        // 测试粘包半包
        ByteBuf s1 = byteBuf.slice(0, 100);
        ByteBuf s2 = byteBuf.slice(100, byteBuf.readableBytes() - 100);
        s1.retain(); // 坑1的解决方案：调用retain之后引用计数变2
        channel.writeInbound(s1); // 坑1：调用writeInbound之后会自动调用release方法将引用计数减为0，此时s1、s2、byteBuf都会被释放掉
        channel.writeInbound(s2);
    }

    static void think() {
        /*
        思考：FRAME_DECODER、LOGGING_HANDLER是否能抽取出来变公共变量供其他EventLoop使用？
        答案：看情况，比方说像FRAME_DECODER，eventLoop1过来发了一个半包，那么FRAME_DECODER会把它记录下来，等待下一次eventLoop1发数据，直到数据完整之后再交给下一个处理器，
        而此时eventLoop2也过来发了一些数据，FRAME_DECODER会把eventLoop2的数据拼接到上一次eventLoop1的数据后面，显然这是不对的，说明FRAME_DECODER不适合被共享使用；再看
        LOGGING_HANDLER，它是无状态的，来一次操作就打印一次日志，因此它可以被共享使用。

        那么怎么知道处理器是否能被共享使用呢？
        进入处理器的类，如果被@Sharable注解标注则表示可以被共享使用。
         */
        LengthFieldBasedFrameDecoder FRAME_DECODER = new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0);
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        EmbeddedChannel channel = new EmbeddedChannel(
                FRAME_DECODER,
                LOGGING_HANDLER,
                new MessageCodec()
        );
    }
}
