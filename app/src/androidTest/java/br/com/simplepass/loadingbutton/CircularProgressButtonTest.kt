package br.com.simplepass.loadingbutton

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import br.com.simplepass.loadingbuttonsample.MainActivity
import br.com.simplepass.loadingbuttonsample.R
import org.hamcrest.Matchers.not
import org.hamcrest.core.AllOf.allOf
import org.junit.Rule
import org.junit.Test

/**
 * [Testing Fundamentals](http://d.android.com/tools/testing/testing_android.html)
 */
class CircularProgressButtonTest {

    @JvmField
    @Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, true)

    @Test
    fun testTextVisibleBeforeSpinnerAnimation() {
        onView(withId(R.id.buttonTest1))
                .check(matches(allOf<View>(isDisplayed(), isEnabled(), isClickable(), withText(R.string.send))))
                .perform(click())
                .check(matches(not(withText(R.string.send))))
    }

    @Test
    fun testTextVisibleBeforeAndAfterSpinnerAnimation() {
        onView(withId(R.id.buttonTest2))
                .check(matches(allOf<View>(isDisplayed(), isEnabled(), isClickable(), withText(R.string.send))))
                .perform(click())
                .check(matches(not(withText(R.string.send)))) // verify spinner is showing immediately after click...

        Wait.onView(withId(R.id.buttonTest2))
                .duration(5000)
                .delay(50)
                .until(matches(withText(R.string.send)))
    }

    @Test
    fun testTextReShownImmediatelyAfterClick() {
        onView(withId(R.id.buttonTest3))
                .check(matches(allOf<View>(isDisplayed(), isEnabled(), isClickable(), withText(R.string.send))))
                .perform(click())
                .check(matches(withText(R.string.send)))
    }
}