package com.example.tenny.uitest;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
public class MainMenu extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_menu);

        FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);

        tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        //1
        tabHost.addTab(tabHost.newTabSpec("Fragment1")
                        .setIndicator("Fragment1"),
                Fragment1.class,
                null);
        //2
        tabHost.addTab(tabHost.newTabSpec("Fragment2")
                        .setIndicator("Fragment2"),
                Fragment2.class,
                null);
        //3
        tabHost.addTab(tabHost.newTabSpec("Fragment3")
                        .setIndicator("Fragment3"),
                Fragment3.class,
                null);
        //4
        //tabHost.addTab(tabHost.newTabSpec("Twitter")
        //                .setIndicator("Twitter"),
        //        TwitterFragment.class,
        //        null);
    }

    /**************************
     *
     *
     *
     **************************/
    public String getAppleData(){
        return "Apple 123";
    }
    public String getGoogleData(){
        return "Google 456";
    }
    public String getFacebookData(){
        return "Facebook 789";
    }
    public String getTwitterData(){
        return "Twitter abc";
    }
}