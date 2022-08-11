package cn.itcast.netty.c2;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 4种事件类型：
 * accept - 会在有连接请求时触发
 * connect - 客户端连接建立后触发
 * read - 可读事件
 * write - 可写事件
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {
        selector();
    }

    /**
     * Selector
     * @throws IOException
     */
    private static void selector() throws IOException {


        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8888));

        List<SocketChannel> channels = new ArrayList<>();

        while (true) {

        }
    }

    /**
     * 非阻塞
     * @throws IOException
     */
    private static void nonBlocked() throws IOException {
        // 使用nio来理解阻塞模式。单线程

        // ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false); // 设置非阻塞模式
        // 监听端口
        ssc.bind(new InetSocketAddress(8888));

        List<SocketChannel> channels = new ArrayList<>();

        while (true) {
            // accept 建立与客户端连接，SocketChannel用来与客户端之间通信
            final SocketChannel channel = ssc.accept(); // 非阻塞，线程还会继续运行，如果没有连接建立，channel返回是null
            if (channel != null) {
                log.debug("connected... {}", channel);
                channel.configureBlocking(false); // 设置非阻塞模式
                channels.add(channel);
            }
            for (SocketChannel sc : channels) {
                // 接收客户端发送数据
                final int read = sc.read(buffer);// 非阻塞，线程仍然会继续执行，如果没有读到数据，read返回0
                if (read > 0) {
                    buffer.flip();
                    buffer.get(new byte[6]);
                    buffer.clear();
                    log.debug("after read... {}", sc);
                }
            }
        }
    }

    /**
     * 阻塞
     * @throws IOException
     */
    private static void blocked() throws IOException {
        // 使用nio来理解阻塞模式。单线程

        // ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();

        // 监听端口
        ssc.bind(new InetSocketAddress(8888));

        List<SocketChannel> channels = new ArrayList<>();

        while (true) {
            // accept 建立与客户端连接，SocketChannel用来与客户端之间通信
            log.debug("connecting...");
            final SocketChannel channel = ssc.accept(); // 阻塞方法，线程停止运行
            log.debug("connected... {}", channel);
            channels.add(channel);
            for (SocketChannel sc : channels) {
                // 接收客户端发送数据
                log.debug("before read... {}", sc);
                sc.read(buffer); // 阻塞方法，线程停止运行
                buffer.flip();
                buffer.get(new byte[6]);
                buffer.clear();
                log.debug("after read... {}", sc);
            }
        }
    }
}
