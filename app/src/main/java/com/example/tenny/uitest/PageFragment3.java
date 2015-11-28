package com.example.tenny.uitest;

import android.app.Activity;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

//No use for now
// In this case, the fragment displays simple text based on the page
public class PageFragment3 extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private String menuArray[];
    private ArrayAdapter<String> menuAdapter;
    private ListView listView3;

    public static PageFragment3 newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment3 fragment = new PageFragment3();
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
        View view = inflater.inflate(R.layout.fragment_page3, container, false);
        listView3 = (ListView) view.findViewById(R.id.listView);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        menuArray = new String[] {"配料歷史", "加香情形", "加香歷史", "換牌情形", "換牌歷史"};
        menuAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menuArray);
        listView3.setAdapter(menuAdapter);
        listView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                ListView listView = (ListView) adapter;
                String item = listView.getItemAtPosition(position).toString();
                Toast.makeText(getActivity(), "clicked:" + item, Toast.LENGTH_SHORT).show();
                //if(position < 3) {
                    Intent intent = new Intent(getActivity(), RecipeActivity.class);
                    intent.putExtra("ActionName", item);
                    startActivity(intent);
                //}
            }
        });
    }
}