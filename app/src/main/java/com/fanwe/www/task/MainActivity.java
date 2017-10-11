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
            protected void onRun() throws Exception
            {
                Log.i(TAG, "start---------->");
                long i = 0;
                while (!isCancelled() && i < 5)
                {
                    i++;
                    Log.i(TAG, "looper:" + i);
                    Thread.sleep(1000);
                }
            }

            @Override
            protected void onCancel()
            {
                super.onCancel();
                Log.e(TAG, "onCancel");
            }
        }.submit(this);
    }

    public void onClickCancelTask(View view)
    {
        SDTask.cancel(this);
    }

}
