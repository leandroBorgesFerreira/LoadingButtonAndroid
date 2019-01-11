package br.com.simplepass.loadingbutton

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.FailureHandler
import androidx.test.espresso.Root
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.RootMatchers
import org.hamcrest.Matcher

class Wait private constructor(
    private val delay: Long,
    private val duration: Long,
    private val startTime: Long,
    private val viewMatcher: Matcher<View>,
    private val rootMatcher: Matcher<Root>,
    private val assertion: ViewAssertion
) : FailureHandler {

    init {
        checkAssertion()
    }

    override fun handle(error: Throwable?, viewMatcher: Matcher<View>?) {
        val currentTime = System.currentTimeMillis()

        if (currentTime > (startTime + duration)) {
            throw WaitingTimeoutException(error)
        }

        try {
            Thread.sleep(delay)
        } catch (e: InterruptedException) {
            // let it go
        }

        checkAssertion()
    }

    private fun checkAssertion() {
        Espresso.onView(viewMatcher)
                .inRoot(rootMatcher)
                .withFailureHandler(this)
                .check(assertion)
    }

    class Builder constructor(private val viewMatcher: Matcher<View>) {
        private var delay: Long = 50
        private var duration: Long = 5000
        private var rootMatcher: Matcher<Root> = RootMatchers.DEFAULT

        fun inRoot(matcher: Matcher<Root>): Builder {
            this.rootMatcher = matcher
            return this
        }

        fun delay(delay: Long): Builder {
            this.delay = delay
            return this
        }

        fun duration(duration: Long): Builder {
            this.duration = duration
            return this
        }

        fun until(assertion: ViewAssertion): Wait {
            val startTime = System.currentTimeMillis()
            return Wait(delay, duration, startTime, viewMatcher, rootMatcher, assertion)
        }
    }

    companion object {
        @JvmStatic
        fun onView(viewMatcher: Matcher<View>): Builder {
            return Builder(viewMatcher)
        }
    }
}

class WaitingTimeoutException(cause: Throwable?) : RuntimeException(cause)