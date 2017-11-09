package com.fanwe.lib.task;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Created by zhengjun on 2017/9/12.
 */
public abstract class SDTask implements Runnable
{
    public static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private String mTag;

    /**
     * 设置tag，设置后不能修改
     *
     * @param tag
     * @return
     */
    public SDTask setTag(String tag)
    {
        if (mTag == null)
        {
            mTag = tag;
        }
        return this;
    }

    /**
     * 返回任务对应的tag
     *
     * @return
     */
    public String getTag()
    {
        return mTag;
    }

    private SDTaskInfo getTaskInfo()
    {
        return SDTaskManager.getInstance().getTaskInfo(this);
    }

    /**
     * 提交任务
     *
     * @return
     */
    public final SDTaskInfo submit()
    {
        onSubmit();
        return SDTaskManager.getInstance().submit(this, getTag());
    }

    /**
     * 提交任务，按提交的顺序一个个执行
     *
     * @return
     */
    public final SDTaskInfo submitSequence()
    {
        onSubmit();
        return SDTaskManager.getInstance().submitSequence(this, getTag());
    }

    /**
     * 提交要执行的任务
     *
     * @param executorService 要执行任务的线程池
     * @return
     */
    public final SDTaskInfo submitTo(ExecutorService executorService)
    {
        onSubmit();
        return SDTaskManager.getInstance().submitTo(this, executorService, getTag());
    }

    /**
     * 取消任务
     *
     * @param mayInterruptIfRunning true-如果线程已经执行有可能被打断
     * @return
     */
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        return SDTaskManager.getInstance().cancel(this, mayInterruptIfRunning);
    }

    /**
     * 是否被取消
     *
     * @return
     */
    public boolean isCancelled()
    {
        return getTaskInfo() == null ? false : getTaskInfo().isCancelled();
    }

    /**
     * 任务是否完成
     *
     * @return
     */
    public boolean isDone()
    {
        return getTaskInfo() == null ? false : getTaskInfo().isDone();
    }

    /**
     * 根据tag取消Runnable
     *
     * @param tag
     * @param mayInterruptIfRunning true-如果线程已经执行有可能被打断
     * @return 取消成功的数量
     */
    public static int cancelTag(String tag, boolean mayInterruptIfRunning)
    {
        return SDTaskManager.getInstance().cancelTag(tag, mayInterruptIfRunning);
    }

    /**
     * 查找tag对应的任务
     *
     * @param tag
     * @return
     */
    public static List<SDTask> getTask(String tag)
    {
        List<SDTask> listTask = new ArrayList<>();

        List<Map.Entry<Runnable, SDTaskInfo>> listInfo = SDTaskManager.getInstance().getTaskInfo(tag);
        if (!listInfo.isEmpty())
        {
            for (Map.Entry<Runnable, SDTaskInfo> item : listInfo)
            {
                Runnable runnable = item.getKey();
                if (runnable instanceof SDTask)
                {
                    listTask.add((SDTask) runnable);
                }
            }
        }
        return listTask;
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
     * 任务提交回调
     */
    protected void onSubmit()
    {
    }

    /**
     * 任务执行回调
     *
     * @throws Exception
     */
    protected abstract void onRun() throws Exception;

    /**
     * 任务执行异常回调
     *
     * @param e
     */
    protected void onError(Exception e)
    {
    }

    /**
     * 任务执行完成回调
     */
    protected void onFinally()
    {
    }

    /**
     * 主线程执行Runnable
     *
     * @param runnable
     */
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
}
