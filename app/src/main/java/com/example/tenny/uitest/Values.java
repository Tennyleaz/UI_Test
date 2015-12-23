package com.example.tenny.uitest;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class Values extends Activity {
    private AsyncTask task = null;
    static final int MAX_LINE = 9;
    private ArrayList<ValueItem> valueArray;
    private ValueAdapter valueAdapter;
    private ListView valueListView;
    private TextView updateTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.values_layout);
        updateTime = (TextView) findViewById(R.id.updateTime);
        updateTime.setVisibility(View.GONE);
        valueListView = (ListView) findViewById(R.id.valueListView);

        valueArray = new ArrayList<ValueItem>();
        for (int i=1; i<=MAX_LINE; i++) {
            ValueItem v = new ValueItem("(無)", "0", "0", "0", "0", "0", "0", "0", "0", "0", "");
            valueArray.add(v);
        }
        valueAdapter = new ValueAdapter(Values.this, valueArray);
        valueListView.setAdapter(valueAdapter);
        valueAdapter.notifyDataSetChanged();

    }

    @Override
    public void onStart() {
        super.onStart();
        task = new UpdateTask();
        AsyncTaskTools.execute(task);
        Log.d("Mylog", "Fragment 4 start");
    }

    private class UpdateTask extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... v) {
            while (!isCancelled()) {
                String result;
                result = SocketHandler.getOutput();
                Log.d("Mylog", "result:" + result);
                publishProgress(result);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e("Mylog", "Thread in Values:" + e.toString());
                }
                if (isCancelled())
                    break;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            String result = values[0];
            if (result == null || result.length() == 0) return;
            String[] lines = result.split("<END>");
            int length = lines.length;

            Log.d("Mylog", "lines.length=" + length);
            boolean updateList = false;
            for (String s : lines) {
                if(s!=null && s.contains("UPDATE_VALUE\t")) {  //時間\t線號\t品牌名稱\t重量max\t重量value\t重量min\t圓周max\t圓周value\t圓周min\t透氣率max\t透氣率value\t透氣率min
                    s = s.replaceAll("UPDATE_VALUE\t", "");
                    s = s.replaceAll("<N>", "\n");
                    s = s.replaceAll("<END>", "");
                    String[] items = s.split("\n");
                    for(String i: items) {
                        Log.d("Mylog","line i=" + i);
                        String[] single_item = i.split("\t");
                        if(single_item.length >= 12) {
                            int lineNumber = Integer.parseInt(single_item[1]) - 1;
                            String name = "生產線" + single_item[1] + " " + single_item[2];
                            String time = "最後更新: " + single_item[0];
                            ValueItem v = new ValueItem(name, single_item[3], single_item[4], single_item[5], single_item[6], single_item[7], single_item[8], single_item[9], single_item[10], single_item[11], time);
                            valueArray.set(lineNumber, v);
                        }
                    }
                    valueAdapter.notifyDataSetChanged();
                    updateTime.setVisibility(View.VISIBLE);
                    Calendar c = Calendar.getInstance();
                    updateTime.setText("最後更新：" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
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
