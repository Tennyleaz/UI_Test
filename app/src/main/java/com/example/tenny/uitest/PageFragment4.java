package com.example.tenny.uitest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.net.Socket;

// In this case, the fragment displays simple text based on the page
public class PageFragment4 extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private String result;
    private int mPage;

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
        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText("設備狀態 #" + mPage);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        String temps = null;
        new UpdateTask().execute(temps);
    }

    private class UpdateTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            UpdateStatus();
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            //message.setText(values[0]);
        }
    }

    private void UpdateStatus() {
        String cmd = "QUERY ONLINE_STATE<END>";
        SocketHandler.writeToSocket(cmd);
        Log.d("Mylog", "command:" + cmd);
        result = SocketHandler.getOutput();
    }
}