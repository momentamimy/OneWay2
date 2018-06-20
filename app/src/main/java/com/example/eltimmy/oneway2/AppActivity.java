package com.example.eltimmy.oneway2;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class AppActivity extends AppCompatActivity {

    BottomNavigationView navigationView;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        viewPager=findViewById(R.id.navigation_frame);
        CustomViewAdapter customViewAdapter=new CustomViewAdapter(getSupportFragmentManager());
        viewPager.setAdapter(customViewAdapter);
        navigationView=findViewById(R.id.bottom_navigation_view);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position)
                {
                    case 0:
                        navigationView.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        navigationView.setSelectedItemId(R.id.nav_setting);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_setting:
                        viewPager.setCurrentItem(1);
                        break;
                }
                return true;
            }
        });

    }


    }

