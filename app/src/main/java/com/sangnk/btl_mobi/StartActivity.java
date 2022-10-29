package com.sangnk.btl_mobi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.sangnk.btl_mobi.utils.SharedPrefManager;

public class StartActivity extends AppCompatActivity {
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        init();

        checkLogin();

    }

    private void checkLogin() {
        if (sharedPrefManager.getSPSudahLogin()) {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
//            move to login activit
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    // Init View Method
    private void init() {
        sharedPrefManager = new SharedPrefManager(this);

    }


    @Override
    protected void onStart() {
        super.onStart();

    }


}