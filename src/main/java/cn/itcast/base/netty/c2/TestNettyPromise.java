package cn.itcast.base.netty.c2;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
public class TestNettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 准备EventLoop对象
        EventLoop eventLoop = new NioEventLoopGroup().next();

        // 可以主动创建Promise，结果容器
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        // 示例1
        new Thread(() -> {
            // 任意一个线程执行计算，计算完毕后向promise填充结果
            log.debug("开始计算...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            promise.setSuccess(80);
        }).start();

        // 接受结果的线程
        log.debug("等待结果...");
        log.debug("结果是：{}", promise.get());

        // 示例2
        new Thread(() -> {
            // 任意一个线程执行计算，计算完毕后向promise填充结果
            log.debug("开始计算...");
            try {
                int i = 1 / 0;
                Thread.sleep(1000);
                promise.setSuccess(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
                promise.setFailure(e);
            }
        }).start();

        // 接受结果的线程
        log.debug("等待结果...");
        log.debug("结果是：{}", promise.get());
    }
}
