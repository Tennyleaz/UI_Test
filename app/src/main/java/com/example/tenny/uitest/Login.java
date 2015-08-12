package com.example.tenny.uitest;

/**
 * Created by Tenny on 2015/7/19.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
    static final String SERVERIP = "140.113.167.14";
    static final int SERVERPORT = 9000; //8000= echo server, 9000=real server
    private String str1="0",str2="0";
    private static Socket socket;
    private static ProgressDialog pd;
    private static short connected;
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
        }
    }

    View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(connected == 1)
                new LoginTask().execute(username.getText().toString(), password.getText().toString());
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
        socket = SocketHandler.initSocket(SERVERIP, SERVERPORT);
        String init = "CONNECT MI_2<END>";
        SocketHandler.writeToSocket(init);

        //receive result
        str2 = SocketHandler.getOutput();
        Log.d("Mylog", str2);
        if (str2.equals("CONNECT_OK<END>"))
            connected = 1;
        else if (str2.equals("CONNECT_WRONG<END>"))
            connected = 2;
        else if (str2.equals("CONNECT_EXIST<END>"))
            connected = 3;
        else if (str2.equals("CONNECT_REPEAT<END>"))
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
            String str_p = MD5.getMD5EncryptedString(password.getText().toString());
            String cmd = "LOGIN TABLET " + str_u + " " + str_p + "<END>";
            SocketHandler.writeToSocket(cmd);
            str1 = SocketHandler.getOutput();

            //if (str1.contains("LOGIN_REPLY")) {
            if (true) {
                Intent intent = new Intent(Login.this, MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                Bundle bundle = new Bundle();
                bundle.putString("User", str_u);
                bundle.putString("Password", str_p);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                Log.e("Mylog", "Login error:" + str1);
                publishProgress("Wrong username or password.\nPlease try again.");
                return  str1;
            }
            String s = "Login Success";
            return s;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            message.setText(values[0]);
        }
        @Override
        protected void onPostExecute(String s){
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            //message.setText(s);
        }
    }
}
