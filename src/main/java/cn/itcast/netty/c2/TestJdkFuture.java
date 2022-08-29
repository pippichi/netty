package cn.itcast.netty.c2;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class TestJdkFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 线程池
        final ExecutorService service = Executors.newFixedThreadPool(2);

        // 提交任务
        Future<Object> future = service.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                log.debug("执行计算");
                Thread.sleep(1000);
                return 50;
            }
        });

        // 主线程通过future来获取结果
        log.debug("等待结果");
        log.debug("结果是：{}", future.get());
    }
}
