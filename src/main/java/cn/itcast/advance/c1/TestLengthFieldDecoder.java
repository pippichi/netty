package cn.itcast.advance.c1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TestLengthFieldDecoder {
    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 1 ,5),
                new LoggingHandler(LogLevel.DEBUG)
        );
        // 4个字节的内容长度，实际内容
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        collectMsg(buf, "hello,world");
        collectMsg(buf, "hi");
        collectMsg(buf, "nihao");
        channel.writeInbound(buf);
    }

    private static void collectMsg(ByteBuf buf, String content) {
        final byte[] bytes = content.getBytes(); // 实际内容
        final int length = bytes.length; // 实际内容长度
        buf.writeInt(length);
        buf.writeByte(1);
        buf.writeBytes(bytes);
    }
}
