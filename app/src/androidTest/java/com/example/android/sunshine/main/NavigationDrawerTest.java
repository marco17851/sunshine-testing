package com.example.android.sunshine.main;

import android.content.SharedPreferences;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.DrawerMatchers;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.android.sunshine.MainActivity;
import com.example.android.sunshine.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class NavigationDrawerTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup(){
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(DrawerActions.open());
    }

    @Test
    public void shouldCloseDrawerWhenDrawerIsOpenAndUserPressesBackButton() {
        pressBack();
        onView(ViewMatchers.withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isClosed()));
    }

    @Test
    public void shouldShowCorrectHeaderInNavigationDrawer(){
        onView(ViewMatchers.withId(R.id.navigation_header_title)).check(matches(withText(R.string.header_title)));
        onView(ViewMatchers.withId(R.id.navigation_header_description)).check(matches(withText(R.string.header_description)));
    }

    @Test
    public void shouldTellUserTheyHaveNotYetAddedAnyLocationsToTheirWatchlistIfWatchlistIsEmpty(){
        onView(ViewMatchers.withId(R.id.navigation_body_text)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(ViewMatchers.withId(R.id.navigation_recycler_view)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(ViewMatchers.withId(R.id.navigation_body_text)).check(matches(withText(R.string.drawer_body_empty)));
    }

    @Test
    public void shouldShowWatchedCitiesIfUserHasAddedThemToTheirList(){
        String new_york = "New York, New York";

        addLocationToWatchList(new_york);

        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activityRule.getActivity().updateWatchedLocations();
            }
        });

        onView(ViewMatchers.withId(R.id.navigation_body_text)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(ViewMatchers.withId(R.id.navigation_recycler_view)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(ViewMatchers.withId(R.id.navigation_rec_location)).check(matches(withText(new_york)));
    }

    private void addLocationToWatchList(String location) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activityRule.getActivity().getApplicationContext());
        Set<String> locations = preferences.getStringSet("watch_locations", new HashSet<String>());
        locations.add(location);
        preferences.edit().putStringSet("watch_locations", locations).commit();
    }
}