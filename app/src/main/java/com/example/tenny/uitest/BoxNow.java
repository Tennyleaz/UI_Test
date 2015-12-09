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

/**
 * Created by Tenny on 2015/11/28.
 */
public class BoxNow extends Activity {
    private AsyncTask task = null;
    static final int MAX_LINE = 9;
    private BoxAdapter boxAdapter;
    private ArrayList<BoxItem> boxArray;
    private ListView boxListView;
    private TextView updateTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.box_now_layout);
        updateTime = (TextView) findViewById(R.id.updateTime);
        updateTime.setVisibility(View.GONE);
        boxListView = (ListView) findViewById(R.id.boxListView);

        boxArray = new ArrayList<BoxItem>();
        for (int i=1; i<=MAX_LINE; i++) {
            BoxItem b = new BoxItem(String.valueOf(i), "0", "0");
            boxArray.add(b);
        }
        boxAdapter = new BoxAdapter(BoxNow.this, boxArray);
        boxListView.setAdapter(boxAdapter);
        boxAdapter.notifyDataSetChanged();

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
                if(s!=null && s.contains("UPDATE_BOX\t")) { //UPDATE_BOX \t 線號 \t 現在箱數 \t 目標箱數
                    s = s.replaceAll("UPDATE_BOX\t", "");
                    s = s.replaceAll("<N>", "\n");
                    s = s.replaceAll("<END>", "");
                    String[] items = s.split("\n");
                    for(String i: items) {
                        Log.d("Mylog","line i=" + i);
                        String[] single_item = i.split("\t");
                        if(single_item.length >= 3) {
                            int lineNumber = Integer.parseInt(single_item[0]) - 1;
                            BoxItem b = new BoxItem(single_item[0], single_item[1], single_item[2]);
                            boxArray.set(lineNumber, b);
                        }
                    }
                    boxAdapter.notifyDataSetChanged();
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
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }
}
