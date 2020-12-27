package com.github.wang.jie.thread;

import com.github.wang.jie.UserDetailInfo;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class InheritableThreadLocalDefectDemo {
    private static InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        inheritableThreadLocal.set("threadLocal做不到的事");


        // 子线程
        new Thread(() -> {
            // 输出为"threadLocal做不到的事"
            System.out.println(inheritableThreadLocal.get());
        }).start();

        // 线程池复用
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("ProcessMessage-pool-%d").build();
        ExecutorService executorService = new ThreadPoolExecutor(1, 1, 2000,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(200),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());

        Future<?> submit = executorService.submit(() -> {
            // 线程池中新创建的线程是可以显示数据
            System.out.println(inheritableThreadLocal.get());

            // 假设某个线程并没有设置，在后期复用数据时，是不是会显示数据
            inheritableThreadLocal.set(null);
        });
        Object o = submit.get();

        executorService.submit(() -> {
            // 复用的新线程无法获取数据
            System.out.println(inheritableThreadLocal.get());
        });
        Object o1 = submit.get();

        executorService.shutdown();
    }
}
