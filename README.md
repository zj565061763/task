## About
简单封装的异步执行库

## Gradle
[![](https://jitpack.io/v/zj565061763/task.svg)](https://jitpack.io/#zj565061763/task)

## 使用方法
```java
SDTask task = new SDTask()
{
    @Override
    protected void onSubmit()
    {
        super.onSubmit();
        //任务被提交到线程池
    }

    @Override
    protected void onRun() throws Exception
    {
        //任务执行
    }

    @Override
    protected void onError(Exception e)
    {
        super.onError(e);
        //任务异常回调
        if (isCancelled())
        {
            //任务被取消
        }
    }

    @Override
    protected void onFinally()
    {
        super.onFinally();
        //最终执行的回调方法
    }
};

task.submit(this); //把任务提交到线程池，参数为该任务对应的tag，可用于取消任务
task.cancel(); //取消任务
SDTask.cancelTag(this); //根据tag取消任务
```
