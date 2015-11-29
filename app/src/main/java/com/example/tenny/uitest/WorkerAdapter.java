package com.example.tenny.uitest;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tenny on 2015/11/29.
 */
public class WorkerAdapter extends ArrayAdapter<Worker> {
    public WorkerAdapter(Context context, ArrayList<Worker> items) {
        super(context, R.layout.worker_listview_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Worker w = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.worker_listview_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(w.Name);
        if (w.WorkTime.equals("5001")) { //晚班
            viewHolder.name.setBackgroundColor(Color.parseColor("#546e7a"));
            viewHolder.name.setTextColor(Color.parseColor("#fafafa"));
        } else {
            viewHolder.name.setBackgroundColor(Color.parseColor("#eeeeee"));
            viewHolder.name.setTextColor(Color.parseColor("#424242"));
        }
        return convertView;
    }

    static class ViewHolder {
        public TextView name;
    }
}