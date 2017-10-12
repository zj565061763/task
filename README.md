## About
简单封装的异步执行库

## 使用方法
```java
SDTask task = new SDTask()
{
    @Override
    protected void onSubmit()
    {
        super.onSubmit();
        //提交到线程池回调
    }

    @Override
    protected void onRun() throws Exception
    {
        //执行回调
    }

    @Override
    protected void onCancel()
    {
        super.onCancel();
        //任务被取消回调
    }

    @Override
    protected void onError(Exception e)
    {
        super.onError(e);
        //任务异常回调
    }
};

task.submit(this); //把任务提交到线程池，参数为该任务对应的tag，可用于取消任务
task.cancel(); //取消任务
SDTask.cancel(this); //根据tag取消任务
```
