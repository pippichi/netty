package cn.itcast.chatserver.protocol;

import cn.itcast.chatserver.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@Slf4j
@ChannelHandler.Sharable // 如果当前处理器是无状态的，则可以加@Sharable，反之如果处理器需要保存上一次数据的，就不能加。
/*
 * 必须和LengthFieldBasedFrameDecoder一起使用，确保接到的ByteBuf消息是完整的
 */
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 1. 4个字节的魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 2. 1个字节的版本
        out.writeByte(1);
        // 3. 1个字节的序列化方式 0 -> jdk, 1 -> json
        out.writeByte(0);
        // 4. 1个字节的指令类型
        out.writeByte(message.getMessageType());
        // 5. 4个字节
        out.writeInt(message.getSequenceId());
        // 无意义，对齐填充用的（原本协议长度：4 + 1 + 1 + 1 + 4 + 4 = 15，没有达到2的整数倍，因此再写入一个字节）
        out.writeByte(0xff);
        // 6. 获取内容的字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(message);
        byte[] bytes = bos.toByteArray();
        // 7. 长度
        out.writeInt(bytes.length);
        // 8. 写入内容
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 解码就是编码的逆工程
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte(); // 读取无意义字节，跳过不处理
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        if (serializerType == 0) {
            // 约定好的为0即表示jdk序列化
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Message message = (Message) ois.readObject();
            log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
            log.debug("{}", message);
            out.add(message);
        }
    }
}
