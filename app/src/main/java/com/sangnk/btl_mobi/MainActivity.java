package com.sangnk.btl_mobi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sangnk.btl_mobi.Fragments.HomeFragment;
import com.sangnk.btl_mobi.Fragments.NotificationFragment;
import com.sangnk.btl_mobi.Fragments.ProfileFragment;
import com.sangnk.btl_mobi.Fragments.SearchFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    List<String> fragments = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        setBottomNavigationView();
        //check if user is logged in move tp home activity

        //Mặc định khi kich hoạt activity sẽ hiển thị fragment home
        setHomeAfterLogin();
    }

    /**
     * Setup Bottom Navigation View
     * <p>
     * set the fragment on th base of their id's
     */
    private void setBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
//                        check if current fragment is home fragment not to reload it
                        if (selectorFragment != null && selectorFragment instanceof HomeFragment) {
                            return true;
                        }
                        selectorFragment = new HomeFragment();
                        Log.d(TAG, "onNavigationItemSelected: Setting Home Fragment");
                        break;
                    case R.id.nav_search:
                        if (selectorFragment != null && selectorFragment instanceof SearchFragment) {
                            return true;
                        }
                        selectorFragment = new SearchFragment();
                        Log.d(TAG, "onNavigationItemSelected: setting Search Fragment");
                        break;
                    case R.id.nav_add:
                        selectorFragment = null;
                        Log.d(TAG, "onNavigationItemSelected: Starting action intent post Activity");
                        startActivity(new Intent(MainActivity.this, com.sangnk.btl_mobi.PostActivity.class));
                        break;
                    case R.id.nav_heart:
                        if (selectorFragment != null && selectorFragment instanceof NotificationFragment) {
                            return true;
                        }
                        selectorFragment = new NotificationFragment();
                        Log.d(TAG, "onNavigationItemSelected: Setting Notification Fragment");
                        break;
                    case R.id.nav_profile:
                        if (selectorFragment != null && selectorFragment instanceof ProfileFragment) {
                            return true;
                        }
                        selectorFragment = new ProfileFragment();
                        Log.d(TAG, "onNavigationItemSelected: setting ProfileFragment");
                        break;

                }
                if (selectorFragment != null) {
                    //add fragment to stack
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).addToBackStack(null).commit();
                }

                return true;
            }
        });
    }

    //after login directly start Home Activity
    private void setHomeAfterLogin() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }
}