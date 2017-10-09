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
    public boolean cancel()
    {
        if (mFuture != null)
        {
            return mFuture.cancel(true);
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

    protected abstract void onRun();

    protected void onError(Exception e)
    {

    }
}
