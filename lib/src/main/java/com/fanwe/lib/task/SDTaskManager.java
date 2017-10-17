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

    private static final int CORE_POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = 64;
    private static final int KEEP_ALIVE = 1;

    private static final ExecutorService DEFAULT_EXECUTOR = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE,
            KEEP_ALIVE, TimeUnit.SECONDS,
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
    public synchronized SDTaskInfo submit(Runnable runnable, String tag)
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

    public synchronized List<Map.Entry<Runnable, SDTaskInfo>> getTaskInfo(String tag)
    {
        List<Map.Entry<Runnable, SDTaskInfo>> listInfo = new ArrayList<>();
        if (tag != null && !mMapRunnable.isEmpty())
        {
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
                        listInfo.add(item);
                    }
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
    public synchronized int cancelTag(String tag, boolean mayInterruptIfRunning)
    {
        int count = 0;
        if (tag != null && !mMapRunnable.isEmpty())
        {
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
        }
        return count;
    }
}
