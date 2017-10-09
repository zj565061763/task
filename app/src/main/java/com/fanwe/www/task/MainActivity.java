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
        mIncreaseTask.submit();
    }

    public void onClickButton(View view)
    {
        Log.i(TAG, "onClickButton");
        mIncreaseTask.cancel();
    }

    private SDTask mIncreaseTask = new SDTask()
    {
        @Override
        protected void onRun() throws Exception
        {
            long i = 0;
            while (!isCancelled() && i < Long.MAX_VALUE)
            {
                i++;
                Log.i(TAG, "looper:" + i);
            }
        }

        @Override
        protected void onCancelCalled()
        {
            super.onCancelCalled();
            Log.i(TAG, "onCancelCalled");
        }
    };


}
