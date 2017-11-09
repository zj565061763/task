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

    public SDTask setTag(String tag)
    {
        mTag = tag;
        return this;
    }

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
    public final SDTaskInfo submit(ExecutorService executorService)
    {
        onSubmit();
        return SDTaskManager.getInstance().submit(this, executorService, getTag());
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
            notifyError(e);
        } finally
        {
            notifyFinally();
        }
    }

    private void notifyError(final Exception e)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                onError(e);
            }
        });
    }

    private void notifyFinally()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                onFinally();
            }
        });
    }

    protected void onSubmit()
    {
    }

    protected abstract void onRun() throws Exception;

    protected void onError(Exception e)
    {
    }

    protected void onFinally()
    {
    }

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
