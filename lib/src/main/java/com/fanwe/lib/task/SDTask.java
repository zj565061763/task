package com.fanwe.lib.task;

import android.os.Handler;
import android.os.Looper;

import java.io.InterruptedIOException;
import java.util.concurrent.Future;

/**
 * Created by zhengjun on 2017/9/12.
 */
public abstract class SDTask implements Runnable
{
    public static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private Future<?> mFuture;

    public static void runOnUiThread(Runnable runnable)
    {
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            runnable.run();
        } else
        {
            MAIN_HANDLER.post(runnable);
        }
    }

    /**
     * 提交任务
     *
     * @param tag 任务对应的tag
     * @return
     */
    public final Future<?> submit(Object tag)
    {
        mFuture = SDTaskManager.getInstance().submit(this, tag);
        onSubmit();
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
     * 任务是否完成
     *
     * @return
     */
    public boolean isDone()
    {
        return mFuture == null ? false : mFuture.isDone();
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
        } finally
        {
            onFinally();
        }
    }

    protected void onSubmit()
    {

    }

    protected abstract void onRun() throws Exception;

    protected void onError(Exception e)
    {

    }

    protected void onCancel()
    {

    }

    protected void onFinally()
    {
    }
}
