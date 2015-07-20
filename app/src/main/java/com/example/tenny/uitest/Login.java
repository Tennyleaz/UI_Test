package com.example.tenny.uitest;

/**
 * Created by Tenny on 2015/7/19.
 */
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Login extends ActionBarActivity {
    private EditText username;
    private EditText password;
    private Button login_btn;
    private TextView message;
    private final String SERVERIP = "140.113.210.14";
    private final int SERVERPORT = 8888;
    private String str1="0",str2="0";
    private Thread t;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //to enable action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);

        message = (TextView) findViewById(R.id.message);
        message.setText("");
        username = (EditText) findViewById(R.id.accounts);
        password = (EditText) findViewById(R.id.password);
        login_btn = (Button) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(onclick);
        //建立 thread 的物件
        //t = new mythread();
    }

    View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (username.getText().toString().equals("admin") && password.getText().toString().equals("123")) {
                //啟動thread
                //t.start();
                runOnUiThread(thRead);
                Toast.makeText(getApplicationContext(), "Thread started...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //class mythread extends Thread{
    Runnable thRead = new Runnable(){
        public void run() {
            try{
                message.setText("Waitting to connect......");
                Log.d("Mylog", "Waitting to connect...");
                //String server=username.getText().toString();
                //int servPort=1025;
                Socket socket=new Socket(SERVERIP, SERVERPORT);
                InputStream in=socket.getInputStream();
                OutputStream out=socket.getOutputStream();
                message.setText("Connected!!");
                Log.d("Mylog", "Connected!!");
                Toast.makeText(getApplicationContext(), "Connected!!", Toast.LENGTH_SHORT).show();

                String str_u = username.getText().toString();
                byte[] sendstr1 = new byte[21];
                System.arraycopy(str_u.getBytes(), 0, sendstr1, 0, str_u.length());
                out.write(sendstr1);
                String str_p = password.getText().toString();
                byte[] sendstr2 = new byte[21];
                System.arraycopy(str_p.getBytes(), 0, sendstr2, 0, str_p.length());
                out.write(sendstr2);

                //receive result
                byte[] readbyte = new byte[18];
                in.read(readbyte);
                str2 = new String(readbyte);
                if(str2.equals("ok")) {
                    Intent intent = new Intent(Login.this, MainMenu.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else{
                    message.setText("Wrong username or password.\nPlease try again.");
                }
            }catch(Exception e)
            {
                System.out.println("Error: "+e.getMessage());
            }
        }
    };
}
