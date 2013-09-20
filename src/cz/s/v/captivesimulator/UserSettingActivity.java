/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.s.v.captivesimulator;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

/**
 *
 * @author vitex
 */
public class UserSettingActivity extends PreferenceActivity {

    private ListPreference preference;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        preference = (ListPreference) findPreference("prefTestType");

        String[] testTypes = getResources().getStringArray(R.array.testTargetType);
        String[] testTypeValues = getResources().getStringArray(R.array.testTargetValues);
        for (int i = 0; i < testTypes.length; i++) {
            if (preference.getValue().equals(testTypeValues[i])) {
                preference.setSummary(testTypes[i]);
            }
        }

        preference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ListPreference listPreference = (ListPreference) preference;
                int id = 0;
                for (int i = 0; i < listPreference.getEntryValues().length; i++) {
                    if (listPreference.getEntryValues()[i].equals(newValue.toString())) {
                        id = i;
                        break;
                    }
                }
                preference.setSummary(listPreference.getEntries()[id]);
                return true;
            }
        });

    }

}
