package com.fanwe.lib.task;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Future;

/**
 * Created by zhengjun on 2017/9/12.
 */
public abstract class SDTask<T> implements Runnable
{
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

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
            T result = onRun();
            notifyResult(result);
        } catch (Exception e)
        {
            onError(e);
        }
    }

    protected abstract T onRun();

    protected abstract void onResult(T result);

    /**
     * 主线程通知结果
     *
     * @param result
     */
    protected final void notifyResult(final T result)
    {
        if (Looper.getMainLooper() == Looper.myLooper())
        {
            onResult(result);
        } else
        {
            MAIN_HANDLER.post(new Runnable()
            {
                @Override
                public void run()
                {
                    onResult(result);
                }
            });
        }
    }

    protected void onError(Exception e)
    {

    }
}
