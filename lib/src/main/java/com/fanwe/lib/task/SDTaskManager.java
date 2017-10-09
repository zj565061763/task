package com.fanwe.lib.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhengjun on 2017/9/12.
 */
public class SDTaskManager
{
    private static SDTaskManager sInstance;

    private static final int DEFAULT_CORE_POOL_SIZE = 3;
    private static final int DEFAULT_MAX_POOL_SIZE = 128;
    private static final int DEFAULT_KEEP_ALIVE = 1;

    private static final ExecutorService DEFAULT_EXECUTOR = new ThreadPoolExecutor(
            DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE,
            DEFAULT_KEEP_ALIVE, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    private SDTaskManager()
    {
    }

    public static SDTaskManager getInstance()
    {
        if (sInstance == null)
        {
            synchronized (SDTaskManager.class)
            {
                if (sInstance == null)
                {
                    sInstance = new SDTaskManager();
                }
            }
        }
        return sInstance;
    }

    public Future<?> submit(Runnable runnable)
    {
        Future<?> future = DEFAULT_EXECUTOR.submit(runnable);
        if (runnable instanceof SDTask)
        {
            SDTask task = (SDTask) runnable;
            task.mFuture = future;
        }
        return future;
    }

}
