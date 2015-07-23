package com.example.tenny.uitest;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Tenny on 2015/7/23.
 */
public class SocketHandler {
    private static Socket socket = null;
    private static boolean isCreated = false;
    private static InputStream in = null;
    private static OutputStream out = null;

    public static synchronized Socket getSocket(){
        if(isCreated)
            return socket;
        else
            return null;
    }

    public static synchronized Socket initSocket(String SERVERIP, int SERVERPORT){
        try {
            socket = new Socket(SERVERIP, SERVERPORT);
            isCreated = true;
            in = socket.getInputStream();
            out = socket.getOutputStream();
        }
        catch (UnknownHostException e)
        {
            System.out.println("Error0: "+e.getMessage());
        }
        catch(IOException e)
        {
            System.out.println("Error1: " + e.getMessage());
        }
        return socket;
    }

    public static synchronized void setSocket(Socket socket){
        SocketHandler.socket = socket;
    }

    public static synchronized String getOutput(){
        if(isCreated) {
            String s = "";
            int i;
            byte[] readbyte = new byte[1024];
            try {
                while((i=in.read(readbyte)) != -1) {
                    s += new String(readbyte, 0, i);
                    readbyte = null;
                    readbyte = new byte[1024];
                    Log.d("Mylog", "i=" + i + ", s="+s);
                    if(s.contains("<END>"))
                        break;
                }
            } catch (IOException e) {
                System.out.println("Error getOutput: " + e.getMessage());
            }
            return s;
        }
        else
            return null;
    }

    public static synchronized void writeToSocket(String s){
        if(isCreated) {
            try {
                out.write(s.getBytes());
            } catch (IOException e) {
                System.out.println("Error writeToSocket: " + e.getMessage());
            }
        }
        else
            Log.e("Mylog", "socket not created, cant write!");
    }
}