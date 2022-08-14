package cn.itcast.netty.c1;

import java.nio.ByteBuffer;

public class TestByteBufferExam {
    public static void main(String[] args) {
        /*
          网络上有多条数据发送给服务端，数据之间使用 \n 进行分割
          但由于某种原因这些数据在接收时，被进行了重新组合，例如原始数据3条为
           Hello,world\n
           I'm zhangsan\n
           How are you?\n
          变成了下面两个byteBuffer（这种现象叫粘包，半包(粘包：存在大于等于两条完整数据合并成一条；半包：发送数据的服务器缓冲区大小决定了他一次能发多少数据，
          有时候一整条数据一次发不完就会分多次发)）
           Hello,world\nI'm zhangsan\nHo
           w are you?\n
          现在要求编写程序将错乱的数据恢复成原始的按 \n 分割的数据
         */
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,world\\nI'm zhangsan\\nHo".getBytes());
        split(source);
        source.put("w are you?\\n".getBytes());
        split(source);
    }

    /**
     * 读取一条完整的信息
     * @param source
     */
    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); ++i) {
            // 找到一条完整的信息
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                // 把整条完整消息存入新的ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从source读，向target写
                for (int j = 0; j < length; ++j) {
                    target.put(source.get());
                }
            }
        }
        source.compact();
    }
}
