package kit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;


public class ThreadPoolKit {

    private static ExecutorService pool;
    //双重校验 double-checked locking, 使用volatile防止虫排序
    private static volatile ThreadPoolKit poolKit;

    public static ThreadPoolKit getInstance() {
        if (poolKit == null) {
            synchronized (ThreadPoolKit.class) {
                if (poolKit == null) {
                    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(
                            "video-kit-pool-%d").build();
                    pool = new ThreadPoolExecutor(5, 200, 0L,
                            TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<>(1024),
                            threadFactory, new AbortPolicy());
                    poolKit = new ThreadPoolKit();
                }
            }
        }

        return poolKit;
    }

    public void execute(Runnable task) {
        pool.execute(task);
    }

    public Future<?> submit(Runnable task) {
        return pool.submit(task);
    }

    public void shutDown() {
        if (!pool.isShutdown()) {
            pool.shutdown();
        }
    }

}
