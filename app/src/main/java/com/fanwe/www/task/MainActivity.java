package com.fanwe.www.task;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.fanwe.lib.task.FTask;
import com.fanwe.lib.task.FTaskManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        new FTask(TAG)
        {
            @Override
            protected void onRun() throws Exception
            {
                long i = 0;
                while (i < 5)
                {
                    i++;
                    Log.i(TAG, "looper:" + i);

                    if (isCancelled())
                    {
                        break;
                    } else
                    {
                        Thread.sleep(1000);
                    }
                }
            }

            @Override
            protected void onError(Exception e)
            {
                super.onError(e);
                //任务异常回调
                Log.e(TAG, "onError:" + e);
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
        }.submit();
    }
}
