package com.fanwe.lib.task;

import java.io.InterruptedIOException;
import java.util.concurrent.Future;

/**
 * Created by zhengjun on 2017/9/12.
 */
public abstract class SDTask implements Runnable
{
    private Object mTag;

    public final Future<?> submit()
    {
        return submit(null);
    }

    /**
     * 提交任务
     *
     * @param tag 任务对应的tag
     * @return
     */
    public final Future<?> submit(Object tag)
    {
        mTag = tag;
        return SDTaskManager.getInstance().submit(this, tag);
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
        return SDTaskManager.getInstance().isCancelled(this);
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
