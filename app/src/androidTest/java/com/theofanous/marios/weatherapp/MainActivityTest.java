package com.theofanous.marios.weatherapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.matcher.IntentMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.intent.Intents.*;
import static android.support.test.espresso.Espresso.*;
import static org.hamcrest.CoreMatchers.anything;

/**
 * Created by Marios on 06/01/2017.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> activityTestRule
            = new IntentsTestRule<MainActivity>(MainActivity.class);

    @Test
    public void settingsMenuClicked(){
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText("Settings"))
                .perform(click());
        intended(hasComponent(SettingsActivity.class.getName()));
    }

    @Test
    public void aboutMenuClicked(){
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText("About"))
                .perform(click());
        intended(hasComponent(AboutActivity.class.getName()));
    }

    @Test
    public void listItemSelected(){
        Context context = InstrumentationRegistry.getTargetContext();
        Fragment list = activityTestRule.getActivity().getSupportFragmentManager().findFragmentById(R.id.detail_fragment_land);
        //We are not in large-land
        if(list==null){
            onData(anything()).inAdapterView(withId(R.id.weather_listview)).atPosition(0).perform(click());
            intended(hasComponent(DetailsActivity.class.getName()));
        }
    }
}