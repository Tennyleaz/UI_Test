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
 * Created by Tenny on 2015/11/28.
 */
public class Workers extends Activity {
    private AsyncTask task = null;
    private TextView updateTime;
    private static ListView lv1_1, lv1_2, lv1_3, lv1_4, lv1_5, lv1_6, lv1_7, lv1_8, lv1_9, lv2_1, lv2_2, lv2_3, lv2_4, lv2_5, lv2_6, lv2_7, lv2_8, lv2_9, lvO1, lvO2, lvO3, lvO4, lvO5, lvO6;
    private static WorkerAdapter listAdapter1_1, listAdapter1_2, listAdapter1_3, listAdapter1_4, listAdapter1_5, listAdapter1_6, listAdapter1_7, listAdapter1_8, listAdapter1_9,
            listAdapter2_1, listAdapter2_2, listAdapter2_3, listAdapter2_4, listAdapter2_5, listAdapter2_6, listAdapter2_7, listAdapter2_8, listAdapter2_9,
            listAdapterO1, listAdapterO2, listAdapterO3, listAdapterO4, listAdapterO5, listAdapterO6;
    private ArrayList<Worker> lm11, lm12, lm13, lm14, lm15, lm16, lm17, lm18, lm19, lm21, lm22, lm23, lm24, lm25, lm26, lm27, lm28, lm29, lo1, lo2, lo3, lo4, lo5, lo6;
    private ProgressDialog pd;
    private String schedule_detail, work_area, work_type;
    private boolean needReUpdate=false, afterUpdate=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workers_layout);
        updateTime = (TextView) findViewById(R.id.updateTime);
        updateTime.setVisibility(View.GONE);

        lm11 = new ArrayList<Worker>();
        lm12 = new ArrayList<Worker>();
        lm13 = new ArrayList<Worker>();
        lm14 = new ArrayList<Worker>();
        lm15 = new ArrayList<Worker>();
        lm16 = new ArrayList<Worker>();
        lm17 = new ArrayList<Worker>();
        lm18 = new ArrayList<Worker>();
        lm19 = new ArrayList<Worker>();
        lm21 = new ArrayList<Worker>();
        lm22 = new ArrayList<Worker>();
        lm23 = new ArrayList<Worker>();
        lm24 = new ArrayList<Worker>();
        lm25 = new ArrayList<Worker>();
        lm26 = new ArrayList<Worker>();
        lm27 = new ArrayList<Worker>();
        lm28 = new ArrayList<Worker>();
        lm29 = new ArrayList<Worker>();
        lo1 = new ArrayList<Worker>();
        lo2 = new ArrayList<Worker>();
        lo3 = new ArrayList<Worker>();
        lo4 = new ArrayList<Worker>();
        lo5 = new ArrayList<Worker>();
        lo6 = new ArrayList<Worker>();
        listAdapter1_1 = new WorkerAdapter(this, lm11);
        listAdapter1_2 = new WorkerAdapter(this, lm12);
        listAdapter1_3 = new WorkerAdapter(this, lm13);
        listAdapter1_4 = new WorkerAdapter(this, lm14);
        listAdapter1_5 = new WorkerAdapter(this, lm15);
        listAdapter1_6 = new WorkerAdapter(this, lm16);
        listAdapter1_7 = new WorkerAdapter(this, lm17);
        listAdapter1_8 = new WorkerAdapter(this, lm18);
        listAdapter1_9 = new WorkerAdapter(this, lm19);
        listAdapter2_1 = new WorkerAdapter(this, lm21);
        listAdapter2_2 = new WorkerAdapter(this, lm22);
        listAdapter2_3 = new WorkerAdapter(this, lm23);
        listAdapter2_4 = new WorkerAdapter(this, lm24);
        listAdapter2_5 = new WorkerAdapter(this, lm25);
        listAdapter2_6 = new WorkerAdapter(this, lm26);
        listAdapter2_7 = new WorkerAdapter(this, lm27);
        listAdapter2_8 = new WorkerAdapter(this, lm28);
        listAdapter2_9 = new WorkerAdapter(this, lm29);
        listAdapterO1 =  new WorkerAdapter(this, lo1);
        listAdapterO2 =  new WorkerAdapter(this, lo2);
        listAdapterO3 =  new WorkerAdapter(this, lo3);
        listAdapterO4 =  new WorkerAdapter(this, lo4);
        listAdapterO5 =  new WorkerAdapter(this, lo5);
        listAdapterO6 =  new WorkerAdapter(this, lo6);
        lv1_1 = (ListView) findViewById(R.id.listView1_1);
        lv1_2 = (ListView) findViewById(R.id.listView1_2);
        lv1_3 = (ListView) findViewById(R.id.listView1_3);
        lv1_4 = (ListView) findViewById(R.id.listView1_4);
        lv1_5 = (ListView) findViewById(R.id.listView1_5);
        lv1_6 = (ListView) findViewById(R.id.listView1_6);
        lv1_7 = (ListView) findViewById(R.id.listView1_7);
        lv1_8 = (ListView) findViewById(R.id.listView1_8);
        lv1_9 = (ListView) findViewById(R.id.listView1_9);
        lv2_1 = (ListView) findViewById(R.id.listView2_1);
        lv2_2 = (ListView) findViewById(R.id.listView2_2);
        lv2_3 = (ListView) findViewById(R.id.listView2_3);
        lv2_4 = (ListView) findViewById(R.id.listView2_4);
        lv2_5 = (ListView) findViewById(R.id.listView2_5);
        lv2_6 = (ListView) findViewById(R.id.listView2_6);
        lv2_7 = (ListView) findViewById(R.id.listView2_7);
        lv2_8 = (ListView) findViewById(R.id.listView2_8);
        lv2_9 = (ListView) findViewById(R.id.listView2_9);
        lv1_1.setAdapter(listAdapter1_1);
        lv1_2.setAdapter(listAdapter1_2);
        lv1_3.setAdapter(listAdapter1_3);
        lv1_4.setAdapter(listAdapter1_4);
        lv1_5.setAdapter(listAdapter1_5);
        lv1_6.setAdapter(listAdapter1_6);
        lv1_7.setAdapter(listAdapter1_7);
        lv1_8.setAdapter(listAdapter1_8);
        lv1_9.setAdapter(listAdapter1_9);
        lv2_1.setAdapter(listAdapter2_1);
        lv2_2.setAdapter(listAdapter2_2);
        lv2_3.setAdapter(listAdapter2_3);
        lv2_4.setAdapter(listAdapter2_4);
        lv2_5.setAdapter(listAdapter2_5);
        lv2_6.setAdapter(listAdapter2_6);
        lv2_7.setAdapter(listAdapter2_7);
        lv2_8.setAdapter(listAdapter2_8);
        lv2_9.setAdapter(listAdapter2_9);
        lvO1 = (ListView) findViewById(R.id.listViewO1);
        lvO2 = (ListView) findViewById(R.id.listViewO2);
        lvO3 = (ListView) findViewById(R.id.listViewO3);
        lvO4 = (ListView) findViewById(R.id.listViewO4);
        lvO5 = (ListView) findViewById(R.id.listViewO5);
        lvO6 = (ListView) findViewById(R.id.listViewO6);
        lvO1.setAdapter(listAdapterO1);
        lvO2.setAdapter(listAdapterO2);
        lvO3.setAdapter(listAdapterO3);
        lvO4.setAdapter(listAdapterO4);
        lvO5.setAdapter(listAdapterO5);
        lvO6.setAdapter(listAdapterO6);
    }

    @Override
    public void onStart() {
        super.onStart();
        pd = ProgressDialog.show(Workers.this, "LOADING", "Fetching data, \nPlease wait...");   /* 開啟一個新線程，在新線程裡執行耗時的方法 */
        new Thread(new Runnable() {
            @Override
            public void run() {
                InitServer();
                handler.sendEmptyMessage(0);// 執行耗時的方法之後發送消給handler
            }

        }).start();
        task = new UpdateTask().execute();
        Log.d("Mylog", "Workers start");
    }

    private void InitServer() {
        Log.d("mylog", "InitServer...");
        String s = "QUERY\tSCHEDULE_DETAIL<END>";
        SocketHandler.writeToSocket(s);
        schedule_detail = SocketHandler.getOutput();
        Log.d("mylog", "schedule_detail=" + schedule_detail);
        /*try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
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
        listAdapterO1.clear();
        listAdapterO2.clear();
        listAdapterO3.clear();
        listAdapterO4.clear();
        listAdapterO5.clear();
        listAdapterO6.clear();
        listAdapter1_1.clear();
        listAdapter1_2.clear();
        listAdapter1_3.clear();
        listAdapter1_4.clear();
        listAdapter1_5.clear();
        listAdapter1_6.clear();
        listAdapter1_7.clear();
        listAdapter1_8.clear();
        listAdapter1_9.clear();
        listAdapter2_1.clear();
        listAdapter2_2.clear();
        listAdapter2_3.clear();
        listAdapter2_4.clear();
        listAdapter2_5.clear();
        listAdapter2_6.clear();
        listAdapter2_7.clear();
        listAdapter2_8.clear();
        listAdapter2_9.clear();

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

                    switch (w[3]) {
                        case "6000":
                            listAdapterO1.add(newWorker);
                            break;
                        case "6001":
                            listAdapterO2.add(newWorker);
                            break;
                        case "6002":
                            listAdapterO3.add(newWorker);
                            break;
                        case "6003":
                            listAdapterO4.add(newWorker);
                            break;
                        case "6004":
                            listAdapterO5.add(newWorker);
                            break;
                        case "6005":
                            listAdapterO6.add(newWorker);
                            break;
                        case "6010":
                            listAdapter1_1.add(newWorker);
                            break;
                        case "6011":
                            listAdapter2_1.add(newWorker);
                            break;
                        case "6020":
                            listAdapter1_2.add(newWorker);
                            break;
                        case "6021":
                            listAdapter2_2.add(newWorker);
                            break;
                        case "6030":
                            listAdapter1_3.add(newWorker);
                            break;
                        case "6031":
                            listAdapter2_3.add(newWorker);
                            break;
                        case "6040":
                            listAdapter1_4.add(newWorker);
                            break;
                        case "6041":
                            listAdapter2_4.add(newWorker);
                            break;
                        case "6050":
                            listAdapter1_5.add(newWorker);
                            break;
                        case "6051":
                            listAdapter2_5.add(newWorker);
                            break;
                        case "6060":
                            listAdapter1_6.add(newWorker);
                            break;
                        case "6061":
                            listAdapter2_6.add(newWorker);
                            break;
                        case "6070":
                            listAdapter1_7.add(newWorker);
                            break;
                        case "6071":
                            listAdapter2_7.add(newWorker);
                            break;
                        case "6080":
                            listAdapter1_8.add(newWorker);
                            break;
                        case "6081":
                            listAdapter2_8.add(newWorker);
                            break;
                        case "6090":
                            listAdapter1_9.add(newWorker);
                            break;
                        case "6091":
                            listAdapter2_9.add(newWorker);
                            break;
                    } //end switch
                }
            } //end for
        }
        pd.dismiss();// 關閉ProgressDialog
    }

    private class UpdateTask extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... v) {
            while (!isCancelled()) {
                if(needReUpdate) {
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
            if (result == null || result.length() == 0) return;
            String[] lines = result.split("<END>");
            int length = lines.length;

            Log.d("Mylog", "lines.length=" + length);
            boolean updateList = false;
            for (String s : lines) {
                if(s!=null && s.contains("UPDATE_SCHEDULE\t")) {
                    needReUpdate = true;
                }
            }
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
