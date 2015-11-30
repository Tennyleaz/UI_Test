package com.example.tenny.uitest;

/**
 * Created by Tenny on 2015/11/29.
 */

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Scanner;
/**
 * Created by Tenny on 2015/11/28.
 */
public class BoxHistory extends Activity {
    private AsyncTask task = null;
    private String result;
    private TextView updateTime;
    private static ProgressDialog pd;
    static private TableLayout TL;
    private Button date_btn;
    private int mymonth, myyear, mydate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.box_history_layout);
        TL = (TableLayout) findViewById(R.id.box_table);
        TextView title = (TextView) findViewById(R.id.activityTitile);
        title.setText("歷史箱數");
        updateTime = (TextView) findViewById(R.id.updateTimer);

        Calendar c = Calendar.getInstance();
        mymonth = c.get(Calendar.MONTH) + 1;
        myyear = c.get(Calendar.YEAR);
        mydate = c.get(Calendar.DATE);
        date_btn = (Button) findViewById(R.id.button);
        date_btn.setText(myyear + " - " + mymonth + "月 " + mydate + "日");
        date_btn.setOnClickListener(onclick);

        pd = ProgressDialog.show(BoxHistory.this, "LOADING", "Fetching data, \nPlease wait...");
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
            Calendar c = Calendar.getInstance();
            DatePickerDialog dpd = new DatePickerDialog(
                    BoxHistory.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            if (view.isShown()) {
                                if(task!=null)
                                    task.cancel(true);
                                pd = ProgressDialog.show(BoxHistory.this, "LOADING", "Fetching data, \nPlease wait...");
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

    private void QueryItems() {
        Log.d("Mylog", "Recipe Avtivity: QueryItems...");
        Calendar c = Calendar.getInstance();
        String cmd = "";
        cmd = "QUERY\tBOX_HISTORY\t" +myyear + "\t" + mymonth + "\t" + mydate + "\t" + myyear + "\t" + mymonth + "\t" + mydate + "<END>";

        SocketHandler.writeToSocket(cmd);
        Log.d("Mylog", "command:" + cmd);

        result = null;
        while(result == null || result.length() == 0) {
            result = SocketHandler.getOutput();
            if(result == null || result.length() == 0)
                continue;
            String[] lines = result.split("<END>");
            boolean ok = false;
            for (String s : lines) {
                if (s != null && s.contains("QUERY_REPLY\t")) {
                    result = s;
                    ok = true;
                    break;
                } else if(s != null && s.contains("QUERY_NULL")){
                    ok = true;
                    result = s;
                    break;
                }
            }
            if(!ok)
                result = null;
        }

        Log.d("Mylog", "result:" + result);
        result = result.replaceAll("QUERY_REPLY\t", "");
        result = result.replaceAll("<N>", "\n");
        result = result.replaceAll("<END>", "");
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
            if(line.contains("QUERY_NULL") || line.contains("UPDATE_VALUE"))
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
                TableRow.LayoutParams tlr = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                tlr.setMargins(1, 0, 1, 0);
                TextView tv = new TextView(this);
                tv.setMaxEms(12);
                tv.setLayoutParams(tlr);
                tv.setBackgroundColor(Color.parseColor("#f3f3f3"));
                tv.setText(items[i]);
                row.addView(tv);
            }
        }
        scanner.close();

        if(TL.getChildCount() == 0) {
            updateTime.setVisibility(View.VISIBLE);
            updateTime.setText("No Data");
        }
        else {
            updateTime.setVisibility(View.VISIBLE);
            Calendar c = Calendar.getInstance();
            updateTime.setText("最後更新：" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DATE)  + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
        }
    }

    private class UpdateTask extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... v) {
            while(!isCancelled()){
                try {
                    Log.d("Mylog", "UpdateTask do...");
                    String s = UpdateStatus();
                    publishProgress(s);
                } catch (Exception e) {
                    Log.e("Mylog", e.toString(), e);
                }
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            Scanner scanner = new Scanner(values[0]);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.contains("QUERY_NULL") || line.contains("UPDATE_VALUE"))
                    continue;
                //message.setVisibility(View.GONE);
                // process the line
                TableRow row = new TableRow(BoxHistory.this);
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
                    TextView tv = new TextView(BoxHistory.this);
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
                updateTime.setText("No Data");
            }
            else {
                Calendar c = Calendar.getInstance();
                updateTime.setText("最後更新：" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
            }
        }

    }

    private String UpdateStatus() {
        String result;
        result = SocketHandler.getOutput();
        if(result != null && (result.contains("UPDATE_ONLINE") || result.contains("UPDATE_VALUE")))
            return UpdateStatus();
        Log.d("Mylog", "update status receive:" + result);
        //result = result.replaceAll("UPDATE_WH_HISTORY\t" + realname + "\t", "");
        result = result.replaceAll("<N>", "\n");
        result = result.replaceAll("<END>", "");
        return result;
    }


    public void onPause() {
        super.onPause();
        Log.d("Mylog", "Recipe avtivity paused");
        if(task!=null) {
            Log.d("Mylog", "task.cancel(true);");
            //SocketHandler.closeAndRestartSocket();
            task.cancel(true);
        }
    }
}

