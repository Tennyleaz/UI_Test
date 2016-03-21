package com.example.tenny.uitest;

/**
 * Created by Tenny on 2015/7/19.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Login extends ActionBarActivity {

    private EditText username;
    private EditText password;
    private Button login_btn;
    private static TextView message;
    static final String SERVERIP = "192.168.1.250";//"140.113.167.14";
    static final int SERVERPORT = 9000; //8000= echo server, 9000=real server
    private String str1="0",str2="0";
    private static ProgressDialog pd;
    private static short connected;
    private static int rebootCount;
    private AsyncTask task;
    private static String BoardID = "MI_6";
    static final String version = "v1.0.03";
    //private Thread t;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //if (android.os.Build.VERSION.SDK_INT > 9) {
        //    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //    StrictMode.setThreadPolicy(policy);
        //}
        //to enable action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
        connected = 0;
        message = (TextView) findViewById(R.id.message);
        message.setText("");
        username = (EditText) findViewById(R.id.accounts);
        password = (EditText) findViewById(R.id.password);
        login_btn = (Button) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(onclick);

        TextView appid = (TextView) findViewById(R.id.appID);
        TextView ver = (TextView) findViewById(R.id.versionNumber);

        final SharedPreferences settings = getApplicationContext().getSharedPreferences("EC510", 0);
        String id = settings.getString("board_ID", "1");
        BoardID = "MI_" + id;
        Log.d("mylog", "BOARD_ID=" + BoardID);
        appid.setText("主管查詢" + id);
        ver.setText(version);

        if(!isNetworkConnected()){  //close when not connected
            AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);
            dialog.setTitle("警告");
            dialog.setMessage("無網路連線,\n程式即將關閉");
            dialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface dialoginterface, int i) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    });
            dialog.show();
            Log.e("Mylog", "no network");
            //android.os.Process.killProcess(android.os.Process.myPid());
            //System.exit(1);
        }
        else {
            pd = ProgressDialog.show(Login.this, "連線中", "Please wait...");
        /* 開啟一個新線程，在新線程裡執行耗時的方法 */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InitServer();// 耗時的方法
                    handler.sendEmptyMessage(0);// 執行耗時的方法之後發送消給handler
                }

            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {  // 需要背景作的事
                    try {
                        for (int i = 0; i < 10; i++) {
                            Thread.sleep(1000);
                        }
                        if(connected == 1)
                            return;
                        else {
                            Log.e("Mylog", "1000ms timeout");
                            ServerDownHandler.sendEmptyMessage(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(connected == 1)
                task = new LoginTask().execute(username.getText().toString(), password.getText().toString());
            else
                message.setText("Error:" + connected + " " + str2);
        }
    };

    private boolean isNetworkConnected(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void InitServer() {
        SocketHandler.closeSocket();
        SocketHandler.initSocket(SERVERIP, SERVERPORT);
        String init = "CONNECT\t" + BoardID + "<END>";
        SocketHandler.writeToSocket(init);

        //receive result
        str2 = SocketHandler.getOutput();
        //Log.d("Mylog", str2);
        if (str2!=null && str2.equals("CONNECT_OK<END>"))
            connected = 1;
        else if (str2!=null && str2.equals("CONNECT_WRONG<END>"))
            connected = 2;
        else if (str2!=null && str2.equals("CONNECT_EXIST<END>"))
            connected = 3;
        else if (str2!=null && str2.equals("CONNECT_REPEAT<END>"))
            connected = 4;
        else
            connected = 0;
        Log.d("Mylog", "connected=" + connected);
    }

    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息後就會執行此方法
            pd.dismiss();// 關閉ProgressDialog
        }
    };

    private Handler ServerDownHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息後就會執行此方法
            pd.dismiss();// 關閉ProgressDialog
            AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);
            dialog.setTitle("警告");
            dialog.setMessage("伺服器無回應,\n程式即將關閉");
            dialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    });
            dialog.show();
        }
    };

    private class LoginTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            Log.d("Mylog", "Waitting to connect...");
            //message.setText("Waitting to connect......");
            publishProgress("Waitting to connect...");
            //socket = new Socket(SERVERIP, SERVERPORT);
            //InputStream in = socket.getInputStream();
            //OutputStream out = socket.getOutputStream();
            //Log.d("Mylog", "Connected!!");
            //publishProgress("Connected!!");

            String str_u = username.getText().toString();
            //String str_u = "1310568";
            String str_p = MD5.getMD5EncryptedString(password.getText().toString());
            String cmd = "LOGIN\tMASTER\t" + str_u + "\t" + str_p + "<END>";
            SocketHandler.writeToSocket(cmd);
            //SocketHandler.getOutput();
            str1 = SocketHandler.getOutput();
            for(int i=0; i<5; i++) {
                if (str1.contains("LOGIN_REPLY")) {
                    //if (true) {
                    Intent intent = new Intent(Login.this, MainMenu.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Bundle bundle = new Bundle();
                    bundle.putString("User", str_u);
                    bundle.putString("Password", str_p);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    return "Login Success.";
                } else if(str1.contains("LOGIN_NULL")) {
                    Log.e("Mylog", "Login error:" + str1);
                    publishProgress("Wrong username or password.\nPlease try again.");
                    return "Login Wrong.";
                }
                str1 = SocketHandler.getOutput();
            }
            return "Please try arain.";
        }
        @Override
        protected void onProgressUpdate(String... values) {
            message.setText(values[0]);
        }
        @Override
        protected void onPostExecute(String s){
            if(s!= null && s.length()>0)
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("Mylog", "back is pressed");
            SocketHandler.closeSocket();
            finish();
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            rebootCount++;
            if(rebootCount > 5) {
                if (task != null)
                    task.cancel(true);
                //SocketHandler.closeSocket();
                if (pd != null)
                    pd.dismiss();
                Log.d("mylog", "count=5");
                Thread[] threads = new Thread[Thread.activeCount()];  //close all running threads
                Thread.enumerate(threads);
                for (Thread t : threads) {
                    t.interrupt();
                }
                Log.d("mylog", "KEYCODE_VOLUME_UP key long press!");
                Intent intent = new Intent(Login.this, ChangeID.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*@Override
    public void onStop() {
        SocketHandler.closeSocket();
        finish();
        super.onStop();
    }*/
}
