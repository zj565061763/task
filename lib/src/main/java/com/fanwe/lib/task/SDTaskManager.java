package com.fanwe.lib.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    private Map<Runnable, SDTaskInfo> mMapRunnable = new WeakHashMap<>();

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

    public SDTaskInfo submit(Runnable runnable)
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
    public synchronized SDTaskInfo submit(Runnable runnable, Object tag)
    {
        Future<?> future = DEFAULT_EXECUTOR.submit(runnable);

        SDTaskInfo info = new SDTaskInfo();
        info.setFuture(future);
        info.setTag(tag);

        mMapRunnable.put(runnable, info);

        return info;
    }

    public synchronized SDTaskInfo getTaskInfo(Runnable runnable)
    {
        SDTaskInfo info = mMapRunnable.get(runnable);
        if (info != null && info.isDone())
        {
            mMapRunnable.remove(runnable);
            info = null;
        }
        return info;
    }

    public synchronized List<Map.Entry<Runnable, SDTaskInfo>> getTaskInfo(Object tag)
    {
        List<Map.Entry<Runnable, SDTaskInfo>> listInfo = new ArrayList<>();
        if (tag != null && !mMapRunnable.isEmpty())
        {
            for (Map.Entry<Runnable, SDTaskInfo> item : mMapRunnable.entrySet())
            {
                SDTaskInfo info = item.getValue();
                if (tag.equals(info.getTag()))
                {
                    listInfo.add(item);
                }
            }
        }
        return listInfo;
    }

    /**
     * 取消Runnable
     *
     * @param runnable
     * @param mayInterruptIfRunning true-如果线程已经执行有可能被打断
     * @return true-申请取消成功
     */
    public synchronized boolean cancel(Runnable runnable, boolean mayInterruptIfRunning)
    {
        SDTaskInfo info = getTaskInfo(runnable);
        if (info == null)
        {
            return false;
        }

        info.cancel(mayInterruptIfRunning);
        mMapRunnable.remove(runnable);
        return true;
    }

    /**
     * 根据tag取消Runnable
     *
     * @param tag
     * @param mayInterruptIfRunning true-如果线程已经执行有可能被打断
     * @return 申请取消成功的数量
     */
    public synchronized int cancelTag(Object tag, boolean mayInterruptIfRunning)
    {
        if (mMapRunnable.isEmpty() || tag == null)
        {
            return 0;
        }

        int count = 0;

        Iterator<Map.Entry<Runnable, SDTaskInfo>> it = mMapRunnable.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<Runnable, SDTaskInfo> item = it.next();
            SDTaskInfo info = item.getValue();
            if (info.isDone())
            {
                it.remove();
            } else
            {
                if (tag.equals(info.getTag()))
                {
                    info.cancel(mayInterruptIfRunning);
                    it.remove();
                    count++;
                }
            }
        }
        return count;
    }
}
