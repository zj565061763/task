package com.fanwe.lib.task;

import java.lang.ref.WeakReference;
import java.util.concurrent.Future;

/**
 * Created by zhengjun on 2017/10/13.
 */
public class FTaskInfo
{
    private Future mFuture;
    private String mTag;
    private WeakReference<Runnable> mRunnable;

    FTaskInfo(Future future, String tag, Runnable runnable)
    {
        mFuture = future;
        mTag = tag;
        mRunnable = new WeakReference<>(runnable);
    }

    /**
     * 取消任务
     *
     * @param mayInterruptIfRunning true-如果线程已经执行有可能被打断
     * @return
     */
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        return mFuture.cancel(mayInterruptIfRunning);
    }

    /**
     * 任务是否被取消
     *
     * @return
     */
    public boolean isCancelled()
    {
        return mFuture.isCancelled();
    }

    /**
     * 任务是否已经完成
     *
     * @return
     */
    public boolean isDone()
    {
        return mFuture.isDone();
    }

    /**
     * 任务对应的tag
     *
     * @return
     */
    public String getTag()
    {
        return mTag;
    }

    /**
     * 任务对应的Runnable对象
     *
     * @return
     */
    public Runnable getRunnable()
    {
        return mRunnable == null ? null : mRunnable.get();
    }
}
