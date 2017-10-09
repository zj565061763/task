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
        mIncreaseTask.cancel();
    }

    private SDTask mIncreaseTask = new SDTask()
    {
        @Override
        protected void onRun() throws Exception
        {
            int i = 0;
            while (i < Integer.MAX_VALUE)
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
            Log.i(TAG, "onCancel");
        }
    };


}
