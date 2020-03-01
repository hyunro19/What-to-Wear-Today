package com.hyunro.layout.util;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.hyunro.layout.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

    }

    @Override
    public void onStart() {
        super.onStart();

        SwitchPreference agreeMarketingAlarm = (SwitchPreference) findPreference("agreeMarketingAlarm");
        agreeMarketingAlarm.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean beforeChange = ((SwitchPreference) preference).isChecked();
                boolean afterChange = !beforeChange;
                ((SwitchPreference) preference).setChecked(afterChange);
                FirebaseMessaging fbm = FirebaseMessaging.getInstance();
                if(afterChange) {
                    fbm.subscribeToTopic("Marketing");
                } else {
                    fbm.unsubscribeFromTopic("Marketing");
                }

                return false;
            }
        });
//        Boolean agreeMarketingAlarm = sharedPreferences.getBoolean("agreeMarketingAlarm", false);

        View settings_backButton = getActivity().findViewById(R.id.settings_backButton);
        settings_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

    }
}
