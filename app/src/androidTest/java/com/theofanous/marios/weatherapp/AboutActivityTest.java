package com.theofanous.marios.weatherapp;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * Created by Marios on 06/01/2017.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AboutActivityTest {

    @Rule
    public ActivityTestRule<AboutActivity> mActivityRule = new ActivityTestRule<AboutActivity>(AboutActivity.class);




}