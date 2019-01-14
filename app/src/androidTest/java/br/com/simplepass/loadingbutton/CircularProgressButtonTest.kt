package br.com.simplepass.loadingbutton

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
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
    val activityTestRule = ActivityTestRule(MainActivity::class.java, true, true)

    //Buttons that doesn't revert the animation very fast
    private val slowProgressButtons = arrayOf(
        R.id.buttonTest1,
        R.id.buttonTest2,
        R.id.buttonTest6,
        R.id.buttonTest7,
        R.id.buttonTest8
    )

    private val allProgressButtons = arrayOf(
        R.id.buttonTest1,
        R.id.buttonTest2,
        R.id.buttonTest3,
        R.id.buttonTest4,
        R.id.buttonTest5,
        R.id.buttonTest6,
        R.id.buttonTest7,
        R.id.buttonTest8
    )

    private fun testButtonTextVisibilityBeforeAnimation(id: Int) {
        onView(withId(id))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), isEnabled(), isClickable(), withText(R.string.send))))
            .perform(click())
            .check(matches(not(withText(R.string.send))))
    }

    private fun testButtonTextVisibilityBeforeAndAfterAnimation(id: Int) {
        val progressButton: CircularProgressButton = activityTestRule.activity.findViewById(id)

        onView(withId(id))
            .perform(scrollTo())
            .check(matches(allOf(isDisplayed(), isEnabled(), isClickable(), withText(R.string.send))))
            .perform(click())

        val progressButtonIdlingResource = ProgressButtonStateIdlingResource(progressButton)
        IdlingRegistry.getInstance().register(progressButtonIdlingResource)

        onView(withId(id)).check(matches(withText(R.string.send)))
        IdlingRegistry.getInstance().unregister(progressButtonIdlingResource)
    }

    @Test
    fun testTextVisibleBeforeSpinnerAnimation() {
        slowProgressButtons.forEach(::testButtonTextVisibilityBeforeAnimation)
    }

    @Test
    fun testTextVisibleBeforeAndAfterSpinnerAnimation() {
        allProgressButtons.forEach(::testButtonTextVisibilityBeforeAndAfterAnimation)
    }

    @Test
    fun testTextReShownImmediatelyAfterClick() {
        onView(withId(R.id.buttonTest3))
                .check(matches(allOf(isDisplayed(), isEnabled(), isClickable(), withText(R.string.send))))
                .perform(click())
                .check(matches(withText(R.string.send)))
    }
}
