package com.github.wang.jie.data;

import com.github.wang.jie.UserDetailInfo;
import com.github.wang.jie.UserInfo;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class InheritableThreadLocalDefectDataDemo {
    private static InheritableThreadLocal<UserInfo> infoInheritableDataThreadLocal = new InheritableThreadLocal<>();
    private static MyInheritableThreadLocal<UserInfo> myInheritableThreadLocal = new MyInheritableThreadLocal<>();


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        UserInfo userInfo = UserInfo.builder()
                .normalMessage("normalMessage")
                .userDetailInfo(UserDetailInfo.builder().innerMessage("innerMessage").build())
                .build();
        UserInfo userInfo2 = UserInfo.builder()
                .normalMessage("normalMessage")
                .userDetailInfo(UserDetailInfo.builder().innerMessage("innerMessage").build())
                .build();
        infoInheritableDataThreadLocal.set(userInfo2);
        myInheritableThreadLocal.set(userInfo);

        // 线程池复用
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("ProcessMessage-pool-%d").build();
        ExecutorService executorService = new ThreadPoolExecutor(1, 1, 2000,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(200),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());

        Future<?> submit = executorService.submit(() -> {

            System.out.println(infoInheritableDataThreadLocal.get().getNormalMessage());
            System.out.println(infoInheritableDataThreadLocal.get().getUserDetailInfo().getInnerMessage());
            System.out.println(myInheritableThreadLocal.get().getUserDetailInfo().getInnerMessage());
            // 其他线程修改数据会影响到其他线程
            infoInheritableDataThreadLocal.get().setUserDetailInfo(null);

            myInheritableThreadLocal.get().setUserDetailInfo(null);
        });
        Object o = submit.get();

        // 父线程拿不到数据
        System.out.println(infoInheritableDataThreadLocal.get().getUserDetailInfo());

        System.out.println(myInheritableThreadLocal.get().getUserDetailInfo());
        executorService.shutdown();
    }
}
