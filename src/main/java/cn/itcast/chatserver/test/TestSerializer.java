package cn.itcast.chatserver.test;

import cn.itcast.chatserver.config.Config;
import cn.itcast.chatserver.message.LoginRequestMessage;
import cn.itcast.chatserver.message.Message;
import cn.itcast.chatserver.protocol.MessageCodecSharable;
import cn.itcast.chatserver.protocol.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

public class TestSerializer {

    public static void main(String[] args) {
        final MessageCodecSharable CODEC = new MessageCodecSharable();
        final LoggingHandler LOGGING = new LoggingHandler();
        final EmbeddedChannel channel = new EmbeddedChannel(LOGGING, CODEC, LOGGING);

        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
//        channel.writeOutbound(message);
        final ByteBuf buf = msg2Bytes(message);
        channel.writeInbound(buf);
    }

    public static ByteBuf msg2Bytes(Message msg) {
        final int algorithm = Config.getSerializerAlgorithm().ordinal();
        final ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
        out.writeBytes(new byte[]{1,2,3,4});
        out.writeByte(1);
        out.writeByte(algorithm);
        out.writeByte(msg.getMessageType());
        out.writeInt(msg.getSequenceId());
        out.writeByte(0xff);
        final byte[] bytes = Serializer.Algorithm.values()[algorithm].serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        return out;
    }
}
