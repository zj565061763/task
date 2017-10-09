package com.fanwe.www.task;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.fanwe.lib.task.SDTask;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new SDTask<String>()
        {
            @Override
            protected String onRun()
            {
                Log.i(TAG, "onRun");
                return "this is result";
            }

            @Override
            protected void onResult(String result)
            {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        }.submit();
    }
}
