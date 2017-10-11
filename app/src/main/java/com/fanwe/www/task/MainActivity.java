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
                while (i < Long.MAX_VALUE)
                {
                    boolean isCancelled = isCancelled();

                    i++;
                    Log.i(TAG, "looper:" + i + " " + isCancelled);

                    if (isCancelled)
                    {
                        break;
                    }
                }
            }

            @Override
            protected void onCancel()
            {
                super.onCancel();
                Log.e(TAG, "onCancel");
            }
        }.setTag(this).submit();
    }

    public void onClickCancelTask(View view)
    {
        SDTask.cancel(this);
    }

}
