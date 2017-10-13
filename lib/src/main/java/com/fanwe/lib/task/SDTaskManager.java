package com.fanwe.lib.task;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhengjun on 2017/9/12.
 */
public class SDTaskManager
{
    private static SDTaskManager sInstance;

    private static final int DEFAULT_CORE_POOL_SIZE = 3;
    private static final int DEFAULT_MAX_POOL_SIZE = 16;
    private static final int DEFAULT_KEEP_ALIVE = 1;

    private static final ExecutorService DEFAULT_EXECUTOR = new ThreadPoolExecutor(
            DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE,
            DEFAULT_KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    private Map<Runnable, TaskInfo> mMapRunnable = new WeakHashMap<>();

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
        return submit(runnable, null);
    }

    /**
     * 提交要执行的Runnable
     *
     * @param runnable 要执行的Runnable
     * @param tag      对应的tag，可用于取消
     * @return
     */
    public synchronized Future<?> submit(Runnable runnable, Object tag)
    {
        Future<?> future = DEFAULT_EXECUTOR.submit(runnable);

        TaskInfo info = new TaskInfo();
        info.future = future;
        info.tag = tag;
        mMapRunnable.put(runnable, info);

        return future;
    }

    public synchronized TaskInfo getTaskInfo(Runnable runnable)
    {
        return mMapRunnable.get(runnable);
    }

    /**
     * 取消Runnable
     *
     * @param runnable
     * @return
     */
    public synchronized boolean cancel(Runnable runnable)
    {
        TaskInfo info = getTaskInfo(runnable);
        if (info == null)
        {
            return false;
        }
        info.future.cancel(true);
        mMapRunnable.remove(runnable);
        return true;
    }

    /**
     * 根据tag取消Runnable
     *
     * @param tag
     * @return 取消成功的数量
     */
    public synchronized int cancelTag(Object tag)
    {
        if (mMapRunnable.isEmpty() || tag == null)
        {
            return 0;
        }

        int count = 0;

        Iterator<Map.Entry<Runnable, TaskInfo>> it = mMapRunnable.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<Runnable, TaskInfo> item = it.next();
            TaskInfo info = item.getValue();
            if (tag.equals(info.tag))
            {
                info.future.cancel(true);
                it.remove();
                count++;
            }
        }
        return count;
    }

    private static class TaskInfo
    {
        public Future future;
        public Object tag;
    }
}
