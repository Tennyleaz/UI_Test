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
    private TextView message;
    static final String SERVERIP = "140.113.210.29";
    static final int SERVERPORT = 8000;
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
            /*AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setMessage("Error!")
                    .setPositiveButton("Y", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                        }
                    })
                    .setNegativeButton("N", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            builder.create();
            builder.show();*/
            AlertDialog.Builder dialog = new AlertDialog.Builder(Login.this);
            dialog.setTitle("警告");
            dialog.setMessage("");
            dialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface dialoginterface, int i) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    });
            dialog.show();
            Log.d("Mylog", "no network");
            //android.os.Process.killProcess(android.os.Process.myPid());
            //System.exit(1);
        }

        pd = ProgressDialog.show(Login.this, "標題", "加載中，請稍後……");
        /* 開啟一個新線程，在新線程裡執行耗時的方法 */
        new Thread(new Runnable() {
            @Override
            public void run() {
                InitServer();// 耗時的方法
                handler.sendEmptyMessage(0);// 執行耗時的方法之後發送消給handler
            }

        }).start();
    }

    View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new LoginTask().execute(username.getText().toString(), password.getText().toString());
            if (username.getText().toString().equals("admin") && password.getText().toString().equals("123")) {
                //啟動thread
                //t.start();
                //runOnUiThread(thRead);
                new LoginTask().execute(username.getText().toString(), password.getText().toString());
                Toast.makeText(getApplicationContext(), "Thread started...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private boolean isNetworkConnected(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void InitServer() {
        try {
            socket = new Socket(SERVERIP, SERVERPORT);
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            String init = "CONNECT MI_1<END>";
            out.write(init.getBytes());
            //receive result
            byte[] readbyte = new byte[24];
            int i = in.read(readbyte);
            str2 = new String(readbyte, 0, i);
            Log.d("Mylog", str2);
            if(str2.equals("CONNECT_OK<END>"))
                connected = 1;
            else if(str2.equals("CONNECT_WRONG<END>"))
                connected = 2;
            else if(str2.equals("CONNECT_EXIST<END>"))
                connected = 3;
            else if(str2.equals("CONNECT_REPEAT<END>"))
                connected = 4;
            else
                connected = 0;
            Log.d("Mylog", "connected=" + connected);
        }
        catch (UnknownHostException e)
        {
            System.out.println("Error0: "+e.getMessage());
        }
        catch(IOException e)
        {
            System.out.println("Error1: " + e.getMessage());
        }
    }

    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息後就會執行此方法
            pd.dismiss();// 關閉ProgressDialog
        }
    };

    private class LoginTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings){
            Log.d("Mylog", "Waitting to connect...");
            //message.setText("Waitting to connect......");
            publishProgress("Waitting to connect...");
            try {
                //socket = new Socket(SERVERIP, SERVERPORT);
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                //Log.d("Mylog", "Connected!!");
                //publishProgress("Connected!!");

                String str_u = username.getText().toString();
                String str_p = password.getText().toString();

                byte[] sendstr1 = new byte[21];
                System.arraycopy(str_u.getBytes(), 0, sendstr1, 0, str_u.length());
                //out.write(sendstr1);
                byte[] sendstr2 = new byte[21];
                System.arraycopy(str_p.getBytes(), 0, sendstr2, 0, str_p.length());
                //out.write(sendstr2);
                //connect test
                //byte[] sendstr3 = new byte[32];
                //String teststring = "CONNECT MI_1<END>";
                //System.arraycopy(teststring.getBytes(), 0, sendstr3, 0, teststring.length());
                //out.write(teststring.getBytes());

                //receive result
                //byte[] readbyte = new byte[24];
                //int i = in.read(readbyte);
                //str2 = new String(readbyte, 0, i);
                //Log.d("Mylog", str2);

                if(true) {
                    Intent intent = new Intent(Login.this, MainMenu.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    Bundle bundle = new Bundle();
                    bundle.putString("User", str_u);
                    bundle.putString("Password", str_p);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else{
                    publishProgress("Wrong username or password.\nPlease try again.");
                }
            }
            catch (UnknownHostException e)
            {
                System.out.println("Error3: "+e.getMessage());
            }
            catch(IOException e)
            {
                System.out.println("Error4: "+e.getMessage());
            }
            String s="Login Success";
            return s;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            message.setText(values[0]);
        }
        @Override
        protected void onPostExecute(String s){
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }
    }
}
