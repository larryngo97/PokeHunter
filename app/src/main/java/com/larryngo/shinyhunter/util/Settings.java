package com.larryngo.shinyhunter.util;

import android.content.Intent;
import android.os.Bundle;

import com.larryngo.shinyhunter.R;
import com.larryngo.shinyhunter.app.App;
import com.larryngo.shinyhunter.models.Counter;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;


public class Settings extends AppCompatActivity {

    public static String SETTING_NIGHTMODE_KEY = "dark_mode";
    public static String SETTING_VIBRATEMODE_KEY = "vibrate_mode";
    public static String SETTING_SORT_KEY = "sort_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(isDarkModeOn()) {
            setTheme(R.style.HomeThemeDark);
        } else {
            setTheme(R.style.HomeTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down, R.anim.slide_in_down, R.anim.slide_out_up)
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            CheckBoxPreference nightModePreference = findPreference(getResources().getString(R.string.settings_preference_darkmode_key));
            if(nightModePreference != null) {
                if(isDarkModeOn()) {
                    nightModePreference.setChecked(true);
                }
                nightModePreference.setOnPreferenceClickListener(preference -> {
                    if(nightModePreference.isChecked()) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //dark mode
                        saveDarkMode(true);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //light mode
                        saveDarkMode(false);
                    }
                    if(getActivity() != null) {
                        System.out.println("Restarting activity");
                        getActivity().recreate();
                    }
                    return true;
                });
            }

            CheckBoxPreference vibrateModePreference = findPreference(getResources().getString(R.string.settings_preference_vibrate_key));
            if(vibrateModePreference != null) {
                vibrateModePreference.setOnPreferenceClickListener(preference -> {
                    saveVibrateMode(vibrateModePreference.isChecked()); //turns vibrate on if checked, off if not
                    return true;
                });
            }

            ListPreference sortModePreference = findPreference(getResources().getString(R.string.settings_preference_sortlist_key));
            if(sortModePreference != null) {
                if (sortModePreference.getEntry() == null) {
                    sortModePreference.setSummary(sortModePreference.getEntries()[0]); //default summary (for first time use)
                } else {
                    sortModePreference.setSummary(sortModePreference.getEntry()); //current summary
                }

                sortModePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    saveSortKey(newValue.toString()); //stores into preferences the key
                    int index = sortModePreference.findIndexOfValue(newValue.toString()); //index of the selected entry
                    sortModePreference.setSummary(sortModePreference.getEntries()[index]); //have description the selected selected entry
                    return true;
                });
            }
        }
    }

    public static void sortCounter(List<Counter> countersList) {
        String sortKey = getSortKey();
        System.out.println("Sorting by " + sortKey);
        switch(sortKey) {
            case "date_old":
                countersList.sort(Counter.COMPARE_BY_LISTID_ASC); //oldest
                break;
            case "count_highest":
                countersList.sort(Counter.COMPARE_BY_COUNT_DESC); //most count
                break;
            case "count_lowest":
                countersList.sort(Counter.COMPARE_BY_COUNT_ASC); //least count
                break;
            case "name_atoz":
                countersList.sort(Counter.COMPARE_BY_NICKNAME_ASC); //name A-Z
                break;
            case "name_ztoa":
                countersList.sort(Counter.COMPARE_BY_NICKNAME_DESC); //name Z-A
                break;
            case "dex_new":
                countersList.sort(Counter.COMPARE_BY_POKEMONID_ASC); //pokemon species lowest to highest
                break;
            case "dex_old":
                countersList.sort(Counter.COMPARE_BY_POKEMONID_DESC); //pokemon species highest to lowest
                break;
            case "gen_new":
                countersList.sort(Counter.COMPARE_BY_GAME_ASC); //game newest to oldest
                break;
            case "gen_old":
                countersList.sort(Counter.COMPARE_BY_GAME_DESC); //game oldest to newest
                break;
            default:
                countersList.sort(Counter.COMPARE_BY_LISTID_DESC);
                break;
        }
    }

    public static String getSortKey() {
        return App.getTinyDB().getString(SETTING_SORT_KEY);
    }

    public static void saveSortKey(String value) {
        App.getTinyDB().putString(SETTING_SORT_KEY, value);
    }

    public static boolean isDarkModeOn() {
        return App.getTinyDB().getBoolean(SETTING_NIGHTMODE_KEY, false);
    }

    public static void saveDarkMode(boolean value) {
        App.getTinyDB().putBoolean(SETTING_NIGHTMODE_KEY, value);
    }

    public static boolean isVibrateModeOn () {
        return App.getTinyDB().getBoolean(SETTING_VIBRATEMODE_KEY, true);
    }

    public static void saveVibrateMode(boolean value) {
        App.getTinyDB().putBoolean(SETTING_VIBRATEMODE_KEY, value);
    }
}