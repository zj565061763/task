package com.sd.www.task;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.sd.lib.task.FTaskManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FTaskManager.getInstance().setDebug(true);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_add_task:
                addTask();
                break;
            case R.id.btn_cancel_task:
                FTaskManager.getInstance().cancelTag(TAG, true);
                break;
        }
    }

    private void addTask()
    {
        final Runnable runnable = new TestRunnable();
        FTaskManager.getInstance().submit(runnable, TAG, new FTaskManager.TaskCallback()
        {
            @Override
            public void onError(Throwable e)
            {
                Log.i(TAG, "onError:" + e + " " + runnable);
            }

            @Override
            public void onCancel()
            {
                Log.i(TAG, "onCancel" + " " + runnable);
            }

            @Override
            public void onFinish()
            {
                Log.i(TAG, "onFinish" + " " + runnable);
            }
        });
    }


    private class TestRunnable implements Runnable
    {
        @Override
        public void run()
        {
            long i = 0;
            while (i < 5)
            {
                i++;
                Log.i(TAG, "looper:" + i + " " + this);

                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
