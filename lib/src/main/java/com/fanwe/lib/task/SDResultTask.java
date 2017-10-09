package com.fanwe.lib.task;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by zhengjun on 2017/9/12.
 */
public abstract class SDResultTask<T> extends SDTask
{
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

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
}
