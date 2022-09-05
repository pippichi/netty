package cn.itcast.base.nio.c3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class TestClient {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress(8888));
        sc.write(Charset.defaultCharset().encode("1234567890"));
        System.in.read();
    }
}
