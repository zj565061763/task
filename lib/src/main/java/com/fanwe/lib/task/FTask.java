package com.fanwe.lib.task;

/**
 * Created by zhengjun on 2017/9/12.
 */
public abstract class FTask extends FBaseTask
{
    public FTask()
    {
    }

    public FTask(String tag)
    {
        super(tag);
    }

    @Override
    public final void run()
    {
        try
        {
            onRun();
        } catch (final Exception e)
        {
            onError(e);
        } finally
        {
            onFinally();
        }
    }

    /**
     * 任务执行回调（任务执行线程）
     *
     * @throws Exception
     */
    protected abstract void onRun() throws Exception;

    /**
     * 任务执行异常回调（任务执行线程）
     *
     * @param e
     */
    protected void onError(Exception e)
    {
    }

    /**
     * 任务执行完成回调（任务执行线程）
     */
    protected void onFinally()
    {
    }
}
