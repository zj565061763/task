package com.fanwe.lib.task;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
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

    private Map<Runnable, RunnableInfo> mMapRunnable;

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

    private Map<Runnable, RunnableInfo> getMapRunnable()
    {
        if (mMapRunnable == null)
        {
            mMapRunnable = new WeakHashMap<>();
        }
        return mMapRunnable;
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

        RunnableInfo info = new RunnableInfo();
        info.future = future;
        info.tag = tag;
        getMapRunnable().put(runnable, info);

        return future;
    }

    public synchronized RunnableInfo getRunnableInfo(Runnable runnable)
    {
        if (mMapRunnable == null || mMapRunnable.isEmpty() || runnable == null)
        {
            return null;
        }
        return mMapRunnable.get(runnable);
    }

    /**
     * Runnable是否已经被取消
     *
     * @param runnable
     * @return
     */
    public synchronized boolean isCancelled(Runnable runnable)
    {
        RunnableInfo info = getRunnableInfo(runnable);
        if (info == null)
        {
            return false;
        }
        return info.future.isCancelled();
    }

    /**
     * 取消Runnable
     *
     * @param runnable
     * @return
     */
    public synchronized boolean cancel(Runnable runnable)
    {
        RunnableInfo info = getRunnableInfo(runnable);
        if (info == null)
        {
            return false;
        }
        info.future.cancel(true);
        mMapRunnable.remove(runnable);

        if (mMapRunnable.isEmpty())
        {
            mMapRunnable = null;
        }
        return true;
    }

    /**
     * 根据tag取消Runnable
     *
     * @param tag
     * @return 取消成功的数量
     */
    public synchronized int cancel(Object tag)
    {
        if (mMapRunnable == null || mMapRunnable.isEmpty() || tag == null)
        {
            return 0;
        }

        int count = 0;

        Iterator<Map.Entry<Runnable, RunnableInfo>> it = mMapRunnable.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<Runnable, RunnableInfo> item = it.next();
            RunnableInfo info = item.getValue();
            if (tag.equals(info.tag))
            {
                info.future.cancel(true);
                it.remove();
                count++;
            }
        }

        if (mMapRunnable.isEmpty())
        {
            mMapRunnable = null;
        }
        return count;
    }

    private static class RunnableInfo
    {
        public Future future;
        public Object tag;
    }
}
