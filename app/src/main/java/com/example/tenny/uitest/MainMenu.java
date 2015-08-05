package com.example.tenny.uitest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;

import com.astuetz.PagerSlidingTabStrip;

public class MainMenu extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);
        tabsStrip.setTextSize(24);
        tabsStrip.setIndicatorColor(Color.parseColor("#03a9f4"));


    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        //final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[] { "物料", "生產", "設備狀態" };
        //private String tabTitles[] = new String[] { "物料", "設備狀態" };

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("Mylog", "getItem " + position);
            switch (position) {
                case 0:
                    Log.d("Mylog", "position 1 ");
                    return PageFragment1.newInstance(position + 1);
                case 1:
                    Log.d("Mylog", "position 2 ");
                    return PageFragment2.newInstance(position + 1);
                    //return PageFragment4.newInstance(position + 1);
                case 2:
                    Log.d("Mylog", "position 3 ");
                    return PageFragment4.newInstance(position + 1);
                //case 3:
                //    Log.d("Mylog", "position 4 ");
                //    return PageFragment4.newInstance(position + 1);
                default:
                    return null;
            }
            //return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }

        /*@Override
        protected void onDestroy(){
            super.onDestroy();
            Log.d("Mylog", "on destroy");
            finish();
        }*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("Mylog", "back is pressed");
            SocketHandler.closeSocket();
            /*try{
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                Log.e("Mylog", "Thread in fragment4:" + e.toString());
            }*/
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}