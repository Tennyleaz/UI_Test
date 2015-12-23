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
    private boolean active = false;

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
        Log.d("Mylog", "command:" + cmd);
        Log.d("mylog", "before queryitems write to socket");
        SocketHandler.writeToSocket(cmd);
        Log.d("mylog", "after queryitems write to socket, before get output");
        result = SocketHandler.getOutput();
        Log.d("Mylog", "result:" + result);
        /*while(result==null ) {
            Log.d("Mylog", "get nothing, redo...");
            result = SocketHandler.getOutput();
        }*/
        while(result!=null && !(result.contains("QUERY_REPLY") || result.contains("QUERY_NULL")) ) {
            Log.d("Mylog", "get nothing, redo...");
            result = SocketHandler.getOutput();
        }
        //result = result.replaceAll("QUERY_REPLY\t", "");
        //result = result.replaceAll("<N>", "\n");
        //result = result.replaceAll("<END>", "");
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息後就會執行此方法
            //t1.setText(result);
            update();
            pd.dismiss();// 關閉ProgressDialog
            active = true;
            task = new UpdateTask().execute();
        }
    };

    private void update(){
        TableRow row0 = new TableRow(this);
        TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams tlr = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        tlr.setMargins(1, 0, 1, 0);
        tableRowParams.setMargins(1, 1, 1, 1);
        TextView tv1, tv2, tv3, tv4, tv5, tv6;
        tv1 = new TextView(this);
        tv2 = new TextView(this);
        tv3 = new TextView(this);
        tv4 = new TextView(this);
        tv5 = new TextView(this);
        if(activityName.equals("配料歷史")) {
            TL.removeAllViews();
            tv1.setText("時間");
            tv2.setText("品牌號碼");
            tv3.setText("品牌名稱");
            tv4.setText("流水編號");
            tv5.setText("員工");
            row0.addView(tv1);
            row0.addView(tv2);
            row0.addView(tv3);
            row0.addView(tv4);
            row0.addView(tv5);
            TL.addView(row0);
        } else if(activityName.equals("換牌歷史")) {
            TL.removeAllViews();
            tv1.setText("時間");
            tv2.setText("品牌名稱");
            tv3.setText("換成品牌");
            tv4.setText("位置");
            tv5.setText("員工");
            row0.addView(tv1);
            row0.addView(tv2);
            row0.addView(tv3);
            row0.addView(tv4);
            row0.addView(tv5);
            TL.addView(row0);
        } else if(activityName.equals("加香歷史")) {
            TL.removeAllViews();
            tv1.setText("時間");
            tv2.setText("配方名稱");
            tv3.setText("配方編號");
            tv5.setText("員工");
            row0.addView(tv1);
            row0.addView(tv2);
            row0.addView(tv3);
            row0.addView(tv5);
            TL.addView(row0);
        }

        //display table
        String[] lines = result.split("<END>");
        for(String s: lines) {
            if( ! (s.contains("QUERY_REPLY") || s.contains("QUERY_NULL")) ) continue;
            s = s.replaceAll("QUERY_REPLY\t", "");
            s = s.replaceAll("<N>", "\n");
            s = s.replaceAll("<END>", "");
            Log.d("Mylog", "s in line=" + s);
            Scanner scanner = new Scanner(s);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Log.d("mylog", "line=" + line);
                //if(line.contains("QUERY_NULL") || line.contains("UPDATE_VALUE") || line.contains("UPDATE_BOX") || line.contains("BOX_RECENT") || line.contains("UPDATE_ONLINE") || line.contains("SCHEDULE"))
                //    continue;
                //if(!(line.contains("QUERY_REPLY") || line.contains("QUERY_NULL")))
                //    continue;
                if (line.contains("QUERY_NULL"))
                    continue;
                // process the line
                TableRow row = new TableRow(this);
                row.setBackgroundColor(Color.parseColor("#bbbbbb"));//f3f3f3
                //set margin
                row.setLayoutParams(tableRowParams);
                TL.addView(row);
                //process each item
                String[] items = line.split("\t");
                for (int i = 0; i < items.length; i++) {
                    if (i == items.length - 1 && activityName.equals("配料歷史"))
                        break;
                    TextView tv = new TextView(this);
                    if (i == 0 && activityName.equals("加香情形")) {
                        if(!isInteger(items[i])) return;
                        tv.setText("加香槽 " + items[i]);
                    } else if (i == 0 && activityName.equals("換牌情形")) {  // 0=gray, 1=red, 2=yellow, 3=green
                        switch (items[i]) {
                            case "CM":
                                tv.setText("捲菸機 " + items[i + 1]);
                                break;
                            case "PM":
                                tv.setText("包裝機 " + items[i + 1]);
                                break;
                            case "FF":
                                tv.setText("濾嘴機 " + items[i + 1]);
                                break;
                            default:
                                tv.setText(items[i]);
                        }
                        tv.setMaxEms(12);
                        tv.setLayoutParams(tlr);
                        tv.setBackgroundColor(Color.parseColor("#f3f3f3"));
                        tv.setTextSize(18);
                        row.addView(tv);

                        TextView tvb = new TextView(this);
                        tvb.setMaxEms(12);
                        tvb.setLayoutParams(tlr);
                        tvb.setBackgroundColor(Color.parseColor("#f3f3f3"));
                        tvb.setText(items[3]);
                        tvb.setTextSize(18);
                        switch (items[2]) {
                            case "0":
                                tvb.setTextColor(getResources().getColor(R.color.swaplight_grey));
                                break;
                            case "1":
                                tvb.setTextColor(getResources().getColor(R.color.swaplight_red));
                                break;
                            case "2":
                                tvb.setTextColor(getResources().getColor(R.color.swaplight_yellow_text));
                                break;
                            case "3":
                                tvb.setTextColor(getResources().getColor(R.color.swaplight_green));
                                break;
                        }
                        row.addView(tvb);
                        break;
                    } else {
                        tv.setText(items[i]);
                    }
                    if (activityName.equals("加香情形"))
                        tv.setTextSize(18);
                    tv.setMaxEms(12);
                    tv.setLayoutParams(tlr);
                    tv.setBackgroundColor(Color.parseColor("#f3f3f3"));
                    row.addView(tv);
                }
            }
            scanner.close();
        }

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
                    if(isCancelled()) break;
                    if (!active) return null;
                    result = SocketHandler.getOutput();
                    if(isCancelled()) break;
                    publishProgress(result);
                    if(isCancelled()) break;
                } catch (Exception e) {
                    Log.e("Mylog", e.toString(), e);
                }
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            String[] lines = values[0].split("<END>");
            for(String s: lines) {
                if (!(s.contains("QUERY_REPLY") || s.contains("QUERY_NULL"))) continue;
                s = s.replaceAll("QUERY_REPLY\t", "");
                s = s.replaceAll("<N>", "\n");
                s = s.replaceAll("<END>", "");
                Scanner scanner = new Scanner(s);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    //if(line.contains("QUERY_NULL") || line.contains("UPDATE_VALUE") || line.contains("UPDATE_BOX") || line.contains("BOX_RECENT") || line.contains("UPDATE_ONLINE") || line.contains("SCHEDULE"))
                    if (line.contains("QUERY_NULL"))
                        continue;
                    //message.setVisibility(View.GONE);
                    // process the line
                    TableRow row = new TableRow(RecipeActivity.this);
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
            }
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
        if(result != null && !(result.contains("QUERY_REPLY") || result.contains("QUERY_NULL")) )
            return UpdateStatus();
        Log.d("Mylog", "update status receive:" + result);
        //result = result.replaceAll("UPDATE_WH_HISTORY\t" + realname + "\t", "");
        result = result.replaceAll("<N>", "\n");
        //result = result.replaceAll("<END>", "");
        return result;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public void onPause() {
        super.onPause();
        active = false;
        Log.d("Mylog", "Recipe avtivity paused");
        if(task!=null) {
            Log.d("Mylog", "task.cancel(true);");
            //SocketHandler.closeAndRestartSocket();
            task.cancel(true);
        }
    }

    public void onBackPressed(){
        Log.d("mylog", "back is pressed");
        if(task!=null) {
            task.cancel(true);
        }
        active = false;
        Thread[] threads = new Thread[Thread.activeCount()];  //close all running threads
        Thread.enumerate(threads);
        for (Thread t : threads) {
            if(t!=null) t.interrupt();
        }
        if(task!=null) {
            Log.d("mylog", "task is " + task.getStatus());
        } else {
            Log.d("mylog", "task is null!");
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }
}
