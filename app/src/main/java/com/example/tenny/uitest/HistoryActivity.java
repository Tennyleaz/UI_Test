package com.example.tenny.uitest;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Tenny on 2015/7/27.
 * 庫存情形, 查詢歷史紀錄
 */
public class HistoryActivity extends Activity {
    private TextView t;
    private TextView message;
    static private TableLayout TL;
    private Button date_btn;
    private Calendar c;
    private String Qname;
    private String Gname;
    static private List<String> result;
    private static ProgressDialog pd;
    private int mymonth, myyear, mydate;
    private String realgroup, realname;
    private AsyncTask task = null;
    private boolean isImport;
    private String importItemName;
    private int importNumber;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);
        t = (TextView) findViewById(R.id.tempText);
        TL = (TableLayout) findViewById(R.id.table_history);
        t.setText("123");
        message = (TextView) findViewById(R.id.table_message);
        Intent intent = getIntent();
        Qname = intent.getStringExtra("HouseName");
        Gname = intent.getStringExtra("GroupClass");
        t.setText(Gname + " " + Qname);
        date_btn = (Button) findViewById(R.id.date_btn);
        if(Qname.equals("線邊倉") && Gname.equals("庫存情形"))
            date_btn.setVisibility(View.GONE);
        c = Calendar.getInstance();
        mymonth = c.get(Calendar.MONTH) + 1;
        myyear = c.get(Calendar.YEAR);
        mydate = c.get(Calendar.DATE);
        date_btn.setText(myyear + " - " + mymonth + "月 " + mydate + "日");
        date_btn.setOnClickListener(onclick);
        result = new ArrayList<String>();
        pd = ProgressDialog.show(HistoryActivity.this, "LOADING", "Fetching data, \nPlease wait...");
        //開啟一個新線程，在新線程裡執行耗時的方法
        new Thread(new Runnable() {
            @Override
            public void run() {
                QueryItems();// 耗時的方法
                handler.sendEmptyMessage(0);// 執行耗時的方法之後發送消給handler
            }

        }).start();
    }

    View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerDialog dpd = new DatePickerDialog(
                    HistoryActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            if (view.isShown()) {
                                if(task!=null)
                                    task.cancel(true);
                                pd = ProgressDialog.show(HistoryActivity.this, "LOADING", "Fetching data, \nPlease wait...");
                                Log.d("Mylog", "Date selected.");
                                // 完成選擇，顯示日期
                                myyear = year;
                                mymonth = monthOfYear + 1;
                                mydate = dayOfMonth;
                                date_btn.setText(myyear + " - " + mymonth + "月 " + mydate + "日");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        QueryItems();// 耗時的方法
                                        handler.sendEmptyMessage(0);// 執行耗時的方法之後發送消給handler
                                    }

                                }).start();
                            }
                        }
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH));
            dpd.getDatePicker().setCalendarViewShown(false);

                        dpd.show();
        }
    };

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息後就會執行此方法
            //t1.setText(result);
            Log.d("Mylog", "handleMessage is called");
            updateUI();
            pd.dismiss();// 關閉ProgressDialog
            task = new UpdateTask().execute();
        }
    };

    private void updateUI(){
        //display table
        Log.d("Mylog", "History: updating UI...");
        TL.removeAllViews();

        for(String s:result) {
            //Log.d("Mylog", "s:result=" + s);
            Scanner scanner = new Scanner(s);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.contains("QUERY_NULL"))
                    continue;
                // process the line
                TableRow row = new TableRow(this);
                row.setBackgroundColor(Color.parseColor("#bbbbbb"));//f3f3f3
                //set margin
                TableLayout.LayoutParams tableRowParams =
                        new TableLayout.LayoutParams
                                (TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                tableRowParams.setMargins(1, 1, 1, 1);
                row.setLayoutParams(tableRowParams);
                TL.addView(row);
                //process each item
                String[] items = line.split("\t");
                for (int i = 0; i < items.length; i++) {
                    if(i==0)
                        items[i] = items[i].replaceAll(myyear + "/", "");
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
        }

        if(TL.getChildCount() == 0) {
            message.setVisibility(View.VISIBLE);
            message.setText("No Data");
        }
        else {
            //message.setVisibility(View.GONE);
            Calendar c = Calendar.getInstance();
            message.setText("最後更新：" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DATE)  + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
        }

    }

    private void QueryItems() {
        //Socket socket = SocketHandler.getSocket();
        result.clear();
        if (Qname.equals("3號倉庫"))
            realname = "3";
        else if (Qname.equals("5號倉庫"))
            realname = "5";
        else if (Qname.equals("6號倉庫"))
            realname = "6";
        else if (Qname.equals("線邊倉"))
            realname = "0";
        else
            realname = null;

        if (Gname.equals("查詢進出貨歷史紀錄"))
            realgroup = "WH_HISTORY";
        else if (Gname.equals("查詢庫存歷史紀錄"))
            realgroup = "WH_NOW";
        else
            realgroup = null;

        //example: QUERY WH_HISTORY 3 2015 07 01<END>
        //for(int day=1; day<31; day++) {
        String cmd;
        if (realname.equals("0"))
            cmd = "QUERY\tSH_HISTORY\t" + myyear + "\t" + mymonth + "\t" + mydate + "<END>";
        else if(realgroup.equals("WH_NOW"))
            cmd = "QUERY\t" + realgroup + "\t" + realname + "\t" + myyear + "\t" + mymonth + "\t" + mydate + "<END>";
        else
            cmd = "QUERY\t" + realgroup + "\t" + realname + "\t" + myyear + "\t" + mymonth + "\t" + mydate + "\t" + myyear + "\t" + mymonth + "\t" + mydate +"<END>";

        if(!SocketHandler.isCreated)
            SocketHandler.initSocket("140.113.167.14", 9000);
        SocketHandler.writeToSocket(cmd);
        Log.d("Mylog", "QueryItems::command:" + cmd);
        String output = SocketHandler.getOutput();
        while(output == null || !output.contains("QUERY_REPLY") ) {
            //return;
            if (output!=null && output.contains("QUERY_NULL"))
                break;
            output = SocketHandler.getOutput();
            Log.d("Mylog", "history activity get nothing, re-recieve");
        }
        output = output.replaceAll("QUERY_REPLY\t", "");
        output = output.replaceAll("<N>", "\n");
        output = output.replaceAll("<END>", "");
        result.add(output);
        //}
        Log.d("Mylog", "Query done.");
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
            //if
            Scanner scanner = new Scanner(values[0]);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(realgroup.equals("WH_HISTORY")) {  //update 進出貨情況
                    //message.setVisibility(View.GONE);
                    // process the line
                    TableRow row = new TableRow(HistoryActivity.this);
                    row.setBackgroundColor(Color.parseColor("#bbbbbb"));//f3f3f3
                    //set margin
                    TableLayout.LayoutParams tableRowParams =
                            new TableLayout.LayoutParams
                                    (TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                    tableRowParams.setMargins(1, 1, 1, 1);
                    row.setLayoutParams(tableRowParams);
                    TL.addView(row, 0);
                    //process each item
                    String[] items = line.split("\t");
                    for (int i = 0; i < items.length; i++) {
                        TextView tv = new TextView(HistoryActivity.this);
                        tv.setMaxEms(8);
                        TableRow.LayoutParams tlr = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        tlr.setMargins(1, 0, 1, 0);
                        tv.setLayoutParams(tlr);
                        tv.setText(items[i]);
                        tv.setBackgroundColor(Color.parseColor("#f3f3f3"));
                        row.addView(tv);
                    }
                }
                else if(realgroup.equals("WH_NOW")) {  //update 庫存情形
                    for(int i = 0, j = TL.getChildCount(); i < j; i++) {
                        TableRow row = (TableRow)TL.getChildAt(i);
                        TextView tv = (TextView) row.getChildAt(0);
                        TextView number = (TextView) row.getChildAt(2);
                        if(tv.getText().equals(importItemName)) {
                            //Log.d("Mylog", "tv is " + tv.getText().toString());
                            //Log.d("Mylog", "number is " + number.getText().toString());
                            /*int num = Integer.valueOf(number.getText().toString());
                            if(isImport)
                                num += importNumber;
                            else
                                num -= importNumber;*/
                            number.setText(Integer.toString(importNumber));
                            break;
                        }
                    }
                }
                else {
                    //Log.e("Mylog", "Something is wrong!!! onProgressUpdate");
                }
            }
            scanner.close();
            Calendar c = Calendar.getInstance();
            message.setVisibility(View.VISIBLE);
            message.setText("最後更新：" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
        }
    }

    private String UpdateStatus() {   //realgroup: UPDATE_WH_NOW = 庫存情形    UPDATE_WH_HISTORY=進出貨
        String result;
        result = SocketHandler.getOutput();
        if(result == null || result.length() <= 0)
            return "";

        if(result.contains("UPDATE_ONLINE"))
            return "";

        String[] items = result.split("\t");

        if(result.contains("UPDATE_WH_HISTORY")) {
            if (items[3].equals("收料")) {
                isImport = true;
            } else if (items[3].equals("出料")) {
                isImport = false;
            }
        }

        if(realgroup.equals("WH_HISTORY") && result.contains("UPDATE_WH_HISTORY")) {
            Log.d("Mylog", "進出貨 receive:" + result);
            result = result.replaceAll("UPDATE_" + realgroup + realname + "\t", "");
            result = result.replaceAll("<N>", "\n");
            result = result.replaceAll("<END>", "");
        }
        else if(realgroup.equals("WH_NOW") && result.contains("UPDATE_WH_NOW")) {
            Log.d("Mylog", "庫存更新 receive:" + result);
            importItemName = items[2];
            importNumber = (int) Double.parseDouble(items[4]);
            Log.d("Mylog", "now " + items[2] + " " + importNumber);
        }
        else {
            Log.e("Mylog", "Something is wrong!!! realgroup:" + realgroup);
        }
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
}
