package com.dev.jzw.helper.picture;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @company 上海道枢信息科技-->
 * @anthor created by jingzhanwu
 * @date 2018/3/13 0013
 * @change
 * @describe 只有一个线程的线程池
 **/
public class IOThread {
    private static ExecutorService singleThread;

    static Executor getSingleThread() {
        if (singleThread == null) {
            singleThread = Executors.newSingleThreadExecutor();
        }
        return singleThread;
    }
}
