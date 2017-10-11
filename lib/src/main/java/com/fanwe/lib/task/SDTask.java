package com.fanwe.lib.task;

import java.io.InterruptedIOException;
import java.util.concurrent.Future;

/**
 * Created by zhengjun on 2017/9/12.
 */
public abstract class SDTask implements Runnable
{
    private Object mTag;
    private Future<?> mFuture;

    /**
     * 提交任务
     *
     * @return
     */
    public final Future<?> submit()
    {
        mFuture = SDTaskManager.getInstance().submit(this, getTag());
        return mFuture;
    }

    /**
     * 取消任务
     *
     * @return
     */
    public boolean cancel()
    {
        return SDTaskManager.getInstance().cancel(this);
    }

    /**
     * 是否被取消
     *
     * @return
     */
    public boolean isCancelled()
    {
        return mFuture == null ? false : mFuture.isCancelled();
    }

    /**
     * 根据tag取消任务
     *
     * @param tag
     * @return 取消成功的数量
     */
    public static int cancel(Object tag)
    {
        return SDTaskManager.getInstance().cancel(tag);
    }

    /**
     * 返回任务对应的tag
     *
     * @return
     */
    public Object getTag()
    {
        return mTag;
    }

    /**
     * 设置任务对应的tag
     *
     * @param tag
     * @return
     */
    public SDTask setTag(Object tag)
    {
        if (mTag == null)
        {
            mTag = tag;
        }
        return this;
    }

    @Override
    public final void run()
    {
        try
        {
            onRun();
        } catch (Exception e)
        {
            if (e instanceof InterruptedException || e instanceof InterruptedIOException)
            {
                onCancel();
            } else
            {
                onError(e);
            }
        }
    }

    protected abstract void onRun() throws Exception;

    protected void onError(Exception e)
    {

    }

    protected void onCancel()
    {

    }
}
