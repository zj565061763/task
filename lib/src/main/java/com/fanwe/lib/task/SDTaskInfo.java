package com.fanwe.lib.task;

import java.util.concurrent.Future;

/**
 * Created by zhengjun on 2017/10/13.
 */

public class SDTaskInfo
{
    private Future future;
    private String tag;

    public boolean cancel(boolean mayInterruptIfRunning)
    {
        return future.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled()
    {
        return future.isCancelled();
    }

    public boolean isDone()
    {
        return future.isDone();
    }

    public String getTag()
    {
        return tag;
    }

    void setFuture(Future future)
    {
        this.future = future;
    }

    void setTag(String tag)
    {
        this.tag = tag;
    }
}
