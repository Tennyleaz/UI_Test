package com.example.tenny.uitest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

// 捲包
// In this case, the fragment displays simple text based on the page
public class PageFragment2 extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    public static AsyncTask task = null;
    private TableLayout TL;
    //private TableRow TR1, TR2, TR3, TR4, TR5, TR6, TR7, TR8, FF1, FF2, FF3, FF4, FF5;
    private TableRow TR[];
    private Button more_btn[];
    private String products[], swaps[];
    private TextView updateTime, productNames[], swapsLights[], swapsCommands[];

    public static PageFragment2 newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment2 fragment = new PageFragment2();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page2, container, false);
        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        updateTime = (TextView) view.findViewById(R.id.updateTime);
        TL = (TableLayout) view.findViewById(R.id.TL1);
        tvTitle.setText("捲包");
        products = new String[13];
        swaps = new String[13];
        TR = new TableRow[13];
        more_btn = new Button[13];
        productNames = new TextView[13];
        swapsLights = new TextView[21];
        swapsCommands = new TextView[21];
        int j = TL.getChildCount();
        Log.d("Mylog", "TL.getChildCount()=" + j);
        //int swap_count = 0;
        for(int i = 0; i < j; i++) {
            View v = TL.getChildAt(i);
            if (v instanceof TableRow) {
                if(v.getId() == R.id.title_row)
                    continue;
                TR[i-1] = (TableRow) v;
                //Log.d("Mylog", "i=" + i + " name=" + getResources().getResourceEntryName(TR[i-1].getId()));
                productNames [i-1] = (TextView) TR[i-1].getChildAt(1);
                more_btn[i-1] = (Button) TR[i-1].getChildAt(2);
            }
        }
        swapsLights[0] = (TextView) view.findViewById(R.id.light1_1);
        swapsLights[1] = (TextView) view.findViewById(R.id.light1_2);
        swapsLights[2] = (TextView) view.findViewById(R.id.light2_1);
        swapsLights[3] = (TextView) view.findViewById(R.id.light2_2);
        swapsLights[4] = (TextView) view.findViewById(R.id.light3_1);
        swapsLights[5] = (TextView) view.findViewById(R.id.light3_2);
        swapsLights[6] = (TextView) view.findViewById(R.id.light4_1);
        swapsLights[7] = (TextView) view.findViewById(R.id.light4_2);
        swapsLights[8] = (TextView) view.findViewById(R.id.light5_1);
        swapsLights[9] = (TextView) view.findViewById(R.id.light5_2);
        swapsLights[10] = (TextView) view.findViewById(R.id.light6_1);
        swapsLights[11] = (TextView) view.findViewById(R.id.light6_2);
        swapsLights[12] = (TextView) view.findViewById(R.id.light7_1);
        swapsLights[13] = (TextView) view.findViewById(R.id.light7_2);
        swapsLights[14] = (TextView) view.findViewById(R.id.light8_1);
        swapsLights[15] = (TextView) view.findViewById(R.id.light8_2);
        swapsLights[16] = (TextView) view.findViewById(R.id.light9);
        swapsLights[17] = (TextView) view.findViewById(R.id.light10);
        swapsLights[18] = (TextView) view.findViewById(R.id.light11);
        swapsLights[19] = (TextView) view.findViewById(R.id.light12);
        swapsLights[20] = (TextView) view.findViewById(R.id.light13);
        swapsCommands[0] = (TextView) view.findViewById(R.id.cmd1_1);
        swapsCommands[1] = (TextView) view.findViewById(R.id.cmd1_2);
        swapsCommands[2] = (TextView) view.findViewById(R.id.cmd2_1);
        swapsCommands[3] = (TextView) view.findViewById(R.id.cmd2_2);
        swapsCommands[4] = (TextView) view.findViewById(R.id.cmd3_1);
        swapsCommands[5] = (TextView) view.findViewById(R.id.cmd3_2);
        swapsCommands[6] = (TextView) view.findViewById(R.id.cmd4_1);
        swapsCommands[7] = (TextView) view.findViewById(R.id.cmd4_2);
        swapsCommands[8] = (TextView) view.findViewById(R.id.cmd5_1);
        swapsCommands[9] = (TextView) view.findViewById(R.id.cmd5_2);
        swapsCommands[10] = (TextView) view.findViewById(R.id.cmd6_1);
        swapsCommands[11] = (TextView) view.findViewById(R.id.cmd6_2);
        swapsCommands[12] = (TextView) view.findViewById(R.id.cmd7_1);
        swapsCommands[13] = (TextView) view.findViewById(R.id.cmd7_2);
        swapsCommands[14] = (TextView) view.findViewById(R.id.cmd8_1);
        swapsCommands[15] = (TextView) view.findViewById(R.id.cmd8_2);
        swapsCommands[16] = (TextView) view.findViewById(R.id.cmd9);
        swapsCommands[17] = (TextView) view.findViewById(R.id.cmd10);
        swapsCommands[18] = (TextView) view.findViewById(R.id.cmd11);
        swapsCommands[19] = (TextView) view.findViewById(R.id.cmd12);
        swapsCommands[20] = (TextView) view.findViewById(R.id.cmd13);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        task = new UpdateTask();
        AsyncTaskTools.execute(task);
        Log.d("Mylog", "Fragment 2 start");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Mylog", "Fragment 2 paused");
        if (task != null)
            task.cancel(true);
    }

    private class UpdateTask extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... v) {
            Log.d("Mylog", "UpdateTask in fragment 2 doInBackground");
            while(!isCancelled()){
                if(MainMenu.currentPage!=1)
                    continue;
                String cmd = "QUERY\tPRODUCT<END>";
                SocketHandler.writeToSocket(cmd);
                Log.d("Mylog", "command is:" + cmd);
                String s = UpdateStatus("PRODUCT");
                cmd = "QUERY\tSWAP<END>";
                SocketHandler.writeToSocket(cmd);
                Log.d("Mylog", "command is:" + cmd);
                String s2 = UpdateStatus("SWAP");
                publishProgress(s, s2);
                try{
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    Log.e("Mylog", "Thread in fragment2:" + e.toString());
                }
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            //if(MainMenu.currentPage != 1)
            //    return;
            if(values.length < 2 || values[0].length() == 0)
                return;
            Calendar c = Calendar.getInstance();
            updateTime.setText("Last Update: " + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
            //TL1.removeAllViews();
            products = values[0].split("\n");
            swaps = values[1].split("\n");

            for(int i=0; i<13; i++) {
                String[] item = products[i].split("\t");

                if(item.length >= 3) {
                    productNames[i].setText(item[2]);
                    more_btn[i].setOnClickListener(new Page2_OnClickListener(getActivity(), products[i]));
                    more_btn[i].setEnabled(true);
                }
                else {
                    productNames[i].setText("No Data");
                    more_btn[i].setEnabled(false);
                }
            }
            for(int i=0; i<21; i++) {
                Log.d("Mylog", "swaps[" + i + "]=" + swaps[i]);
                String[] item2 = swaps[i].split("\t");
                if(item2.length >= 3) {
                    if(item2[1].equals("0")) {
                        swapsLights[i].setBackgroundColor(getResources().getColor(R.color.swaplight_grey));
                        swapsCommands[i].setText("無換牌指令");
                    } else if(item2[1].equals("1")){
                        swapsLights[i].setBackgroundColor(getResources().getColor(R.color.swaplight_red));
                        swapsCommands[i].setText(item2[2]);
                    } else if(item2[1].equals("2")){
                        swapsLights[i].setBackgroundColor(getResources().getColor(R.color.swaplight_yellow));
                        swapsCommands[i].setText(item2[2]);
                    } else if(item2[1].equals("3")){
                        swapsLights[i].setBackgroundColor(getResources().getColor(R.color.swaplight_green));
                        swapsCommands[i].setText(item2[2]);
                    }
                }
            }
        }
    }

    private String UpdateStatus(String input) {
        String result;
        result = SocketHandler.getOutput();
        //Log.d("Mylog", "query result:" + cmd);
        if(result==null || result.length()==0)
            return UpdateStatus(input);

        if(!result.contains("QUERY_REPLY") )
            return UpdateStatus(input);

        String[] lines = result.split("<END>");
        for(String s: lines) {
            if(s != null && s.contains(s)) {
                s = s.replaceAll("QUERY_REPLY\t", "");
                s = s.replaceAll(input + "\t", "Product");
                s = s.replaceAll("FF\t", "F");
                s = s.replaceAll("CM\t", "C");
                s = s.replaceAll("PM\t", "P");
                s = s.replaceAll("<N>", "\n");
                s = s.replaceAll("<END>", "");
                Log.d("Mylog", "s=\n" + s);
                return s;
            }
        }
        return UpdateStatus(input);
    }
}