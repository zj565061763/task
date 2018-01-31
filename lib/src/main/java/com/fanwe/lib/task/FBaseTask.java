package com.fanwe.lib.task;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by zhengjun on 2017/9/12.
 */
public abstract class FBaseTask implements Runnable
{
    public static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    private final String mTag;

    public FBaseTask()
    {
        this(null);
    }

    public FBaseTask(String tag)
    {
        mTag = tag;
    }

    /**
     * 返回任务对应的tag
     *
     * @return
     */
    public final String getTag()
    {
        return mTag;
    }

    private FTaskInfo getTaskInfo()
    {
        return FTaskManager.getInstance().getTaskInfo(this);
    }

    /**
     * 提交任务
     *
     * @return
     */
    public final FTaskInfo submit()
    {
        return FTaskManager.getInstance().submit(this, getTag());
    }

    /**
     * 提交任务，按提交的顺序一个个执行
     *
     * @return
     */
    public final FTaskInfo submitSequence()
    {
        return FTaskManager.getInstance().submitSequence(this, getTag());
    }

    /**
     * 提交要执行的任务
     *
     * @param executorService 要执行任务的线程池
     * @return
     */
    public final FTaskInfo submitTo(ExecutorService executorService)
    {
        return FTaskManager.getInstance().submitTo(this, executorService, getTag());
    }

    /**
     * 取消任务
     *
     * @param mayInterruptIfRunning true-如果线程已经执行有可能被打断
     * @return
     */
    public final boolean cancel(boolean mayInterruptIfRunning)
    {
        return FTaskManager.getInstance().cancel(this, mayInterruptIfRunning);
    }

    /**
     * 是否被取消
     *
     * @return
     */
    public final boolean isCancelled()
    {
        return getTaskInfo() == null ? false : getTaskInfo().isCancelled();
    }

    /**
     * 任务是否完成
     *
     * @return
     */
    public final boolean isDone()
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
    public static final int cancelTag(String tag, boolean mayInterruptIfRunning)
    {
        return FTaskManager.getInstance().cancelTag(tag, mayInterruptIfRunning);
    }

    /**
     * 查找tag对应的任务
     *
     * @param tag
     * @return
     */
    public static final List<FTask> getTask(String tag)
    {
        List<FTask> listTask = new ArrayList<>();

        List<FTaskInfo> listInfo = FTaskManager.getInstance().getTaskInfo(tag);
        if (!listInfo.isEmpty())
        {
            for (FTaskInfo item : listInfo)
            {
                Runnable runnable = item.getRunnable();
                if (runnable instanceof FTask)
                {
                    listTask.add((FTask) runnable);
                }
            }
        }
        return listTask;
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
