package cn.itcast.nio.c1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        try (
                FileChannel from = new FileInputStream("data.txt").getChannel();
                FileChannel to = new FileOutputStream("to.txt").getChannel()
        ) {
//            // 效率高，底层利用操作系统零拷贝进行优化。上限2g数据
//            from.transferTo(0, from.size(), to);

            // 由于transferTo上限2g数据，因此最好这样写
            final long size = from.size();
            for (long left = size; left > 0; ) {
                left -= from.transferTo((size - left), left, to);
            }
        } catch (IOException e) {
        }
    }
}
