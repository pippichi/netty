package cn.itcast.base.nio.c1;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TestByteBufferString {

    public static void main(String[] args) {
        // 1.字符串转为ByteBuffer
        ByteBuffer buffer1 = ByteBuffer.allocate(16);
        buffer1.put("hello".getBytes());

        // 2.Charset
        Charset.defaultCharset(); // 默认字符集
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello"); // 标准字符集

        // 3.wrap
        buffer2 = ByteBuffer.wrap("hello".getBytes());

        // 4.转为字符串
        buffer1.flip();
        System.out.println(StandardCharsets.UTF_8.decode(buffer1));

        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer2);
        System.out.println(charBuffer.toString());


    }
}
