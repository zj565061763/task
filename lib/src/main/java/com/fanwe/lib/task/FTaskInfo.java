package com.fanwe.lib.task;

import java.lang.ref.WeakReference;
import java.util.concurrent.Future;

/**
 * Created by zhengjun on 2017/10/13.
 */
public class FTaskInfo
{
    private Future future;
    private String tag;
    private WeakReference<Runnable> runnable;

    FTaskInfo(Future future, String tag, Runnable runnable)
    {
        this.future = future;
        this.tag = tag;
        this.runnable = new WeakReference<>(runnable);
    }

    /**
     * 取消任务
     *
     * @param mayInterruptIfRunning true-如果线程已经执行有可能被打断
     * @return
     */
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        return future.cancel(mayInterruptIfRunning);
    }

    /**
     * 任务是否被取消
     *
     * @return
     */
    public boolean isCancelled()
    {
        return future.isCancelled();
    }

    /**
     * 任务是否已经完成
     *
     * @return
     */
    public boolean isDone()
    {
        return future.isDone();
    }

    /**
     * 任务对应的tag
     *
     * @return
     */
    public String getTag()
    {
        return tag;
    }

    /**
     * 任务对应的Runnable对象
     *
     * @return
     */
    public Runnable getRunnable()
    {
        return runnable == null ? null : runnable.get();
    }
}
