package com.sd.lib.task;

import android.os.Handler;
import android.os.Looper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public abstract class FTask
{
    private final String mTag;
    private volatile boolean mIsCancelled;

    private final Map<InternalRunnable, String> mRunnableHolder = new ConcurrentHashMap<>();

    public FTask()
    {
        this(null);
    }

    public FTask(String tag)
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

    /**
     * 提交任务
     *
     * @return
     */
    public final FTaskInfo submit()
    {
        return FTaskManager.getInstance().submit(mTaskRunnable, getTag());
    }

    /**
     * 提交任务，按提交的顺序一个个执行
     *
     * @return
     */
    public final FTaskInfo submitSequence()
    {
        return FTaskManager.getInstance().submitSequence(mTaskRunnable, getTag());
    }

    /**
     * 提交要执行的任务
     *
     * @param executorService 要执行任务的线程池
     * @return
     */
    public final FTaskInfo submitTo(ExecutorService executorService)
    {
        return FTaskManager.getInstance().submitTo(mTaskRunnable, getTag(), executorService);
    }

    /**
     * 取消任务
     *
     * @param mayInterruptIfRunning true-如果线程已经执行有可能被打断
     * @return
     */
    public final boolean cancel(boolean mayInterruptIfRunning)
    {
        removeMainRunnable();
        return FTaskManager.getInstance().cancel(mTaskRunnable, mayInterruptIfRunning);
    }

    /**
     * 任务是否已提交（提交未执行或者执行中）
     *
     * @return
     */
    public final boolean isSubmitted()
    {
        final FTaskInfo taskInfo = FTaskManager.getInstance().getTaskInfo(mTaskRunnable);
        return taskInfo != null && !taskInfo.isDone();
    }

    /**
     * 当前任务是否已被取消
     *
     * @return
     */
    public final boolean isCancelled()
    {
        return mIsCancelled;
    }

    private final FTaskManager.TaskRunnable mTaskRunnable = new FTaskManager.TaskRunnable()
    {
        @Override
        public void onSubmit()
        {
            mIsCancelled = false;
            FTask.this.onSubmit();
        }

        @Override
        public void onRun() throws Exception
        {
            FTask.this.onRun();
        }

        @Override
        public void onError(Exception e)
        {
            FTask.this.onError(e);
        }

        @Override
        public void onCancel()
        {
            mIsCancelled = true;
            FTask.this.onCancel();
        }

        @Override
        public void onFinish()
        {
            FTask.this.onFinish();
        }

        @Override
        public String toString()
        {
            return FTask.this.toString();
        }
    };

    /**
     * 任务被提交回调
     */
    protected void onSubmit()
    {
    }

    /**
     * 执行回调（执行线程）
     *
     * @throws Exception
     */
    protected abstract void onRun() throws Exception;

    /**
     * 错误回调（执行线程）
     *
     * @param e
     */
    protected void onError(final Exception e)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 取消回调（执行线程）
     */
    protected void onCancel()
    {
    }

    /**
     * 结束回调（执行线程）
     */
    protected void onFinish()
    {
    }

    /**
     * 提交一个主线程执行的{@link Runnable}，如果当前任务已经被取消，则不执行
     *
     * @param runnable
     * @return true-提交成功
     */
    protected synchronized final boolean postMain(Runnable runnable)
    {
        if (runnable == null)
            return false;

        if (isCancelled())
            return false;

        final InternalRunnable internalRunnable = new InternalRunnable(runnable);
        mRunnableHolder.put(internalRunnable, "");
        runOnUiThread(internalRunnable);
        return true;
    }

    private synchronized void removeMainRunnable()
    {
        for (InternalRunnable runnable : mRunnableHolder.keySet())
        {
            removeCallbacks(runnable);
        }
        mRunnableHolder.clear();
    }

    private final class InternalRunnable implements Runnable
    {
        private final Runnable mRunnable;

        public InternalRunnable(Runnable runnable)
        {
            if (runnable == null)
                throw new NullPointerException("runnable is null");
            mRunnable = runnable;
        }

        @Override
        public void run()
        {
            synchronized (FTask.this)
            {
                mRunnableHolder.remove(this);
            }

            if (!isCancelled())
                mRunnable.run();
        }
    }

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    public static void runOnUiThread(Runnable runnable)
    {
        if (runnable == null)
            return;

        if (Looper.myLooper() == Looper.getMainLooper())
            runnable.run();
        else
            MAIN_HANDLER.post(runnable);
    }

    public static void removeCallbacks(Runnable runnable)
    {
        if (runnable == null)
            return;

        MAIN_HANDLER.removeCallbacks(runnable);
    }
}
