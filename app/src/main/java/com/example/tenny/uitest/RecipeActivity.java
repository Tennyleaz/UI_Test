package com.example.tenny.uitest;

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
public class RecipeActivity extends Activity {
    private AsyncTask task = null;
    private String activityName = null, result;
    private TextView updateTime;
    private static ProgressDialog pd;
    static private TableLayout TL;
    private Button date_btn;
    private int mymonth, myyear, mydate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_layout);
        TL = (TableLayout) findViewById(R.id.recipe_table);
        TextView title = (TextView) findViewById(R.id.activityTitile);
        Intent intent = getIntent();
        activityName = intent.getStringExtra("ActionName");
        title.setText(activityName);
        updateTime = (TextView) findViewById(R.id.updateTimer);

        Calendar c = Calendar.getInstance();
        mymonth = c.get(Calendar.MONTH) + 1;
        myyear = c.get(Calendar.YEAR);
        mydate = c.get(Calendar.DATE);
        date_btn = (Button) findViewById(R.id.button);
        date_btn.setText(myyear + " - " + mymonth + "月 " + mydate + "日");
        date_btn.setOnClickListener(onclick);
        if (activityName.contains("情形"))
            date_btn.setVisibility(View.GONE);

        pd = ProgressDialog.show(RecipeActivity.this, "LOADING", "Fetching data, \nPlease wait...");
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
                    RecipeActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            if (view.isShown()) {
                                if(task!=null)
                                    task.cancel(true);
                                pd = ProgressDialog.show(RecipeActivity.this, "LOADING", "Fetching data, \nPlease wait...");
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
        switch (activityName) {
            case ("配料歷史"):
                cmd = "QUERY\tMS_HISTORY\t" +myyear + "\t" + mymonth + "\t" + mydate + "\t" + myyear + "\t" + mymonth + "\t" + mydate + "<END>";
                break;
            case ("加香情形") :
                cmd = "QUERY\tRECIPE_NOW<END>";
                break;
            case ("加香歷史") :
                cmd = "QUERY\tAS_HISTORY\t" +myyear + "\t" + mymonth + "\t" + mydate + "\t" + myyear + "\t" + mymonth + "\t" + mydate + "<END>";
                break;
            case ("換牌情形") :
                cmd = "QUERY\tSWAP<END>";
                break;
            case ("換牌歷史") :
                cmd = "QUERY\tSWAP_HISTORY\t" +myyear + "\t" + mymonth + "\t" + mydate + "\t" + myyear + "\t" + mymonth + "\t" + mydate + "<END>";
                break;

      }

        SocketHandler.writeToSocket(cmd);
        result = SocketHandler.getOutput();
        Log.d("Mylog", "command:" + cmd);
        Log.d("Mylog", "result:" + result);
        while(result==null || !(result.contains("QUERY_REPLY") || result.contains("QUERY_NULL")) ) {
            Log.d("Mylog", "get nothing, redo...");
            result = SocketHandler.getOutput();
        }
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
            if(line.contains("QUERY_NULL") || line.contains("UPDATE_VALUE") || line.contains("UPDATE_BOX"))
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
                if(i==items.length-1 && activityName.equals("配料歷史"))
                    break;
                TableRow.LayoutParams tlr = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                tlr.setMargins(1, 0, 1, 0);
                TextView tv = new TextView(this);
                if(i==0 && activityName.equals("加香情形")) {
                    tv.setText("加香槽 " + items[i]);
                } else if(i==0 && activityName.equals("換牌情形")) {  // 0=gray, 1=red, 2=yellow, 3=green
                    switch (items[i]) {
                        case "CM":
                            tv.setText("捲菸機 " + items[i+1]);
                            break;
                        case "PM":
                            tv.setText("包裝機 " + items[i+1]);
                            break;
                        case "FF":
                            tv.setText("濾嘴機 " + items[i+1]);
                            break;
                        default:
                            tv.setText(items[i]);
                    }
                    tv.setMaxEms(12);
                    tv.setLayoutParams(tlr);
                    tv.setBackgroundColor(Color.parseColor("#f3f3f3"));
                    tv.setTextSize(18);
                    row.addView(tv);

                    TextView tv2 = new TextView(this);
                    tv2.setMaxEms(12);
                    tv2.setLayoutParams(tlr);
                    tv2.setBackgroundColor(Color.parseColor("#f3f3f3"));
                    tv2.setText(items[3]);
                    tv2.setTextSize(18);
                    switch (items[2]) {
                        case "0":
                            tv2.setTextColor(getResources().getColor(R.color.swaplight_grey));
                            break;
                        case "1":
                            tv2.setTextColor(getResources().getColor(R.color.swaplight_red));
                            break;
                        case "2":
                            tv2.setTextColor(getResources().getColor(R.color.swaplight_yellow_text));
                            break;
                        case "3":
                            tv2.setTextColor(getResources().getColor(R.color.swaplight_green));
                            break;
                    }
                    row.addView(tv2);
                    break;
                } else {
                    tv.setText(items[i]);
                }
                if(activityName.equals("加香情形"))
                    tv.setTextSize(18);
                tv.setMaxEms(12);
                tv.setLayoutParams(tlr);
                tv.setBackgroundColor(Color.parseColor("#f3f3f3"));
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
                if(line.contains("QUERY_NULL") || line.contains("UPDATE_VALUE") || line.contains("UPDATE_BOX"))
                    continue;
                //message.setVisibility(View.GONE);
                // process the line
                TableRow row = new TableRow(RecipeActivity.this);
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
                    TextView tv = new TextView(RecipeActivity.this);
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
        if(result != null && (result.contains("UPDATE_ONLINE") || result.contains("UPDATE_BOX") || result.contains("UPDATE_VALUE")))
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
