package top.angeya.threads;

import java.util.concurrent.*;

/**
 * 通用线程池
 * @author: angeya
 * @date: 2023/10/21 23:09
 * @description:
 */
public class CommonThreadPool {

    private CommonThreadPool(){}

    public static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(5, 8, 60,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(8),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

}
