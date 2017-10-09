package com.fanwe.www.task;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fanwe.lib.task.SDResultTask;
import com.fanwe.lib.task.SDTask;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new SDTask()
        {
            @Override
            protected void onRun()
            {
                Log.i(TAG, "onRun");
            }
        }.submit();


        new SDResultTask<String>()
        {
            @Override
            protected void onRun()
            {
                Log.i(TAG, "onRun");
                notifyResult("this is result");
            }

            @Override
            protected void onResult(String result)
            {
                Log.i(TAG, "onResult:" + result);
            }
        }.submit();
    }
}
