package com.example.tenny.uitest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.net.Socket;

// In this case, the fragment displays simple text based on the page
public class MachineStatus extends Activity {
    private TableLayout TL, TL2;
    private AsyncTask task = null;
    private static ProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.machine_status_layout);
        TL = (TableLayout) findViewById(R.id.table4);
        TL2 = (TableLayout) findViewById(R.id.table4_2);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("設備狀態");
        pd = ProgressDialog.show(MachineStatus.this, "LOADING", "Fetching data, \nPlease wait...");
    }

    @Override
    public void onStart() {
        super.onStart();
        task = new UpdateTask();
        AsyncTaskTools.execute(task);
        Log.d("Mylog", "Fragment 4 start");
    }

    @Override
    public void onPause(){
        super.onPause();
        if(task!=null)
            task.cancel(true);
    }

    private class UpdateTask extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... v) {
            Log.d("Mylog", "UpdateTask in fragment 4 doInBackground");
            boolean exit = false;
            while(!exit){
                if(MainMenu.currentPage!=3)
                    continue;
                String cmd = "QUERY\tONLINE_STATE<END>";
                SocketHandler.writeToSocket(cmd);
                Log.d("Mylog", "command:" + cmd);
                String s = UpdateStatus();
                publishProgress(s);
                try{
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    Log.e("Mylog", "Thread in fragment4:" + e.toString());
                }
                if (isCancelled())
                    break;
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            Log.d("mylog", "values[0]=" + values[0]);
            if(values[0] == null || values[0].length()==0)
                return;
            if(pd!=null)
                pd.dismiss();
            String[] items = values[0].split("\n");
            TL.removeAllViews();
            TL2.removeAllViews();
            for(int i=0; i<items.length; i+=2) {
                String[] item = items[i].split("\t");
                if(item.length <2)
                    continue;
                TableRow row = new TableRow(MachineStatus.this);
                row.setBackgroundColor(Color.parseColor("#eeeeee"));
                //set margin
                TableLayout.LayoutParams tableRowParams =
                        new TableLayout.LayoutParams
                                (TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                tableRowParams.setMargins(1, 1, 1, 1);
                row.setLayoutParams(tableRowParams);
                TL.addView(row);

                TableRow.LayoutParams tlr = new TableRow.LayoutParams(32, 32);
                tlr.setMargins(6, 6, 6, 6);

                ImageView iv1 = new ImageView(MachineStatus.this);
                if(item[1].equals("1"))
                    iv1.setImageResource(R.drawable.green_circle);
                else
                    iv1.setImageResource(R.drawable.red_cross);
                iv1.setLayoutParams(tlr);
                row.addView(iv1);
                //
                TextView tv = new TextView(MachineStatus.this);
                tv.setText(item[0]);
                tv.setTextSize(20);
                row.addView(tv);
                //repeat 2nd table column
                //new switch button
                if(i+1 >= items.length)
                    break;
                item = items[i+1].split("\t");
                if(item.length <2)
                    continue;
                TableRow row2 = new TableRow(MachineStatus.this);
                row2.setBackgroundColor(Color.parseColor("#eeeeee"));
                //set margin
                row2.setLayoutParams(tableRowParams);
                TL2.addView(row2);
                ImageView iv2 = new ImageView(MachineStatus.this);
                if(item[1].equals("1"))
                    iv2.setImageResource(R.drawable.green_circle);
                else
                    iv2.setImageResource(R.drawable.red_cross);
                iv2.setLayoutParams(tlr);
                row2.addView(iv2);
                //
                TextView tv2 = new TextView(MachineStatus.this);
                tv2.setText(item[0]);
                tv2.setTextSize(20);
                row2.addView(tv2);
            }
        }
    }

    private String UpdateStatus() {
        String result;
        result = SocketHandler.getOutput();
        //Log.d("Mylog", "query result:" + cmd);
        while(result == null || result.length() == 0)
            result = SocketHandler.getOutput();;
        String[] lines = result.split("<END>");
        int length = lines.length;
        for (String s : lines) {
            if (s != null && s.contains("QUERY_REPLY\t")) {
                s = s.replaceAll("QUERY_REPLY\t", "");
                s = s.replaceAll("<N>", "\n");
                s = s.replaceAll("<END>", "");
                return  s;
            } else if(s != null && s.contains("UPDATE_VALUE")){
                return UpdateStatus();
            }
        }
        //Log.d("Mylog", "final result:" + result);
        return null;
    }
}