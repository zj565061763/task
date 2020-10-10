package com.sd.www.task;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.sd.lib.task.FTask;
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
                Log.e(TAG, "cancelTag start");
                FTaskManager.getInstance().cancelTag(TAG, true);
                Log.e(TAG, "cancelTag end");
                break;
        }
    }

    private void addTask()
    {
        final FTask task = new FTask(TAG)
        {
            @Override
            protected void onSubmit()
            {
                super.onSubmit();
                Log.i(TAG, "onSubmit" + " " + this + " " + Thread.currentThread().getName());
            }

            @Override
            protected void onRun() throws Exception
            {
                Log.i(TAG, "onRun start" + " " + this + " " + Thread.currentThread().getName());
                new TestRunnable().run();
                Log.i(TAG, "onRun finish"
                        + " isCancelled:" + isCancelled()
                        + " " + this + " " + Thread.currentThread().getName());
            }

            @Override
            protected void onError(Exception e)
            {
                super.onError(e);
                Log.i(TAG, "onError:" + e + " " + this + " " + Thread.currentThread().getName());
            }

            @Override
            protected void onCancel()
            {
                super.onCancel();
                Log.i(TAG, "onCancel" + " " + this + " " + Thread.currentThread().getName());
            }

            @Override
            protected void onFinish()
            {
                super.onFinish();
                Log.i(TAG, "onFinish" + " " + this + " " + Thread.currentThread().getName());
            }
        };

        task.submit();
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

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        FTaskManager.getInstance().cancelTag(TAG, true);
    }
}
