package com.theofanous.marios.weatherapp;


import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        Preference unitPreference = findPreference(getString(R.string.pref_temp_key));
        unitPreference.setOnPreferenceChangeListener(this);
        onPreferenceChange(unitPreference,
                PreferenceManager
                        .getDefaultSharedPreferences(unitPreference.getContext())
                        .getString(unitPreference.getKey(), ""));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String value = newValue.toString();

        if(preference instanceof ListPreference){
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(value);
            if(index>=0){
                preference.setSummary(listPreference.getEntries()[index]);
            } else
                preference.setSummary(((ListPreference) preference).getValue());
        }
        return true;
    }
}
