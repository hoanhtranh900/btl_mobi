package com.sangnk.btl_mobi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sangnk.btl_mobi.utils.SharedPrefManager;


public class OptionActivity extends AppCompatActivity {

    //activio drop down menu ở màn profile
    private TextView setting;
    private TextView logout;
    SharedPrefManager sharedPrefManager;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        logout = findViewById(R.id.logout);
        // this setting hasn't been setup yet
        setting = findViewById(R.id.settings);
        sharedPrefManager = new SharedPrefManager(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        //set toolbar to act as action bar so that on this back and other icons can perform action
        setSupportActionBar(toolbar);

        //setting title to Options
        getSupportActionBar().setTitle("Options");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        //called when user clicked start of the tollbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //logout the firebase account and return user to startActivity
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
                //delete all the data from shared preference
                sharedPrefManager.clear();


                startActivity(new Intent(OptionActivity.this,StartActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });
    }
}