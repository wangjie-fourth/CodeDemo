package com.github.wang.jie.thread;

public class ThreadLocalDefectDemo {
    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) {
        threadLocal.set("threadLocal做不到的事");

        // 子线程
        new Thread(() -> {
            // 输出为null
            System.out.println(threadLocal.get());
        }).start();
    }
}
