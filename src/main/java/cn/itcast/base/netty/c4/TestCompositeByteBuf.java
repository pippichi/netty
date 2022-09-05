package cn.itcast.base.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static cn.itcast.base.netty.c4.TestByteBuf.log;

public class TestCompositeByteBuf {
    public static void main(String[] args) {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{1, 2, 3, 4, 5});

        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{5, 6, 7, 8, 9});

        /*ByteBuf buf3 = ByteBufAllocator.DEFAULT.buffer();
        buf3.writeBytes(buf1).writeBytes(buf2);
        log(buf3);*/

        // CompositeByteBuf可避免内存复制
        CompositeByteBuf buffer = ByteBufAllocator.DEFAULT.compositeBuffer();
        buffer.addComponents(true, buf1, buf2);
        buffer.retain(); // 同样也需要注意引用计数问题
        log(buffer);
    }
}
