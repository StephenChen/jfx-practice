package jfx.concurrent.feature;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.*;

/**
 * FeatureTest1
 * <p>
 * runnable 接口实现的没有返回值的并发编程。
 *
 * @author cxy
 * @date 2021/01/08
 */
public class FeatureTest1 {

    public static class Task implements Runnable {
        @Override
        public void run() {
            System.out.println("execute...");
        }
    }

    public static void main(String[] args) {

        // 阿里建议使用 TheadPoolExecutor 构造，并使用自定义或第三方 ThreadFactory 以方便命名线程
        // ExecutorService executorService = Executors.newCachedThreadPool();
        ExecutorService executorService = new ThreadPoolExecutor(
                0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new DefaultThreadFactory("FeatureTest1"));

        for (int i = 0; i < 10; i++) {
            executorService.submit(new Task());
        }

        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
