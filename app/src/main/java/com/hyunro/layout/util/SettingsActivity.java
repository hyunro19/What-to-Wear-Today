package com.hyunro.layout.util;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;

import com.hyunro.layout.R;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 이거 해주면 Preference List가 2번 뜸(중복)
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.settings_container, new SettingsFragment())
//                .commit();

    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
