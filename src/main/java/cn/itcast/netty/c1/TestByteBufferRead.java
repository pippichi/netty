package cn.itcast.netty.c1;

import java.nio.ByteBuffer;

public class TestByteBufferRead {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();

        // rewind 从头开始读
        buffer.get(new byte[4]);
        buffer.rewind();
        System.out.println((char) buffer.get());

        // mark & reset
        // mark 做一个标记，记录position位置，reset是将position重置到mark的位置
        buffer.clear();
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();
        System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());
        buffer.mark(); // 加标记，索引2的位置
        System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());
        buffer.reset(); // 将position重置到索引2的位置
        System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());

        // get(i) 不会改变索引的位置
        System.out.println((char) buffer.get(3));
    }
}
