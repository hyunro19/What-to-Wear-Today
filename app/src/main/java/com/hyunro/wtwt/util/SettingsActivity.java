package com.hyunro.wtwt.util;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hyunro.wtwt.R;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
