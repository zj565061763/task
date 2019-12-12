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
            protected void onRun()
            {
                long i = 0;
                while (i < 5)
                {
                    i++;
                    Log.i(TAG, "looper:" + i + " " + this);

                    if (isCancelled())
                    {
                        break;
                    } else
                    {
                        try
                        {
                            Thread.sleep(1000);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            protected void onFinally()
            {
                super.onFinally();
                Log.e(TAG, "onFinally " + this);
            }
        }.submit();
    }
}
