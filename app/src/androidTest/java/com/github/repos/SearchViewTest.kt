package com.github.repos

import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.runner.AndroidJUnit4
import android.widget.AutoCompleteTextView

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before
import com.github.repos.views.activities.MainActivity
import android.support.test.rule.ActivityTestRule
import org.junit.Rule
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom


@RunWith(AndroidJUnit4::class)
class SearchViewTest {

    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun launchMainActivity() {
        activityTestRule.launchActivity(Intent())
    }

    @Test
    fun testToolbarSearchView() {

        onView(withId(R.id.action_search)).perform(click())

        onView(isAssignableFrom(AutoCompleteTextView::class.java)).perform(click(),typeText("samples"))
        Thread.sleep(2000)
    }

}
