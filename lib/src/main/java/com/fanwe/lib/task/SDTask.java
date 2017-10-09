package com.fanwe.lib.task;

import java.util.concurrent.Future;

/**
 * Created by zhengjun on 2017/9/12.
 */
public abstract class SDTask implements Runnable
{
    Future<?> mFuture;

    /**
     * 取消任务
     *
     * @return
     */
    public synchronized boolean cancel()
    {
        boolean result = false;
        if (mFuture != null)
        {
            result = mFuture.cancel(true);
        }
        if (result)
        {
            onCancelCalled();
        }
        return result;
    }

    /**
     * 任务是否已经被取消
     *
     * @return
     */
    public synchronized boolean isCancelled()
    {
        if (mFuture != null)
        {
            return mFuture.isCancelled();
        } else
        {
            return false;
        }
    }

    /**
     * 执行任务
     *
     * @return
     */
    public final Future<?> submit()
    {
        return SDTaskManager.getInstance().submit(this);
    }

    @Override
    public final void run()
    {
        try
        {
            onRun();
        } catch (Exception e)
        {
            onError(e);
        }
    }

    protected abstract void onRun() throws Exception;

    protected void onError(Exception e)
    {

    }

    protected void onCancelCalled()
    {

    }
}
