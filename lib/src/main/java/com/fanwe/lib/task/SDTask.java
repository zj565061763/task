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

    private volatile Exception mException;

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

    private SDTaskInfo getTaskInfo()
    {
        return SDTaskManager.getInstance().getTaskInfo(this);
    }

    /**
     * 提交任务到默认的线程池
     *
     * @param tag 任务对应的tag
     * @return
     */
    public final SDTaskInfo submit(String tag)
    {
        onSubmit();
        return SDTaskManager.getInstance().submit(this, tag);
    }

    /**
     * 提交任务到单线程线程池
     *
     * @param tag 任务对应的tag
     * @return
     */
    public final SDTaskInfo submitSingle(String tag)
    {
        onSubmit();
        return SDTaskManager.getInstance().submitSingle(this, tag);
    }

    /**
     * 提交要执行的任务
     *
     * @param executorService 要执行任务的线程池
     * @param tag             任务对应的tag
     * @return
     */
    public final SDTaskInfo submit(ExecutorService executorService, String tag)
    {
        onSubmit();
        return SDTaskManager.getInstance().submit(this, executorService, tag);
    }

    /**
     * 取消任务
     *
     * @param mayInterruptIfRunning true-如果线程已经执行有可能被打断
     * @return
     */
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        boolean result = SDTaskManager.getInstance().cancel(this, mayInterruptIfRunning);
        MAIN_HANDLER.removeCallbacks(mErrorRunnable);
        MAIN_HANDLER.removeCallbacks(mFinallyRunnable);
        return result;
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

    public Exception getException()
    {
        return mException;
    }

    @Override
    public final void run()
    {
        try
        {
            onRun();
        } catch (final Exception e)
        {
            mException = e;
            runOnUiThread(getErrorRunnable());
        } finally
        {
            runOnUiThread(mFinallyRunnable);
        }
    }

    private Runnable mErrorRunnable;

    private Runnable getErrorRunnable()
    {
        if (mErrorRunnable == null)
        {
            mErrorRunnable = new Runnable()
            {
                @Override
                public void run()
                {
                    onError(mException);
                }
            };
        }
        return mErrorRunnable;
    }

    private Runnable mFinallyRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            onFinally();
        }
    };

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
