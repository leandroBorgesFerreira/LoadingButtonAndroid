package br.com.simplepass.loadingbutton

import android.app.Application
import android.content.Intent
import android.view.View
import android.view.View.VISIBLE
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import br.com.simplepass.loadingbuttonsample.MainActivity
import br.com.simplepass.loadingbuttonsample.R
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * [Testing Fundamentals](http://d.android.com/tools/testing/testing_android.html)
 */
@RunWith(AndroidJUnit4::class)
class CircularProgressButtonTest {

    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    private lateinit var app: Application

    @Before
    fun setup() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        app = instrumentation.targetContext.applicationContext as Application
    }

    @Test
    fun testTextVisibleBeforeSpinnerAnimation() {
        val mainActivity = activityTestRule.launchActivity(Intent(app, MainActivity::class.java))

        onView(withId(R.id.buttonTest1))
                .check(matches(allOf<View>(
                        isDisplayed(), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), isEnabled())))

        onView(withId(R.id.buttonTest1))
                .check(matches(withText(
                        containsString(mainActivity.resources.getString(R.string.send)))))

        onView(withId(R.id.buttonTest1)).perform(click())

        onView(withId(R.id.buttonTest1))
                .check(matches(not<View>(withText(
                        containsString(mainActivity.resources.getString(R.string.send))))))
    }

    @Test
    fun testTextVisibleBeforeAndAfterSpinnerAnimation() {
        val mainActivity = activityTestRule.launchActivity(Intent(app, MainActivity::class.java))


        onView(withId(R.id.buttonTest2))
                .check(matches(allOf<View>(
                        isDisplayed(), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), isEnabled())))

        onView(withId(R.id.buttonTest2))
                .check(matches(withText(
                        containsString(mainActivity.resources.getString(R.string.send)))))

        onView(withId(R.id.buttonTest2)).perform(click())

        val circularProgressButton = mainActivity.findViewById<CircularProgressButton>(R.id.buttonTest2)
        Espresso.registerIdlingResources(ViewVisibilityIdlingResource(circularProgressButton, VISIBLE))

        onView(withId(R.id.buttonTest2))
                .check(matches(not<View>(withText(
                        containsString(mainActivity.resources.getString(R.string.send))))))
    }

    @Test
    fun testTextReShownImmediatelyAfterClick() {
        val mainActivity = activityTestRule.launchActivity(Intent(app, MainActivity::class.java))

        onView(withId(R.id.buttonTest3))
                .check(matches(allOf<View>(
                        isDisplayed(), withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE), isEnabled())))

        onView(withId(R.id.buttonTest3))
                .check(matches(withText(
                        containsString(mainActivity.resources.getString(R.string.send)))))

        onView(withId(R.id.buttonTest3)).perform(click())

        onView(withId(R.id.buttonTest3))
                .check(matches(withText(
                        containsString(mainActivity.resources.getString(R.string.send)))))
    }

}