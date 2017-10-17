package com.fanwe.lib.task;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhengjun on 2017/9/12.
 */
public abstract class SDTask implements Runnable
{
    public static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private SDTaskInfo mTaskInfo;

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
    public synchronized final SDTaskInfo submit(Object tag)
    {
        mTaskInfo = SDTaskManager.getInstance().submit(this, tag);
        onSubmit();
        return mTaskInfo;
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
    public synchronized boolean isCancelled()
    {
        return mTaskInfo == null ? false : mTaskInfo.isCancelled();
    }

    /**
     * 任务是否完成
     *
     * @return
     */
    public synchronized boolean isDone()
    {
        return mTaskInfo == null ? false : mTaskInfo.isDone();
    }

    /**
     * 根据tag取消Runnable
     *
     * @param tag
     * @param mayInterruptIfRunning true-如果线程已经执行有可能被打断
     * @return 取消成功的数量
     */
    public static int cancelTag(Object tag, boolean mayInterruptIfRunning)
    {
        return SDTaskManager.getInstance().cancelTag(tag, mayInterruptIfRunning);
    }

    /**
     * 查找tag对应的任务
     *
     * @param tag
     * @return
     */
    public static List<SDTask> getTask(Object tag)
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
        } catch (Exception e)
        {
            onError(e);
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

    protected void onFinally()
    {
    }
}
