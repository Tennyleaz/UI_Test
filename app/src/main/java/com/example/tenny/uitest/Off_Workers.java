package com.example.tenny.uitest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tenny on 2016/3/8.
 */
public class Off_Workers extends Activity {
    private AsyncTask task = null;
    private TextView updateTime;
    private static ListView lvOff1, lvOff2, lvOff3;
    private static WorkerAdapter listAdapterOff1, listAdapterOff2, listAdapterOff3;
    private ArrayList<Worker> lof1, lof2, lof3;
    private ProgressDialog pd;
    private String schedule_detail;
    private boolean needReUpdate=false, afterUpdate=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workers_off);
        updateTime = (TextView) findViewById(R.id.updateTime);
        updateTime.setVisibility(View.GONE);

        lof1 = new ArrayList<Worker>();
        lof2 = new ArrayList<Worker>();
        lof3 = new ArrayList<Worker>();
        listAdapterOff1 =  new WorkerAdapter(this, lof1);
        listAdapterOff2 =  new WorkerAdapter(this, lof2);
        listAdapterOff3 =  new WorkerAdapter(this, lof3);
        lvOff1 = (ListView) findViewById(R.id.listViewOff1);
        lvOff2 = (ListView) findViewById(R.id.listViewOff2);
        lvOff3 = (ListView) findViewById(R.id.listViewOff3);
        lvOff1.setAdapter(listAdapterOff1);
        lvOff2.setAdapter(listAdapterOff2);
        lvOff3.setAdapter(listAdapterOff3);
    }

    @Override
    public void onStart() {
        super.onStart();
        pd = ProgressDialog.show(Off_Workers.this, "LOADING", "Fetching data, \nPlease wait...");   /* 開啟一個新線程，在新線程裡執行耗時的方法 */
        new Thread(new Runnable() {
            @Override
            public void run() {
                InitServer();
                handler.sendEmptyMessage(0);// 執行耗時的方法之後發送消給handler
            }

        }).start();
        task = new UpdateTask().execute();
        Log.d("Mylog", "Off_Workers start");
    }

    private void InitServer() {
        Log.d("mylog", "InitServer...");
        String s = "QUERY\tSCHEDULE_DETAIL<END>";
        SocketHandler.writeToSocket(s);
        schedule_detail = SocketHandler.getOutput();
        Log.d("mylog", "schedule_detail=" + schedule_detail);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息後就會執行此方法
            updateUI();
            Log.d("mylog", "handleMessage...");
        }
    };

    private void updateUI() {
        Log.d("mylog", "updateUI...");
        listAdapterOff1.clear();
        listAdapterOff2.clear();
        listAdapterOff3.clear();

        if(schedule_detail!=null) {
            schedule_detail = schedule_detail.replaceAll("QUERY_REPLY\t", "");
            schedule_detail = schedule_detail.replaceAll("<END>", "");
            String[] workers = schedule_detail.split("<N>");

            for(String s: workers){
                if(s==null) continue;
                s = s.replaceAll("<N>", "");
                String[] w = s.split("\t");
                if(w.length >= 4) {
                    Worker newWorker = new Worker(w[0], w[1], w[2], w[3]);
                    Log.d("mylog", "new worker:" + newWorker.Name + "/" + newWorker.WorkPlace + "/" + newWorker.WorkTime);
                    switch (newWorker.WorkTime) {
                        case "5002":
                            //isAbsent = true;
                            listAdapterOff1.add(newWorker);
                            listAdapterOff1.notifyDataSetChanged();
                            Log.d("mylog", "found 5002");
                            break;
                        case "5003":
                            //isAbsent = true;
                            listAdapterOff2.add(newWorker);
                            listAdapterOff2.notifyDataSetChanged();
                            Log.d("mylog", "found 5003");
                            break;
                        case "5004":
                            //isAbsent = true;
                            listAdapterOff3.add(newWorker);
                            listAdapterOff3.notifyDataSetChanged();
                            Log.d("mylog", "found 5004");
                            break;
                    }  //end switch
                    Log.d("mylog", "worker " + newWorker.Name + " goto " + newWorker.WorkPlace + " time:" + newWorker.WorkTime);
                }  //end if
            }  //end for
        }
        if(listAdapterOff1.isEmpty()) {
            Worker noWorker = new Worker("", "(無)", "", "");
            listAdapterOff1.add(noWorker);
            listAdapterOff1.notifyDataSetChanged();
        }
        if(listAdapterOff2.isEmpty()) {
            Worker noWorker = new Worker("", "(無)", "", "");
            listAdapterOff2.add(noWorker);
            listAdapterOff2.notifyDataSetChanged();
        }
        if(listAdapterOff3.isEmpty()) {
            Worker noWorker = new Worker("", "(無)", "", "");
            listAdapterOff3.add(noWorker);
            listAdapterOff3.notifyDataSetChanged();
        }
        pd.dismiss();// 關閉ProgressDialog
    }

    private class UpdateTask extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... v) {
            while (!isCancelled()) {
                if(needReUpdate) {
                    Log.e("mylog", "need update!");
                    InitServer();
                    afterUpdate = true;
                    needReUpdate = false;
                    publishProgress("");
                    continue;
                }
                String result;
                result = SocketHandler.getOutput();
                Log.d("Mylog", "result:" + result);
                publishProgress(result);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e("Mylog", "Thread in Values:" + e.toString());
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if(afterUpdate) {
                afterUpdate = false;
                updateUI();
            }
            String result = values[0];
            if(result == null || result.length() == 0) return;
            if(result.contains("UPDATE_SCHEDULE")) {
                needReUpdate = true;
                Log.d("mylog", "need re update");
            }
            /*String[] lines = result.split("<END>");
            int length = lines.length;

            Log.d("Mylog", "lines.length=" + length);
            boolean updateList = false;
            for (String s : lines) {
                if(s!=null && s.contains("UPDATE_SCHEDULE\t")) {
                    needReUpdate = true;
                    Log.d("mylog", "need re update");
                }
            }*/
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(task!=null)
            task.cancel(true);
    }

    public void onBackPressed(){
        Log.d("mylog", "back is pressed");
        if(task!=null) {
            task.cancel(true);
        }
        Thread[] threads = new Thread[Thread.activeCount()];  //close all running threads
        Thread.enumerate(threads);
        for (Thread t : threads) {
            if(t!=null) t.interrupt();
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }
}
