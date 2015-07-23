package com.example.tenny.uitest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Socket;

/**
 * Created by Tenny on 2015/7/23.
 */
public class IOtempActivity extends Activity {
    private TextView t;
    private static TextView t1, t2, t3;
    private String Qname;
    private String Gname;
    static private String result, result2, result3;
    private static ProgressDialog pd;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.io_template);
        t = (TextView) findViewById(R.id.tempText);
        t1 = (TextView) findViewById(R.id.tempText1);
        t2 = (TextView) findViewById(R.id.tempText2);
        t3 = (TextView) findViewById(R.id.tempText3);
        t.setText("123");
        Intent intent = getIntent();
        Qname = intent.getStringExtra("TestName");
        Gname = intent.getStringExtra("GroupClass");
        t.setText(Gname + " " + Qname);
        pd = ProgressDialog.show(IOtempActivity.this, "LOADING", "Fetching data, \nPlease wait...");
        //開啟一個新線程，在新線程裡執行耗時的方法
        new Thread(new Runnable() {
            @Override
            public void run() {
                QueryItems();// 耗時的方法
                handler.sendEmptyMessage(0);// 執行耗時的方法之後發送消給handler
            }

        }).start();
    }

    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息後就會執行此方法
            pd.dismiss();// 關閉ProgressDialog
            t1.setText(result);
        }
    };

    private void QueryItems() {
        Socket socket = SocketHandler.getSocket();
        String realname = "";
        String realgroup = "";
        if(Qname.equals("3號倉庫"))
            realname = "3";
        else if(Qname.equals("5號倉庫"))
            realname = "5";
        else if(Qname.equals("6號倉庫"))
            realname = "6";
        else if(Qname.equals("線邊倉"))
            realname = "0";
        else
            realname = null;

        if(Gname.equals("進出貨情況"))
            realgroup = "WH_HISTORY";
        else if(Gname.equals("庫存情形"))
            realgroup = "WH_NOW";
        else if(Gname.equals("查詢歷史紀錄"))
            realgroup = "WH_HISTORY";
        else
            realgroup = null;

        String cmd = "QUERY " + realgroup + " " + realname + "<END>";
        SocketHandler.writeToSocket(cmd);
        Log.d("Mylog", "command:" + cmd);
        result = SocketHandler.getOutput();
        result = result.replaceAll("<N>", "\n");
        result = result.replaceAll("<END>", "\n");
    }
}
