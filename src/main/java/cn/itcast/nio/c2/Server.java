package cn.itcast.nio.c2;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
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
        msg_border();
    }

    /**
     * 处理消息边界
     * @throws IOException
     */
    private static void msg_border() throws IOException {
        // 创建Selector，管理多个Channel
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 建立Selector和Channel的联系（注册）
        // SelectionKey就是将来事件发生后，通过他可以知道事件和哪个Channel的事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        // key只关注accept事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key: {}", sscKey);

        ssc.bind(new InetSocketAddress(8888));
        while (true) {
            // select()方法，没有事件发生时线程阻塞，有事件线程才会恢复运行
            // select() 在事件未处理时，不会阻塞。他会把未处理事件重新放入selectedKeys中。意味着事件发生后要么处理要么取消，不可置之不理
            selector.select();
            // 处理事件，selectedKeys内部包含了所有发生的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator(); // accept, read, ...
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                // 处理key时，要从selectKeys集合中删除，否则下次处理就会有问题
                iter.remove();
                log.debug("key: {}", key);
                if (key.isAcceptable()) { // 如果是accept
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16); // attachment
                    // 将一个byteBuffer作为附件关联到selectionKey上
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}", sc);
//                key.cancel();
                } else if (key.isReadable()) { // 如果是read
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        // 获取selectionKey上关联的attachment
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer); // 如果是正常断开，read方法返回值是-1
                        if (read == -1) {
                            key.cancel();
                        } else {
                            split(buffer);
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel(); // 如果客户端断开了，需要将key取消（从selector的keys集合中真正删除key）
                    }
                }
            }
        }
    }

    /**
     * Selector
     * @throws IOException
     */
    private static void selector() throws IOException {
        // 创建Selector，管理多个Channel
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 建立Selector和Channel的联系（注册）
        // SelectionKey就是将来事件发生后，通过他可以知道事件和哪个Channel的事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        // key只关注accept事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key: {}", sscKey);

        ssc.bind(new InetSocketAddress(8888));
        while (true) {
            // select()方法，没有事件发生时线程阻塞，有事件线程才会恢复运行
            // select() 在事件未处理时，不会阻塞。他会把未处理事件重新放入selectedKeys中。意味着事件发生后要么处理要么取消，不可置之不理
            selector.select();
            // 处理事件，selectedKeys内部包含了所有发生的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator(); // accept, read, ...
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                // 处理key时，要从selectKeys集合中删除，否则下次处理就会有问题
                iter.remove();
                log.debug("key: {}", key);
                if (key.isAcceptable()) { // 如果是accept
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}", sc);
//                key.cancel();
                } else if (key.isReadable()) { // 如果是read
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(4);
                        int read = channel.read(buffer); // 如果是正常断开，read方法返回值是-1
                        if (read == -1) {
                            key.cancel();
                        } else {
                            buffer.flip();
                            System.out.println(Charset.defaultCharset().decode(buffer));
                            buffer.clear();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel(); // 如果客户端断开了，需要将key取消（从selector的keys集合中真正删除key）
                    }
                }
            }
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
                target.flip();
                System.out.println(Charset.defaultCharset().decode(target));
            }
        }
        source.compact();
    }
}
