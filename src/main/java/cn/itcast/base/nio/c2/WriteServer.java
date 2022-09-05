package cn.itcast.base.nio.c2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public class WriteServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8888));
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
//                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    // 由于OP_ACCEPT行为的channel就只有ServerSocketChannel这一个，因此上述写法可以简化成：
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    final SelectionKey scKey = sc.register(selector, SelectionKey.OP_READ, null);

                    // 向客户端发送大量数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 3000000; ++i) {
                        sb.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());
                    // 返回值表示实际写入的字节数
                    final int write = sc.write(buffer);
                    System.out.println(write);
                    // 判断是否有剩余内容
                    if (buffer.hasRemaining()) {
                        // 关注可写事件         1                     4
//                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        // 更优雅的写法
                        scKey.interestOps(scKey.interestOps() | SelectionKey.OP_WRITE);
                        // 把未写完的数据挂到scKey上
                        scKey.attach(buffer);
                    }
                } else if (key.isWritable()) {
                    final ByteBuffer buffer = (ByteBuffer) key.attachment();
                    final SocketChannel sc = (SocketChannel) key.channel();
                    final int write = sc.write(buffer);
                    System.out.println(write);
                    // 全部写完之后，清理操作
                    if (!buffer.hasRemaining()) {
                        key.attach(null); // 清除buffer
                        key.interestOps(key.interestOps() ^ SelectionKey.OP_WRITE); // 不需要关注写事件
                    }
                }
            }
        }
    }
}
