package com.fanwe.lib.task;

import java.util.concurrent.Future;

/**
 * Created by zhengjun on 2017/10/13.
 */

public class SDTaskInfo
{
    private Future future;
    private Object tag;

    public void cancel(boolean mayInterruptIfRunning)
    {
        if (!isDone())
        {
            getFuture().cancel(mayInterruptIfRunning);
        }
    }

    public boolean isCancelled()
    {
        return getFuture().isCancelled();
    }

    public boolean isDone()
    {
        return getFuture().isDone();
    }

    private Future getFuture()
    {
        return future;
    }

    void setFuture(Future future)
    {
        this.future = future;
    }

    public Object getTag()
    {
        return tag;
    }

    public void setTag(Object tag)
    {
        this.tag = tag;
    }
}
