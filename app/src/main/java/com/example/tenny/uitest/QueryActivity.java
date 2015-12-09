package com.example.tenny.uitest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Scanner;

/**
 * Created by Tenny on 2015/7/23.
 * //本日進出貨情況
 * WH_HISTORY & SH_HISTORY
 */
public class QueryActivity extends Activity {
    private TextView t, message;
    private static TextView t1, t2;
    static private TableLayout TL;
    private String Qname;  //house name
    private String Gname;
    private static String realname;
    static private String result, result2, result3;
    private static ProgressDialog pd;
    private AsyncTask task = null;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_layout);
        t = (TextView) findViewById(R.id.tempText);
        //t1 = (TextView) findViewById(R.id.tempText1);
        //t2 = (TextView) findViewById(R.id.tempText2);
        message = (TextView) findViewById(R.id.table_message);
        TL = (TableLayout) findViewById(R.id.tab1_table1);
        t.setText("123");
        Intent intent = getIntent();
        Qname = intent.getStringExtra("HouseName");
        Gname = intent.getStringExtra("GroupClass");
        t.setText(Gname + " " + Qname);
        pd = ProgressDialog.show(QueryActivity.this, "LOADING", "Fetching data, \nPlease wait...");
        Log.d("mylog", "Qname=" + Qname + "Gname=" + Gname);
        //開啟一個新線程，在新線程裡執行耗時的方法
        new Thread(new Runnable() {
            @Override
            public void run() {
                QueryItems();// 耗時的方法
                handler.sendEmptyMessage(0);// 執行耗時的方法之後發送消給handler
            }

        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息後就會執行此方法
            //t1.setText(result);
            update();
            pd.dismiss();// 關閉ProgressDialog
            task = new UpdateTask().execute();
        }
    };

    private void update(){
        //display table
        Scanner scanner = new Scanner(result);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(line.contains("QUERY_NULL"))
                continue;
            // process the line
            TableRow row = new TableRow(this);
            row.setBackgroundColor(Color.parseColor("#bbbbbb"));//f3f3f3
            //set margin
            TableLayout.LayoutParams tableRowParams=
                    new TableLayout.LayoutParams
                            (TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
            tableRowParams.setMargins(1, 1, 1, 1);
            row.setLayoutParams(tableRowParams);
            TL.addView(row);
            //process each item
            String[] items = line.split("\t");
            for(int i=0; i<items.length; i++) {
                if(i==0) {
                    Calendar c = Calendar.getInstance();
                    items[i] = items[i].replaceAll(c.get(Calendar.YEAR) + "/", "");
                }
                TextView tv = new TextView(this);
                tv.setMaxEms(12);
                TableRow.LayoutParams tlr = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                tlr.setMargins(1, 0, 1, 0);
                tv.setLayoutParams(tlr);
                tv.setText(items[i]);
                tv.setBackgroundColor(Color.parseColor("#f3f3f3"));
                row.addView(tv);
            }
        }
        scanner.close();

        if(TL.getChildCount() == 0) {
            message.setVisibility(View.VISIBLE);
            message.setText("No Data");
        }
        else {
            message.setVisibility(View.VISIBLE);
            Calendar c = Calendar.getInstance();
            message.setText("最後更新：" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DATE)  + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
        }
    }

    private void QueryItems() {
        //Socket socket = SocketHandler.getSocket();
        //String realname = "";
        String realgroup = "";
        if(Qname.contains("3號倉庫"))
            realname = "3";
        else if(Qname.contains("5號倉庫"))
            realname = "5";
        else if(Qname.contains("6號倉庫"))
            realname = "6";
        else if(Qname.contains("線邊倉"))
            realname = "0";
        else
            realname = null;

        if(Gname.equals("本日進出貨情況"))
            realgroup = "WH_HISTORY";
        else if(Gname.equals("本日庫存情形"))
            realgroup = "WH_NOW";
        //else if(Gname.equals("查詢歷史紀錄"))
        //    realgroup = "WH_HISTORY";
        else
            realgroup = null;

        Calendar c = Calendar.getInstance();
        //example: QUERY WH_HISTORY 3 2015 07 01<END>
        String cmd;
        if(Gname.equals("本日庫存情形")) {  //this if will not execute
            if (realname.equals("0"))
                cmd = "QUERY\tSH_NOW<END>";
            else
            cmd = "QUERY\tWH_NOW\t" + realname + "\t" + c.get(Calendar.YEAR) + "\t" + (c.get(Calendar.MONTH) + 1) + "\t" + c.get(Calendar.DATE) + "<END>";
        }
        else {
            if (realname.equals("0"))
                cmd = "QUERY\tSH_HISTORY\t" + c.get(Calendar.YEAR) + "\t" + (c.get(Calendar.MONTH) + 1) + "\t" + c.get(Calendar.DATE) +  "<END>";
            else
                cmd = "QUERY\t" + realgroup + "\t" + realname + "\t" + c.get(Calendar.YEAR) + "\t" + (c.get(Calendar.MONTH) + 1) + "\t" + c.get(Calendar.DATE) + "\t" + c.get(Calendar.YEAR) + "\t" + (c.get(Calendar.MONTH) + 1) + "\t" + c.get(Calendar.DATE) + "<END>";
        }

        SocketHandler.writeToSocket(cmd);
        Log.d("Mylog", "command:" + cmd);
        result = SocketHandler.getOutput();
        while(result!=null && !(result.contains("QUERY_REPLY") || result.contains("QUERY_NULL")) ) {
            Log.d("Mylog", "get nothing, redo...");
            result = SocketHandler.getOutput();
        }
        result = result.replaceAll("QUERY_REPLY\t", "");
        result = result.replaceAll("<N>", "\n");
        result = result.replaceAll("<END>", "");
    }

    private class UpdateTask extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... v) {
            //boolean exit = false;
            while(!isCancelled()){
                try {
                    //Thread.sleep(10000);
                    Log.d("Mylog", "UpdateTask do...");
                    String s = UpdateStatus();
                    publishProgress(s);
                    /*if (isCancelled()) {
                        Log.d("Mylog", "UpdateTask isCancelled()");
                        SocketHandler.closeAndRestartSocket();
                        break;
                    }*/
                //} catch (InterruptedException e) {
                //    Log.e("Mylog", "Thread in QueryActivity::UpdateTask:" + e.toString());
                } catch (Exception e) {
                    Log.e("Mylog", e.toString(), e);
                }
            }
            //SocketHandler.closeAndRestartSocket();
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            Scanner scanner = new Scanner(values[0]);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(!(line.contains("QUERY_REPLY") || line.contains("QUERY_NULL")))
                    continue;
                if(line.contains("QUERY_NULL"))
                    continue;
                //message.setVisibility(View.GONE);
                // process the line
                TableRow row = new TableRow(QueryActivity.this);
                row.setBackgroundColor(Color.parseColor("#bbbbbb"));//f3f3f3
                //set margin
                TableLayout.LayoutParams tableRowParams=
                        new TableLayout.LayoutParams
                                (TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
                tableRowParams.setMargins(1, 1, 1, 1);
                row.setLayoutParams(tableRowParams);
                TL.addView(row, 0);
                //process each item
                String[] items = line.split("\t");
                for(int i=0; i<items.length; i++) {
                    TextView tv = new TextView(QueryActivity.this);
                    tv.setMaxEms(8);
                    TableRow.LayoutParams tlr = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    tlr.setMargins(1, 0, 1, 0);
                    tv.setLayoutParams(tlr);
                    tv.setText(items[i]);
                    tv.setBackgroundColor(Color.parseColor("#f3f3f3"));
                    row.addView(tv);
                }
            }
            scanner.close();
            if(TL.getChildCount() == 0) {
                message.setText("No Data");
            }
            else {
                Calendar c = Calendar.getInstance();
                message.setText("最後更新：" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
            }
        }

    }

    private String UpdateStatus() {
        String result;
        result = SocketHandler.getOutput();
        if(result != null && !(result.contains("QUERY_REPLY") || result.contains("QUERY_NULL")) )
            return UpdateStatus();
        Log.d("Mylog", "QueryActivity update status " + realname + " receive:" + result);
        result = result.replaceAll("UPDATE_WH_HISTORY\t" + realname + "\t", "");
        result = result.replaceAll("QUERY_REPLY\t", "");
        result = result.replaceAll("<N>", "\n");
        result = result.replaceAll("<END>", "");
        return result;
    }

    public void onPause() {
        super.onPause();
        Log.d("Mylog", "Query avtivity paused");
        if(task!=null) {
            Log.d("Mylog", "task.cancel(true);");
            //SocketHandler.closeAndRestartSocket();
            task.cancel(true);
        }
    }

    public void onBackPressed(){
        Log.d("mylog", "back is pressed");
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }
}
