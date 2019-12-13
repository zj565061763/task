## About
简单封装的异步执行库

## Gradle
[![](https://jitpack.io/v/zj565061763/task.svg)](https://jitpack.io/#zj565061763/task)

## 使用方法
```java
FTask task = new FTask(TAG)
{
    @Override
    protected void onSubmit()
    {
        super.onSubmit();
        Log.i(TAG, "onSubmit" + " " + this);
    }

    @Override
    protected void onRun()
    {
        Log.i(TAG, "onRun" + " " + this);
        new TestRunnable().run();
    }

    @Override
    protected void onError(Throwable e)
    {
        super.onError(e);
        Log.i(TAG, "onError:" + e + " " + this);
    }

    @Override
    protected void onCancel()
    {
        super.onCancel();
        Log.i(TAG, "onCancel" + " " + this);
    }

    @Override
    protected void onFinish()
    {
        super.onFinish();
        Log.i(TAG, "onFinish" + " " + this);
    }
};

task.submit(); //把任务提交到线程池
task.submitSequence(); //把任务提交到线程池，按顺序一个个执行
task.cancel(true); //取消任务，true-如果线程已经执行有可能被打断收到异常，如果不希望线程被打断，取消的时候传false，然后自己在onRun中判断isCancelled()来主动停止线程
FTaskManager.getInstance().cancelTag(TAG, true); //根据tag取消任务，true，false参数解释同上
```
