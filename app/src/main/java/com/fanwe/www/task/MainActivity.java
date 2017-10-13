package com.fanwe.www.task;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.fanwe.lib.task.SDTask;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickAddTask(View view)
    {
        new SDTask()
        {
            @Override
            protected void onSubmit()
            {
                super.onSubmit();
                //任务被提交到线程池
                Log.i(TAG, "onSubmit");
            }

            @Override
            protected void onRun() throws Exception
            {
                long i = 0;
                while (i < Long.MAX_VALUE)
                {
                    boolean isCancelled = isCancelled();
                    i++;
                    Log.i(TAG, "looper:" + i + " " + isCancelled);

                    if (isCancelled)
                    {
                        break;
                    }
                    Thread.sleep(1000);
                }
            }

            @Override
            protected void onError(Exception e)
            {
                super.onError(e);
                //任务异常回调
                Log.e(TAG, "onError");
                if (isCancelled())
                {
                    //任务被取消
                    Log.e(TAG, "task cancelled");
                }
            }

            @Override
            protected void onFinally()
            {
                super.onFinally();
                Log.i(TAG, "onFinally");
            }
        }.submit(this);
    }

    public void onClickCancelTask(View view)
    {
        SDTask.cancelTag(this, false);

//        int count = SDTask.getTask(this).size();
//        Log.i(TAG, "task count:" + count);
    }

}
