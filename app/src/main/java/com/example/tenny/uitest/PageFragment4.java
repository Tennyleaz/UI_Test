package com.example.tenny.uitest;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.net.Socket;

// In this case, the fragment displays simple text based on the page
public class PageFragment4 extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private TableLayout TL, TL2;
    //private String result;
    private int mPage;
    private AsyncTask task = null;

    public static PageFragment4 newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment4 fragment = new PageFragment4();
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
        View view = inflater.inflate(R.layout.fragment_page4, container, false);
        TL = (TableLayout) view.findViewById(R.id.table4);
        TL2 = (TableLayout) view.findViewById(R.id.table4_2);
        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText("設備狀態");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        task = new UpdateTask().execute();
        Log.d("Mylog", "Fragment 4 start");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d("Mylog", "Fragment 4 paused");
        if(task!=null)
            task.cancel(true);
    }

    private class UpdateTask extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... v) {

            boolean exit = false;
            while(!exit){
                String s = UpdateStatus();
                publishProgress(s);
                try{
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    Log.e("Mylog", "Thread in fragment4:" + e.toString());
                }
                if (isCancelled())
                    break;
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            TL.removeAllViews();
            TL2.removeAllViews();
            String[] items = values[0].split("\t");
            for(int i=0; i<items.length; i++) {
                if(i > (items.length-4))
                    break;
                TableRow row = new TableRow(getActivity());
                row.setBackgroundColor(Color.parseColor("#eeeeee"));
                //set margin
                TableLayout.LayoutParams tableRowParams =
                        new TableLayout.LayoutParams
                                (TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
                tableRowParams.setMargins(1, 1, 1, 1);
                row.setLayoutParams(tableRowParams);
                TL.addView(row);

                //new switch button
                TableRow.LayoutParams tlr = new TableRow.LayoutParams(45, 45);
                tlr.setMargins(8, 8, 8, 8);

                ImageView iv1 = new ImageView(getActivity());
                if(items[i+1].equals("1"))
                    iv1.setImageResource(R.drawable.green_circle);
                else
                    iv1.setImageResource(R.drawable.red_cross);
                iv1.setLayoutParams(tlr);
                row.addView(iv1);
                //
                TextView tv = new TextView(getActivity());
                tv.setText(items[i]);
                i++;
                row.addView(tv);
                //repeat
                i++;
                //new switch button
                TableRow row2 = new TableRow(getActivity());
                row2.setBackgroundColor(Color.parseColor("#eeeeee"));
                //set margin
                row2.setLayoutParams(tableRowParams);
                TL2.addView(row2);
                ImageView iv2 = new ImageView(getActivity());
                if(items[i+1].equals("1"))
                    iv2.setImageResource(R.drawable.green_circle);
                else
                    iv2.setImageResource(R.drawable.red_cross);
                iv2.setLayoutParams(tlr);
                row2.addView(iv2);
                //
                TextView tv2 = new TextView(getActivity());
                tv2.setText(items[i]);
                i++;
                row2.addView(tv2);
            }
        }
    }

    private String UpdateStatus() {
        String result;
        String cmd = "QUERY ONLINE_STATE<END>";
        SocketHandler.writeToSocket(cmd);
        Log.d("Mylog", "command:" + cmd);
        result = SocketHandler.getOutput();
        Log.d("Mylog", "query result:" + cmd);
        result = result.replaceAll("QUERY_REPLY\t", "");
        result = result.replaceAll("<N>", "\n");
        result = result.replaceAll("<END>", "");
        return result;
    }
}