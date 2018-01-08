package com.fanwe.lib.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by zhengjun on 2017/9/12.
 */
public class FTaskManager
{
    private static FTaskManager sInstance;

    private static final ExecutorService DEFAULT_EXECUTOR = Executors.newCachedThreadPool();
    private static final ExecutorService SINGLE_EXECUTOR = Executors.newSingleThreadExecutor();

    private Map<Runnable, FTaskInfo> mMapRunnable = new WeakHashMap<>();

    private FTaskManager()
    {
    }

    public static FTaskManager getInstance()
    {
        if (sInstance == null)
        {
            synchronized (FTaskManager.class)
            {
                if (sInstance == null)
                {
                    sInstance = new FTaskManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 提交要执行的Runnable
     *
     * @param runnable
     * @return
     */
    public FTaskInfo submit(Runnable runnable)
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
    public FTaskInfo submit(Runnable runnable, String tag)
    {
        return submitTo(runnable, DEFAULT_EXECUTOR, tag);
    }

    /**
     * 提交要执行的Runnable，按提交的顺序一个个执行
     *
     * @param runnable
     * @return
     */
    public FTaskInfo submitSequence(Runnable runnable)
    {
        return submitSequence(runnable, null);
    }

    /**
     * 提交要执行的Runnable，按提交的顺序一个个执行
     *
     * @param runnable 要执行的Runnable
     * @param tag      对应的tag，可用于取消
     * @return
     */
    public FTaskInfo submitSequence(Runnable runnable, String tag)
    {
        return submitTo(runnable, SINGLE_EXECUTOR, tag);
    }

    /**
     * 提交要执行的Runnable
     *
     * @param runnable        要执行的Runnable
     * @param executorService 要执行Runnable的线程池
     * @param tag             对应的tag，可用于取消
     * @return
     */
    public synchronized FTaskInfo submitTo(Runnable runnable, ExecutorService executorService, String tag)
    {
        Future<?> future = executorService.submit(runnable);

        FTaskInfo info = new FTaskInfo();
        info.setFuture(future);
        info.setTag(tag);

        mMapRunnable.put(runnable, info);

        return info;
    }

    /**
     * 返回Runnable对应的任务信息
     *
     * @param runnable
     * @return
     */
    public synchronized FTaskInfo getTaskInfo(Runnable runnable)
    {
        return mMapRunnable.get(runnable);
    }

    /**
     * 返回tag对应的任务信息列表
     *
     * @param tag
     * @return
     */
    public synchronized List<Map.Entry<Runnable, FTaskInfo>> getTaskInfo(String tag)
    {
        List<Map.Entry<Runnable, FTaskInfo>> listInfo = new ArrayList<>();
        if (tag != null && !mMapRunnable.isEmpty())
        {
            Iterator<Map.Entry<Runnable, FTaskInfo>> it = mMapRunnable.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry<Runnable, FTaskInfo> item = it.next();
                FTaskInfo info = item.getValue();

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
        FTaskInfo info = getTaskInfo(runnable);
        if (info == null)
        {
            return false;
        }

        return info.cancel(mayInterruptIfRunning);
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
            Iterator<Map.Entry<Runnable, FTaskInfo>> it = mMapRunnable.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry<Runnable, FTaskInfo> item = it.next();
                FTaskInfo info = item.getValue();

                if (info.isDone())
                {
                    continue;
                } else
                {
                    if (tag.equals(info.getTag()) && info.cancel(mayInterruptIfRunning))
                    {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
