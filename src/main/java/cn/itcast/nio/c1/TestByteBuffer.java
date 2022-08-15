package cn.itcast.nio.c1;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {
        // FileChannel
        // 1.输入输出流；2、RandomAccessFile
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                // 从 channel 读取数据，向 buffer 写入
                int len = channel.read(buffer);
                log.debug("读取到的字节符长度：{}", len);
                if (len == -1) {
                    break;
                }
                // 打印 buffer 的内容
                buffer.flip(); // 切换成读模式
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
                    log.debug("读取到的字节符：{}", (char) b);
                }
                buffer.clear(); // 切换成写模式
            }
        } catch (IOException e) {
        }
    }
}
