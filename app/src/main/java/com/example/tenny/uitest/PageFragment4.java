package com.example.tenny.uitest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

// In this case, the fragment displays simple text based on the page
public class PageFragment4 extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private String menuArray[];
    private ArrayAdapter<String> menuAdapter;
    private ListView listView4;

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
    }

    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page4, container, false);
        listView4 = (ListView) view.findViewById(R.id.listView4);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        menuArray = new String[] {"設備狀態", "品管", "現在生產箱數", "歷史箱數", "員工狀態", "員工差勤"};
        menuAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menuArray);
        listView4.setAdapter(menuAdapter);
        listView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                ListView listView = (ListView) adapter;
                String item = listView.getItemAtPosition(position).toString();
                Intent intent=null;
                switch (position) {
                    case 0:
                        intent = new Intent(getActivity(), MachineStatus.class);
                        break;
                    case 1:
                        intent = new Intent(getActivity(), Values.class);
                        break;
                    case 2:
                        intent = new Intent(getActivity(), BoxNow.class);
                        break;
                    case 3:
                        intent = new Intent(getActivity(), BoxHistory.class);
                        break;
                    case 4:
                        intent = new Intent(getActivity(), Workers.class);
                        break;
                    case 5:
                        intent = new Intent(getActivity(), Off_Workers.class);
                        break;
                }
                intent.putExtra("ActionName", item);
                startActivity(intent);
            }
        });
    }
}