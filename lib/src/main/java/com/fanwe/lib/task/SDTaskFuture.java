package com.fanwe.lib.task;

/**
 * Created by zhengjun on 2017/10/17.
 */
public interface SDTaskFuture
{
    boolean cancel(boolean mayInterruptIfRunning);

    boolean isCancelled();

    boolean isDone();
}
