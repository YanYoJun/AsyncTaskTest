package com.plbear.asynctasktest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mBtnSyncTask = null;
    private Button mBtnHandlerTest = null;
    private static final String TAG = "AsyncTaskTest";
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // We want at least 2 threads and at most 4 threads in the core pool,
    // preferring to have 1 less than the CPU count to avoid saturating
    // the CPU with background work
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    static {
        Log.d(TAG, "CPU_COUNT:" + CPU_COUNT);
        Log.d(TAG, "CORE_POOL_SIZE:" + CORE_POOL_SIZE);
        Log.d(TAG, "MAXIMUM_POOL_SIZE:" + MAXIMUM_POOL_SIZE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Test
        setContentView(R.layout.activity_main);

        mBtnSyncTask = (Button) findViewById(R.id.btn_async_test);
        mBtnHandlerTest = (Button) findViewById(R.id.btn_handler_test);


        mBtnSyncTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new IAsyncTask().execute("yanlog test");
                int N = 5;
                for (int i = 0; i < N; i++) {
                    Log.d(TAG,"asyncTask post Task:"+i);
                    new IAsyncTask().execute("asyncTask times:"+i);
                }
                /*
                new IAsyncTask().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 5; i++) {
                            Log.i(TAG, "runnable");
                        }
                    }
                });*/
            }
        });

        mBtnHandlerTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Handler handler = new IHandler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = new Integer(i);
                            Log.e(TAG, "post message:" + i);
                            handler.sendMessage(msg);
                        }
                    }
                }).start();
            }
        });
    }

    private class IAsyncTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... args1) {
/*            Log.i(TAG, "doInBackground in:" + args1[0]);
            int times = 10;
            for (int i = 0; i < times; i++) {
                publishProgress(i);//提交之后，会执行onProcessUpdate方法
            }
            Log.i(TAG, "doInBackground out");*/

            Log.i(TAG, "doInBackground in thread:" + args1[0]);
            try {
                int times = 4;
                for (int i = 0; i < times; i++) {
                    Log.i(TAG, "thread alive:" + i + " for times"+args1[0]);
                    Thread.sleep(20);
                }
            } catch (Exception e) {

            }
            return "over";
        }

        /**
         * 在调用cancel方法后会执行到这里
         */
        protected void onCancelled() {
            Log.i(TAG, "onCancelled");
        }

        /**
         * 在doInbackground之后执行
         */
        protected void onPostExecute(String args3) {
            //Log.i(TAG, "onPostExecute:" + args3);
        }

        /**
         * 在doInBackground之前执行
         */
        @Override
        protected void onPreExecute() {
            //Log.i(TAG, "onPreExecute");
        }

        /**
         * 特别赞一下这个多次参数的方法，特别方便
         *
         * @param args2
         */
        @Override
        protected void onProgressUpdate(Integer... args2) {
            Log.i(TAG, "onProgressUpdate:" + args2[0]);
        }
    }

    private class IHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.e(TAG, "handler:" + msg.obj);
                    break;
                default:
                    break;
            }
        }
    }

}
