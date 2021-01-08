package jfx.concurrent.feature;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * FeatureTest2
 * <p>
 * callable 实现的存在返回值的并发编程。（使用 Future 获取返回值，call 的返回值 String 受泛型的影响）
 *
 * @author cxy
 * @date 2021/01/08
 */
public class FeatureTest2 {

    public static class Task implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("execute...");
            return "complete";
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<Future<String>> results = new ArrayList<Future<String>>();
        ExecutorService executorService = new ThreadPoolExecutor(
                0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new DefaultThreadFactory("FeatureTest2"));

        for (int i = 0; i < 10; i++) {
            results.add(executorService.submit(new Task()));
        }
        for (Future<String> future : results) {
            System.out.println(future.get());
        }

        System.out.println("Main complete");

        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
