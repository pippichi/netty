package cn.itcast.base.nio.c1;

import java.nio.ByteBuffer;

public class TestByteBufferReadWrite {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{(byte) 0x61, (byte) 0x62, (byte) 0x63}); // 97 98 99  'a' 'b' 'c'
        buffer.flip(); // 读模式
        buffer.get(); // 读取第一个，读指针往后移一位
        buffer.compact(); // 压缩buffer， 此时buffer变：98 99  'b' 'c'
        buffer.put((byte) 0x65); // 98 99 100  'b' 'c' 'd'
    }
}
