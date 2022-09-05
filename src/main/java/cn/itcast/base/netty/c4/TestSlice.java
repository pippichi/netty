package cn.itcast.base.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static cn.itcast.base.netty.c4.TestByteBuf.log;

public class TestSlice {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        log(buf);

        // 在切片过程中没有发生数据复制
        ByteBuf f1 = buf.slice(0, 5);
        ByteBuf f2 = buf.slice(5, 5);
        log(f1);
        log(f2);
//        f1.writeByte('x'); out of bounds
        f1.setByte(0, 'b');
        log(buf);
        log(f1);

        // retain 引用计数 + 1，防止误释放内存
        f1.retain();
        System.out.println("释放原有byteBuf内存");
        buf.release(); // release 引用计数 - 1
        log(f1);
        f1.release();
        log(buf);

    }
}
