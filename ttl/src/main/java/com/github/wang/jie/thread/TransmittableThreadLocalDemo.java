package com.github.wang.jie.thread;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.github.wang.jie.UserDetailInfo;
import com.github.wang.jie.UserInfo;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class TransmittableThreadLocalDemo {
    private static TransmittableThreadLocal<String> transmittableThreadLocal = new TransmittableThreadLocal<>();
    private static TransmittableThreadLocal<UserInfo> testTTL = new TransmittableThreadLocal<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        UserInfo userInfo = UserInfo.builder()
                .normalMessage("normalMessage")
                .userDetailInfo(UserDetailInfo.builder().innerMessage("innerMessage").build())
                .build();
        testTTL.set(userInfo);
        transmittableThreadLocal.set("全都能看见");

        // 线程池复用
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("ProcessMessage-pool-%d").build();
        ExecutorService executorService = new ThreadPoolExecutor(1, 1, 2000,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(200),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());
        ExecutorService ttlExecutorService = TtlExecutors.getTtlExecutorService(executorService);

        Future<?> submit = ttlExecutorService.submit(() -> {
            // 线程池中新创建的线程是可以显示数据
            System.out.println(transmittableThreadLocal.get());

            String normalMessage = testTTL.get().getNormalMessage();
            System.out.println("normalMessage = " + normalMessage);
            String innerMessage = testTTL.get().getUserDetailInfo().getInnerMessage();
            System.out.println("innerMessage = " + innerMessage);
//            testTTL.get().setUserDetailInfo(null);
            // 假设某个线程并没有设置，在后期复用数据时，是不是会显示数据
            transmittableThreadLocal.set(null);
        });
        Object o = submit.get();

        System.out.println(testTTL.get().getUserDetailInfo());
        transmittableThreadLocal.set("变一下吧");
        ttlExecutorService.submit(() -> {
            // 复用的线程获取数据
            System.out.println(transmittableThreadLocal.get());
        });
        Object o1 = submit.get();

        ttlExecutorService.shutdown();

    }
}
