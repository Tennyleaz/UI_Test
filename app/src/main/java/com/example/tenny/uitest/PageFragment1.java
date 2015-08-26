package com.example.tenny.uitest;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

// In this case, the fragment displays simple text based on the page
public class PageFragment1 extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    View rootView;
    ExpandableListView lv;
    private String[] groups;
    private String[][] children;

    public static PageFragment1 newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment1 fragment = new PageFragment1();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        groups = new String[] {"本日進出貨情況", "本日庫存情形", "查詢進出貨歷史紀錄", "查詢庫存歷史紀錄"};
        children = new String [][] {
                {"3號倉庫", "5號倉庫", "6號倉庫", "線邊倉"},
                {"3號倉庫", "5號倉庫", "6號倉庫", "線邊倉"},
                {"3號倉庫", "5號倉庫", "6號倉庫", "線邊倉"},
                {"3號倉庫", "5號倉庫", "6號倉庫"}
        };
    }

    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_page, container, false);
        //TextView tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        //tvTitle.setText("Fragment #" + mPage);
        //tvTitle.setTextColor(Color.rgb(0, 255, 255));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lv = (ExpandableListView) view.findViewById(R.id.expandableListView1);
        lv.setAdapter(new ExpandableListAdapter(groups, children));
        //lv.setGroupIndicator(null);
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private final LayoutInflater inf;
        private String[] groups;
        private String[][] children;
        public ExpandableListAdapter(String[] groups, String[][] children) {
            this.groups = groups;
            this.children = children;
            inf = LayoutInflater.from(getActivity());
        }

        @Override
        public int getGroupCount() {
            return groups.length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return children[groupPosition].length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return children[groupPosition][childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            String gt = null;
            if (convertView == null) {
                convertView = inf.inflate(R.layout.list_item, parent, false);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.lblListItem);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(getChild(groupPosition, childPosition).toString());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Mylog", holder.text.getText().toString() + " clicked");
                    if(getGroup(groupPosition).toString().equals("本日進出貨情況")) {
                        Intent intent = new Intent(getActivity(), QueryActivity.class);
                        intent.putExtra("HouseName", holder.text.getText().toString());
                        intent.putExtra("GroupClass", getGroup(groupPosition).toString());
                        startActivity(intent);
                    }
                    else if(getGroup(groupPosition).toString().equals("本日庫存情形")) {
                        Intent intent = new Intent(getActivity(), QueryActivity.class);
                        intent.putExtra("HouseName", holder.text.getText().toString());
                        intent.putExtra("GroupClass", getGroup(groupPosition).toString());
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(getActivity(), HistoryActivity.class);  //庫存情形, 查詢歷史紀錄
                        intent.putExtra("HouseName", holder.text.getText().toString());
                        intent.putExtra("GroupClass", getGroup(groupPosition).toString());
                        startActivity(intent);
                    }
                }
            });
            return convertView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inf.inflate(R.layout.list_group, parent, false);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.lblListHeader);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(getGroup(groupPosition).toString());
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private class ViewHolder {
            TextView text;
        }
    }

}