package cn.itcast.nio.c2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress(8888));
        sc.write(Charset.defaultCharset().encode("hello\nworld!wedqwqdqwd\n"));
        System.in.read();
    }
}
