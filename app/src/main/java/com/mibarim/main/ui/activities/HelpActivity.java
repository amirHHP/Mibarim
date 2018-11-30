package com.mibarim.main.ui.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mibarim.main.R;
import com.mibarim.main.adapters.ViewPagerAdapter;
import com.mibarim.main.ui.fragments.helpFragments.firstSlide;
import com.mibarim.main.ui.fragments.helpFragments.fourthSlide;
import com.mibarim.main.ui.fragments.helpFragments.secondSlide;
import com.mibarim.main.ui.fragments.helpFragments.thirdSlide;
import com.viewpagerindicator.CirclePageIndicator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HelpActivity extends AppCompatActivity {

    private LinearLayout layout;

    //    @Bind(R.id.tabs)
//    protected TabLayout tabLayout;
    @Bind(R.id.viewpager)
    protected ViewPager viewPager;
    @Bind(R.id.title)
    protected CirclePageIndicator title;
    @Bind(R.id.skip)
    protected TextView skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);
        ButterKnife.bind(this);
        setupViewPager(viewPager);
        title.setViewPager(viewPager);
        skip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    finsihIt();
                    return true;
                }
                return false;
            }
        });
//        tabLayout.setupWithViewPager(viewPager);
    }

    private void finsihIt() {
        finish();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new fourthSlide(), "FOUR");
        adapter.addFragment(new thirdSlide(), "THREE");
        adapter.addFragment(new secondSlide(), "TWO");
        adapter.addFragment(new firstSlide(), "One");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(3);
    }

    @Override
    public void onBackPressed() {
        return;

    }

}